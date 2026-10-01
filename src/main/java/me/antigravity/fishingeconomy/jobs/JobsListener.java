package me.antigravity.fishingeconomy.jobs;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class JobsListener implements Listener {
    private final FishingEconomy plugin;

    public JobsListener(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (plugin.getJobsManager().getJob(player) == JobsManager.JobType.MINER) {
            // Check if ore
            Material type = event.getBlock().getType();
            if (type.name().contains("ORE") || type.name().contains("DEEPSLATE")) {
                plugin.getJobsManager().addXp(player, 10);
                // Money is handled by selling, but maybe small reward?
                plugin.getEconomyManager().depositPlayer(player, 0.5);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player player = event.getPlayer();
            if (plugin.getJobsManager().getJob(player) == JobsManager.JobType.FISHERMAN) {
                plugin.getJobsManager().addXp(player, 15);
                plugin.getEconomyManager().depositPlayer(player, 1.0);
            }
        }
    }

    // Add other job events (Hunter, Farmer)
}
