package me.antigravity.fishingeconomy.market;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MarketManager {
    private final FishingEconomy plugin;
    private final Map<Material, Double> itemPrices = new HashMap<>();
    private final Random random = new Random();

    public MarketManager(FishingEconomy plugin) {
        this.plugin = plugin;
        initializeMarket();
    }

    private void initializeMarket() {
        // Base prices
        itemPrices.put(Material.DIAMOND, 100.0);
        itemPrices.put(Material.GOLD_INGOT, 50.0);
        itemPrices.put(Material.IRON_INGOT, 20.0);
        itemPrices.put(Material.EMERALD, 150.0);
        itemPrices.put(Material.NETHERITE_INGOT, 500.0);
    }

    public double getPrice(Material material) {
        return itemPrices.getOrDefault(material, 0.0);
    }

    public void fluctuatePrices() {
        for (Map.Entry<Material, Double> entry : itemPrices.entrySet()) {
            double currentPrice = entry.getValue();
            // Fluctuate by +/- 10%
            double change = (random.nextDouble() * 0.2) - 0.1;
            double newPrice = currentPrice * (1.0 + change);

            // Clamp prices
            if (newPrice < 1.0)
                newPrice = 1.0;

            itemPrices.put(entry.getKey(), newPrice);
        }
        plugin.getLogger().info("Market prices have fluctuated!");
    }
}
