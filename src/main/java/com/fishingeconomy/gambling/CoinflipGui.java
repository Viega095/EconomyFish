package com.fishingeconomy.gambling;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
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
        Inventory gui = Bukkit.createInventory(new CoinflipHolder(), 54, "§8Apuestas de Coinflip Activas");

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
            meta.setDisplayName("§eApuesta de " + owner.getName());
            meta.setLore(Arrays.asList(
                    "§7Monto: §a" + plugin.getEconomyManager().format(amount),
                    "§7¡Haz clic para desafiar a este jugador!"));
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
            player.sendMessage("§c¡No puedes jugar contra ti mismo! Usa /coinflip cancel para retirar tu apuesta.");
            return;
        }

        if (!plugin.getGamblingManager().hasCoinflip(owner.getPlayer())) {
            player.sendMessage("§cEste coinflip ya no se encuentra activo.");
            player.closeInventory();
            openGui(player); // Refresh
            return;
        }

        double amount = plugin.getGamblingManager().getCoinflipAmount(owner.getPlayer());

        if (!plugin.getEconomyManager().has(player, amount)) {
            player.sendMessage("§cNo tienes suficiente dinero para igualar esta apuesta.");
            return;
        }

        // Execute Coinflip
        plugin.getEconomyManager().withdrawPlayer(player, amount);
        plugin.getGamblingManager().removeCoinflip(owner.getPlayer());

        player.closeInventory();

        boolean playerWins = random.nextBoolean();
        double totalPot = amount * 2;
        double tax = totalPot * 0.05; // 5% tax
        double winnings = totalPot - tax;

        if (playerWins) {
            plugin.getEconomyManager().depositPlayer(player, winnings);
            player.sendMessage("§a¡Has ganado el coinflip contra " + owner.getName() + " por "
                    + plugin.getEconomyManager().format(winnings) + "!");
            if (owner.isOnline() && owner.getPlayer() != null) {
                owner.getPlayer().sendMessage("§c¡Has perdido el coinflip contra " + player.getName() + " por "
                        + plugin.getEconomyManager().format(amount) + "!");
            }
        } else {
            plugin.getEconomyManager().depositPlayer(owner, winnings);
            player.sendMessage("§c¡Has perdido el coinflip contra " + owner.getName() + " por "
                    + plugin.getEconomyManager().format(amount) + "!");
            if (owner.isOnline() && owner.getPlayer() != null) {
                owner.getPlayer().sendMessage("§a¡Has ganado el coinflip contra " + player.getName() + " por "
                        + plugin.getEconomyManager().format(winnings) + "!");
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof CoinflipHolder) {
            event.setCancelled(true);
        }
    }

    public static class CoinflipHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
