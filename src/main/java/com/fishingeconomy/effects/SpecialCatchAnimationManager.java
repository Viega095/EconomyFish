package com.fishingeconomy.effects;

import com.fishingeconomy.FishingEconomy;
import com.fishingeconomy.fishing.FishManager;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class SpecialCatchAnimationManager {

    private final FishingEconomy plugin;

    public SpecialCatchAnimationManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    /**
     * Plays a high-impact celebration animation when a player lands a special catch.
     * Features:
     * 1. Spiral helix of particles wrapping around the rod and player.
     * 2. Flying water fountain / rainbow geyser from water to player.
     * 3. Temporary 3D celebratory floating item & hologram beacon above player's rod.
     * 4. Harmonic layered sound chimes.
     * 5. Elemental effects depending on fish rarity (Mythic, Legendary, Exotic).
     */
    public void playSpecialCatchCelebration(Player player, FishManager.CustomFish fish, Location waterSpot) {
        if (player == null || !player.isOnline()) return;

        Location playerEye = player.getEyeLocation();
        World world = player.getWorld();

        // 1. Play musical victory fanfares
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.8f);
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1.6f);

        // 2. Water-to-Rod Geyser: projectile particle arc from water location to player's rod tip
        if (waterSpot != null) {
            spawnWaterGeyserArc(waterSpot, player.getLocation().add(0, 1.2, 0));
        }

        // 3. Rod Helix Particle Halo (Spiraling up the player)
        new BukkitRunnable() {
            int step = 0;
            final double radius = 1.3;
            final int maxSteps = 40; // 2 seconds

            @Override
            public void run() {
                if (!player.isOnline() || step >= maxSteps) {
                    cancel();
                    return;
                }

                Location base = player.getLocation();
                double yOffset = (step * 0.08) % 2.5;
                double angle1 = step * 0.45;
                double angle2 = angle1 + Math.PI;

                // Helix arm 1
                double x1 = radius * Math.cos(angle1);
                double z1 = radius * Math.sin(angle1);
                Location pLoc1 = base.clone().add(x1, yOffset, z1);

                // Helix arm 2
                double x2 = radius * Math.cos(angle2);
                double z2 = radius * Math.sin(angle2);
                Location pLoc2 = base.clone().add(x2, yOffset, z2);

                // Particle customization by rarity
                if ("MYTHIC".equalsIgnoreCase(fish.rarity)) {
                    world.spawnParticle(Particle.TOTEM, pLoc1, 2, 0, 0, 0, 0.02);
                    world.spawnParticle(Particle.DRAGON_BREATH, pLoc2, 2, 0, 0, 0, 0.02);
                } else if ("LEGENDARY".equalsIgnoreCase(fish.rarity)) {
                    world.spawnParticle(Particle.FIREWORKS_SPARK, pLoc1, 2, 0, 0, 0, 0.03);
                    world.spawnParticle(Particle.GLOW, pLoc2, 2, 0, 0, 0, 0.01);
                } else {
                    world.spawnParticle(Particle.WATER_WAKE, pLoc1, 3, 0, 0, 0, 0.02);
                    world.spawnParticle(Particle.VILLAGER_HAPPY, pLoc2, 2, 0, 0, 0, 0.01);
                }

                if (step % 8 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 1.2f + (step * 0.02f));
                }

                step++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // 4. Floating Hologram Trophy above the rod for 3 seconds
        spawnTemporaryTrophyHologram(player, fish);
    }

    private void spawnWaterGeyserArc(Location start, Location target) {
        World world = start.getWorld();
        if (world == null) return;

        new BukkitRunnable() {
            int currentStep = 0;
            final int totalSteps = 15;
            final Vector diff = target.toVector().subtract(start.toVector());

            @Override
            public void run() {
                if (currentStep > totalSteps) {
                    cancel();
                    return;
                }

                double fraction = (double) currentStep / totalSteps;
                double arcHeight = Math.sin(fraction * Math.PI) * 3.5; // Curved arc height

                Location point = start.clone().add(diff.clone().multiply(fraction)).add(0, arcHeight, 0);
                world.spawnParticle(Particle.WATER_SPLASH, point, 8, 0.1, 0.1, 0.1, 0.05);
                world.spawnParticle(Particle.BUBBLE_POP, point, 4, 0.05, 0.05, 0.05, 0.02);
                world.spawnParticle(Particle.CRIT_MAGIC, point, 3, 0.05, 0.05, 0.05, 0.02);

                currentStep++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnTemporaryTrophyHologram(Player player, FishManager.CustomFish fish) {
        Location spawnLoc = player.getLocation().add(0, 2.3, 0);
        World world = player.getWorld();
        if (world == null) return;

        // Visual armor stand as floating showcase banner
        ArmorStand stand = (ArmorStand) world.spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.setSmall(true);
        stand.setCustomNameVisible(true);

        String rarityColor = "§e";
        if ("MYTHIC".equalsIgnoreCase(fish.rarity)) rarityColor = "§d§l★ MÍTICO ★ §f";
        else if ("LEGENDARY".equalsIgnoreCase(fish.rarity)) rarityColor = "§6§l✦ LEGENDARIO ✦ §f";
        else if ("RARE".equalsIgnoreCase(fish.rarity)) rarityColor = "§b§l◆ RARO ◆ §f";

        stand.setCustomName(rarityColor + ChatColor.translateAlternateColorCodes('&', fish.name) + " §a($" + (int)fish.price + ")");

        // Floating upwards animation and clean removal after 45 ticks (2.25 seconds)
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks > 45 || !stand.isValid()) {
                    stand.remove();
                    cancel();
                    return;
                }

                stand.teleport(stand.getLocation().add(0, 0.02, 0));
                world.spawnParticle(Particle.FIREWORKS_SPARK, stand.getLocation().add(0, 0.5, 0), 2, 0.2, 0.2, 0.2, 0.02);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
