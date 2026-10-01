package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalFishingTournamentEngine {

    public enum DerbyType {
        WEIGHT_CHAMPIONSHIP("🏆 Campeonato del Pez Más Pesado", "Gana quien capture el pez de mayor peso en kg."),
        QUANTITY_BLITZ("⚡ Ráfaga de Capturas", "Gana quien capture la mayor cantidad de peces."),
        MYTHIC_HUNT("🌟 Cacería Mítica", "Gana quien capture los peces de mayor rareza.");

        private final String name;
        private final String desc;

        DerbyType(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() { return name; }
        public String getDesc() { return desc; }
    }

    public static class ParticipantRecord {
        public double totalWeight = 0.0;
        public int totalCatches = 0;
        public double bestWeight = 0.0;
        public String bestFishName = "Ninguno";
    }

    private final FishingEconomy plugin;
    private final Map<UUID, ParticipantRecord> scoreMap = new ConcurrentHashMap<>();
    private boolean active = false;
    private DerbyType currentType = DerbyType.WEIGHT_CHAMPIONSHIP;
    private int remainingSeconds = 0;
    private BossBar tournamentBar;
    private BukkitTask task;

    public GlobalFishingTournamentEngine(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public boolean isActive() {
        return active;
    }

    public void startTournament(DerbyType type, int durationMinutes) {
        if (active) {
            stopTournament(false);
        }

        this.currentType = type;
        this.active = true;
        this.remainingSeconds = durationMinutes * 60;
        this.scoreMap.clear();

        this.tournamentBar = Bukkit.createBossBar(
                ChatColor.GOLD + "🏆 TORNEO DE PESCA: " + ChatColor.YELLOW + type.getName() + ChatColor.GRAY + " (Restante: " + formatTime(remainingSeconds) + ")",
                BarColor.BLUE,
                BarStyle.SOLID
        );

        for (Player p : Bukkit.getOnlinePlayers()) {
            tournamentBar.addPlayer(p);
            p.sendTitle(ChatColor.GOLD + "🎣 ¡TORNEO INICIADO!", ChatColor.YELLOW + type.getName(), 10, 60, 15);
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
            p.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            p.sendMessage(ChatColor.YELLOW + "🏆 ¡Ha comenzado el " + type.getName() + "!");
            p.sendMessage(ChatColor.GRAY + "ℹ " + type.getDesc());
            p.sendMessage(ChatColor.AQUA + "⏱ Duración: " + durationMinutes + " minutos. ¡Lanza tu caña ahora!");
            p.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }

        this.task = new BukkitRunnable() {
            int totalSeconds = durationMinutes * 60;

            @Override
            public void run() {
                if (!active) {
                    cancel();
                    return;
                }

                remainingSeconds--;
                double progress = Math.max(0.0, Math.min(1.0, (double) remainingSeconds / totalSeconds));
                tournamentBar.setProgress(progress);
                tournamentBar.setTitle(ChatColor.GOLD + "🏆 " + type.getName() + ChatColor.GRAY + " | Restante: " + ChatColor.AQUA + formatTime(remainingSeconds));

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!tournamentBar.getPlayers().contains(p)) {
                        tournamentBar.addPlayer(p);
                    }
                }

                if (remainingSeconds <= 0) {
                    stopTournament(true);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void recordCatch(Player player, FishManager.CustomFish fish) {
        if (!active || player == null || fish == null) return;

        ParticipantRecord record = scoreMap.computeIfAbsent(player.getUniqueId(), k -> new ParticipantRecord());
        record.totalCatches++;
        
        // Calculate dynamic weight based on price/rarity
        double weight = (fish.price * 0.12) + (Math.random() * 4.5);
        record.totalWeight += weight;
        if (weight > record.bestWeight) {
            record.bestWeight = weight;
            record.bestFishName = fish.name;
        }

        player.sendMessage(ChatColor.GOLD + "🎣 [Torneo] " + ChatColor.AQUA + "+1 Registro: " + ChatColor.WHITE + fish.name + " " + String.format("(%.2f kg)", weight));
    }

    public void stopTournament(boolean announceWinners) {
        this.active = false;
        if (task != null) {
            task.cancel();
        }
        if (tournamentBar != null) {
            tournamentBar.removeAll();
        }

        if (announceWinners) {
            List<Map.Entry<UUID, ParticipantRecord>> sorted = new ArrayList<>(scoreMap.entrySet());
            sorted.sort((a, b) -> {
                if (currentType == DerbyType.QUANTITY_BLITZ) {
                    return Integer.compare(b.getValue().totalCatches, a.getValue().totalCatches);
                } else {
                    return Double.compare(b.getValue().bestWeight, a.getValue().bestWeight);
                }
            });

            Bukkit.broadcastMessage(ChatColor.GOLD + "══════════════════════════════════════════════════");
            Bukkit.broadcastMessage(ChatColor.GOLD + "🏆      " + ChatColor.YELLOW + "¡RESULTADOS FINALES DEL TORNEO DE PESCA!" + ChatColor.GOLD + "      🏆");
            Bukkit.broadcastMessage(ChatColor.GOLD + "══════════════════════════════════════════════════");

            if (sorted.isEmpty()) {
                Bukkit.broadcastMessage(ChatColor.GRAY + "Nadie participó en esta ronda. ¡Mejor suerte la próxima vez!");
            } else {
                for (int i = 0; i < Math.min(3, sorted.size()); i++) {
                    Map.Entry<UUID, ParticipantRecord> entry = sorted.get(i);
                    Player winner = Bukkit.getPlayer(entry.getKey());
                    String name = winner != null ? winner.getName() : "Pescador Desconocido";
                    ParticipantRecord rec = entry.getValue();

                    double prize = (3 - i) * 1500.0;
                    if (winner != null && winner.isOnline()) {
                        plugin.getEconomyManager().depositPlayer(winner, prize);
                        winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                    }

                    String medal = i == 0 ? "🥇 1er Lugar: " : (i == 1 ? "🥈 2do Lugar: " : "🥉 3er Lugar: ");
                    Bukkit.broadcastMessage(ChatColor.YELLOW + medal + ChatColor.WHITE + name + 
                            ChatColor.GRAY + " (" + String.format("%.2f kg", rec.bestWeight) + " - " + rec.bestFishName + 
                            ChatColor.GOLD + " | Premio: +" + plugin.getEconomyManager().format(prize) + ")");
                }
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + "══════════════════════════════════════════════════");
        }
    }

    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}
