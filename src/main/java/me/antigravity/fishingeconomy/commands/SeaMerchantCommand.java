package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SeaMerchantCommand implements CommandExecutor {

    private final FishingEconomy plugin;

    public SeaMerchantCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            plugin.getSeaMerchantManager().openBlackMarket(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("buy") && args.length >= 2) {
            plugin.getSeaMerchantManager().purchaseItem(player, args[1]);
            return true;
        }

        player.sendMessage(ChatColor.YELLOW + "Uso: /merchant <list|buy <anzuelo|mapa|cebo>>");
        return true;
    }
}
