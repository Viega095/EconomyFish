package me.antigravity.fishingeconomy.auction;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        Inventory gui = Bukkit.createInventory(new AuctionHolder(), 54, "§8Global Auction House");

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
            lore.add("§7Seller: §e" + seller.getName());
            lore.add("§7Price: §a" + plugin.getEconomyManager().format(listing.price));
            lore.add("§eClick to Buy!");

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

        // Find listing (simplified matching by index for now, ideally use NBT or unique
        // ID)
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
            player.sendMessage("§aListing cancelled.");
            player.closeInventory();
            openGui(player);
            return;
        }

        if (!plugin.getEconomyManager().has(player, listing.price)) {
            player.sendMessage("§cYou don't have enough money.");
            return;
        }

        // Buy
        OfflinePlayer seller = Bukkit.getOfflinePlayer(listing.seller);
        plugin.getEconomyManager().withdrawPlayer(player, listing.price);
        plugin.getEconomyManager().depositPlayer(seller, listing.price);

        plugin.getAuctionHouseManager().removeListing(listing);
        player.getInventory().addItem(listing.item);

        player.sendMessage("§aItem purchased for " + plugin.getEconomyManager().format(listing.price));
        if (seller.isOnline()) {
            seller.getPlayer().sendMessage("§a" + player.getName() + " bought your item for "
                    + plugin.getEconomyManager().format(listing.price));
        }

        player.closeInventory();
        openGui(player);
    }

    private static class AuctionHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
