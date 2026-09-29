package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CoinflipCommand implements CommandExecutor {
    private final FishingEconomy plugin;

    public CoinflipCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            plugin.getCoinflipGui().openGui(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("cancel")) {
            if (plugin.getGamblingManager().hasCoinflip(player)) {
                double amount = plugin.getGamblingManager().getCoinflipAmount(player);
                plugin.getGamblingManager().removeCoinflip(player);
                plugin.getEconomyManager().depositPlayer(player, amount);
                player.sendMessage("§aCoinflip cancelled. Money refunded.");
            } else {
                player.sendMessage("§cYou don't have an active coinflip.");
            }
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount. Usage: /coinflip <amount> or /coinflip cancel");
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage("§cAmount must be positive.");
            return true;
        }

        if (plugin.getGamblingManager().hasCoinflip(player)) {
            sender.sendMessage("§cYou already have an active coinflip. Type /coinflip cancel to remove it.");
            return true;
        }

        if (!plugin.getEconomyManager().has(player, amount)) {
            sender.sendMessage("§cYou don't have enough money.");
            return true;
        }

        plugin.getEconomyManager().withdrawPlayer(player, amount);
        plugin.getGamblingManager().createCoinflip(player, amount);
        player.sendMessage("§aCoinflip created for " + plugin.getEconomyManager().format(amount) + "!");

        return true;
    }
}
