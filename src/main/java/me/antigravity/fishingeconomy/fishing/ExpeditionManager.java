package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.*;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ExpeditionManager {

    private final FishingEconomy plugin;
    private final Set<UUID> activeExpeditions = new HashSet<>();

    public ExpeditionManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public boolean startExpedition(Player captain) {
        if (activeExpeditions.contains(captain.getUniqueId())) {
            captain.sendMessage(ChatColor.RED + "✖ Ya tienes una expedición marina en curso.");
            return false;
        }

        double fee = 1000.0;
        if (!plugin.getEconomyManager().has(captain, fee)) {
            captain.sendMessage(ChatColor.RED + "✖ Iniciar una expedición al Océano Abisal cuesta " + plugin.getEconomyManager().format(fee));
            return false;
        }

        plugin.getEconomyManager().withdraw(captain, fee);
        activeExpeditions.add(captain.getUniqueId());

        captain.sendMessage(ChatColor.DARK_AQUA + "⚓ [Expedición Abisal] ¡Zarpan hacia la Fosa de las Marianas!");
        captain.playSound(captain.getLocation(), Sound.EVENT_RAID_HORN, 1f, 0.8f);

        new BukkitRunnable() {
            int duration = 0;

            @Override
            public void run() {
                if (!captain.isOnline()) {
                    activeExpeditions.remove(captain.getUniqueId());
                    this.cancel();
                    return;
                }

                duration += 5;

                // Weather and waves effects
                Location loc = captain.getLocation();
                loc.getWorld().spawnParticle(Particle.WATER_SPLASH, loc.clone().add(0, 1, 0), 40, 2, 1, 2, 0.2);

                if (duration == 15) {
                    captain.sendMessage(ChatColor.DARK_PURPLE + "🌊 ¡Las aguas se oscurecen! Un Leviatán del Abismo se aproxima...");
                    captain.playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 0.7f);
                }

                if (duration >= 30) {
                    this.cancel();
                    activeExpeditions.remove(captain.getUniqueId());
                    triggerKrakenEncounter(captain);
                }
            }
        }.runTaskTimer(plugin, 0L, 100L);

        return true;
    }

    private void triggerKrakenEncounter(Player captain) {
        Location loc = captain.getLocation();
        ElderGuardian kraken = (ElderGuardian) loc.getWorld().spawnEntity(loc, EntityType.ELDER_GUARDIAN);
        kraken.setCustomName(ChatColor.DARK_RED + "✦ " + ChatColor.GOLD + ChatColor.BOLD + "Kraken de las Profundidades" + ChatColor.DARK_RED + " ✦");
        kraken.setCustomNameVisible(true);

        captain.sendMessage(ChatColor.RED + "🚨 ¡EL KRAKEN HA EMERGIDO! ¡Derrótalo para obtener botín Mítico!");
        captain.playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1f, 0.5f);
        loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 3);
    }
}
