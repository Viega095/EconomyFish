package com.fishingeconomy.economy;

import com.fishingeconomy.FishingEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EconomyManager {
    private final FishingEconomy plugin;
    private final Map<UUID, Double> balanceCache = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;
    private Economy vaultEconomy = null;
    private boolean vaultChecked = false;

    public EconomyManager(FishingEconomy plugin) {
        this.plugin = plugin;
        loadData();
    }

    public void setupVault() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            vaultChecked = true;
            return;
        }

        try {
            RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                Economy provider = rsp.getProvider();
                if (provider != null && !provider.getClass().getName().contains("com.fishingeconomy")) {
                    this.vaultEconomy = provider;
                    plugin.getLogger().info("§a✔ Hooked directly into Vault Economy provider: " + provider.getName() + " (Unified server wallet)");
                }
            }
        } catch (Throwable t) {
            plugin.getLogger().warning("Could not hook into Vault economy: " + t.getMessage());
        }
        vaultChecked = true;
    }

    public Economy getVaultEconomy() {
        if (!vaultChecked || vaultEconomy == null) {
            setupVault();
        }
        return vaultEconomy;
    }

    public boolean isUsingVault() {
        return getVaultEconomy() != null;
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

    public double getInternalBalance(OfflinePlayer player) {
        if (player == null) return 0.0;
        return balanceCache.getOrDefault(player.getUniqueId(), plugin.getConfig().getDouble("starting-balance", 0.0));
    }

    public double getBalance(OfflinePlayer player) {
        if (player == null) return 0.0;
        Economy vEco = getVaultEconomy();
        if (vEco != null) {
            return vEco.getBalance(player);
        }
        return getInternalBalance(player);
    }

    public void setBalance(OfflinePlayer player, double amount) {
        if (player == null) return;
        double target = Math.max(0, amount);
        balanceCache.put(player.getUniqueId(), target);
        saveAsync();

        Economy vEco = getVaultEconomy();
        if (vEco != null) {
            double current = vEco.getBalance(player);
            double diff = target - current;
            if (diff > 0) {
                vEco.depositPlayer(player, diff);
            } else if (diff < 0) {
                vEco.withdrawPlayer(player, -diff);
            }
        }
    }

    public void addBalance(OfflinePlayer player, double amount) {
        if (player == null || amount <= 0) return;
        Economy vEco = getVaultEconomy();
        if (vEco != null) {
            vEco.depositPlayer(player, amount);
            balanceCache.put(player.getUniqueId(), vEco.getBalance(player));
            saveAsync();
            return;
        }
        setBalance(player, getBalance(player) + amount);
    }

    public void withdrawBalance(OfflinePlayer player, double amount) {
        if (player == null || amount <= 0) return;
        Economy vEco = getVaultEconomy();
        if (vEco != null) {
            vEco.withdrawPlayer(player, amount);
            balanceCache.put(player.getUniqueId(), vEco.getBalance(player));
            saveAsync();
            return;
        }
        setBalance(player, getBalance(player) - amount);
    }

    public boolean hasBalance(OfflinePlayer player, double amount) {
        if (player == null) return false;
        Economy vEco = getVaultEconomy();
        if (vEco != null) {
            return vEco.has(player, amount);
        }
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
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        return hasBalance(op, amount);
    }

    public void withdraw(UUID uuid, double amount) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        withdrawBalance(op, amount);
    }

    public void deposit(UUID uuid, double amount) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        addBalance(op, amount);
    }

    public double getBalance(UUID uuid) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        return getBalance(op);
    }

    public String format(double amount) {
        Economy vEco = getVaultEconomy();
        if (vEco != null) {
            try {
                return vEco.format(amount);
            } catch (Throwable ignored) {}
        }
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
        saveAll();
    }
}
