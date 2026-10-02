package me.antigravity.fishingeconomy.gui;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.fishing.BaitManager;
import me.antigravity.fishingeconomy.fishing.FishManager;
import me.antigravity.fishingeconomy.fishing.RodCraftingManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CustomRodStatsGui implements Listener {

    public static class RodStatsHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final FishingEconomy plugin;
    private final NamespacedKey keyCatches;
    private final NamespacedKey keyMaxWeight;
    private final NamespacedKey keyLevel;
    private final NamespacedKey keyExp;

    public CustomRodStatsGui(FishingEconomy plugin) {
        this.plugin = plugin;
        this.keyCatches = new NamespacedKey(plugin, "rod_catches_count");
        this.keyMaxWeight = new NamespacedKey(plugin, "rod_max_weight");
        this.keyLevel = new NamespacedKey(plugin, "rod_marine_level");
        this.keyExp = new NamespacedKey(plugin, "rod_marine_exp");
    }

    public void updateRodStats(ItemStack rod, FishManager.CustomFish caughtFish) {
        if (rod == null || rod.getType() != Material.FISHING_ROD || caughtFish == null) return;
        ItemMeta meta = rod.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        int catches = pdc.getOrDefault(keyCatches, PersistentDataType.INTEGER, 0) + 1;
        double currentMax = pdc.getOrDefault(keyMaxWeight, PersistentDataType.DOUBLE, 0.0);
        double fishWeight = (caughtFish.price * 0.12) + (Math.random() * 4.5);
        double maxWeight = Math.max(currentMax, fishWeight);
        int exp = pdc.getOrDefault(keyExp, PersistentDataType.INTEGER, 0) + (int) (caughtFish.price / 10) + 5;
        int level = pdc.getOrDefault(keyLevel, PersistentDataType.INTEGER, 1);

        int requiredExp = level * 100;
        if (exp >= requiredExp) {
            level++;
            exp -= requiredExp;
        }

        pdc.set(keyCatches, PersistentDataType.INTEGER, catches);
        pdc.set(keyMaxWeight, PersistentDataType.DOUBLE, maxWeight);
        pdc.set(keyExp, PersistentDataType.INTEGER, exp);
        pdc.set(keyLevel, PersistentDataType.INTEGER, level);

        rod.setItemMeta(meta);
    }

    public void open(Player player) {
        ItemStack rod = player.getInventory().getItemInMainHand();
        if (rod.getType() != Material.FISHING_ROD) {
            rod = player.getInventory().getItemInOffHand();
        }

        if (rod.getType() != Material.FISHING_ROD) {
            player.sendMessage(ChatColor.RED + "✖ Debes tener una caña de pescar en la mano para ver sus estadísticas.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        Inventory inv = Bukkit.createInventory(new RodStatsHolder(), 45, "§8🎣 §bInspección de Caña & Cebos §8🎣");

        // Border
        for (int i = 0; i < 45; i++) {
            if (i < 9 || i >= 36 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, createPane(Material.CYAN_STAINED_GLASS_PANE));
            }
        }

        // Stats from PDC
        ItemMeta meta = rod.getItemMeta();
        int catches = 0;
        double maxWeight = 0.0;
        int level = 1;
        int exp = 0;
        if (meta != null) {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            catches = pdc.getOrDefault(keyCatches, PersistentDataType.INTEGER, 0);
            maxWeight = pdc.getOrDefault(keyMaxWeight, PersistentDataType.DOUBLE, 0.0);
            level = pdc.getOrDefault(keyLevel, PersistentDataType.INTEGER, 1);
            exp = pdc.getOrDefault(keyExp, PersistentDataType.INTEGER, 0);
        }

        int reqExp = level * 100;

        // Check equipped bait in offhand
        ItemStack offhand = player.getInventory().getItemInOffHand();
        BaitManager.BaitType equippedBait = plugin.getBaitManager() != null ? plugin.getBaitManager().getBaitFromItem(offhand) : null;
        String baitStatus = equippedBait != null ? "§a" + equippedBait.displayName + " §7(x" + offhand.getAmount() + ")" : "§cNinguno equipado en Mano Secundaria";

        // Slot 13: Central Rod Display
        ItemStack displayRod = rod.clone();
        ItemMeta dMeta = displayRod.getItemMeta();
        if (dMeta != null) {
            List<String> lore = dMeta.hasLore() && dMeta.getLore() != null ? new ArrayList<>(dMeta.getLore()) : new ArrayList<>();
            lore.add("");
            lore.add("§6══════════ §eEstadísticas de la Caña §6══════════");
            lore.add("§7✦ Nivel Marino de Caña: §eNivel " + level);
            lore.add("§7✦ Experiencia de Caña: §b" + exp + "/" + reqExp + " EXP");
            lore.add("§7✦ Peces Totales Capturados: §a" + catches + " capturas");
            lore.add("§7✦ Mayor Récord de Peso: §d" + String.format(Locale.US, "%.2f", maxWeight) + " kg");
            lore.add("§7✦ Cebo en Mano Secundaria: " + baitStatus);
            lore.add("§6══════════════════════════════════════");
            dMeta.setLore(lore);
            displayRod.setItemMeta(dMeta);
        }
        inv.setItem(13, displayRod);

        // Bait Fast-Loader Buttons: Slots 20, 22, 24
        inv.setItem(20, createBaitBtn("golden_worm", "§6🪱 Equipar Gusano Dorado", Arrays.asList(
                "§7Rareza: §a+50% Suerte",
                "§7Velocidad: §e+30% Pique Rápido",
                "",
                "§e▶ Clic para recibir / equipar en mano secundaria"
        )));

        inv.setItem(22, createBaitBtn("abyssal_chum", "§5🪱 Equipar Engodo Abisal", Arrays.asList(
                "§7Rareza: §d+120% Probabilidad Mítica",
                "§7Atracción: §5Criaturas de las Profundidades",
                "",
                "§e▶ Clic para recibir / equipar en mano secundaria"
        )));

        inv.setItem(24, createBaitBtn("starlight_krill", "§b🪱 Equipar Krill Luz Estelar", Arrays.asList(
                "§7Velocidad: §b+100% Pique Instantáneo",
                "§7Economía: §a+80% Valor de Venta",
                "",
                "§e▶ Clic para recibir / equipar en mano secundaria"
        )));

        // Shortcut Buttons
        inv.setItem(29, createBtn(Material.ANVIL, "§e🔨 Astillero de Cañas Míticas", Arrays.asList("§7Forja cañas superiores con", "§7habilidades y bonos pasivos.", "", "§e▶ Clic para abrir")));
        inv.setItem(31, createBtn(Material.ENCHANTING_TABLE, "§d✨ Altar de Encantamientos", Arrays.asList("§7Imbuye tu caña con encantamientos:", "§7Llamada Abisal, Sonar, Resonancia.", "", "§d▶ Clic para abrir")));
        inv.setItem(33, createBtn(Material.CAULDRON, "§a🧪 Caldero de Cebos", Arrays.asList("§7Crea y fabrica nuevos cebos", "§7con ingredientes recolectados.", "", "§a▶ Clic para abrir")));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 1.2f);
    }

    private ItemStack createBaitBtn(String baitId, String name, List<String> lore) {
        BaitManager.BaitType type = plugin.getBaitManager() != null ? plugin.getBaitManager().createBaitItem(baitId, 1) != null ? plugin.getBaitManager().getBaitFromItem(plugin.getBaitManager().createBaitItem(baitId, 1)) : null : null;
        Material mat = type != null ? type.material : Material.GLOW_BERRIES;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createBtn(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createPane(Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof RodStatsHolder) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;

            int slot = event.getRawSlot();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, 1f);

            if (slot == 20) {
                equipBaitToPlayer(player, "golden_worm");
            } else if (slot == 22) {
                equipBaitToPlayer(player, "abyssal_chum");
            } else if (slot == 24) {
                equipBaitToPlayer(player, "starlight_krill");
            } else if (slot == 29) {
                player.closeInventory();
                plugin.getCustomRodGui().open(player);
            } else if (slot == 31) {
                player.closeInventory();
                plugin.getRodEnchantManager().openEnchantGUI(player);
            } else if (slot == 33) {
                player.closeInventory();
                plugin.getBaitCraftingStation().openGUI(player);
            }
        }
    }

    private void equipBaitToPlayer(Player player, String baitId) {
        if (plugin.getBaitManager() == null) return;
        ItemStack baitItem = plugin.getBaitManager().createBaitItem(baitId, 16);
        if (baitItem != null) {
            player.getInventory().setItemInOffHand(baitItem);
            player.sendMessage(ChatColor.GREEN + "🪱 ¡Cebo " + ChatColor.GOLD + baitId + ChatColor.GREEN + " equipado en tu mano secundaria (x16)!");
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1f, 1.4f);
            open(player);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof RodStatsHolder) {
            event.setCancelled(true);
        }
    }
}
