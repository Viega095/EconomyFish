package com.fishingeconomy.commands;

import com.fishingeconomy.FishingEconomy;
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
            sender.sendMessage("§cSolo jugadores pueden usar este comando.");
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
                player.sendMessage("§aCoinflip cancelado. Fondos reembolsados.");
            } else {
                player.sendMessage("§cNo tienes ningún coinflip activo.");
            }
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cCantidad inválida. Uso: /coinflip <monto> o /coinflip cancel");
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage("§cEl monto debe ser positivo.");
            return true;
        }

        if (plugin.getGamblingManager().hasCoinflip(player)) {
            sender.sendMessage("§cYa posees un coinflip activo. Escribe /coinflip cancel para retirarlo.");
            return true;
        }

        if (!plugin.getEconomyManager().has(player, amount)) {
            sender.sendMessage("§cNo tienes suficiente dinero.");
            return true;
        }

        plugin.getEconomyManager().withdrawPlayer(player, amount);
        plugin.getGamblingManager().createCoinflip(player, amount);
        player.sendMessage("§a¡Coinflip creado exitosamente por " + plugin.getEconomyManager().format(amount) + "!");

        return true;
    }
}
