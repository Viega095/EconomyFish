package me.antigravity.fishingeconomy.banking;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        Inventory gui = Bukkit.createInventory(new BankHolder(), 27, "§8Bank Account");

        double balance = plugin.getBankManager().getBalance(player);

        // Info Item
        ItemStack info = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = info.getItemMeta();
        meta.setDisplayName("§eBank Balance");
        meta.setLore(Arrays.asList("§7Current: §a" + plugin.getEconomyManager().format(balance)));
        info.setItemMeta(meta);
        gui.setItem(13, info);

        // Deposit Button (All)
        ItemStack deposit = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta depMeta = deposit.getItemMeta();
        depMeta.setDisplayName("§aDeposit All");
        depMeta.setLore(Arrays.asList("§7Click to deposit all", "§7your cash."));
        deposit.setItemMeta(depMeta);
        gui.setItem(11, deposit);

        // Withdraw Button (All)
        ItemStack withdraw = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta withMeta = withdraw.getItemMeta();
        withMeta.setDisplayName("§cWithdraw All");
        withMeta.setLore(Arrays.asList("§7Click to withdraw all", "§7your funds."));
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
                player.sendMessage("§aDeposited " + plugin.getEconomyManager().format(pocket));
                openBankGui(player); // Refresh
            } else {
                player.sendMessage("§cYou have no money to deposit.");
            }
        } else if (clicked.getType() == Material.REDSTONE_BLOCK) {
            double bank = plugin.getBankManager().getBalance(player);
            if (bank > 0) {
                plugin.getBankManager().withdraw(player, bank);
                plugin.getEconomyManager().addBalance(player, bank);
                player.sendMessage("§aWithdrew " + plugin.getEconomyManager().format(bank));
                openBankGui(player); // Refresh
            } else {
                player.sendMessage("§cYou have no funds in the bank.");
            }
        }
    }

    private static class BankHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
