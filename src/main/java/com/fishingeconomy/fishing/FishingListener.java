package com.fishingeconomy.fishing;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.persistence.PersistentDataType;

public class FishingListener implements Listener {
    private final FishingEconomy plugin;
    private final Map<UUID, Long> poseidonCooldowns = new HashMap<>();
    private final Map<UUID, Long> cosmicCooldowns = new HashMap<>();

    public FishingListener(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        // If player already in reeling session, intercept all fishing events as right-click reel inputs
        if (plugin.getReelingManager() != null && plugin.getReelingManager().hasActiveSession(player.getUniqueId())) {
            event.setCancelled(true);
            plugin.getReelingManager().handleRightClick(player);
            return;
        }

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            // Consume bait if available
            BaitManager.BaitType bait = null;
            if (plugin.getBaitManager() != null) {
                bait = plugin.getBaitManager().consumeEquippedBait(player);
                if (bait != null) {
                    player.sendMessage(ChatColor.GOLD + "🪱 [Cebo] " + ChatColor.YELLOW + "Has usado " + bait.displayName);
                }
            }

            // Apply Rod Enchants & Custom Rod Roll Bonus
            ItemStack rod = player.getInventory().getItemInMainHand();
            boolean hasAbyssalCall = plugin.getRodEnchantManager() != null && plugin.getRodEnchantManager().hasEnchant(rod, RodEnchantManager.RodEnchant.ABYSSAL_CALL);

            FishManager.CustomFish fish = plugin.getFishManager().rollFish(player);
            if (fish != null && hasAbyssalCall && fish.rarity.equalsIgnoreCase("COMMON") && ThreadLocalRandom.current().nextDouble() < 0.35) {
                // Abyssal Call triggers high-rarity upgrade
                FishManager.CustomFish upgraded = plugin.getFishManager().rollFish(player);
                if (upgraded != null && !upgraded.rarity.equalsIgnoreCase("COMMON")) {
                    fish = upgraded;
                    player.sendMessage(ChatColor.DARK_PURPLE + "✦ [Llamada Abisal] ¡Tu caña ha atraído a un ser de las profundidades!");
                    player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.7f, 1.6f);
                }
            }

            if (fish != null) {
                // Exotic Event: Meteor Fish Rain boost
                if (plugin.getExoticFishingEventManager() != null && plugin.getExoticFishingEventManager().isMeteorFishRainActive()) {
                    if (fish.rarity.equalsIgnoreCase("COMMON") && ThreadLocalRandom.current().nextDouble() < 0.60) {
                        FishManager.CustomFish upgraded = plugin.getFishManager().rollFish(player);
                        if (upgraded != null) {
                            fish = upgraded;
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "☄️ [Lluvia Meteorítica] ¡Un pez cósmico ha mordido el anzuelo!");
                            player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
                        }
                    }
                }

                // Check if interactive reeling minigame should start
                boolean isHighRarity = !fish.rarity.equalsIgnoreCase("COMMON");
                boolean isForcedMinigame = plugin.getFishManager().isForceMinigame(player.getUniqueId());
                if (isForcedMinigame) {
                    plugin.getFishManager().setForceMinigame(player.getUniqueId(), false);
                }

                if ((isHighRarity || isForcedMinigame) && plugin.getReelingManager() != null && event.getHook() != null) {
                    event.setCancelled(true);
                    plugin.getReelingManager().startSession(player, event.getHook(), fish);
                    return;
                }

                // Normal catch process for basic fish
                giveFishToPlayer(player, fish, rod);

