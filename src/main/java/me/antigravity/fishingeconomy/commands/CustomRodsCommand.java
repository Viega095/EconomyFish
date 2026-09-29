package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.fishing.RodCraftingManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CustomRodsCommand implements CommandExecutor {

    private final FishingEconomy plugin;

    public CustomRodsCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden adquirir cañas personalizadas.");
            return true;
        }

        Player player = (Player) sender;
        RodCraftingManager rods = plugin.getRodCraftingManager();

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            player.sendMessage(ChatColor.GOLD + "=== 🎣 " + ChatColor.YELLOW + "ASTILLERO DE CAÑAS MÍTICAS" + ChatColor.GOLD + " ===");
            for (RodCraftingManager.CustomRodType type : RodCraftingManager.CustomRodType.values()) {
                player.sendMessage(ChatColor.AQUA + "• " + type.displayName + " §8| §a" + plugin.getEconomyManager().format(type.cost));
                for (String line : type.lore) {
                    player.sendMessage(ChatColor.DARK_GRAY + "   " + line);
                }
            }
            player.sendMessage(ChatColor.GRAY + "Para comprar una caña: " + ChatColor.YELLOW + "/customrod buy <leviathan|magma|siren>");
            return true;
        }

        if (args[0].equalsIgnoreCase("buy") && args.length >= 2) {
            String name = args[1].toLowerCase();
            RodCraftingManager.CustomRodType selected = null;
            if (name.contains("leviathan")) selected = RodCraftingManager.CustomRodType.LEVIATHAN_BANE;
            else if (name.contains("magma")) selected = RodCraftingManager.CustomRodType.MAGMA_FISHER;
            else if (name.contains("siren")) selected = RodCraftingManager.CustomRodType.SIREN_WEAVER;

            if (selected == null) {
                player.sendMessage(ChatColor.RED + "Caña desconocida. Opciones: leviathan, magma, siren.");
                return true;
            }

            rods.purchaseRod(player, selected);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Uso: /customrod list o /customrod buy <nombre>");
        return true;
    }
}
