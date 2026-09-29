package me.antigravity.fishingeconomy.market;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class StockMarketGui implements Listener {
    private final FishingEconomy plugin;

    public StockMarketGui(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void openStockMarket(Player player) {
        Inventory gui = Bukkit.createInventory(new MarketHolder(), 27, "§8Stock Market");

        addMarketItem(gui, 10, Material.DIAMOND);
        addMarketItem(gui, 11, Material.GOLD_INGOT);
        addMarketItem(gui, 12, Material.IRON_INGOT);
        addMarketItem(gui, 13, Material.EMERALD);
        addMarketItem(gui, 14, Material.NETHERITE_INGOT);

        player.openInventory(gui);
    }

    private void addMarketItem(Inventory gui, int slot, Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        double price = plugin.getMarketManager().getPrice(mat);
        meta.setDisplayName("§e" + mat.name());
        meta.setLore(Arrays.asList("§7Current Price: §a" + plugin.getEconomyManager().format(price)));
        item.setItemMeta(meta);
        gui.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        if (event.getClickedInventory() == null)
            return;
        if (!(event.getView().getTopInventory().getHolder() instanceof MarketHolder))
            return;

        event.setCancelled(true);
    }

    private static class MarketHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
