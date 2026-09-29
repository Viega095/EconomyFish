package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TournamentManager {

    public enum TournamentType {
        HEAVIEST_FISH("El Pez Más Pesado"),
        MOST_CATCHES("Maratón de Capturas"),
        MYTHIC_HUNT("Cacería de Criaturas Míticas");

        public final String displayName;

        TournamentType(String displayName) {
            this.displayName = displayName;
        }
    }

    private final FishingEconomy plugin;
    private boolean active = false;
    private TournamentType currentType;
    private int timeRemainingSeconds = 0;
    private final Map<UUID, Double> scores = new ConcurrentHashMap<>();

    public TournamentManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public boolean startTournament(TournamentType type, int durationMinutes) {
        if (active) return false;

        this.active = true;
        this.currentType = type;
        this.timeRemainingSeconds = durationMinutes * 60;
        this.scores.clear();

        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "🏆 ¡HA COMENZADO UN TORNEO DE PESCA! 🏆");
        Bukkit.broadcastMessage(ChatColor.AQUA + "Modalidad: " + ChatColor.WHITE + type.displayName);
        Bukkit.broadcastMessage(ChatColor.AQUA + "Duración: " + ChatColor.GREEN + durationMinutes + " minutos");
        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.EVENT_RAID_HORN, 1f, 1f);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!active) {
                    cancel();
                    return;
                }

                timeRemainingSeconds--;

                if (timeRemainingSeconds % 60 == 0 && timeRemainingSeconds > 0) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "⏳ Quedan " + (timeRemainingSeconds / 60) +
                            " minutos en el Torneo de Pesca (" + currentType.displayName + ")!");
                }

                if (timeRemainingSeconds <= 0) {
                    endTournament();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);

        return true;
    }

    public void recordCatch(Player player, double scoreValue) {
        if (!active) return;
        scores.put(player.getUniqueId(), scores.getOrDefault(player.getUniqueId(), 0.0) + scoreValue);
        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(ChatColor.GOLD + "🎣 [Torneo] +" + String.format("%.1f", scoreValue) + " pts | Puntos Totales: " +
                String.format("%.1f", scores.get(player.getUniqueId()))));
    }

    public void endTournament() {
        if (!active) return;
        active = false;

        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");
        Bukkit.broadcastMessage(ChatColor.GREEN + "🏁 ¡EL TORNEO DE PESCA HA FINALIZADO! 🏁");

        if (scores.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "No hubo participantes con capturas registradas.");
            Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");
            return;
        }

        List<Map.Entry<UUID, Double>> sorted = new ArrayList<>(scores.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        double[] prizes = {50000.0, 25000.0, 10000.0};

        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            Map.Entry<UUID, Double> entry = sorted.get(i);
            Player winner = Bukkit.getPlayer(entry.getKey());
            String name = (winner != null) ? winner.getName() : "Desconectado";
            double prize = prizes[i];

            Bukkit.broadcastMessage(ChatColor.YELLOW + "#" + (i + 1) + " " + ChatColor.WHITE + name +
                    " §8- §e" + String.format("%.1f", entry.getValue()) + " pts §8| §aPremio: " +
                    plugin.getEconomyManager().format(prize));

            if (winner != null && winner.isOnline()) {
                plugin.getEconomyManager().deposit(winner, prize);
                winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
            }
        }
        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");
    }

    public boolean isActive() {
        return active;
    }

    public TournamentType getCurrentType() {
        return currentType;
    }

    public int getTimeRemainingSeconds() {
        return timeRemainingSeconds;
    }

    public Map<UUID, Double> getScores() {
        return scores;
    }
}
