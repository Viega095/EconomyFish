package me.antigravity.fishingeconomy.jobs;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JobsManager {
    private final FishingEconomy plugin;
    private final Map<UUID, JobType> playerJobs = new HashMap<>();
    private final Map<UUID, Integer> playerJobLevels = new HashMap<>();
    private final Map<UUID, Double> playerJobXp = new HashMap<>();

    public enum JobType {
        NONE, MINER, FARMER, HUNTER, FISHERMAN, WOODCUTTER
    }

    public JobsManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public JobType getJob(Player player) {
        return playerJobs.getOrDefault(player.getUniqueId(), JobType.NONE);
    }

    public void setJob(Player player, JobType job) {
        playerJobs.put(player.getUniqueId(), job);
        playerJobLevels.putIfAbsent(player.getUniqueId(), 1);
        playerJobXp.putIfAbsent(player.getUniqueId(), 0.0);
    }

    public int getLevel(Player player) {
        return playerJobLevels.getOrDefault(player.getUniqueId(), 1);
    }

    public void addXp(Player player, double amount) {
        UUID uuid = player.getUniqueId();
        double currentXp = playerJobXp.getOrDefault(uuid, 0.0);
        double newXp = currentXp + amount;
        int level = getLevel(player);

        // Simple leveling: Level * 100 XP required
        double required = level * 100;
        if (newXp >= required) {
            newXp -= required;
            playerJobLevels.put(uuid, level + 1);
            player.sendMessage("§aLevel Up! You are now level " + (level + 1) + " " + getJob(player).name());
            plugin.getEconomyManager().depositPlayer(player, (level + 1) * 100);
        }
        playerJobXp.put(uuid, newXp);
    }

    public double getMultiplier(Player player) {
        return 1.0 + (getLevel(player) * 0.1); // 10% increase per level
    }
}
