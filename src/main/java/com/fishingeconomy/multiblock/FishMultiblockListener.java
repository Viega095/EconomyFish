package com.fishingeconomy.multiblock;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class FishMultiblockListener implements Listener {

    private final FishingEconomy plugin;
    private final FishMultiblockManager manager;

    public FishMultiblockListener(FishingEconomy plugin, FishMultiblockManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        FishMultiblockType type = manager.getStructureTypeFromItem(item);
        if (type == null) return;

        Player player = event.getPlayer();
        Location placeLoc = event.getBlockPlaced().getLocation();

        // Check clearance for entire structure
        if (!manager.canPlaceStructure(placeLoc, type)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "✖ Espacio insuficiente para desplegar " + type.getDisplayName() + ChatColor.RED + ".");
            player.sendMessage(ChatColor.GRAY + "Asegúrate de que el área circundante esté despejada (aire o agua).");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        // Cancel vanilla placement and deploy multiblock atomically
        event.setCancelled(true);
        item.setAmount(item.getAmount() - 1);

        manager.placeStructure(player, placeLoc, type);
        player.sendMessage(ChatColor.GREEN + "✔ ¡" + type.getDisplayName() + ChatColor.GREEN + " desplegada con éxito en el mundo!");
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.4f);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();

        if (manager.isPartOfStructure(loc)) {
            event.setCancelled(true);
            event.setDropItems(false);
            event.setExpToDrop(0);

            Player player = event.getPlayer();
            manager.dismantleStructure(loc, player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block b : event.getBlocks()) {
            if (manager.isPartOfStructure(b.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block b : event.getBlocks()) {
            if (manager.isPartOfStructure(b.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(b -> manager.isPartOfStructure(b.getLocation()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(b -> manager.isPartOfStructure(b.getLocation()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null) return;

        ActiveFishMultiblock structure = manager.getStructureAt(clicked.getLocation());
        if (structure == null) return;

        Player player = event.getPlayer();

        if (structure.getType() == FishMultiblockType.BAIT_STATION) {
            event.setCancelled(true);
            if (plugin.getBaitCraftingStation() != null) {
                plugin.getBaitCraftingStation().openGUI(player);
                player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL, 1f, 1.2f);
            }
        } else if (structure.getType() == FishMultiblockType.MAGIC_AQUARIUM) {
            event.setCancelled(true);
            if (clicked.getType() == Material.SMOKER) {
                openFilletBench(player);
            } else {
                if (plugin.getAquariumManager() != null) {
                    plugin.getAquariumManager().openAquariumGUI(player);
                    player.playSound(player.getLocation(), Sound.AMBIENT_UNDERWATER_ENTER, 1f, 1.2f);
                }
            }
        }
    }

    private void openFilletBench(Player player) {
        FilletBenchGUI.open(plugin, player);
    }
}
