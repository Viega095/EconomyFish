package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class FishingListener implements Listener {
    private final FishingEconomy plugin;

    public FishingListener(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            // Check if player has an active reeling session
            if (plugin.getReelingManager() != null && plugin.getReelingManager().hasActiveSession(player.getUniqueId())) {
                plugin.getReelingManager().handleRightClick(player);
                event.setCancelled(true);
                return;
            }

            // Consume bait if available
            BaitManager.BaitType bait = null;
            if (plugin.getBaitManager() != null) {
                bait = plugin.getBaitManager().consumeEquippedBait(player);
                if (bait != null) {
                    player.sendMessage(ChatColor.GOLD + "🪱 [Cebo] " + ChatColor.YELLOW + "Has usado " + bait.displayName);
                }
            }

            FishManager.CustomFish fish = plugin.getFishManager().rollFish(player);
            if (fish != null) {
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

                // Normal catch process
                giveFishToPlayer(player, fish);

                // Custom Rod Bonuses
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                if (plugin.getRodCraftingManager() != null && plugin.getRodCraftingManager().isCustomRod(mainHand)) {
                    double doubleCatchBonus = plugin.getRodCraftingManager().getDoubleCatchBonus(mainHand);
                    if (ThreadLocalRandom.current().nextDouble() < doubleCatchBonus) {
                        player.sendMessage(ChatColor.GOLD + "✦ [Caña Mítica] ¡Efecto de Doble Captura Activado!");
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.8f);
                        FishManager.CustomFish extraFish = plugin.getFishManager().rollFish(player);
                        if (extraFish != null) {
                            giveFishToPlayer(player, extraFish);
                        }
                    }
                }
            }
        } else if (event.getState() == PlayerFishEvent.State.REEL_IN || event.getState() == PlayerFishEvent.State.IN_GROUND) {
            if (plugin.getReelingManager() != null && plugin.getReelingManager().hasActiveSession(player.getUniqueId())) {
                plugin.getReelingManager().handleRightClick(player);
                event.setCancelled(true);
            }
        }
    }

    private void giveFishToPlayer(Player player, FishManager.CustomFish fish) {
        ItemStack fishItem = plugin.getFishManager().createFishItem(fish);
        HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(fishItem);
        if (!leftOver.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), fishItem);
        }

        String rawRarity = plugin.getConfigManager().getFishConfig().getString("rarities." + fish.rarity + ".display", fish.rarity);
        String msg = plugin.getConfigManager().getMessage("fishing.caught")
                .replace("%rarity%", rawRarity)
                .replace("%fish%", fish.name)
                .replace("%value%", plugin.getEconomyManager().format(fish.price));
        
        // Ensure final message with all placeholders is completely colorized
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.8f, 1.2f);

        if (plugin.getFishCodexManager() != null) {
            plugin.getFishCodexManager().recordCatch(player, fish.id);
        }

        // Deep Sea Treasure Salvage chance
        if (plugin.getDeepSeaTreasureSalvage() != null && ThreadLocalRandom.current().nextDouble() < 0.08) {
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (plugin.getReelingManager() != null && plugin.getReelingManager().hasActiveSession(player.getUniqueId())) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                plugin.getReelingManager().handleLeftClick(player);
                event.setCancelled(true);
            } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                plugin.getReelingManager().handleRightClick(player);
                event.setCancelled(true);
            }
        }
    }
}
