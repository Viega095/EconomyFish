package me.antigravity.fishingeconomy.trading;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

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
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof TradeSession) {
            ((TradeSession) event.getInventory().getHolder()).handleClose(event);
        }
    }
}