                // Custom Rod Bonuses
                if (plugin.getRodCraftingManager() != null && plugin.getRodCraftingManager().isCustomRod(rod)) {
                    double doubleCatchBonus = plugin.getRodCraftingManager().getDoubleCatchBonus(rod);
                    if (ThreadLocalRandom.current().nextDouble() < doubleCatchBonus) {
                        player.sendMessage(ChatColor.GOLD + "✦ [Caña Mítica] ¡Efecto de Doble Captura Activado!");
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.8f);
                        FishManager.CustomFish extraFish = plugin.getFishManager().rollFish(player);
                        if (extraFish != null) {
                            giveFishToPlayer(player, extraFish, rod);
                        }
                    }
                }
            }
        }
    }

    public void giveFishToPlayer(Player player, FishManager.CustomFish fish, ItemStack rod) {
        boolean hasMagneticPull = plugin.getRodEnchantManager() != null && plugin.getRodEnchantManager().hasEnchant(rod, RodEnchantManager.RodEnchant.MAGNETIC_PULL);
        boolean hasNeptuneBlessing = plugin.getRodEnchantManager() != null && plugin.getRodEnchantManager().hasEnchant(rod, RodEnchantManager.RodEnchant.NEPTUNE_BLESSING);
        boolean hasSonar = plugin.getRodEnchantManager() != null && plugin.getRodEnchantManager().hasEnchant(rod, RodEnchantManager.RodEnchant.BIOLUMINESCENT_SONAR);

        ItemStack fishItem = plugin.getFishManager().createFishItem(fish);
        HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(fishItem);
        if (!leftOver.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), fishItem);
        } else if (hasMagneticPull) {
            player.spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 15, 0.2, 0.2, 0.2, 0.5);
        }

        double finalPrice = hasNeptuneBlessing ? (fish.price * 1.30) : fish.price;

        // Exotic Event: Ghost Leviathan Tide sale multiplier
        if (plugin.getExoticFishingEventManager() != null && plugin.getExoticFishingEventManager().isGhostLeviathanTideActive()) {
            finalPrice *= 2.50;
            player.spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0, 1, 0), 12, 0.2, 0.2, 0.2, 0.05);
        }

        // Custom Rod Specific Bonuses
        double salvageChance = hasSonar ? 0.25 : 0.08;
        if (rod != null && rod.hasItemMeta()) {
            org.bukkit.persistence.PersistentDataContainer pdc = rod.getItemMeta().getPersistentDataContainer();
            String rodType = pdc.get(plugin.getRodCraftingManager().ROD_TYPE_KEY, org.bukkit.persistence.PersistentDataType.STRING);
            if ("TIDAL_CELESTIAL_ROD".equals(rodType)) {
                finalPrice *= 2.50;
                player.sendMessage(ChatColor.BLUE + "🌊 [Mareas Celestiales] ¡+150% de valor por sintonización lunar!");
            } else if ("PHOENIX_ABYSSAL_ROD".equals(rodType)) {
                finalPrice *= 1.35;
                player.spawnParticle(Particle.FLAME, player.getLocation().add(0, 1, 0), 15, 0.2, 0.2, 0.2, 0.05);
                player.sendMessage(ChatColor.GOLD + "🔥 [Fénix Abisal] ¡Captura imbuida en llamas celestiales!");
            } else if ("VOID_ASTRAL_ROD".equals(rodType)) {
                salvageChance = 0.45;
                player.spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 25, 0.3, 0.3, 0.3, 0.5);
            }

            if (plugin.getCustomRodStatsGui() != null) {
                int sellUpgrade = pdc.getOrDefault(plugin.getCustomRodStatsGui().keyUpgradeSell, org.bukkit.persistence.PersistentDataType.INTEGER, 0);
                if (sellUpgrade > 0) {
                    finalPrice *= (1.0 + (sellUpgrade * 0.20));
                }
            }
        }

        String rawRarity = plugin.getConfigManager().getFishConfig().getString("rarities." + fish.rarity + ".display", fish.rarity);
        String msg = plugin.getConfigManager().getMessage("fishing.caught")
                .replace("%rarity%", rawRarity)
                .replace("%fish%", fish.name)
                .replace("%value%", plugin.getEconomyManager().format(finalPrice));
        
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        if (hasNeptuneBlessing) {
            player.sendMessage(ChatColor.GOLD + "🔱 [Bendición de Neptuno] ¡+30% de valor otorgado a esta captura!");
        }
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.8f, 1.2f);

        // Special catch rod animations & celebrations
        if (plugin.getSpecialCatchAnimationManager() != null &&
                (!"COMMON".equalsIgnoreCase(fish.rarity) || plugin.getRodCraftingManager().isCustomRod(rod))) {
            plugin.getSpecialCatchAnimationManager().playSpecialCatchCelebration(player, fish, player.getLocation());
        }

        if (plugin.getFishCodexManager() != null) {
            plugin.getFishCodexManager().recordCatch(player, fish.id);
        }

        // Update active rod stats & Marine EXP
        if (plugin.getCustomRodStatsGui() != null) {
            plugin.getCustomRodStatsGui().updateRodStats(rod, fish);
        }

        // Fisherman Job XP
        if (plugin.getJobsManager() != null && plugin.getJobsManager().getJob(player) == com.fishingeconomy.jobs.JobsManager.JobType.FISHERMAN) {
            plugin.getJobsManager().addXp(player, 15);
            plugin.getEconomyManager().depositPlayer(player, 1.0);
        }

        // Deep Sea Treasure Salvage chance (boosted with Sonar and Void Rod)
        if (plugin.getDeepSeaTreasureSalvage() != null && ThreadLocalRandom.current().nextDouble() < salvageChance) {
            DeepSeaTreasureSalvage.SalvageTier[] tiers = DeepSeaTreasureSalvage.SalvageTier.values();
            DeepSeaTreasureSalvage.SalvageTier rolledTier = tiers[ThreadLocalRandom.current().nextInt(tiers.length)];
            ItemStack crate = plugin.getDeepSeaTreasureSalvage().createSalvageItem(rolledTier);
            if (!player.getInventory().addItem(crate).isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), crate);
            }
            player.sendMessage(ChatColor.GOLD + "⚓ [Rescate Marino] ¡Has desenterrado un " + rolledTier.getDisplayName() + ChatColor.GOLD + "!");
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1.2f);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Check if player is holding a fishing rod and shift-right clicks (Sneaking)
        if (event.getHand() == EquipmentSlot.HAND && player.isSneaking()) {
            ItemStack inHand = player.getInventory().getItemInMainHand();
            if (inHand.getType() == Material.FISHING_ROD && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                if (plugin.getReelingManager() == null || !plugin.getReelingManager().hasActiveSession(player.getUniqueId())) {
                    event.setCancelled(true);
                    if (plugin.getCustomRodStatsGui() != null) {
                        plugin.getCustomRodStatsGui().open(player);
                    }
                    return;
                }
            }
        }

        // Poseidon Trident Rod Active Skill (Left Click)
        if (event.getHand() == EquipmentSlot.HAND && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (plugin.getReelingManager() == null || !plugin.getReelingManager().hasActiveSession(player.getUniqueId())) {
                ItemStack inHand = player.getInventory().getItemInMainHand();
                if (inHand.getType() == Material.FISHING_ROD && inHand.hasItemMeta()) {
                    String rodType = inHand.getItemMeta().getPersistentDataContainer().get(plugin.getRodCraftingManager().ROD_TYPE_KEY, PersistentDataType.STRING);
                    if (rodType != null && rodType.equals(RodCraftingManager.CustomRodType.POSEIDON_TRIDENT_ROD.name())) {
                        long now = System.currentTimeMillis();
                        long last = poseidonCooldowns.getOrDefault(player.getUniqueId(), 0L);
                        if (now - last < 120000) {
                            long rem = (120000 - (now - last)) / 1000;
                            player.sendMessage(ChatColor.AQUA + "✖ Vórtice de Poseidón en enfriamiento: " + rem + "s restantes.");
                        } else {
                            poseidonCooldowns.put(player.getUniqueId(), now);
                            plugin.getFishManager().setForceMythic(player.getUniqueId(), true);
                            player.sendMessage(ChatColor.AQUA + "🌊 §l[Vórtice de Poseidón] §b¡El mar ruge ante ti! Tu próxima captura será un ser MÍTICO garantizado.");
                            player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1f, 1f);
                            player.playSound(player.getLocation(), Sound.AMBIENT_UNDERWATER_ENTER, 1f, 0.8f);
                            player.getWorld().spawnParticle(Particle.WATER_WAKE, player.getLocation().add(0, 1, 0), 35, 1.0, 0.5, 1.0, 0.2);
                            player.getWorld().spawnParticle(Particle.DOLPHIN, player.getLocation().add(0, 1.5, 0), 20, 0.8, 0.8, 0.8, 0.1);
                        }
                    } else if (rodType != null && rodType.equals(RodCraftingManager.CustomRodType.VOID_COSMIC_ROD.name())) {
                        long now = System.currentTimeMillis();
                        long last = cosmicCooldowns.getOrDefault(player.getUniqueId(), 0L);
                        if (now - last < 90000) {
                            long rem = (90000 - (now - last)) / 1000;
                            player.sendMessage(ChatColor.DARK_PURPLE + "✖ Agujero Negro Marino en enfriamiento: " + rem + "s restantes.");
                        } else {
                            cosmicCooldowns.put(player.getUniqueId(), now);
                            plugin.getFishManager().setForceMythic(player.getUniqueId(), true);
                            player.sendMessage(ChatColor.DARK_PURPLE + "🌌 §l[Agujero Negro Marino] §d¡El vacío estelar distorsiona el océano! Próxima captura garantizada de tier MÍTICO.");
                            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1f, 1.2f);
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
                            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 50, 1.0, 0.5, 1.0, 0.5);
                            player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, player.getLocation().add(0, 1.5, 0), 30, 0.5, 0.5, 0.5, 0.1);
                        }
                    }
                }
            }
        }

        if (plugin.getReelingManager() != null && plugin.getReelingManager().hasActiveSession(player.getUniqueId())) {
            // Filter only main hand to avoid double processing off-hand
            if (event.getHand() == null || event.getHand() == EquipmentSlot.HAND) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    plugin.getReelingManager().handleRightClick(player);
                } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    plugin.getReelingManager().handleLeftClick(player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onArmSwing(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            if (plugin.getReelingManager() != null && plugin.getReelingManager().hasActiveSession(player.getUniqueId())) {
                plugin.getReelingManager().handleLeftClick(player);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getReelingManager() != null && plugin.getReelingManager().hasActiveSession(player.getUniqueId())) {
            ItemStack dropped = event.getItemDrop().getItemStack();
            if (dropped.getType() == Material.FISHING_ROD) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.getReelingManager() != null) {
            plugin.getReelingManager().cancelSession(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (plugin.getReelingManager() != null) {
            plugin.getReelingManager().cancelSession(event.getEntity().getUniqueId());
        }
    }
}
