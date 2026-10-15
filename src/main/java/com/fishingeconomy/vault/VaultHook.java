package com.fishingeconomy.vault;

import com.fishingeconomy.FishingEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;

public class VaultHook {

    public static void hook(FishingEconomy plugin) {
        plugin.getServer().getServicesManager().register(
                Economy.class,
                new EconomyImplementer(plugin),
                plugin,
                ServicePriority.Highest);
        plugin.getLogger().info("Vault economy registered!");
    }
}
