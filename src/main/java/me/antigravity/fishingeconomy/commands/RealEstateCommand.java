package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.realestate.RealEstateManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RealEstateCommand implements CommandExecutor {

    private final FishingEconomy plugin;

    public RealEstateCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden adquirir bienes raíces.");
            return true;
        }

        Player player = (Player) sender;
        RealEstateManager realEstate = plugin.getRealEstateManager();

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            player.sendMessage(ChatColor.GOLD + "=== 🏛 " + ChatColor.YELLOW + "MERCADO DE BIENES RAÍCES Y LOCALES" + ChatColor.GOLD + " ===");
            for (RealEstateManager.Property p : realEstate.getAllProperties()) {
                String status = (p.owner == null) ?
                        ChatColor.GREEN + "[DISPONIBLE: " + plugin.getEconomyManager().format(p.purchasePrice) + "]" :
                        ChatColor.RED + "[COMPRADO]";
                player.sendMessage(ChatColor.AQUA + "• ID: " + ChatColor.WHITE + p.id + " §8| §e" + p.name + " " + status);
                player.sendMessage(ChatColor.GRAY + "   Renta Pasiva: " + ChatColor.GREEN + "+" + plugin.getEconomyManager().format(p.passiveIncome) + "/min");
            }
            player.sendMessage(ChatColor.GRAY + "Para comprar: " + ChatColor.YELLOW + "/property buy <id>");
            return true;
        }

        if (args[0].equalsIgnoreCase("buy") && args.length >= 2) {
            String propId = args[1].toLowerCase();
            realEstate.buyProperty(player, propId);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Uso: /property list o /property buy <id>");
        return true;
    }
}
