package me.antigravity.fishingeconomy.corporations;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class CorporationManager {

    private final FishingEconomy plugin;
    private final Map<String, Corporation> corporationsByTicker = new HashMap<>();

    public CorporationManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public Corporation getCorporation(String ticker) {
        return corporationsByTicker.get(ticker.toUpperCase());
    }

    public boolean createCorporation(Player founder, String name, String ticker, double initialSharePrice) {
        ticker = ticker.toUpperCase();
        if (corporationsByTicker.containsKey(ticker)) {
            founder.sendMessage(ChatColor.RED + "✖ Ya existe una corporación con el ticker: " + ticker);
            return false;
        }

        double creationCost = 5000.0;
        if (!plugin.getEconomyManager().has(founder, creationCost)) {
            founder.sendMessage(ChatColor.RED + "✖ Crear una empresa requiere " + plugin.getEconomyManager().format(creationCost));
            return false;
        }

        plugin.getEconomyManager().withdraw(founder, creationCost);
        Corporation corp = new Corporation(UUID.randomUUID().toString(), name, ticker, founder.getUniqueId(), 1000, initialSharePrice);
        corporationsByTicker.put(ticker, corp);

        Bukkit.broadcastMessage(ChatColor.GOLD + "🏛 [Bolsa IPO] ¡La empresa " + ChatColor.YELLOW + ChatColor.BOLD +
                name + " (" + ticker + ")" + ChatColor.GOLD + " ha salido a bolsa!");
        return true;
    }

    public boolean buyShares(Player investor, String ticker, int amount) {
        Corporation corp = getCorporation(ticker);
        if (corp == null) {
            investor.sendMessage(ChatColor.RED + "✖ Empresa no encontrada.");
            return false;
        }

        UUID founderUuid = corp.getFounder();
        int availableShares = corp.getShares(founderUuid);
        if (availableShares < amount) {
            investor.sendMessage(ChatColor.RED + "✖ Solo hay " + availableShares + " acciones disponibles para compra directa.");
            return false;
        }

        double cost = amount * corp.getSharePrice();
        if (!plugin.getEconomyManager().has(investor, cost)) {
            investor.sendMessage(ChatColor.RED + "✖ Fondos insuficientes (" + plugin.getEconomyManager().format(cost) + ")");
            return false;
        }

        plugin.getEconomyManager().withdraw(investor, cost);
        corp.depositVault(cost * 0.9); // 90% goes to corporate vault
        corp.transferShares(founderUuid, investor.getUniqueId(), amount);

        // Share price increases slightly with demand
        corp.setSharePrice(corp.getSharePrice() * (1.0 + (amount * 0.0002)));

        investor.sendMessage(ChatColor.GREEN + "✓ ¡Has comprado " + amount + " acciones de " + corp.getName() + " por " +
                plugin.getEconomyManager().format(cost) + "!");
        return true;
    }

    public boolean payDividends(Player founder, String ticker, double totalPayout) {
        Corporation corp = getCorporation(ticker);
        if (corp == null || !corp.getFounder().equals(founder.getUniqueId())) {
            founder.sendMessage(ChatColor.RED + "✖ Solo el fundador puede emitir dividendos.");
            return false;
        }

        if (!corp.withdrawVault(totalPayout)) {
            founder.sendMessage(ChatColor.RED + "✖ La bóveda corporativa no tiene fondos suficientes.");
            return false;
        }

        for (Map.Entry<UUID, Integer> entry : corp.getShareholders().entrySet()) {
            double fraction = (double) entry.getValue() / corp.getTotalShares();
            double sharePayout = totalPayout * fraction;
            Player shareholder = Bukkit.getPlayer(entry.getKey());
            if (shareholder != null && shareholder.isOnline()) {
                plugin.getEconomyManager().deposit(shareholder, sharePayout);
                shareholder.sendMessage(ChatColor.GOLD + "💰 [Dividendos] Has recibido " +
                        plugin.getEconomyManager().format(sharePayout) + " de " + corp.getName());
            }
        }
        return true;
    }

    public Collection<Corporation> getAllCorporations() {
        return corporationsByTicker.values();
    }
}
