package me.antigravity.fishingeconomy.config;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {
    private final FishingEconomy plugin;
    private FileConfiguration fishConfig;
    private File fishFile;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public ConfigManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        // Load config.yml
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        // Load fish.yml
        fishFile = new File(plugin.getDataFolder(), "fish.yml");
        if (!fishFile.exists()) {
            plugin.saveResource("fish.yml", false);
        }
        fishConfig = YamlConfiguration.loadConfiguration(fishFile);

        // Load messages.yml
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getFishConfig() {
        if (fishConfig == null) {
            loadConfigs();
        }
        return fishConfig != null ? fishConfig : new YamlConfiguration();
    }

    public FileConfiguration getMessagesConfig() {
        if (messagesConfig == null) {
            loadConfigs();
        }
        return messagesConfig != null ? messagesConfig : new YamlConfiguration();
    }

    public static String colorize(String text) {
        if (text == null) return "";
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getMessage(String path) {
        if (messagesConfig == null) {
            loadConfigs();
        }
        if (messagesConfig == null) {
            return colorize("&7" + path);
        }
        String msg = messagesConfig.getString(path);
        if (msg == null) {
            return colorize("&7" + path);
        }
        return colorize(msg);
    }
}
