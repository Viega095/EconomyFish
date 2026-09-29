package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TradeCommand implements CommandExecutor {
    private final FishingEconomy plugin;

    public TradeCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can trade.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /trade <player> or /trade accept <player>");
            return true;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /trade accept <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }

            if (plugin.getTradeManager().hasRequest(target, player)) {
                plugin.getTradeManager().removeRequest(target);
                plugin.getTradeManager().startTrade(target, player);
            } else {
                sender.sendMessage("§cNo trade request from that player.");
            }
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        if (target.equals(player)) {
            sender.sendMessage("§cYou cannot trade with yourself.");
            return true;
        }

        plugin.getTradeManager().sendRequest(player, target);
        player.sendMessage("§aSent trade request to " + target.getName());
        target.sendMessage(
                "§a" + player.getName() + " wants to trade with you. Type /trade accept " + player.getName());

        return true;
    }
}
