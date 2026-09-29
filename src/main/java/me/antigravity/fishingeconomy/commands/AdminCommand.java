package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class AdminCommand implements CommandExecutor {
    private final FishingEconomy plugin;

    public AdminCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!sender.hasPermission("fishingeconomy.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("gui")) {
            if (sender instanceof Player) {
                plugin.getGuiManager().openMainGui((Player) sender);
            } else {
                sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            }
            return true;
        }

        if (args.length < 3) {
            sendHelp(sender);
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-amount"));
            return true;
        }

        EconomyManager eco = plugin.getEconomyManager();
        double newBalance;

        switch (sub) {
            case "set":
                eco.setBalance(target, amount);
                newBalance = eco.getBalance(target);
                sender.sendMessage(plugin.getConfigManager().getMessage("balance.set")
                        .replace("%player%", target.getName() != null ? target.getName() : "Unknown")
                        .replace("%balance%", eco.format(newBalance)));
                break;
            case "add":
                eco.addBalance(target, amount);
                newBalance = eco.getBalance(target);
                sender.sendMessage(plugin.getConfigManager().getMessage("balance.add")
                        .replace("%player%", target.getName() != null ? target.getName() : "Unknown")
                        .replace("%amount%", eco.format(amount))
                        .replace("%balance%", eco.format(newBalance)));
                break;
            case "take":
                eco.withdrawBalance(target, amount);
                newBalance = eco.getBalance(target);
                sender.sendMessage(plugin.getConfigManager().getMessage("balance.take")
                        .replace("%player%", target.getName() != null ? target.getName() : "Unknown")
                        .replace("%amount%", eco.format(amount))
                        .replace("%balance%", eco.format(newBalance)));
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§8[§bFishingEconomy§8] §eAdmin Commands:");
        sender.sendMessage("§7/eco set <player> <amount>");
        sender.sendMessage("§7/eco add <player> <amount>");
        sender.sendMessage("§7/eco take <player> <amount>");
        sender.sendMessage("§7/eco gui");
    }
}
