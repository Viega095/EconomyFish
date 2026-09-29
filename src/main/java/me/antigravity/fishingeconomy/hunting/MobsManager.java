package me.antigravity.fishingeconomy.hunting;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MobsManager implements Listener {
    private final FishingEconomy plugin;
    private final Map<EntityType, Double> mobPrices = new HashMap<>();

    public MobsManager(FishingEconomy plugin) {
        this.plugin = plugin;
        loadMobs();
    }

    public void loadMobs() {
        File file = new File(plugin.getDataFolder(), "mobs.yml");
        if (!file.exists()) {
            plugin.saveResource("mobs.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        mobPrices.clear();
        if (config.contains("mobs")) {
            for (String key : config.getConfigurationSection("mobs").getKeys(false)) {
                try {
                    EntityType type = EntityType.valueOf(key);
                    double price = config.getDouble("mobs." + key + ".price");
                    mobPrices.put(type, price);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid entity type in mobs.yml: " + key);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            Player killer = event.getEntity().getKiller();
            EntityType type = event.getEntityType();

            if (mobPrices.containsKey(type)) {
                double price = mobPrices.get(type);
                plugin.getEconomyManager().addBalance(killer, price);
                // Optional: Send message (maybe action bar to avoid spam)
                // For now, let's keep it silent or minimal spam.
                // killer.sendMessage("§a+ " + plugin.getEconomyManager().format(price));
            }
        }
    }
}
