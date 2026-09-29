package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpeditionCommand implements CommandExecutor {

    private final FishingEconomy plugin;

    public ExpeditionCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden zarpar en expediciones.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0 || args[0].equalsIgnoreCase("start")) {
            plugin.getExpeditionManager().startExpedition(player);
            return true;
        }

        player.sendMessage(ChatColor.YELLOW + "Uso: /expedition start ($1,000 para zarpar)");
        return true;
    }
}
