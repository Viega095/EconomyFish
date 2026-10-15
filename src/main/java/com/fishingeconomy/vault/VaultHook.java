package com.fishingeconomy.vault;

import com.fishingeconomy.FishingEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

public class VaultHook {

    public static void hook(FishingEconomy plugin) {
        try {
            RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null && rsp.getProvider() != null) {
                // An existing Vault economy provider (e.g. VaultEconomy / Essentials) is present!
                plugin.getEconomyManager().setupVault();
                plugin.getLogger().info("✔ Hooked into existing Vault economy provider: " + rsp.getProvider().getName() + " (Unified server wallet)");
                return;
            }

            // No existing economy provider detected: provide Economy for the server
            plugin.getServer().getServicesManager().register(
                    Economy.class,
                    new EconomyImplementer(plugin),
                    plugin,
                    ServicePriority.Normal);
            plugin.getLogger().info("No external economy provider detected. Registered FishingEconomy as Vault provider!");
        } catch (Throwable t) {
            plugin.getLogger().warning("Could not complete Vault hook: " + t.getMessage());
        }
    }
}
