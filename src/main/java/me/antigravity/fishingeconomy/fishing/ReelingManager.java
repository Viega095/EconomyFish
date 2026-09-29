package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ReelingManager {

    private final FishingEconomy plugin;
    private final Map<UUID, ActiveReelSession> activeSessions = new HashMap<>();

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

    public void handleClick(Player player) {
        ActiveReelSession session = activeSessions.get(player.getUniqueId());
        if (session != null) {
            session.onReelInput();
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
        private final FishManager.CustomFish fish;
        private double progress = 35.0; // 0% to 100%
        private double cursor = 0.5; // 0.0 to 1.0
        private double targetMin = 0.4;
        private double targetMax = 0.6;
        private double targetVelocity = 0.02;
        private BukkitTask task;
        private int ticks = 0;

        public ActiveReelSession(Player player, FishHook hook, FishManager.CustomFish fish) {
            this.player = player;
            this.hook = hook;
            this.fish = fish;
            
            // Adjust difficulty based on rarity
            if (fish.rarity.equalsIgnoreCase("LEGENDARY") || fish.rarity.equalsIgnoreCase("MYTHIC")) {
                this.targetMin = 0.42;
                this.targetMax = 0.58;
                this.targetVelocity = 0.035;
            } else if (fish.rarity.equalsIgnoreCase("EPIC") || fish.rarity.equalsIgnoreCase("RARE")) {
                this.targetMin = 0.38;
                this.targetMax = 0.62;
                this.targetVelocity = 0.025;
            } else {
                this.targetMin = 0.35;
                this.targetMax = 0.65;
                this.targetVelocity = 0.015;
            }
        }

        public void start() {
            player.sendMessage(ChatColor.GOLD + "🎣 ¡Un pez " + fish.rarity + " ha picado! " +
                    ChatColor.YELLOW + "¡Haz clic derecho cuando el cursor esté en la zona verde!");
            player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, 1f, 1.2f);

            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || hook.isDead() || !hook.isValid()) {
                        cancelSession(player.getUniqueId());
                        return;
                    }

                    ticks++;
                    updatePhysics();
                    renderActionBar();

                    // Natural decay if not reeling in green zone
                    boolean inGreen = cursor >= targetMin && cursor <= targetMax;
                    if (inGreen) {
                        progress += 0.35;
                    } else {
                        progress -= 0.45;
                    }

                    // Water particles at hook location
                    if (ticks % 3 == 0) {
                        Location hookLoc = hook.getLocation();
                        hookLoc.getWorld().spawnParticle(Particle.WATER_SPLASH, hookLoc, 10, 0.2, 0.1, 0.2, 0.1);
                        hookLoc.getWorld().spawnParticle(Particle.BUBBLE_POP, hookLoc, 5, 0.3, 0.2, 0.3, 0.05);
                    }

                    if (progress >= 100.0) {
                        onCatchSuccess();
                        cancelSession(player.getUniqueId());
                    } else if (progress <= 0.0) {
                        onFishEscape();
                        cancelSession(player.getUniqueId());
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }

        private void updatePhysics() {
            // Target moves randomly back and forth
            cursor += targetVelocity;
            if (cursor >= 0.95 || cursor <= 0.05) {
                targetVelocity = -targetVelocity;
            }
            if (ThreadLocalRandom.current().nextDouble() < 0.08) {
                targetVelocity = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.06;
            }
            cursor = Math.max(0.0, Math.min(1.0, cursor));
        }

        public void onReelInput() {
            boolean inGreen = cursor >= targetMin && cursor <= targetMax;
            if (inGreen) {
                progress += 5.5;
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6f, 1.8f);
                player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0, 1, 0), 3, 0.2, 0.2, 0.2);
            } else {
                progress -= 4.0;
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.6f);
                player.sendMessage(ChatColor.RED + "⚠ ¡El sedal sufre demasiada tensión!");
            }
        }

        private void renderActionBar() {
            int totalBars = 24;
            int cursorIndex = (int) (cursor * totalBars);
            int minIndex = (int) (targetMin * totalBars);
            int maxIndex = (int) (targetMax * totalBars);

            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.DARK_GRAY).append("[");
            for (int i = 0; i <= totalBars; i++) {
                if (i == cursorIndex) {
                    sb.append(ChatColor.WHITE).append("▲");
                } else if (i >= minIndex && i <= maxIndex) {
                    sb.append(ChatColor.GREEN).append("█");
                } else {
                    sb.append(ChatColor.RED).append("░");
                }
            }
            sb.append(ChatColor.DARK_GRAY).append("] ");

            // Progress bar
            int progPercent = (int) Math.max(0, Math.min(100, progress));
            ChatColor progColor = progPercent > 66 ? ChatColor.GREEN : (progPercent > 33 ? ChatColor.YELLOW : ChatColor.RED);
            sb.append(progColor).append(progPercent).append("% ");
            sb.append(ChatColor.GRAY).append("(").append(fish.name).append(ChatColor.GRAY).append(")");

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(sb.toString()));
        }

        private void onCatchSuccess() {
            ItemStack fishItem = plugin.getFishManager().createFishItem(fish);
            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(fishItem);
            if (!leftOver.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), fishItem);
            }

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.4f);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.2f);
            player.spawnParticle(Particle.TOTEM, player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);

            String msg = plugin.getConfigManager().getMessage("fishing.caught")
                    .replace("%rarity%", plugin.getConfigManager().getFishConfig().getString("rarities." + fish.rarity + ".display", fish.rarity))
                    .replace("%fish%", fish.name)
                    .replace("%value%", plugin.getEconomyManager().format(fish.price));
            player.sendMessage(msg);
        }

        private void onFishEscape() {
            player.sendMessage(ChatColor.RED + "💨 ¡El pez se ha soltado y escapó a las profundidades!");
            player.playSound(player.getLocation(), Sound.ENTITY_FISH_SWIM, 1f, 0.8f);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.7f, 1.2f);
        }

        public void cleanup() {
            if (task != null) {
                task.cancel();
            }
        }
    }
}
