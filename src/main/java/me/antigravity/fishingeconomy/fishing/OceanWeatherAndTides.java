package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class OceanWeatherAndTides {

    public enum TideState {
        MIDNIGHT_TIDE("🌙 Marea Viva de Medianoche", BarColor.PURPLE, 1.0, 1.3, "§7Peces con +30% más de peso y récord."),
        BIOLUMINESCENT_BLOOM("🌟 Florecimiento Bioluminiscente", BarColor.BLUE, 1.0, 2.0, "§7Doble XP pesquera y partículas marinas."),
        KRAKEN_STORM("⚡ Tempestad del Kraken", BarColor.RED, 1.5, 1.5, "§7Probabilidad x2 de capturas Míticas y Jefes."),
        SOLAR_CALM("☀️ Calma Solar de Alta Mar", BarColor.YELLOW, 1.35, 1.0, "§7+35% de valor en todas las ventas del mercado.");

        private final String name;
        private final BarColor barColor;
        private final double priceMultiplier;
        private final double xpOrWeightMultiplier;
        private final String description;

        TideState(String name, BarColor barColor, double priceMultiplier, double xpOrWeightMultiplier, String description) {
            this.name = name;
            this.barColor = barColor;
            this.priceMultiplier = priceMultiplier;
            this.xpOrWeightMultiplier = xpOrWeightMultiplier;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public BarColor getBarColor() {
            return barColor;
        }

        public double getPriceMultiplier() {
            return priceMultiplier;
        }

        public double getXpOrWeightMultiplier() {
            return xpOrWeightMultiplier;
        }

        public String getDescription() {
            return description;
        }
    }

    private final FishingEconomy plugin;
    private TideState currentTide = TideState.SOLAR_CALM;
    private BossBar tideBar;
    private int secondsRemaining = 600;

    public OceanWeatherAndTides(FishingEconomy plugin) {
        this.plugin = plugin;
        this.tideBar = Bukkit.createBossBar(currentTide.getName(), currentTide.getBarColor(), BarStyle.SOLID);
        this.tideBar.setVisible(true);
        startTideCycle();
    }

    public TideState getCurrentTide() {
        return currentTide;
    }

    public void showTideStatus(Player player) {
        player.sendMessage("§b🌊 === " + currentTide.getName() + " §b===");
        player.sendMessage("§7Efecto activo: " + currentTide.getDescription());
        player.sendMessage("§7Multiplicador de venta: §a+" + String.format("%.0f%%", (currentTide.getPriceMultiplier() - 1.0) * 100));
        player.sendMessage("§7Tiempo restante: §e" + (secondsRemaining / 60) + "m " + (secondsRemaining % 60) + "s");
    }

    private void startTideCycle() {
        new BukkitRunnable() {
            @Override
            public void run() {
                secondsRemaining--;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    tideBar.addPlayer(p);
                }

                tideBar.setTitle(currentTide.getName() + " §7(" + (secondsRemaining / 60) + "m " + (secondsRemaining % 60) + "s)");
                tideBar.setProgress(Math.max(0.0, Math.min(1.0, (double) secondsRemaining / 600.0)));

                if (secondsRemaining <= 0) {
                    rotateTide();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void rotateTide() {
        TideState[] states = TideState.values();
        int nextIndex = (currentTide.ordinal() + 1) % states.length;
        currentTide = states[nextIndex];
        secondsRemaining = 600;

        tideBar.setColor(currentTide.getBarColor());

        Bukkit.broadcastMessage("§b🌊 =============================================");
        Bukkit.broadcastMessage("§9🌊 ¡EL CLIMA OCEÁNICO Y LA MAREA HAN CAMBIADO!");
        Bukkit.broadcastMessage(currentTide.getName());
        Bukkit.broadcastMessage(currentTide.getDescription());
        Bukkit.broadcastMessage("§b🌊 =============================================");

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ITEM_BUCKET_FILL, 1f, 0.8f);
        }
    }
}
