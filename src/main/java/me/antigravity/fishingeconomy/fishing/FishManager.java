package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class FishManager {
    private final FishingEconomy plugin;
    private final Map<String, CustomFish> fishMap = new HashMap<>();
    private final Map<String, Double> rarityChances = new HashMap<>();
    private final NamespacedKey fishKey;

    public FishManager(FishingEconomy plugin) {
        this.plugin = plugin;
        this.fishKey = new NamespacedKey(plugin, "custom_fish_id");
        loadFish();
    }

    public void loadFish() {
        fishMap.clear();
        rarityChances.clear();

        ConfigurationSection rarities = plugin.getConfigManager().getFishConfig().getConfigurationSection("rarities");
        if (rarities != null) {
            for (String key : rarities.getKeys(false)) {
                rarityChances.put(key, rarities.getDouble(key + ".chance"));
            }
        }

        ConfigurationSection fishSection = plugin.getConfigManager().getFishConfig().getConfigurationSection("fish");
        if (fishSection != null) {
            for (String key : fishSection.getKeys(false)) {
                String path = "fish." + key;
                String rarity = plugin.getConfigManager().getFishConfig().getString(path + ".rarity");
                Material material = Material
                        .valueOf(plugin.getConfigManager().getFishConfig().getString(path + ".material", "COD"));
                String name = plugin.getConfigManager().getFishConfig().getString(path + ".name");
                List<String> lore = plugin.getConfigManager().getFishConfig().getStringList(path + ".lore");
                double price = plugin.getConfigManager().getFishConfig().getDouble(path + ".price");
                double chance = plugin.getConfigManager().getFishConfig().getDouble(path + ".chance");

                fishMap.put(key, new CustomFish(key, rarity, material, name, lore, price, chance));
            }
        }
    }

    public CustomFish rollFish() {
        // First roll rarity
        String rarity = rollRarity();
        if (rarity == null)
            return null;

        // Then roll fish of that rarity
        List<CustomFish> candidates = new ArrayList<>();
        for (CustomFish fish : fishMap.values()) {
            if (fish.rarity.equals(rarity)) {
                candidates.add(fish);
            }
        }

        if (candidates.isEmpty())
            return null;

        // Simple weighted random for fish within rarity
        double totalWeight = 0;
        for (CustomFish fish : candidates) {
            totalWeight += fish.chance;
        }

        double random = ThreadLocalRandom.current().nextDouble() * totalWeight;
        for (CustomFish fish : candidates) {
            random -= fish.chance;
            if (random <= 0) {
                return fish;
            }
        }

        return candidates.get(0);
    }

    private String rollRarity() {
        double totalWeight = 0;
        for (double chance : rarityChances.values()) {
            totalWeight += chance;
        }

        double random = ThreadLocalRandom.current().nextDouble() * totalWeight;
        for (Map.Entry<String, Double> entry : rarityChances.entrySet()) {
            random -= entry.getValue();
            if (random <= 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    public ItemStack createFishItem(CustomFish fish) {
        ItemStack item = new ItemStack(fish.material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', fish.name));
            List<String> lore = new ArrayList<>();
            for (String line : fish.lore) {
                lore.add(ChatColor.translateAlternateColorCodes('&',
                        line.replace("%price%", String.valueOf(fish.price))));
            }

            // Add auto-generated lore
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Rarity: &f" + fish.rarity));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Chance: &f" + fish.chance + "%"));
            lore.add(ChatColor.translateAlternateColorCodes('&',
                    "&7Market Value: &a" + plugin.getEconomyManager().format(fish.price)));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7AH Value: &c$0")); // Placeholder for Phase 9

            meta.setLore(lore);
            meta.getPersistentDataContainer().set(fishKey, PersistentDataType.STRING, fish.id);
            item.setItemMeta(meta);
        }
        return item;
    }

    public CustomFish getFishFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return null;
        String id = item.getItemMeta().getPersistentDataContainer().get(fishKey, PersistentDataType.STRING);
        return fishMap.get(id);
    }

    public static class CustomFish {
        public final String id;
        public final String rarity;
        public final Material material;
        public final String name;
        public final List<String> lore;
        public final double price;
        public final double chance;

        public CustomFish(String id, String rarity, Material material, String name, List<String> lore, double price,
                double chance) {
            this.id = id;
            this.rarity = rarity;
            this.material = material;
            this.name = name;
            this.lore = lore;
            this.price = price;
            this.chance = chance;
        }
    }
}
