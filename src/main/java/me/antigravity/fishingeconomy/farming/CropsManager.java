package me.antigravity.fishingeconomy.farming;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CropsManager implements Listener {
    private final FishingEconomy plugin;
    private final Map<Material, Double> cropPrices = new HashMap<>();

    public CropsManager(FishingEconomy plugin) {
        this.plugin = plugin;
        loadCrops();
    }

    public void loadCrops() {
        File file = new File(plugin.getDataFolder(), "crops.yml");
        if (!file.exists()) {
            plugin.saveResource("crops.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        cropPrices.clear();
        if (config.contains("crops")) {
            for (String key : config.getConfigurationSection("crops").getKeys(false)) {
                try {
                    Material mat = Material.valueOf(key);
                    double price = config.getDouble("crops." + key + ".price");
                    cropPrices.put(mat, price);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material in crops.yml: " + key);
                }
            }
        }
    }

    @EventHandler
    public void onCropBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // Check if it's a fully grown crop
        if (block.getBlockData() instanceof Ageable) {
            Ageable ageable = (Ageable) block.getBlockData();
            if (ageable.getAge() != ageable.getMaximumAge())
                return;
        }

        // Check if we have a price for drops
        // This is a bit complex because BlockBreakEvent drops items naturally.
        // We can either clear drops and give money, or let them pick up and sell later.
        // The requirement said "Crop sell logic", usually implies auto-sell or manual
        // sell.
        // Let's implement auto-sell for now as it's more fun, or maybe just support
        // /sellall for crops too.
        // Actually, OresManager's /sellall only checked Ores. Let's make /sellall check
        // everything that has a price.
        // But for this Phase 2, let's stick to the requirement "Crop sell logic".
        // I'll update /sellall to include crops if I can, or just add crops to
        // OresManager? No, separate managers.
        // Let's make CropsManager handle /sellall logic too by exposing a method, or
        // just listen to break and auto-sell if enabled?
        // Let's keep it simple: /sellall will sell ores. I should probably update
        // /sellall to sell crops too.
        // But for now, let's just register the manager and maybe add a method to sell
        // crops.
    }

    public double getPrice(Material material) {
        return cropPrices.getOrDefault(material, 0.0);
    }

    public boolean isCrop(Material material) {
        return cropPrices.containsKey(material);
    }

    public void sellAllCrops(Player player) {
        double total = 0;
        int count = 0;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && cropPrices.containsKey(item.getType())) {
                double price = cropPrices.get(item.getType());
                total += price * item.getAmount();
                count += item.getAmount();
                player.getInventory().setItem(i, null);
            }
        }

        if (count > 0) {
            plugin.getEconomyManager().addBalance(player, total);
            player.sendMessage("§7Sold §e" + count + " §7crops for §a" + plugin.getEconomyManager().format(total));
        }
    }

    public Map<Material, Double> getCropPrices() {
        return new HashMap<>(cropPrices);
    }
}
