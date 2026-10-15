package com.fishingeconomy.commands;

import com.fishingeconomy.FishingEconomy;
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
            sender.sendMessage("§cSolo jugadores pueden comerciar.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage("§cUso: /trade <jugador> o /trade accept <jugador>");
            return true;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length < 2) {
                sender.sendMessage("§cUso: /trade accept <jugador>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cJugador no encontrado.");
                return true;
            }

            if (plugin.getTradeManager().hasRequest(target, player)) {
                plugin.getTradeManager().removeRequest(target);
                plugin.getTradeManager().startTrade(target, player);
            } else {
                sender.sendMessage("§cNo tienes solicitudes de intercambio de ese jugador.");
            }
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cJugador no encontrado.");
            return true;
        }

        if (target.equals(player)) {
            sender.sendMessage("§cNo puedes comerciar contigo mismo.");
            return true;
        }

        plugin.getTradeManager().sendRequest(player, target);
        player.sendMessage("§aHas enviado una solicitud de intercambio a " + target.getName());
        target.sendMessage(
                "§a" + player.getName() + " desea comerciar contigo. Escribe /trade accept " + player.getName());

        return true;
    }
}
