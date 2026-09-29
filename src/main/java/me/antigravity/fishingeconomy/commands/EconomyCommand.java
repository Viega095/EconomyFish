package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class EconomyCommand implements CommandExecutor {
    private final FishingEconomy plugin;

    public EconomyCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fishingeconomy.user")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (command.getName().equalsIgnoreCase("balance") || command.getName().equalsIgnoreCase("bal")) {
            handleBalance(sender, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("pay")) {
            handlePay(sender, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("baltop")) {
            handleBaltop(sender);
            return true;
        }

        return false;
    }

    private void handleBalance(CommandSender sender, String[] args) {
        OfflinePlayer target;
        if (args.length > 0 && sender.hasPermission("fishingeconomy.admin")) {
            target = Bukkit.getOfflinePlayer(args[0]);
        } else if (sender instanceof Player) {
            target = (OfflinePlayer) sender;
        } else {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            return;
        }

        double balance = plugin.getEconomyManager().getBalance(target);
        String msg = (sender instanceof Player && ((Player) sender).getUniqueId().equals(target.getUniqueId()))
                ? "balance.self"
                : "balance.other";
        sender.sendMessage(plugin.getConfigManager().getMessage(msg)
                .replace("%player%", target.getName() != null ? target.getName() : "Unknown")
                .replace("%balance%", plugin.getEconomyManager().format(balance)));
    }

    private void handlePay(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            return;
        }
        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage("§cUsage: /pay <player> <amount>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            player.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("§cYou cannot pay yourself.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getConfigManager().getMessage("invalid-amount"));
            return;
        }

        if (amount <= 0) {
            player.sendMessage(plugin.getConfigManager().getMessage("invalid-amount"));
            return;
        }

        EconomyManager eco = plugin.getEconomyManager();
        if (!eco.hasBalance(player, amount)) {
            player.sendMessage(plugin.getConfigManager().getMessage("insufficient-funds"));
            return;
        }

        eco.withdrawBalance(player, amount);
        eco.addBalance(target, amount);

        player.sendMessage(plugin.getConfigManager().getMessage("balance.pay-sent")
                .replace("%player%", target.getName() != null ? target.getName() : "Unknown")
                .replace("%amount%", eco.format(amount)));

        if (target.isOnline()) {
            ((Player) target).sendMessage(plugin.getConfigManager().getMessage("balance.pay-received")
                    .replace("%player%", player.getName())
                    .replace("%amount%", eco.format(amount)));
        }
    }

    private void handleBaltop(CommandSender sender) {
        sender.sendMessage("§8[§bFishingEconomy§8] §eTop Richest Players:");
        int i = 1;
        for (Map.Entry<UUID, Double> entry : plugin.getEconomyManager().getTopRich(10)) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(entry.getKey());
            String name = p.getName() != null ? p.getName() : "Unknown";
            sender.sendMessage(
                    "§6" + i + ". §f" + name + " §7- §a" + plugin.getEconomyManager().format(entry.getValue()));
            i++;
        }
    }
}
