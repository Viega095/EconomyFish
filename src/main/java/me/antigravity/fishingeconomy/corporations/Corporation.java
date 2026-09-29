package me.antigravity.fishingeconomy.corporations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Corporation {

    private final String id;
    private final String name;
    private final String ticker;
    private final UUID founder;
    private int totalShares;
    private double sharePrice;
    private double corporateVault;
    private final Map<UUID, Integer> shareholders = new HashMap<>();

    public Corporation(String id, String name, String ticker, UUID founder, int totalShares, double sharePrice) {
        this.id = id;
        this.name = name;
        this.ticker = ticker.toUpperCase();
        this.founder = founder;
        this.totalShares = totalShares;
        this.sharePrice = sharePrice;
        this.corporateVault = 0;
        this.shareholders.put(founder, totalShares);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public UUID getFounder() {
        return founder;
    }

    public int getTotalShares() {
        return totalShares;
    }

    public double getSharePrice() {
        return sharePrice;
    }

    public void setSharePrice(double sharePrice) {
        this.sharePrice = Math.max(1.0, sharePrice);
    }

    public double getCorporateVault() {
        return corporateVault;
    }

    public void depositVault(double amount) {
        this.corporateVault += amount;
    }

    public boolean withdrawVault(double amount) {
        if (corporateVault >= amount) {
            corporateVault -= amount;
            return true;
        }
        return false;
    }

    public Map<UUID, Integer> getShareholders() {
        return shareholders;
    }

    public int getShares(UUID uuid) {
        return shareholders.getOrDefault(uuid, 0);
    }

    public void transferShares(UUID from, UUID to, int amount) {
        int fromShares = getShares(from);
        if (fromShares >= amount) {
            shareholders.put(from, fromShares - amount);
            shareholders.put(to, getShares(to) + amount);
        }
    }
}
