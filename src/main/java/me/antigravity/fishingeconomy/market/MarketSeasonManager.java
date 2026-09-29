package me.antigravity.fishingeconomy.market;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.World;

public class MarketSeasonManager {

    private final FishingEconomy plugin;

    public MarketSeasonManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public double getWeatherPriceMultiplier(World world) {
        if (world.isThundering()) {
            return 1.8; // +80% during thunderstorms!
        } else if (world.hasStorm()) {
            return 1.35; // +35% during rain
        } else if (world.getTime() > 13000 && world.getTime() < 23000) {
            return 1.20; // +20% during night
        }
        return 1.0;
    }

    public String getCurrentWeatherStatus(World world) {
        if (world.isThundering()) return "⚡ Tormenta Eléctrica (+80% Valor)";
        if (world.hasStorm()) return "🌧 Lluvia Intensa (+35% Valor)";
        if (world.getTime() > 13000 && world.getTime() < 23000) return "🌙 Noche Serena (+20% Valor)";
        return "☀️ Soleado (Precio Normal)";
    }
}
