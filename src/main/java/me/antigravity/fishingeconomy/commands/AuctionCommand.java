package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
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
            sender.sendMessage("§cOnly players can use the auction house.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            plugin.getAuctionGui().openGui(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("sell")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /ah sell <price>");
                return true;
            }

            double price;
            try {
                price = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid price.");
                return true;
            }

            if (price <= 0) {
                sender.sendMessage("§cPrice must be positive.");
                return true;
            }

            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand == null || hand.getType() == Material.AIR) {
                sender.sendMessage("§cYou must hold an item to sell.");
                return true;
            }

            plugin.getAuctionHouseManager().addListing(player.getUniqueId(), hand.clone(), price);
            player.getInventory().setItemInMainHand(null);
            player.sendMessage("§aItem listed for " + plugin.getEconomyManager().format(price));
            return true;
        }

        return true;
    }
}
