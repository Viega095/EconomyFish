package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.entity.FishHook;
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
                plugin.getReelingManager().handleClick(player);
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

            FishManager.CustomFish fish = plugin.getFishManager().rollFish();
            if (fish != null) {
                // If the fish is Rare, Epic, Legendary or Mythic, start the interactive reeling minigame!
                boolean isHighRarity = !fish.rarity.equalsIgnoreCase("COMMON");
                if (isHighRarity && plugin.getReelingManager() != null && event.getHook() != null) {
                    event.setCancelled(true);
                    plugin.getReelingManager().startSession(player, event.getHook(), fish);
                    return;
                }

                // Otherwise, normal catch
                ItemStack fishItem = plugin.getFishManager().createFishItem(fish);
                HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(fishItem);
                if (!leftOver.isEmpty()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), fishItem);
                }

                String msg = plugin.getConfigManager().getMessage("fishing.caught")
                        .replace("%rarity%", plugin.getConfigManager().getFishConfig().getString("rarities." + fish.rarity + ".display", fish.rarity))
                        .replace("%fish%", fish.name)
                        .replace("%value%", plugin.getEconomyManager().format(fish.price));
                player.sendMessage(msg);
            }
        } else if (event.getState() == PlayerFishEvent.State.REEL_IN || event.getState() == PlayerFishEvent.State.IN_GROUND) {
            if (plugin.getReelingManager() != null && plugin.getReelingManager().hasActiveSession(player.getUniqueId())) {
                plugin.getReelingManager().handleClick(player);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (plugin.getReelingManager() != null && plugin.getReelingManager().hasActiveSession(player.getUniqueId())) {
                plugin.getReelingManager().handleClick(player);
            }
        }
    }
}
