package me.antigravity.fishingeconomy.bounties;

import me.antigravity.fishingeconomy.FishingEconomy;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BountyManager {
    private final FishingEconomy plugin;
    private final Map<UUID, Double> bounties = new HashMap<>();
    private File file;
    private FileConfiguration config;

    public BountyManager(FishingEconomy plugin) {
        this.plugin = plugin;
        loadBounties();
    }

    private void loadBounties() {
        file = new File(plugin.getDataFolder(), "bounties.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);

        if (config.contains("bounties")) {
            for (String uuidStr : config.getConfigurationSection("bounties").getKeys(false)) {
                bounties.put(UUID.fromString(uuidStr), config.getDouble("bounties." + uuidStr));
            }
        }
    }

    public void saveBounties() {
        config.set("bounties", null); // Clear old data
        for (Map.Entry<UUID, Double> entry : bounties.entrySet()) {
            config.set("bounties." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBounty(OfflinePlayer target, double amount) {
        bounties.put(target.getUniqueId(), amount);
        saveBounties();
    }

    public double getBounty(OfflinePlayer target) {
        return bounties.getOrDefault(target.getUniqueId(), 0.0);
    }

    public void removeBounty(OfflinePlayer target) {
        bounties.remove(target.getUniqueId());
        saveBounties();
    }

    public Map<UUID, Double> getAllBounties() {
        return new HashMap<>(bounties);
    }
}
