package com.fishingeconomy.commands;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AuctionCommand implements CommandExecutor {
    private final FishingEconomy plugin;

    public AuctionCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSolo jugadores pueden usar la casa de subastas.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            plugin.getAuctionGui().openGui(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("sell")) {
            if (args.length < 2) {
                sender.sendMessage("§cUso: /ah sell <precio>");
                return true;
            }

            double price;
            try {
                price = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cPrecio inválido.");
                return true;
            }

            if (price <= 0) {
                sender.sendMessage("§cEl precio debe ser un número positivo.");
                return true;
            }

            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand == null || hand.getType() == Material.AIR) {
                sender.sendMessage("§cDebes sostener un ítem en tu mano principal para subastarlo.");
                return true;
            }

            plugin.getAuctionHouseManager().addListing(player.getUniqueId(), hand.clone(), price);
            player.getInventory().setItemInMainHand(null);
            player.sendMessage("§aÍtem publicado en subasta por " + plugin.getEconomyManager().format(price));
            return true;
        }

        return true;
    }
}
