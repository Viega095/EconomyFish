package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.fishing.TournamentManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TournamentCommand implements CommandExecutor {

    private final FishingEconomy plugin;

    public TournamentCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TournamentManager tm = plugin.getTournamentManager();

        if (args.length == 0 || args[0].equalsIgnoreCase("status")) {
            if (!tm.isActive()) {
                sender.sendMessage(ChatColor.YELLOW + "No hay ningún torneo de pesca activo en este momento.");
                sender.sendMessage(ChatColor.GRAY + "Los administradores pueden iniciar uno con " + ChatColor.GOLD + "/ftournament start <minutos>");
                return true;
            }

            sender.sendMessage(ChatColor.GOLD + "=== 🏆 " + ChatColor.YELLOW + "TORNEO DE PESCA EN CURSO" + ChatColor.GOLD + " ===");
            sender.sendMessage(ChatColor.AQUA + "Modalidad: " + ChatColor.WHITE + tm.getCurrentType().displayName);
            sender.sendMessage(ChatColor.YELLOW + "Tiempo restante: " + (tm.getTimeRemainingSeconds() / 60) + "m " + (tm.getTimeRemainingSeconds() % 60) + "s");
            return true;
        }

        if (args[0].equalsIgnoreCase("start") && sender.hasPermission("fishingeconomy.admin")) {
            int mins = (args.length >= 2) ? Integer.parseInt(args[1]) : 5;
            tm.startTournament(TournamentManager.TournamentType.HEAVIEST_FISH, mins);
            return true;
        }

        if (args[0].equalsIgnoreCase("end") && sender.hasPermission("fishingeconomy.admin")) {
            tm.endTournament();
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Uso: /ftournament <status|start [minutos]|end>");
        return true;
    }
}
