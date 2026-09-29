package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.banking.BondsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BondsCommand implements CommandExecutor {

    private final FishingEconomy plugin;

    public BondsCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden gestionar bonos.");
            return true;
        }

        Player player = (Player) sender;
        BondsManager bonds = plugin.getBondsManager();

        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "=== 📜 " + ChatColor.YELLOW + "MERCADO DE BONOS DEL ESTADO" + ChatColor.GOLD + " ===");
            player.sendMessage(ChatColor.AQUA + "Tu Puntuación Crediticia: " + ChatColor.GREEN + bonds.getCreditScore(player.getUniqueId()) + " / 850 pts");
            player.sendMessage(ChatColor.GRAY + "Adquiere bonos a plazo fijo para ganar intereses garantizados:");
            player.sendMessage(ChatColor.YELLOW + "/bonds buy 1h <monto> " + ChatColor.GRAY + "- 1 Hora (+2% ganancia)");
            player.sendMessage(ChatColor.YELLOW + "/bonds buy 24h <monto> " + ChatColor.GRAY + "- 24 Horas (+6% ganancia)");
            player.sendMessage(ChatColor.YELLOW + "/bonds buy 7d <monto> " + ChatColor.GRAY + "- 7 Días (+18% ganancia)");
            player.sendMessage(ChatColor.YELLOW + "/bonds claim " + ChatColor.GRAY + "- Reclamar bonos vencidos con intereses");
            return true;
        }

        if (args[0].equalsIgnoreCase("claim")) {
            bonds.claimMaturedBonds(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("buy") && args.length >= 3) {
            String durationArg = args[1].toLowerCase();
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Monto inválido.");
                return true;
            }

            BondsManager.BondDuration duration;
            if (durationArg.equals("1h") || durationArg.equals("1hora")) {
                duration = BondsManager.BondDuration.ONE_HOUR;
            } else if (durationArg.equals("24h") || durationArg.equals("1dia") || durationArg.equals("1d")) {
                duration = BondsManager.BondDuration.ONE_DAY;
            } else if (durationArg.equals("7d") || durationArg.equals("7dias") || durationArg.equals("1sem")) {
                duration = BondsManager.BondDuration.ONE_WEEK;
            } else {
                player.sendMessage(ChatColor.RED + "Duración no válida. Usa: 1h, 24h, o 7d.");
                return true;
            }

            bonds.purchaseBond(player, duration, amount);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Uso: /bonds o /bonds buy <1h|24h|7d> <monto> o /bonds claim");
        return true;
    }
}
