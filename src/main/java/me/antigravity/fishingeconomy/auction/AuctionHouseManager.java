package me.antigravity.fishingeconomy.auction;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionHouseManager {
    private final List<AuctionItem> listings = new ArrayList<>();

    public AuctionHouseManager(FishingEconomy plugin) {
        // Plugin instance not currently used but kept for constructor compatibility
    }

    public void addListing(UUID seller, ItemStack item, double price) {
        listings.add(new AuctionItem(seller, item, price, System.currentTimeMillis()));
    }

    public void removeListing(AuctionItem item) {
        listings.remove(item);
    }

    public List<AuctionItem> getListings() {
        return new ArrayList<>(listings);
    }

}
