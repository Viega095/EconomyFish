package com.fishingeconomy.multiblock;

import com.fishingeconomy.FishingEconomy;
import com.fishingeconomy.fishing.FishManager;
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

import java.util.ArrayList;
import java.util.List;

public class FilletBenchGUI implements Listener {

    public static class FilletHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final FishingEconomy plugin;

    public FilletBenchGUI(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public static void open(FishingEconomy plugin, Player player) {
        Inventory inv = Bukkit.createInventory(new FilletHolder(), 27, "§8🔪 §bBanco de Fileteado Marino §8🔪");

        // Border panes
        ItemStack border = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, border);
        }

        // Count fish in player inventory
        int fishCount = 0;
        double estimatedValue = 0.0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                FishManager.CustomFish fish = plugin.getFishManager().getFishFromItem(item);
                if (fish != null) {
                    fishCount += item.getAmount();
                    estimatedValue += (fish.price * 1.20) * item.getAmount();
                }
            }
        }

        // Slot 11: Filetear Todo
        ItemStack filletBtn = new ItemStack(Material.IRON_SWORD);
        ItemMeta fMeta = filletBtn.getItemMeta();
        if (fMeta != null) {
            fMeta.setDisplayName("§e🔪 Filetear Capturas en Inventario");
            List<String> lore = new ArrayList<>();
            lore.add("§7Procesa todos los peces en filetes limpios.");
            lore.add("§7Otorga un §a+20% de bono §7sobre el precio normal.");
            lore.add("");
            lore.add("§7Peces detectados: §e" + fishCount);
            lore.add("§7Valor total estimado: §a" + plugin.getEconomyManager().format(estimatedValue));
            lore.add("");
            lore.add("§a▶ Haz clic para filetear y vender todo");
            fMeta.setLore(lore);
            filletBtn.setItemMeta(fMeta);
        }
        inv.setItem(11, filletBtn);

        // Slot 13: Info
        ItemStack info = new ItemStack(Material.SMOKER);
        ItemMeta iMeta = info.getItemMeta();
        if (iMeta != null) {
            iMeta.setDisplayName("§b✦ Banco de Fileteado Profesional ✦");
            List<String> lore = new ArrayList<>();
            lore.add("§7Convierte tus peces en filetes gourmet.");
            lore.add("§7Parte esencial de tu Acuario Mágico.");
            lore.add("§7Maximiza tus ganancias de pesca.");
            iMeta.setLore(lore);
            info.setItemMeta(iMeta);
        }
        inv.setItem(13, info);

        // Slot 15: Ir al Acuario
        ItemStack aquaBtn = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta aMeta = aquaBtn.getItemMeta();
        if (aMeta != null) {
            aMeta.setDisplayName("§3🐠 Abrir Acuario Mágico");
            List<String> lore = new ArrayList<>();
            lore.add("§7Inspecciona las especies en exhibición");
            lore.add("§7y recoge ingresos pasivos de turismo.");
            lore.add("");
            lore.add("§3▶ Haz clic para ver tu Acuario");
            aMeta.setLore(lore);
            aquaBtn.setItemMeta(aMeta);
        }
        inv.setItem(15, aquaBtn);

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1f, 1.2f);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof FilletHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();

        if (slot == 11) {
            // Process filleting
            int processed = 0;
            double totalIncome = 0.0;

            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null) {
                    FishManager.CustomFish fish = plugin.getFishManager().getFishFromItem(item);
                    if (fish != null) {
                        int amount = item.getAmount();
                        processed += amount;
                        totalIncome += (fish.price * 1.20) * amount;
                        player.getInventory().remove(item);
                    }
                }
            }

            if (processed > 0) {
                plugin.getEconomyManager().deposit(player, totalIncome);
                player.sendMessage(ChatColor.GREEN + "🔪 ¡Has fileteado " + ChatColor.YELLOW + processed +
                        ChatColor.GREEN + " peces por un total de " + ChatColor.GOLD +
                        plugin.getEconomyManager().format(totalIncome) + ChatColor.GREEN + " (+20% bono)!");
                player.playSound(player.getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1f, 1.3f);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.8f);
                player.closeInventory();
            } else {
                player.sendMessage(ChatColor.RED + "✖ No tienes peces en tu inventario para filetear.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            }
        } else if (slot == 15) {
            player.closeInventory();
            if (plugin.getAquariumManager() != null) {
                plugin.getAquariumManager().openAquariumGUI(player);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof FilletHolder) {
            event.setCancelled(true);
        }
    }
}
