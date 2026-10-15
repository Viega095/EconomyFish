package com.fishingeconomy.banking;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BankGui implements Listener {
    private final FishingEconomy plugin;

    public BankGui(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void openBankGui(Player player) {
        Inventory gui = Bukkit.createInventory(new BankHolder(), 27, "§8Cuenta Bancaria");

        double balance = plugin.getBankManager().getBalance(player);

        // Info Item
        ItemStack info = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = info.getItemMeta();
        meta.setDisplayName("§eSaldo Bancario");
        meta.setLore(Arrays.asList("§7Fondos guardados: §a" + plugin.getEconomyManager().format(balance)));
        info.setItemMeta(meta);
        gui.setItem(13, info);

        // Deposit Button (All)
        ItemStack deposit = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta depMeta = deposit.getItemMeta();
        depMeta.setDisplayName("§aDepositar Todo");
        depMeta.setLore(Arrays.asList("§7Haz clic para depositar todo", "§7tu efectivo en mano."));
        deposit.setItemMeta(depMeta);
        gui.setItem(11, deposit);

        // Withdraw Button (All)
        ItemStack withdraw = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta withMeta = withdraw.getItemMeta();
        withMeta.setDisplayName("§cRetirar Todo");
        withMeta.setLore(Arrays.asList("§7Haz clic para retirar todos", "§7tus fondos bancarios."));
        withdraw.setItemMeta(withMeta);
        gui.setItem(15, withdraw);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        if (event.getClickedInventory() == null)
            return;
        if (!(event.getView().getTopInventory().getHolder() instanceof BankHolder))
            return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR)
            return;

        if (clicked.getType() == Material.EMERALD_BLOCK) {
            double pocket = plugin.getEconomyManager().getBalance(player);
            if (pocket > 0) {
                plugin.getEconomyManager().withdrawBalance(player, pocket);
                plugin.getBankManager().deposit(player, pocket);
                player.sendMessage("§aHas depositado " + plugin.getEconomyManager().format(pocket) + " en tu cuenta.");
                openBankGui(player); // Refresh
            } else {
                player.sendMessage("§cNo tienes dinero en mano para depositar.");
            }
        } else if (clicked.getType() == Material.REDSTONE_BLOCK) {
            double bank = plugin.getBankManager().getBalance(player);
            if (bank > 0) {
                plugin.getBankManager().withdraw(player, bank);
                plugin.getEconomyManager().addBalance(player, bank);
                player.sendMessage("§aHas retirado " + plugin.getEconomyManager().format(bank) + " del banco.");
                openBankGui(player); // Refresh
            } else {
                player.sendMessage("§cNo tienes fondos en el banco para retirar.");
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof BankHolder) {
            event.setCancelled(true);
        }
    }

    public static class BankHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
