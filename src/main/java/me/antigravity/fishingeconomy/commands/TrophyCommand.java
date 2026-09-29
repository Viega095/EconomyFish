package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TrophyCommand implements CommandExecutor {

    private final FishingEconomy plugin;

    public TrophyCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand == null || hand.getType().isAir()) {
            player.sendMessage(ChatColor.YELLOW + "Sostén el pez que deseas convertir en trofeo de pared y escribe /trophy.");
            return true;
        }

        double weight = 2.5 + (Math.random() * 15.0);
        plugin.getFishTrophyManager().createTrophy(player, hand, weight);
        return true;
    }
}
