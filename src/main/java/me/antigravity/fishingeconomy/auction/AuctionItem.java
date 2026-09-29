package me.antigravity.fishingeconomy.auction;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionItem {
    public final UUID seller;
    public final ItemStack item;
    public final double price;
    public final long timestamp;

    public AuctionItem(UUID seller, ItemStack item, double price, long timestamp) {
        this.seller = seller;
        this.item = item;
        this.price = price;
        this.timestamp = timestamp;
    }
}
