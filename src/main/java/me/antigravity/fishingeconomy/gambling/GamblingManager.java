package me.antigravity.fishingeconomy.gambling;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamblingManager {
    private final Map<UUID, Double> activeCoinflips = new HashMap<>();

    public GamblingManager(FishingEconomy plugin) {
        // Plugin instance not currently used but kept for constructor compatibility
    }

    public void createCoinflip(Player player, double amount) {
        activeCoinflips.put(player.getUniqueId(), amount);
    }

    public void removeCoinflip(Player player) {
        activeCoinflips.remove(player.getUniqueId());
    }

    public Double getCoinflipAmount(Player player) {
        return activeCoinflips.get(player.getUniqueId());
    }

    public Map<UUID, Double> getActiveCoinflips() {
        return new HashMap<>(activeCoinflips);
    }

    public boolean hasCoinflip(Player player) {
        return activeCoinflips.containsKey(player.getUniqueId());
    }
}
