package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ShipwreckExpeditionManager implements Listener {

    public static class ShipwreckHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final FishingEconomy plugin;
    private final Map<UUID, LockpickGame> activeLockpicks = new HashMap<>();

    public static class LockpickGame {
        public int[] solution = new int[4];
        public int[] current = new int[4];
        public int attemptsLeft = 5;

        public LockpickGame() {
            for (int i = 0; i < 4; i++) {
                solution[i] = ThreadLocalRandom.current().nextInt(1, 6);
                current[i] = 1;
            }
        }
    }

    public ShipwreckExpeditionManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void openExpeditionMapGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new ShipwreckHolder(), 27, "§8🚢 §1Expediciones a Galeones Hundidos §8🚢");

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, createPane(Material.BLUE_STAINED_GLASS_PANE));
        }

        inv.setItem(11, createBtn(Material.MAP, "§6🗺️ Mapa: Galeón de las Caimán (Tier I)",
                Arrays.asList("§7Profundidad: 15 metros", "§7Recompensa: Doblones de Oro & Perlas", "§7Dificultad: Baja", "", "§a▶ Clic para bucear & forzar cofre")));

        inv.setItem(13, createBtn(Material.FILLED_MAP, "§b🗺️ Mapa: Fragata 'El Conquistador' (Tier II)",
                Arrays.asList("§7Profundidad: 45 metros", "§7Recompensa: Joyas de Rubí & Fragmento de Tridente", "§7Dificultad: Media", "", "§a▶ Clic para bucear & forzar cofre")));

        inv.setItem(15, createBtn(Material.MAP, "§d🗺️ Mapa: Acorazado del Rey del Mar (Tier III)",
                Arrays.asList("§7Profundidad: 90 metros (Requiere Traje)", "§7Recompensa: Corona de Poseidón & Reliquias Míticas", "§7Dificultad: Alta", "", "§a▶ Clic para bucear & forzar cofre")));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.7f, 1.2f);
    }

    public void startLockpickMinigame(Player player, int tier) {
        LockpickGame game = new LockpickGame();
        activeLockpicks.put(player.getUniqueId(), game);

        renderLockpickGUI(player, game, tier);
    }

    private void renderLockpickGUI(Player player, LockpickGame game, int tier) {
        Inventory inv = Bukkit.createInventory(new ShipwreckHolder(), 36, "§8🔐 §6Ganzúa Submarina - Cofre T" + tier);

        for (int i = 0; i < 36; i++) {
            inv.setItem(i, createPane(Material.GRAY_STAINED_GLASS_PANE));
        }

        // Display tumblers (Slots 10, 12, 14, 16)
        int[] slots = { 10, 12, 14, 16 };
        for (int i = 0; i < 4; i++) {
            int val = game.current[i];
            inv.setItem(slots[i], createBtn(Material.TRIPWIRE_HOOK, "§ePerno #" + (i + 1) + " §7(Nivel: " + val + "/5)",
                    Arrays.asList("§7Haz clic para ajustar la altura del perno.", "", "§a▶ Clic Izquierdo: +1", "§c◀ Clic Derecho: -1")));
        }

        // Test button
        inv.setItem(31, createBtn(Material.LEVER, "§a🔓 Probar Ganzúa §7(Intentos: " + game.attemptsLeft + ")",
                Arrays.asList("§7Gira la cerradura para comprobar", "§7si los pernos coinciden.", "", "§a▶ Clic para intentar abrir")));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 0.8f, 1.4f);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof ShipwreckHolder) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            String title = event.getView().getTitle();

            if (title.contains("Expediciones")) {
                int slot = event.getRawSlot();
                if (slot == 11) {
                    player.closeInventory();
                    startLockpickMinigame(player, 1);
                } else if (slot == 13) {
                    player.closeInventory();
                    startLockpickMinigame(player, 2);
                } else if (slot == 15) {
                    player.closeInventory();
                    startLockpickMinigame(player, 3);
                }
                return;
            }

            if (title.contains("Ganzúa Submarina")) {
                LockpickGame game = activeLockpicks.get(player.getUniqueId());
                if (game == null) return;

                int slot = event.getRawSlot();
                int[] tumblerSlots = { 10, 12, 14, 16 };

                for (int i = 0; i < 4; i++) {
                    if (slot == tumblerSlots[i]) {
                        if (event.isLeftClick()) {
                            game.current[i] = (game.current[i] % 5) + 1;
                        } else {
                            game.current[i] = game.current[i] <= 1 ? 5 : game.current[i] - 1;
                        }
                        player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5f, 1.6f);
                        renderLockpickGUI(player, game, 1);
                        return;
                    }
                }

                if (slot == 31) {
                    // Check solution
                    boolean correct = true;
                    for (int i = 0; i < 4; i++) {
                        if (game.current[i] != game.solution[i]) {
                            correct = false;
                            break;
                        }
                    }

                    if (correct) {
                        player.closeInventory();
                        activeLockpicks.remove(player.getUniqueId());

                        double reward = 2500.0 + (Math.random() * 5000.0);
                        plugin.getEconomyManager().depositPlayer(player, reward);
                        player.sendTitle("§a✨ ¡COFRE DESBLOQUEADO!", "§6+" + plugin.getEconomyManager().format(reward), 10, 60, 20);
                        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
                        player.sendMessage(ChatColor.GOLD + "⚓ [Naufragio] ¡Has forzado la cerradura y obtenido tesoros antiguos!");
                    } else {
                        game.attemptsLeft--;
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.6f, 0.8f);

                        if (game.attemptsLeft <= 0) {
                            player.closeInventory();
                            activeLockpicks.remove(player.getUniqueId());
                            player.sendMessage(ChatColor.RED + "💥 ¡La ganzúa se ha partido y el mecanismo se bloqueó!");
                            player.playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.8f, 0.6f);
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "⚠ La ganzúa no encajó. Te quedan " + game.attemptsLeft + " intentos.");
                            renderLockpickGUI(player, game, 1);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof ShipwreckHolder) {
            event.setCancelled(true);
        }
    }

    private ItemStack createPane(Material material) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            pane.setItemMeta(meta);
        }
        return pane;
    }

    private ItemStack createBtn(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
