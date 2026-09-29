package me.antigravity.fishingeconomy.mining;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class OresManager {
    private final FishingEconomy plugin;
    private final Map<Material, Double> orePrices = new HashMap<>();

    public OresManager(FishingEconomy plugin) {
        this.plugin = plugin;
        loadOres();
    }

    public void loadOres() {
        File file = new File(plugin.getDataFolder(), "ores.yml");
        if (!file.exists()) {
            plugin.saveResource("ores.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        orePrices.clear();
        if (config.contains("ores")) {
            for (String key : config.getConfigurationSection("ores").getKeys(false)) {
                try {
                    Material mat = Material.valueOf(key);
                    double price = config.getDouble("ores." + key + ".price");
                    orePrices.put(mat, price);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material in ores.yml: " + key);
                }
            }
        }
    }

    public void sellAllOres(Player player) {
        double total = 0;
        int count = 0;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && orePrices.containsKey(item.getType())) {
                double price = orePrices.get(item.getType());
                total += price * item.getAmount();
                count += item.getAmount();
                player.getInventory().setItem(i, null);
            }
        }

        if (count > 0) {
            plugin.getEconomyManager().addBalance(player, total);
            player.sendMessage("§7Sold §e" + count + " §7ores for §a" + plugin.getEconomyManager().format(total));
        } else {
            player.sendMessage("§cYou don't have any ores to sell.");
        }
    }
}
