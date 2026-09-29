package me.antigravity.fishingeconomy.bounties;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class BountyListener implements Listener {
    private final FishingEconomy plugin;

    public BountyListener(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null)
            return;

        double bounty = plugin.getBountyManager().getBounty(victim);
        if (bounty > 0) {
            plugin.getEconomyManager().depositPlayer(killer, bounty);
            plugin.getBountyManager().removeBounty(victim);

            String formattedBounty = plugin.getEconomyManager().format(bounty);
            Bukkit.broadcastMessage("§c§lBOUNTY! §e" + killer.getName() + " §7has claimed the bounty of §a"
                    + formattedBounty + " §7on §e" + victim.getName() + "§7!");
        }
    }
}
