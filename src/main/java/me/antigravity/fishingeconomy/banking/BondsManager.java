package me.antigravity.fishingeconomy.banking;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BondsManager {

    public enum BondDuration {
        ONE_HOUR("1 Hora", 3600 * 1000L, 0.02),
        ONE_DAY("24 Horas", 86400 * 1000L, 0.06),
        ONE_WEEK("7 Días", 7 * 86400 * 1000L, 0.18);

        public final String label;
        public final long durationMillis;
        public final double yieldRate;

        BondDuration(String label, long durationMillis, double yieldRate) {
            this.label = label;
            this.durationMillis = durationMillis;
            this.yieldRate = yieldRate;
        }
    }

    public static class ActiveBond {
        public final String id;
        public final UUID owner;
        public final double principal;
        public final BondDuration duration;
        public final long purchaseTime;

        public ActiveBond(String id, UUID owner, double principal, BondDuration duration, long purchaseTime) {
            this.id = id;
            this.owner = owner;
            this.principal = principal;
            this.duration = duration;
            this.purchaseTime = purchaseTime;
        }

        public boolean isMatured() {
            return System.currentTimeMillis() >= (purchaseTime + duration.durationMillis);
        }

        public double getPayout() {
            return principal * (1.0 + duration.yieldRate);
        }
    }

    private final FishingEconomy plugin;
    private final Map<UUID, List<ActiveBond>> playerBonds = new HashMap<>();
    private final Map<UUID, Integer> creditScores = new HashMap<>();
    private File bondsFile;
    private FileConfiguration bondsConfig;

    public BondsManager(FishingEconomy plugin) {
        this.plugin = plugin;
        loadData();
    }

    private void loadData() {
        bondsFile = new File(plugin.getDataFolder(), "bonds_data.yml");
        if (!bondsFile.exists()) {
            try {
                bondsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bondsConfig = YamlConfiguration.loadConfiguration(bondsFile);

        if (bondsConfig.contains("scores")) {
            for (String key : bondsConfig.getConfigurationSection("scores").getKeys(false)) {
                creditScores.put(UUID.fromString(key), bondsConfig.getInt("scores." + key, 600));
            }
        }
    }

    public void saveData() {
        for (Map.Entry<UUID, Integer> entry : creditScores.entrySet()) {
            bondsConfig.set("scores." + entry.getKey(), entry.getValue());
        }
        try {
            bondsConfig.save(bondsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCreditScore(UUID uuid) {
        return creditScores.getOrDefault(uuid, 650); // Default 650
    }

    public void adjustCreditScore(UUID uuid, int delta) {
        int current = getCreditScore(uuid);
        int newScore = Math.max(300, Math.min(850, current + delta));
        creditScores.put(uuid, newScore);
        saveData();
    }

    public boolean purchaseBond(Player player, BondDuration duration, double amount) {
        if (!plugin.getEconomyManager().has(player, amount)) {
            player.sendMessage(ChatColor.RED + "✖ Fondos insuficientes para adquirir el bono (" +
                    plugin.getEconomyManager().format(amount) + ")");
            return false;
        }

        plugin.getEconomyManager().withdraw(player, amount);
        ActiveBond bond = new ActiveBond(UUID.randomUUID().toString(), player.getUniqueId(), amount, duration, System.currentTimeMillis());
        playerBonds.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(bond);

        adjustCreditScore(player.getUniqueId(), +5);

        player.sendMessage(ChatColor.GOLD + "📜 [Bonos del Tesoro] ¡Has adquirido un bono de " +
                plugin.getEconomyManager().format(amount) + " a " + duration.label + " (+ " + (duration.yieldRate * 100) + "% retorno)!");
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
        return true;
    }

    public void claimMaturedBonds(Player player) {
        List<ActiveBond> bonds = playerBonds.get(player.getUniqueId());
        if (bonds == null || bonds.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No tienes bonos activos.");
            return;
        }

        double totalPayout = 0;
        Iterator<ActiveBond> it = bonds.iterator();
        while (it.hasNext()) {
            ActiveBond bond = it.next();
            if (bond.isMatured()) {
                totalPayout += bond.getPayout();
                it.remove();
            }
        }

        if (totalPayout > 0) {
            plugin.getEconomyManager().deposit(player, totalPayout);
            adjustCreditScore(player.getUniqueId(), +15);
            player.sendMessage(ChatColor.GREEN + "💰 ¡Has liquidado bonos vencidos por un total de " +
                    plugin.getEconomyManager().format(totalPayout) + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.4f);
        } else {
            player.sendMessage(ChatColor.YELLOW + "Tus bonos aún están en período de maduración.");
        }
    }
}
