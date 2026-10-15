package com.fishingeconomy.commands;

import com.fishingeconomy.FishingEconomy;
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
            sender.sendMessage("§cUso: /bounty <set/list> [jugador] [monto]");
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cSolo jugadores pueden fijar recompensas.");
                return true;
            }
            Player player = (Player) sender;

            if (args.length < 3) {
                sender.sendMessage("§cUso: /bounty set <jugador> <monto>");
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cMonto inválido.");
                return true;
            }

            if (amount <= 0) {
                sender.sendMessage("§cEl monto debe ser positivo.");
                return true;
            }

            if (!plugin.getEconomyManager().has(player, amount)) {
                sender.sendMessage("§cNo tienes suficiente dinero.");
                return true;
            }

            plugin.getEconomyManager().withdrawPlayer(player, amount);
            double currentBounty = plugin.getBountyManager().getBounty(target);
            plugin.getBountyManager().setBounty(target, currentBounty + amount);

            Bukkit.broadcastMessage("§c§l¡RECOMPENSA! §e" + player.getName() + " §7ha colocado una recompensa de §a"
                    + plugin.getEconomyManager().format(amount) + " §7sobre §e" + target.getName() + "§7!");
            return true;
        } else if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("§8§m------------------------");
            sender.sendMessage("§6§lRecompensas Activas:");
            for (Map.Entry<UUID, Double> entry : plugin.getBountyManager().getAllBounties().entrySet()) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(entry.getKey());
                sender.sendMessage(
                        "§7- §e" + target.getName() + "§7: §a" + plugin.getEconomyManager().format(entry.getValue()));
            }
            sender.sendMessage("§8§m------------------------");
            return true;
        }

        sender.sendMessage("§cUso: /bounty <set/list> [jugador] [monto]");
        return true;
    }
}
