package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SubmarineManager {

    private final FishingEconomy plugin;
    private final Set<UUID> activeSubmariners = new HashSet<>();

    public SubmarineManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public boolean deploySubmarine(Player player) {
        double cost = 20000.0;
        if (!plugin.getEconomyManager().has(player, cost)) {
            player.sendMessage(ChatColor.RED + "✖ El alquiler del submarino abisal cuesta " + plugin.getEconomyManager().format(cost));
            return false;
        }

        plugin.getEconomyManager().withdraw(player, cost);
        activeSubmariners.add(player.getUniqueId());

        Location loc = player.getLocation();
        Boat sub = (Boat) loc.getWorld().spawnEntity(loc, EntityType.BOAT);
        sub.setCustomName(ChatColor.YELLOW + "✦ Submarino Nautilus-X ✦");
        sub.setCustomNameVisible(true);
        sub.addPassenger(player);

        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 6000, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 6000, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 6000, 1));

        player.sendMessage(ChatColor.DARK_AQUA + "🌊 [Sistemas Submarinos] ¡Submarino desplegado! Presión y oxígeno estabilizados por 5 minutos.");
        player.playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 1f, 0.7f);
        loc.getWorld().spawnParticle(Particle.WATER_BUBBLE, loc, 50, 1, 1, 1, 0.1);
        return true;
    }
}
