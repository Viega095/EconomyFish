package com.fishingeconomy.listeners;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GuiSecurityListener implements Listener {

    private final FishingEconomy plugin;

    public GuiSecurityListener(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    /**
     * Determines whether an inventory belongs to FishingEconomy's custom GUIs.
     */
    public boolean isPluginGui(Inventory inventory) {
        if (inventory == null) return false;
        InventoryHolder holder = inventory.getHolder();
        if (holder == null) return false;

        String className = holder.getClass().getName();
        return className.startsWith("com.fishingeconomy");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInv = event.getView().getTopInventory();
        if (!isPluginGui(topInv)) return;

        int topSize = topInv.getSize();
        // Cancel drag if any raw slot affected falls into the top inventory
        boolean affectsTop = event.getRawSlots().stream().anyMatch(slot -> slot < topSize);
        if (affectsTop) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInv = event.getView().getTopInventory();
        if (!isPluginGui(topInv)) return;

        int topSize = topInv.getSize();
        int rawSlot = event.getRawSlot();

        // 1. Anti-Dupe: Prevent COLLECT_TO_CURSOR from pulling decorative items
        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            event.setCancelled(true);
            return;
        }

        // 2. Anti-Theft: Cancel clicks on decorative glass panes and barrier icons in top inventory
        if (rawSlot >= 0 && rawSlot < topSize) {
            ItemStack current = event.getCurrentItem();
            if (current != null && isDecorativeFiller(current.getType())) {
                event.setCancelled(true);
                return;
            }
        }

        // 3. Prevent shift-clicking into view-only / fixed GUIs from player inventory
        if (rawSlot >= topSize && event.isShiftClick()) {
            InventoryHolder holder = topInv.getHolder();
            if (holder != null) {
                String name = holder.getClass().getSimpleName();
                // If it's a fixed menu or test GUI, block shift-clicking items into it
                if (name.contains("MarketHolder") || name.contains("BankHolder") ||
                        name.contains("JobsHolder") || name.contains("AuctionHolder") ||
                        name.contains("CoinflipHolder") || name.contains("MasteryHolder") ||
                        name.contains("EcoHolder") || name.contains("LabHolder") ||
                        name.contains("RodStatsHolder") || name.contains("RodGuiHolder") ||
                        name.contains("FilletHolder") || name.contains("CodexHolder") ||
                        name.contains("MuseumHolder") || name.contains("SuitHolder") ||
                        name.contains("FarmingGui")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isDecorativeFiller(Material mat) {
        return mat == Material.CYAN_STAINED_GLASS_PANE ||
                mat == Material.GRAY_STAINED_GLASS_PANE ||
                mat == Material.LIGHT_GRAY_STAINED_GLASS_PANE ||
                mat == Material.BLACK_STAINED_GLASS_PANE ||
                mat == Material.BLUE_STAINED_GLASS_PANE ||
                mat == Material.WHITE_STAINED_GLASS_PANE ||
                mat == Material.BARRIER;
    }
}
