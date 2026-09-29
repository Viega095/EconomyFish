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
        return fishConfig;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public static String colorize(String text) {
        if (text == null) return "";
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getMessage(String path) {
        String msg = messagesConfig.getString(path);
        if (msg == null)
            return "Message not found: " + path;
        return colorize(msg);
    }
}
