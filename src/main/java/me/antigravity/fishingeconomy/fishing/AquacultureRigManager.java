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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AquacultureRigManager implements Listener {

    public static class FishingRig {
        public final UUID owner;
        public final String name;
        public final List<FishManager.CustomFish> storage = new ArrayList<>();
        public int capacity = 15;
        public boolean active = true;

        public FishingRig(UUID owner, String name) {
            this.owner = owner;
            this.name = name;
        }
    }

    private final FishingEconomy plugin;
    private final Map<UUID, FishingRig> rigs = new ConcurrentHashMap<>();

    public AquacultureRigManager(FishingEconomy plugin) {
        this.plugin = plugin;
        startRigTask();
    }

    private void startRigTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (FishingRig rig : rigs.values()) {
                    if (rig.active && rig.storage.size() < rig.capacity) {
                        FishManager.CustomFish fish = plugin.getFishManager().rollFish();
                        if (fish != null) {
                            rig.storage.add(fish);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 600L, 600L); // Passive catch every 30 seconds
    }

    public boolean deployRig(Player player) {
        if (rigs.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "✖ Ya tienes una Plataforma de Acuicultura desplegada. Usa /fish rig para gestionarla.");
            return false;
        }

        double cost = 25000.0;
        if (!plugin.getEconomyManager().has(player, cost)) {
            player.sendMessage(ChatColor.RED + "✖ Necesitas " + plugin.getEconomyManager().format(cost) + " para desplegar una Plataforma de Pesca.");
            return false;
        }

        plugin.getEconomyManager().withdrawPlayer(player, cost);
        FishingRig rig = new FishingRig(player.getUniqueId(), "Plataforma Acuática de " + player.getName());
        rigs.put(player.getUniqueId(), rig);

        player.sendMessage(ChatColor.AQUA + "🏗️ ¡Plataforma de Acuicultura desplegada exitosamente! Capturará peces pasivamente en alta mar.");
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
        return true;
    }

    public void openRigGUI(Player player) {
        FishingRig rig = rigs.get(player.getUniqueId());
        if (rig == null) {
            player.sendMessage(ChatColor.YELLOW + "No posees una plataforma activa. Despliégala con " + ChatColor.GOLD + "/fish rig deploy");
            return;
        }

        Inventory inv = Bukkit.createInventory(new RigHolder(), 27, "§8🏗️ Plataforma de Pesca Pasiva");

        ItemStack filler = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta fMeta = filler.getItemMeta();
        if (fMeta != null) {
            fMeta.setDisplayName(" ");
            filler.setItemMeta(fMeta);
        }
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        // Fish items
        int slot = 10;
        double totalValue = 0;
        for (FishManager.CustomFish fish : rig.storage) {
            if (slot > 16) break;
            ItemStack item = plugin.getFishManager().createFishItem(fish);
            inv.setItem(slot++, item);
            totalValue += fish.price;
        }

        // Collect button
        ItemStack collect = new ItemStack(Material.HOPPER);
        ItemMeta cMeta = collect.getItemMeta();
        if (cMeta != null) {
            cMeta.setDisplayName("§a§lRecolectar Capturas (" + rig.storage.size() + "/" + rig.capacity + ")");
            cMeta.setLore(Arrays.asList(
                    "§7Valor acumulado: §a" + plugin.getEconomyManager().format(totalValue),
                    "",
                    "§e▶ Haz clic para transferir todos los peces a tu inventario"
            ));
            collect.setItemMeta(cMeta);
        }
        inv.setItem(22, collect);

        player.openInventory(inv);
    }

    public void collectRig(Player player) {
        FishingRig rig = rigs.get(player.getUniqueId());
        if (rig == null || rig.storage.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "La plataforma no tiene capturas listas para recolectar.");
            return;
        }

        int count = 0;
        for (FishManager.CustomFish fish : new ArrayList<>(rig.storage)) {
            ItemStack item = plugin.getFishManager().createFishItem(fish);
            HashMap<Integer, ItemStack> left = player.getInventory().addItem(item);
            if (!left.isEmpty()) {
                player.getWorld().dropItem(player.getLocation(), item);
            }
            count++;
        }
        rig.storage.clear();

        player.sendMessage(ChatColor.GREEN + "✓ Has recolectado exitosamente " + count + " peces de tu plataforma.");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.5f);
        player.closeInventory();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof RigHolder) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;

            if (event.getRawSlot() == 22) {
                collectRig(player);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof RigHolder) {
            event.setCancelled(true);
        }
    }

    public static class RigHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
