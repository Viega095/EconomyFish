package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SeaMerchantManager {

    private final FishingEconomy plugin;

    public SeaMerchantManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void openBlackMarket(Player player) {
        player.sendMessage(ChatColor.DARK_BLUE + "=== 🏴‍☠️ " + ChatColor.BLUE + "GALEÓN MERCANTE DEL MERCADO NEGRO" + ChatColor.DARK_BLUE + " ===");
        player.sendMessage(ChatColor.DARK_AQUA + "1. " + ChatColor.YELLOW + "Anzuelo del Vacío Abisal " + ChatColor.GRAY + "($75,000) - " + ChatColor.AQUA + "/merchant buy anzuelo");
        player.sendMessage(ChatColor.DARK_AQUA + "2. " + ChatColor.YELLOW + "Mapa del Tesoro Sumergido " + ChatColor.GRAY + "($30,000) - " + ChatColor.AQUA + "/merchant buy mapa");
        player.sendMessage(ChatColor.DARK_AQUA + "3. " + ChatColor.YELLOW + "Cebo Prohibido de Sirena " + ChatColor.GRAY + "($15,000) - " + ChatColor.AQUA + "/merchant buy cebo");
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1f, 0.8f);
    }

    public boolean purchaseItem(Player player, String itemKey) {
        double cost = 0;
        ItemStack item = null;

        if (itemKey.equalsIgnoreCase("anzuelo")) {
            cost = 75000.0;
            item = new ItemStack(Material.NAUTILUS_SHELL);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§5§l✦ Anzuelo del Vacío Abisal ✦");
            meta.setLore(List.of("§7Atrae monstruos de las profundidades insondables.", "§d+100% Suerte de Pesca Mítica"));
            item.setItemMeta(meta);
        } else if (itemKey.equalsIgnoreCase("mapa")) {
            cost = 30000.0;
            item = new ItemStack(Material.FILLED_MAP);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6§l✦ Mapa del Tesoro Sumergido ✦");
            meta.setLore(List.of("§7Revela la ubicación de un galeón pirata hundido.", "§eContiene lingotes y reliquias"));
            item.setItemMeta(meta);
        } else if (itemKey.equalsIgnoreCase("cebo")) {
            cost = 15000.0;
            item = new ItemStack(Material.GLOW_BERRIES);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§b§l✦ Cebo Prohibido de Sirena ✦");
            meta.setLore(List.of("§7Acelera la velocidad de mordida al instante.", "§b+80% Velocidad de Captura"));
            item.setItemMeta(meta);
        } else {
            player.sendMessage(ChatColor.RED + "Artículo no encontrado en el mercado negro.");
            return false;
        }

        if (!plugin.getEconomyManager().has(player, cost)) {
            player.sendMessage(ChatColor.RED + "✖ Fondos insuficientes (" + plugin.getEconomyManager().format(cost) + ")");
            return false;
        }

        plugin.getEconomyManager().withdraw(player, cost);
        player.getInventory().addItem(item);
        player.sendMessage(ChatColor.GOLD + "🏴‍☠️ [Galeón Mercante] ¡Has adquirido " + item.getItemMeta().getDisplayName() + ChatColor.GOLD + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
        return true;
    }
}
