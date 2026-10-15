package com.fishingeconomy.fishing;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ExoticFishingEventManager {

    public enum ExoticEventType {
        NONE("Ninguno", BarColor.WHITE),
        METEOR_FISH_RAIN("☄️ Lluvia de Peces Meteoríticos", BarColor.PINK),
        GHOST_LEVIATHAN_TIDE("👻 Marea del Leviatán Fantasma", BarColor.BLUE);

        public final String displayName;
        public final BarColor barColor;

        ExoticEventType(String displayName, BarColor barColor) {
            this.displayName = displayName;
            this.barColor = barColor;
        }
    }

    private final FishingEconomy plugin;
    private ExoticEventType currentEvent = ExoticEventType.NONE;
    private BossBar eventBar;
    private int secondsRemaining = 0;
    private BukkitTask eventTask;
    private BukkitTask particleTask;

    public ExoticFishingEventManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public boolean isMeteorFishRainActive() {
        return currentEvent == ExoticEventType.METEOR_FISH_RAIN;
    }

    public boolean isGhostLeviathanTideActive() {
        return currentEvent == ExoticEventType.GHOST_LEVIATHAN_TIDE;
    }

    public ExoticEventType getCurrentEvent() {
        return currentEvent;
    }

    public int getSecondsRemaining() {
        return secondsRemaining;
    }

    public void startMeteorFishRain() {
        startEvent(ExoticEventType.METEOR_FISH_RAIN, 300);

        Bukkit.broadcastMessage("§d☄️ ==================================================");
        Bukkit.broadcastMessage("§d☄️ ¡HA COMENZADO UNA LLUVIA DE PECES METEORÍTICOS!");
        Bukkit.broadcastMessage("§7Polvo cósmico llueve sobre los océanos del mundo.");
        Bukkit.broadcastMessage("§e✦ ¡Probabilidad de capturas MÍTICAS triplicada por 5 minutos!");
        Bukkit.broadcastMessage("§d☄️ ==================================================");

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1.2f);
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1.5f);
        }
    }

    public void startGhostLeviathanTide() {
        startEvent(ExoticEventType.GHOST_LEVIATHAN_TIDE, 300);

        Bukkit.broadcastMessage("§3👻 ==================================================");
        Bukkit.broadcastMessage("§3👻 ¡UNA MAREA DEL LEVIATÁN FANTASMA SE HA LEVANTADO!");
        Bukkit.broadcastMessage("§7Nieblas espectrales cubren las aguas marinas.");
        Bukkit.broadcastMessage("§b✦ ¡Valor de venta en mercado multiplicado x2.5 por 5 minutos!");
        Bukkit.broadcastMessage("§3👻 ==================================================");

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 1f, 0.7f);
            p.playSound(p.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.6f, 1.4f);
        }
    }

    private void startEvent(ExoticEventType type, int durationSeconds) {
        stopAllEvents();

        this.currentEvent = type;
        this.secondsRemaining = durationSeconds;

        this.eventBar = Bukkit.createBossBar(type.displayName, type.barColor, BarStyle.SOLID);
        this.eventBar.setVisible(true);

        for (Player p : Bukkit.getOnlinePlayers()) {
            eventBar.addPlayer(p);
        }

        this.eventTask = new BukkitRunnable() {
            @Override
            public void run() {
                secondsRemaining--;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    eventBar.addPlayer(p);
                }

                eventBar.setTitle(type.displayName + " §7(" + (secondsRemaining / 60) + "m " + (secondsRemaining % 60) + "s)");
                eventBar.setProgress(Math.max(0.0, Math.min(1.0, (double) secondsRemaining / (double) durationSeconds)));

                if (secondsRemaining <= 0) {
                    stopAllEvents();
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "✦ El evento exótico " + type.displayName + ChatColor.YELLOW + " ha concluido.");
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);

        this.particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentEvent == ExoticEventType.METEOR_FISH_RAIN) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.getWorld().spawnParticle(Particle.END_ROD, p.getLocation().add((Math.random() - 0.5) * 12, 4 + Math.random() * 3, (Math.random() - 0.5) * 12), 4, 0.1, -0.3, 0.1, 0.05);
                        p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation().add((Math.random() - 0.5) * 8, 2 + Math.random() * 2, (Math.random() - 0.5) * 8), 2, 0.05, 0.05, 0.05, 0.02);
                    }
                } else if (currentEvent == ExoticEventType.GHOST_LEVIATHAN_TIDE) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, p.getLocation().add((Math.random() - 0.5) * 10, 0.2, (Math.random() - 0.5) * 10), 3, 0.1, 0.1, 0.1, 0.02);
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 10L);
    }

    public void stopAllEvents() {
        if (eventTask != null) {
            eventTask.cancel();
            eventTask = null;
        }
        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }
        if (eventBar != null) {
            eventBar.removeAll();
            eventBar.setVisible(false);
            eventBar = null;
        }
        currentEvent = ExoticEventType.NONE;
        secondsRemaining = 0;
    }
}
