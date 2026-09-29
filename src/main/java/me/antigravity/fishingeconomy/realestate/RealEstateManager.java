package me.antigravity.fishingeconomy.realestate;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class RealEstateManager {

    public static class Property {
        public final String id;
        public final String name;
        public final double purchasePrice;
        public final double passiveIncome;
        public UUID owner;

        public Property(String id, String name, double purchasePrice, double passiveIncome) {
            this.id = id;
            this.name = name;
            this.purchasePrice = purchasePrice;
            this.passiveIncome = passiveIncome;
            this.owner = null;
        }
    }

    private final FishingEconomy plugin;
    private final Map<String, Property> properties = new HashMap<>();

    public RealEstateManager(FishingEconomy plugin) {
        this.plugin = plugin;
        registerDefaultProperties();
        startRentDistributionTask();
    }

    private void registerDefaultProperties() {
        properties.put("shop_stall_1", new Property("shop_stall_1", "Puesto en Muelle de Pescadores #1", 25000.0, 150.0));
        properties.put("shop_stall_2", new Property("shop_stall_2", "Puesto en Muelle de Pescadores #2", 25000.0, 150.0));
        properties.put("market_stall_a", new Property("market_stall_a", "Local Comercial en Gran Bazar", 80000.0, 500.0));
        properties.put("bank_vault_suite", new Property("bank_vault_suite", "Bóveda VIP en Distrito Financiero", 250000.0, 1800.0));
    }

    public Property getProperty(String id) {
        return properties.get(id);
    }

    public boolean buyProperty(Player player, String id) {
        Property prop = getProperty(id);
        if (prop == null) {
            player.sendMessage(ChatColor.RED + "✖ Propiedad no encontrada.");
            return false;
        }

        if (prop.owner != null) {
            player.sendMessage(ChatColor.RED + "✖ Esta propiedad ya tiene un propietario.");
            return false;
        }

        if (!plugin.getEconomyManager().has(player, prop.purchasePrice)) {
            player.sendMessage(ChatColor.RED + "✖ Fondos insuficientes (" + plugin.getEconomyManager().format(prop.purchasePrice) + ")");
            return false;
        }

        plugin.getEconomyManager().withdraw(player, prop.purchasePrice);
        prop.owner = player.getUniqueId();

        Bukkit.broadcastMessage(ChatColor.GOLD + "🏛 [Bienes Raíces] ¡" + ChatColor.YELLOW + player.getName() +
                ChatColor.GOLD + " ha adquirido " + ChatColor.AQUA + prop.name + ChatColor.GOLD + "!");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        return true;
    }

    private void startRentDistributionTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Property p : properties.values()) {
                if (p.owner != null) {
                    Player owner = Bukkit.getPlayer(p.owner);
                    if (owner != null && owner.isOnline()) {
                        plugin.getEconomyManager().deposit(owner, p.passiveIncome);
                        owner.sendMessage(ChatColor.DARK_AQUA + "🏬 [Bienes Raíces] Has recibido " +
                                ChatColor.GREEN + plugin.getEconomyManager().format(p.passiveIncome) +
                                ChatColor.DARK_AQUA + " por el alquiler de " + p.name);
                    }
                }
            }
        }, 1200L, 1200L); // Every 60 seconds
    }

    public Collection<Property> getAllProperties() {
        return properties.values();
    }
}
