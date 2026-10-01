package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class ReelingManager {

    private final FishingEconomy plugin;
    private final Map<UUID, ActiveReelSession> activeSessions = new ConcurrentHashMap<>();

    public ReelingManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public boolean hasActiveSession(UUID uuid) {
        return activeSessions.containsKey(uuid);
    }

    public void startSession(Player player, FishHook hook, FishManager.CustomFish fish) {
        if (activeSessions.containsKey(player.getUniqueId())) {
            cancelSession(player.getUniqueId());
        }

        ActiveReelSession session = new ActiveReelSession(player, hook, fish);
        activeSessions.put(player.getUniqueId(), session);
        session.start();
    }

    public void handleLeftClick(Player player) {
        ActiveReelSession session = activeSessions.get(player.getUniqueId());
        if (session != null) {
            session.nudgeLeft();
        }
    }

    public void handleRightClick(Player player) {
        ActiveReelSession session = activeSessions.get(player.getUniqueId());
        if (session != null) {
            session.nudgeRight();
        }
    }

    public void cancelSession(UUID uuid) {
        ActiveReelSession session = activeSessions.remove(uuid);
        if (session != null) {
            session.cleanup();
        }
    }

    public class ActiveReelSession {
        private final Player player;
        private final FishHook hook;
        private final Location waterLocation;
        private final FishManager.CustomFish fish;

        private double progress = 35.0; // 0% to 100%
        private double hookPos = 0.50; // Player cursor (0.0 to 1.0)
        private double fishPos = 0.50; // Fish target center (0.0 to 1.0)
        private double fishTarget = 0.50;
        private double zoneWidth = 0.30; // Width of green zone
        private double fishSpeed = 0.055;
        private int targetCooldown = 0;

        private long lastClickTime = 0;
        private BukkitTask task;
        private int ticks = 0;

        public ActiveReelSession(Player player, FishHook hook, FishManager.CustomFish fish) {
            this.player = player;
            this.hook = hook;
            if (hook != null && hook.isValid()) {
                this.waterLocation = hook.getLocation().clone();
            } else {
                this.waterLocation = player.getLocation().add(player.getEyeLocation().getDirection().multiply(4));
            }
            this.fish = fish;

            // Rarity difficulty scaling
            if (fish.rarity.equalsIgnoreCase("MYTHIC") || fish.rarity.equalsIgnoreCase("LEGENDARY")) {
                this.zoneWidth = 0.24;
                this.fishSpeed = 0.080;
            } else if (fish.rarity.equalsIgnoreCase("EPIC") || fish.rarity.equalsIgnoreCase("RARE")) {
                this.zoneWidth = 0.28;
                this.fishSpeed = 0.060;
            } else {
                this.zoneWidth = 0.34;
                this.fishSpeed = 0.045;
            }
        }

        public void start() {
            String rawRarity = plugin.getConfigManager().getFishConfig().getString("rarities." + fish.rarity + ".display", fish.rarity);
            player.sendTitle("§b🎣 ¡Pez Picando!", "§e[Clic Izq ◀ Izquierda] §a[Zona Verde] §e[Derecha ▶ Clic Der]", 5, 40, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, 1f, 1.2f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f);
            player.sendMessage("§6=================================================");
            player.sendMessage("§b🎣 ¡Un pez " + rawRarity + " §bha mordido el anzuelo!");
            player.sendMessage("§e▶ Usa §fClic Izquierdo (Golpear) §epara mover la aguja a la §fIZQUIERDA ◀");
            player.sendMessage("§e▶ Usa §fClic Derecho (Carrete) §epara mover la aguja a la §fDERECHA ▶");
            player.sendMessage("§a✔ ¡Mantén la aguja §f▲ §adentro de la zona verde §a█ §apara capturarlo!");
            player.sendMessage("§6=================================================");

            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || player.isDead()) {
                        cancelSession(player.getUniqueId());
                        return;
                    }

                    // Check if player is holding fishing rod
                    ItemStack held = player.getInventory().getItemInMainHand();
                    if (held == null || held.getType() != Material.FISHING_ROD) {
                        player.sendMessage(ChatColor.RED + "💨 ¡Has soltado la caña y el pez escapó!");
                        onFishEscape();
                        cancelSession(player.getUniqueId());
                        return;
                    }

                    // Check if player moved too far from fishing spot
                    if (player.getLocation().distanceSquared(waterLocation) > 400) {
                        player.sendMessage(ChatColor.RED + "💨 ¡Te has alejado demasiado del agua!");
                        onFishEscape();
                        cancelSession(player.getUniqueId());
                        return;
                    }

                    ticks++;
                    updateFishPhysics();
                    renderActionBar();

                    // Check if player hook cursor is inside fish green zone
                    double min = fishPos - (zoneWidth / 2.0);
                    double max = fishPos + (zoneWidth / 2.0);
                    boolean inZone = (hookPos >= min && hookPos <= max);

                    if (inZone) {
                        progress += 0.85; // ~4 seconds in zone to catch
                        if (ticks % 4 == 0) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.4f, 1.8f);
                            Location pLoc = (hook != null && hook.isValid()) ? hook.getLocation() : waterLocation;
                            pLoc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, pLoc.clone().add(0, 0.5, 0), 2, 0.2, 0.1, 0.2, 0.02);
                        }
                    } else {
                        progress -= 0.35; // ~8 seconds out of zone to lose
                        if (ticks % 8 == 0) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.35f, 0.6f);
                        }
                    }

                    // Water particles at fishing spot
                    if (ticks % 3 == 0) {
                        Location pLoc = (hook != null && hook.isValid()) ? hook.getLocation() : waterLocation;
                        pLoc.getWorld().spawnParticle(Particle.WATER_SPLASH, pLoc, 6, 0.2, 0.1, 0.2, 0.05);
                        pLoc.getWorld().spawnParticle(Particle.BUBBLE_POP, pLoc, 2, 0.2, 0.1, 0.2, 0.02);
                    }

                    if (progress >= 100.0) {
                        onCatchSuccess();
                        cancelSession(player.getUniqueId());
                    } else if (progress <= 0.0 || ticks > 700) { // Max 35s timeout
                        onFishEscape();
                        cancelSession(player.getUniqueId());
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }

        private void updateFishPhysics() {
            targetCooldown--;
            if (targetCooldown <= 0) {
                fishTarget = 0.15 + ThreadLocalRandom.current().nextDouble() * 0.70;
                targetCooldown = ThreadLocalRandom.current().nextInt(16, 36);
            }

            // Smooth sliding towards target
            double diff = fishTarget - fishPos;
            fishPos += diff * fishSpeed;
            fishPos = Math.max(0.10, Math.min(0.90, fishPos));
        }

        public void nudgeLeft() {
            long now = System.currentTimeMillis();
            if (now - lastClickTime < 50) return; // 50ms debounce
            lastClickTime = now;

            hookPos = Math.max(0.0, hookPos - 0.07);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.6f, 1.8f);
            player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 0.4f, 1.4f);
            Location loc = player.getLocation();
            loc.getWorld().spawnParticle(Particle.WATER_WAKE, loc.clone().add(0, 1, 0), 2, 0.2, 0.1, 0.2, 0.05);
        }

        public void nudgeRight() {
            long now = System.currentTimeMillis();
            if (now - lastClickTime < 50) return; // 50ms debounce
            lastClickTime = now;

            hookPos = Math.min(1.0, hookPos + 0.07);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.6f, 2.0f);
            player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 0.4f, 1.6f);
            Location loc = player.getLocation();
            loc.getWorld().spawnParticle(Particle.WATER_WAKE, loc.clone().add(0, 1, 0), 2, 0.2, 0.1, 0.2, 0.05);
        }

        private void renderActionBar() {
            int totalBars = 24;
            int hookIndex = (int) Math.round(hookPos * totalBars);
            double min = fishPos - (zoneWidth / 2.0);
            double max = fishPos + (zoneWidth / 2.0);
            int minIndex = (int) Math.floor(min * totalBars);
            int maxIndex = (int) Math.ceil(max * totalBars);

            boolean inZone = (hookPos >= min && hookPos <= max);

            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.DARK_GRAY).append("[");
            for (int i = 0; i <= totalBars; i++) {
                if (i == hookIndex) {
                    if (inZone) {
                        sb.append(ChatColor.WHITE).append("§l▲");
                    } else {
                        sb.append(ChatColor.YELLOW).append("§l▲");
                    }
                } else if (i >= minIndex && i <= maxIndex) {
                    sb.append(ChatColor.GREEN).append("█");
                } else {
                    sb.append(ChatColor.DARK_GRAY).append("░");
                }
            }
            sb.append(ChatColor.DARK_GRAY).append("] ");

            // Progress percentage & status
            int progPercent = (int) Math.max(0, Math.min(100, progress));
            ChatColor progColor = progPercent > 60 ? ChatColor.GREEN : (progPercent > 30 ? ChatColor.YELLOW : ChatColor.RED);
            sb.append(progColor).append(progPercent).append("% ");

            if (inZone) {
                sb.append(ChatColor.GREEN).append("✔ [ENGANCHADO] ");
            } else {
                if (hookPos < min) {
                    sb.append(ChatColor.GOLD).append("▶ [CLIC DERECHO] ▶ ");
                } else {
                    sb.append(ChatColor.GOLD).append("◀ [CLIC IZQUIERDO] ◀ ");
                }
            }

            sb.append(ChatColor.GRAY).append("(").append(ChatColor.translateAlternateColorCodes('&', fish.name)).append(ChatColor.GRAY).append(")");

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(sb.toString()));
        }

        private void onCatchSuccess() {
            ItemStack rod = player.getInventory().getItemInMainHand();
            boolean hasNeptuneBlessing = plugin.getRodEnchantManager() != null && plugin.getRodEnchantManager().hasEnchant(rod, RodEnchantManager.RodEnchant.NEPTUNE_BLESSING);
            boolean hasMagneticPull = plugin.getRodEnchantManager() != null && plugin.getRodEnchantManager().hasEnchant(rod, RodEnchantManager.RodEnchant.MAGNETIC_PULL);

            ItemStack fishItem = plugin.getFishManager().createFishItem(fish);
            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(fishItem);
            if (!leftOver.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), fishItem);
            } else if (hasMagneticPull) {
                player.spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 15, 0.2, 0.2, 0.2, 0.5);
            }

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.4f);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.2f);
            player.spawnParticle(Particle.TOTEM, player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);

            double finalPrice = hasNeptuneBlessing ? (fish.price * 1.30) : fish.price;

            String rawRarity = plugin.getConfigManager().getFishConfig().getString("rarities." + fish.rarity + ".display", fish.rarity);
            String msg = plugin.getConfigManager().getMessage("fishing.caught")
                    .replace("%rarity%", rawRarity)
                    .replace("%fish%", fish.name)
                    .replace("%value%", plugin.getEconomyManager().format(finalPrice));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            if (hasNeptuneBlessing) {
                player.sendMessage(ChatColor.GOLD + "🔱 [Bendición de Neptuno] ¡+30% de valor otorgado a esta captura!");
            }

            if (plugin.getFishCodexManager() != null) {
                plugin.getFishCodexManager().recordCatch(player, fish.id);
            }

            // Fisherman Job XP
            if (plugin.getJobsManager() != null && plugin.getJobsManager().getJob(player) == me.antigravity.fishingeconomy.jobs.JobsManager.JobType.FISHERMAN) {
                plugin.getJobsManager().addXp(player, 25);
                plugin.getEconomyManager().depositPlayer(player, 2.5);
            }
        }

        private void onFishEscape() {
            player.sendMessage(ChatColor.RED + "💨 ¡El pez se ha zafado del anzuelo y escapó a las profundidades!");
            player.playSound(player.getLocation(), Sound.ENTITY_FISH_SWIM, 1f, 0.8f);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.7f, 1.2f);
        }

        public void cleanup() {
            if (task != null) {
                task.cancel();
            }
            if (hook != null && hook.isValid()) {
                hook.remove();
            }
        }
    }
}
