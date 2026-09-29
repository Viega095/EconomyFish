package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubmarineCommand implements CommandExecutor {

    private final FishingEconomy plugin;

    public SubmarineCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        Player player = (Player) sender;
        plugin.getSubmarineManager().deploySubmarine(player);
        return true;
    }
}
