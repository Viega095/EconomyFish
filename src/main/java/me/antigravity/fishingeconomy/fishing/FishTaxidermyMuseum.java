package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class FishTaxidermyMuseum implements Listener {

    public static class MuseumHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final FishingEconomy plugin;

    public FishTaxidermyMuseum(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void openMuseumGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new MuseumHolder(), 27, "§8🏛️ §eMuseo de Taxidermia & Acuarios §8🏛️");

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, createPane(Material.CYAN_STAINED_GLASS_PANE));
        }

        // Pedestal Showcase Spawner
        inv.setItem(11, createBtn(Material.ARMOR_STAND, "§6🏆 Crear Pedestal de Exhibición 3D",
                Arrays.asList("§7Genera una estatua de taxidermia", "§7con tu mejor captura récord en el mundo.", "", "§a▶ Clic para colocar pedestal")));

        // Collect passive revenue
        inv.setItem(13, createBtn(Material.GOLD_BLOCK, "§e💰 Recaudar Taquilla del Museo",
                Arrays.asList("§7Tus especímenes han generado", "§7ingresos por visitantes curiosos.", "", "§a▶ Clic para reclamar ganancias")));

        // Aquarium showcase
        inv.setItem(15, createBtn(Material.GLASS, "§b🐠 Tanque de Cristal Panorámico",
                Arrays.asList("§7Exhibe peces vivos en tanques", "§7con agua cristalina e iluminación.", "", "§a▶ Clic para gestionar acuario")));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.8f, 1.2f);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof MuseumHolder) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;

            int slot = event.getRawSlot();
            if (slot == 11) {
                player.closeInventory();
                spawnPedestalAtPlayer(player);
            } else if (slot == 13) {
                player.closeInventory();
                double revenue = 1250.0 + (Math.random() * 2000.0);
                plugin.getEconomyManager().depositPlayer(player, revenue);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.5f);
                player.sendMessage(ChatColor.GOLD + "🏛️ [Museo] ¡Has recaudado +" + plugin.getEconomyManager().format(revenue) + " en venta de entradas del museo!");
            } else if (slot == 15) {
                player.closeInventory();
                if (plugin.getAquariumManager() != null) {
                    plugin.getAquariumManager().openAquariumGUI(player);
                }
            }
        }
    }

    private void spawnPedestalAtPlayer(Player player) {
        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        stand.setCustomName("§6🏆 §eGran Captura de " + player.getName() + " §6🏆");
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setArms(true);
        stand.setBasePlate(false);
        stand.setHelmet(new ItemStack(Material.PRISMARINE_BRICKS));
        stand.getEquipment().setItemInMainHand(new ItemStack(Material.TROPICAL_FISH));

        player.sendTitle("§6🏆 ¡PEDESTAL CREADO!", "§eTu trofeo está expuesto al público", 10, 50, 15);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.8f, 1.5f);
        player.sendMessage(ChatColor.GREEN + "🏛️ ¡Pedestal de taxidermia 3D colocado exitosamente en tu ubicación!");
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof MuseumHolder) {
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
