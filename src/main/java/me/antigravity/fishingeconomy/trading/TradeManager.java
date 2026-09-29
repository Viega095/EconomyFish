package me.antigravity.fishingeconomy.trading;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeManager {
    private final FishingEconomy plugin;
    private final Map<UUID, UUID> activeRequests = new HashMap<>(); // Sender -> Target
    private final Map<UUID, TradeSession> activeTrades = new HashMap<>(); // Player -> Session

    public TradeManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void sendRequest(Player sender, Player target) {
        activeRequests.put(sender.getUniqueId(), target.getUniqueId());
    }

    public boolean hasRequest(Player sender, Player target) {
        return activeRequests.containsKey(sender.getUniqueId())
                && activeRequests.get(sender.getUniqueId()).equals(target.getUniqueId());
    }

    public void removeRequest(Player sender) {
        activeRequests.remove(sender.getUniqueId());
    }

    public void startTrade(Player p1, Player p2) {
        TradeSession session = new TradeSession(plugin, p1, p2);
        activeTrades.put(p1.getUniqueId(), session);
        activeTrades.put(p2.getUniqueId(), session);
        session.open();
    }

    public TradeSession getTrade(Player player) {
        return activeTrades.get(player.getUniqueId());
    }

    public void endTrade(TradeSession session) {
        activeTrades.remove(session.getP1().getUniqueId());
        activeTrades.remove(session.getP2().getUniqueId());
    }
}
