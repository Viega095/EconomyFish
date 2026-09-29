package me.antigravity.fishingeconomy.gambling;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CoinflipGui implements Listener {
    private final FishingEconomy plugin;
    private final Random random = new Random();

    public CoinflipGui(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void openGui(Player player) {
        Inventory gui = Bukkit.createInventory(new CoinflipHolder(), 54, "§8Active Coinflips");

        int slot = 0;
        for (Map.Entry<UUID, Double> entry : plugin.getGamblingManager().getActiveCoinflips().entrySet()) {
            if (slot >= 54)
                break;

            UUID ownerId = entry.getKey();
            double amount = entry.getValue();
            OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerId);

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(owner);
            meta.setDisplayName("§e" + owner.getName() + "'s Coinflip");
            meta.setLore(Arrays.asList(
                    "§7Bet: §a" + plugin.getEconomyManager().format(amount),
                    "§7Click to play against this player!"));
            head.setItemMeta(meta);

            gui.setItem(slot++, head);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        if (event.getClickedInventory() == null)
            return;
        if (!(event.getView().getTopInventory().getHolder() instanceof CoinflipHolder))
            return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR)
            return;
        if (clicked.getType() != Material.PLAYER_HEAD)
            return;

        SkullMeta meta = (SkullMeta) clicked.getItemMeta();
        OfflinePlayer owner = meta.getOwningPlayer();

        if (owner == null)
            return;
        if (owner.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("§cYou cannot play against yourself! Use /coinflip cancel to remove your bet.");
            return;
        }

        if (!plugin.getGamblingManager().hasCoinflip(owner.getPlayer())) {
            player.sendMessage("§cThis coinflip is no longer active.");
            player.closeInventory();
            openGui(player); // Refresh
            return;
        }

        double amount = plugin.getGamblingManager().getCoinflipAmount(owner.getPlayer());

        if (!plugin.getEconomyManager().has(player, amount)) {
            player.sendMessage("§cYou don't have enough money to match this bet.");
            return;
        }

        // Play the game
        plugin.getEconomyManager().withdrawPlayer(player, amount);
        plugin.getGamblingManager().removeCoinflip(owner.getPlayer());

        boolean challengerWins = random.nextBoolean();
        double totalPot = amount * 2;

        if (challengerWins) {
            plugin.getEconomyManager().depositPlayer(player, totalPot);
            Bukkit.broadcastMessage("§6§lGAMBLE! §e" + player.getName() + " §7won §a"
                    + plugin.getEconomyManager().format(totalPot) + " §7against §e" + owner.getName() + "§7!");
        } else {
            plugin.getEconomyManager().depositPlayer(owner, totalPot);
            Bukkit.broadcastMessage("§6§lGAMBLE! §e" + owner.getName() + " §7won §a"
                    + plugin.getEconomyManager().format(totalPot) + " §7against §e" + player.getName() + "§7!");
        }

        player.closeInventory();
    }

    private static class CoinflipHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
