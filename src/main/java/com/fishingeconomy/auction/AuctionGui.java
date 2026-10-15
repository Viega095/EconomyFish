package com.fishingeconomy.auction;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AuctionGui implements Listener {
    private final FishingEconomy plugin;

    public AuctionGui(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void openGui(Player player) {
        Inventory gui = Bukkit.createInventory(new AuctionHolder(), 54, "§8Casa de Subastas Global");

        List<AuctionItem> listings = plugin.getAuctionHouseManager().getListings();
        int slot = 0;
        for (AuctionItem listing : listings) {
            if (slot >= 54)
                break;

            ItemStack display = listing.item.clone();
            ItemMeta meta = display.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null)
                lore = new ArrayList<>();

            OfflinePlayer seller = Bukkit.getOfflinePlayer(listing.seller);
            lore.add("");
            lore.add("§7Vendedor: §e" + seller.getName());
            lore.add("§7Precio: §a" + plugin.getEconomyManager().format(listing.price));
            lore.add("§e¡Haz clic para Comprar!");

            meta.setLore(lore);
            display.setItemMeta(meta);

            gui.setItem(slot++, display);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        if (event.getClickedInventory() == null)
            return;
        if (!(event.getView().getTopInventory().getHolder() instanceof AuctionHolder))
            return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR)
            return;

        int slot = event.getRawSlot();
        if (slot >= 54)
            return;

        List<AuctionItem> listings = plugin.getAuctionHouseManager().getListings();
        if (slot >= listings.size())
            return;

        AuctionItem listing = listings.get(slot);

        if (listing.seller.equals(player.getUniqueId())) {
            // Cancel listing
            plugin.getAuctionHouseManager().removeListing(listing);
            player.getInventory().addItem(listing.item);
            player.sendMessage("§aSubasta cancelada. Ítem devuelto a tu inventario.");
            player.closeInventory();
            openGui(player);
            return;
        }

        if (!plugin.getEconomyManager().has(player, listing.price)) {
            player.sendMessage("§cNo tienes suficiente dinero para comprar este ítem.");
            return;
        }

        // Buy
        OfflinePlayer seller = Bukkit.getOfflinePlayer(listing.seller);
        plugin.getEconomyManager().withdrawPlayer(player, listing.price);
        plugin.getEconomyManager().depositPlayer(seller, listing.price);

        plugin.getAuctionHouseManager().removeListing(listing);
        player.getInventory().addItem(listing.item);

        player.sendMessage("§aÍtem comprado exitosamente por " + plugin.getEconomyManager().format(listing.price));
        if (seller.isOnline() && seller.getPlayer() != null) {
            seller.getPlayer().sendMessage("§a¡Tu ítem en subasta fue comprado por " + player.getName() + " por "
                    + plugin.getEconomyManager().format(listing.price) + "!");
        }

        player.closeInventory();
        openGui(player);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof AuctionHolder) {
            event.setCancelled(true);
        }
    }

    public static class AuctionHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
