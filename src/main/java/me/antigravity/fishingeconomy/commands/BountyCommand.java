package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class BountyCommand implements CommandExecutor {
    private final FishingEconomy plugin;

    public BountyCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /bounty <set/list> [player] [amount]");
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can set bounties.");
                return true;
            }
            Player player = (Player) sender;

            if (args.length < 3) {
                sender.sendMessage("§cUsage: /bounty set <player> <amount>");
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount.");
                return true;
            }

            if (amount <= 0) {
                sender.sendMessage("§cAmount must be positive.");
                return true;
            }

            if (!plugin.getEconomyManager().has(player, amount)) {
                sender.sendMessage("§cYou don't have enough money.");
                return true;
            }

            plugin.getEconomyManager().withdrawPlayer(player, amount);
            double currentBounty = plugin.getBountyManager().getBounty(target);
            plugin.getBountyManager().setBounty(target, currentBounty + amount);

            Bukkit.broadcastMessage("§c§lBOUNTY! §e" + player.getName() + " §7has placed a bounty of §a"
                    + plugin.getEconomyManager().format(amount) + " §7on §e" + target.getName() + "§7!");
            return true;
        } else if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("§8§m------------------------");
            sender.sendMessage("§6§lActive Bounties:");
            for (Map.Entry<UUID, Double> entry : plugin.getBountyManager().getAllBounties().entrySet()) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(entry.getKey());
                sender.sendMessage(
                        "§7- §e" + target.getName() + "§7: §a" + plugin.getEconomyManager().format(entry.getValue()));
            }
            sender.sendMessage("§8§m------------------------");
            return true;
        }

        return true;
    }
}
