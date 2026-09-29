package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OceanicBossRaid implements Listener {

    private final FishingEconomy plugin;
    private LivingEntity currentBoss = null;
    private BossBar bossBar = null;
    private String bossName = "";

    public OceanicBossRaid(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public boolean spawnOceanicBoss(Location loc, String type) {
        if (currentBoss != null && currentBoss.isValid()) {
            return false;
        }

        String name = type.equalsIgnoreCase("kraken") ? "§5§l✦ KRAKEN DE LAS PROFUNDIDADES ✦" : "§b§l✦ MEGALODÓN ANCESTRAL ✦";
        this.bossName = name;

        ElderGuardian guardian = (ElderGuardian) loc.getWorld().spawnEntity(loc, EntityType.ELDER_GUARDIAN);
        guardian.setCustomName(name);
        guardian.setCustomNameVisible(true);
        double maxHp = type.equalsIgnoreCase("kraken") ? 1500.0 : 2500.0;
        guardian.setMaxHealth(maxHp);
        guardian.setHealth(maxHp);
        this.currentBoss = guardian;

        this.bossBar = Bukkit.createBossBar(name, BarColor.PURPLE, BarStyle.SEGMENTED_12);
        for (Player p : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(p);
        }

        Bukkit.broadcastMessage("§b🌊 =============================================");
        Bukkit.broadcastMessage("§9☠ ¡UN JEFE OCEÁNICO HA EMERGIDO DEL ABISMO!");
        Bukkit.broadcastMessage(name);
        Bukkit.broadcastMessage("§7Ubicación: §e" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
        Bukkit.broadcastMessage("§b🌊 =============================================");

        // Particle pulse task
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (currentBoss == null || !currentBoss.isValid() || currentBoss.isDead()) {
                    if (bossBar != null) bossBar.removeAll();
                    cancel();
                    return;
                }

                if (bossBar != null) {
                    bossBar.setProgress(Math.max(0.0, Math.min(1.0, currentBoss.getHealth() / currentBoss.getMaxHealth())));
                }

                Location bLoc = currentBoss.getLocation();
                bLoc.getWorld().spawnParticle(Particle.WATER_SPLASH, bLoc, 50, 2.0, 1.0, 2.0, 0.2);

                ticks += 20;
                if (ticks % 100 == 0) { // Every 5 seconds, water shockwave
                    for (Player nearby : bLoc.getWorld().getPlayers()) {
                        if (nearby.getLocation().distance(bLoc) < 25) {
                            nearby.sendMessage(ChatColor.DARK_AQUA + "🌊 ¡La onda de choque marina del jefe te ha alcanzado!");
                            nearby.damage(4.0, currentBoss);
                            nearby.playSound(nearby.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1f, 0.7f);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);

        return true;
    }

    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        if (currentBoss != null && event.getEntity().getUniqueId().equals(currentBoss.getUniqueId())) {
            Player killer = event.getEntity().getKiller();
            if (killer != null) {
                plugin.getEconomyManager().depositPlayer(killer, 25000.0);
                Bukkit.broadcastMessage("§6✦ ¡" + killer.getName() + " ha derrotado a " + bossName + " y recibió una recompensa de $25,000!");
            }

            // Drop Mythic Ancient Pearl
            ItemStack mythicPearl = new ItemStack(Material.HEART_OF_THE_SEA);
            ItemMeta meta = mythicPearl.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§d§l✦ Perla Abisal del Leviatán ✦");
                meta.setLore(Arrays.asList(
                        "§7Extraída del corazón de una bestia marina ancestral.",
                        "§eObjeto de coleccionista de valor incalculable.",
                        "§aValor en mercado: $15,000"
                ));
                mythicPearl.setItemMeta(meta);
            }
            event.getDrops().add(mythicPearl);

            if (bossBar != null) {
                bossBar.removeAll();
                bossBar = null;
            }
            currentBoss = null;
        }
    }

    public boolean hasActiveBoss() {
        return currentBoss != null && currentBoss.isValid();
    }
}
