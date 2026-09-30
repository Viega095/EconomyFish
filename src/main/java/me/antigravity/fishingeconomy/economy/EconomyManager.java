package me.antigravity.fishingeconomy.economy;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EconomyManager {
    private final FishingEconomy plugin;
    private final Map<UUID, Double> balanceCache = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;

    public EconomyManager(FishingEconomy plugin) {
        this.plugin = plugin;
        loadData();
    }

    public void loadData() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml!");
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        // Load all balances into cache
        if (dataConfig.contains("balances")) {
            for (String key : dataConfig.getConfigurationSection("balances").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    double balance = dataConfig.getDouble("balances." + key);
                    balanceCache.put(uuid, balance);
                } catch (IllegalArgumentException e) {
                    // Ignore invalid UUIDs
                }
            }
        }
    }

    public void saveAll() {
        if (dataConfig == null || dataFile == null)
            return;

        for (Map.Entry<UUID, Double> entry : balanceCache.entrySet()) {
            dataConfig.set("balances." + entry.getKey().toString(), entry.getValue());
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml!");
            e.printStackTrace();
        }
    }

    public double getBalance(OfflinePlayer player) {
        return balanceCache.getOrDefault(player.getUniqueId(), plugin.getConfig().getDouble("starting-balance", 0.0));
    }

    public void setBalance(OfflinePlayer player, double amount) {
        balanceCache.put(player.getUniqueId(), Math.max(0, amount));
        saveAsync(); // Save periodically or async could be better, but for now simple save
    }

    public void addBalance(OfflinePlayer player, double amount) {
        setBalance(player, getBalance(player) + amount);
    }

    public void withdrawBalance(OfflinePlayer player, double amount) {
        setBalance(player, getBalance(player) - amount);
    }

    public boolean hasBalance(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    // Aliases for consistency
    public void depositPlayer(OfflinePlayer player, double amount) {
        addBalance(player, amount);
    }

    public void withdrawPlayer(OfflinePlayer player, double amount) {
        withdrawBalance(player, amount);
    }

    public void deposit(OfflinePlayer player, double amount) {
        addBalance(player, amount);
    }

    public void withdraw(OfflinePlayer player, double amount) {
        withdrawBalance(player, amount);
    }

    public boolean has(OfflinePlayer player, double amount) {
        return hasBalance(player, amount);
    }

    public boolean has(UUID uuid, double amount) {
        return balanceCache.getOrDefault(uuid, plugin.getConfig().getDouble("starting-balance", 0.0)) >= amount;
    }

    public void withdraw(UUID uuid, double amount) {
        double current = balanceCache.getOrDefault(uuid, plugin.getConfig().getDouble("starting-balance", 0.0));
        balanceCache.put(uuid, Math.max(0, current - amount));
        saveAsync();
    }

    public void deposit(UUID uuid, double amount) {
        double current = balanceCache.getOrDefault(uuid, plugin.getConfig().getDouble("starting-balance", 0.0));
        balanceCache.put(uuid, current + amount);
        saveAsync();
    }

    public double getBalance(UUID uuid) {
        return balanceCache.getOrDefault(uuid, plugin.getConfig().getDouble("starting-balance", 0.0));
    }

    public String format(double amount) {
        String symbol = plugin.getConfig().getString("currency.symbol", "$");
        String format = plugin.getConfig().getString("currency.format", "%amount% %symbol%");
        return format.replace("%amount%", String.format("%.2f", amount)).replace("%symbol%", symbol).replace("&", "§");
    }

    public List<Map.Entry<UUID, Double>> getTopRich(int limit) {
        return balanceCache.entrySet().stream()
                .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private void saveAsync() {
        // For simplicity in this task, we'll just save on disable or periodically.
        // But to ensure data safety, let's save immediately for now or schedule a task.
        // Given "lightweight", let's just save.
        saveAll();
    }
}
