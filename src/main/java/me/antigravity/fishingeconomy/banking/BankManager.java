package me.antigravity.fishingeconomy.banking;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankManager {
    private final FishingEconomy plugin;
    private final Map<UUID, Double> bankAccounts = new HashMap<>();
    private final File bankFile;

    public BankManager(FishingEconomy plugin) {
        this.plugin = plugin;
        this.bankFile = new File(plugin.getDataFolder(), "bank.yml");
        loadBank();
    }

    public void loadBank() {
        if (!bankFile.exists())
            return;
        FileConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
        if (config.contains("accounts")) {
            for (String key : config.getConfigurationSection("accounts").getKeys(false)) {
                bankAccounts.put(UUID.fromString(key), config.getDouble("accounts." + key));
            }
        }
    }

    public void saveBank() {
        FileConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : bankAccounts.entrySet()) {
            config.set("accounts." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(bankFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save bank.yml!");
        }
    }

    public double getBalance(OfflinePlayer player) {
        return bankAccounts.getOrDefault(player.getUniqueId(), 0.0);
    }

    public void setBalance(OfflinePlayer player, double amount) {
        bankAccounts.put(player.getUniqueId(), amount);
        saveBank(); // Save on change for safety
    }

    public void deposit(OfflinePlayer player, double amount) {
        setBalance(player, getBalance(player) + amount);
    }

    public void withdraw(OfflinePlayer player, double amount) {
        setBalance(player, getBalance(player) - amount);
    }

    public void applyInterest() {
        // 1% interest
        for (Map.Entry<UUID, Double> entry : bankAccounts.entrySet()) {
            double balance = entry.getValue();
            if (balance > 0) {
                double interest = balance * 0.01;
                bankAccounts.put(entry.getKey(), balance + interest);
            }
        }
        saveBank();
        plugin.getLogger().info("Applied bank interest.");
    }
}
