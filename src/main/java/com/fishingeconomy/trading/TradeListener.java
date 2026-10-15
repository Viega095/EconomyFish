package com.fishingeconomy.trading;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class TradeListener implements Listener {
    public TradeListener(FishingEconomy plugin) {
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof TradeSession) {
            ((TradeSession) event.getInventory().getHolder()).handleClick(event);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof TradeSession) {
            if (event.getRawSlots().stream().anyMatch(slot -> slot < event.getInventory().getSize())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof TradeSession) {
            ((TradeSession) event.getInventory().getHolder()).handleClose(event);
        }
    }
}
