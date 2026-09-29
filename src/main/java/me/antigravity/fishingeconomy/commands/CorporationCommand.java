package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.corporations.Corporation;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CorporationCommand implements CommandExecutor {

    private final FishingEconomy plugin;

    public CorporationCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Solo los jugadores pueden gestionar corporaciones.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "create" -> {
                if (args.length < 4) {
                    player.sendMessage(ChatColor.RED + "Uso: /corp create <nombre> <ticker> <precio_accion>");
                    return true;
                }
                String name = args[1];
                String ticker = args[2];
                double price = parseDouble(args[3], 10.0);
                plugin.getCorporationManager().createCorporation(player, name, ticker, price);
            }
            case "invest", "buy" -> {
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Uso: /corp invest <ticker> <cantidad_acciones>");
                    return true;
                }
                String ticker = args[1];
                int amount = parseInt(args[2], 1);
                plugin.getCorporationManager().buyShares(player, ticker, amount);
            }
            case "dividend" -> {
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Uso: /corp dividend <ticker> <monto_total>");
                    return true;
                }
                String ticker = args[1];
                double total = parseDouble(args[2], 100.0);
                plugin.getCorporationManager().payDividends(player, ticker, total);
            }
            case "list" -> {
                player.sendMessage(ChatColor.GOLD + "=== 🏛 Empresas en Bolsa ===");
                for (Corporation corp : plugin.getCorporationManager().getAllCorporations()) {
                    player.sendMessage(ChatColor.YELLOW + "• " + corp.getName() + " (" + ChatColor.AQUA + corp.getTicker() +
                            ChatColor.YELLOW + ") - Acción: " + ChatColor.GREEN + plugin.getEconomyManager().format(corp.getSharePrice()) +
                            ChatColor.GRAY + " | Bóveda: " + plugin.getEconomyManager().format(corp.getCorporateVault()));
                }
            }
            default -> sendHelp(player);
        }
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== 🏛 Gestión de Corporaciones /corp ===");
        player.sendMessage(ChatColor.YELLOW + "/corp create <nombre> <ticker> <precio> " + ChatColor.GRAY + "- Funda una empresa pública");
        player.sendMessage(ChatColor.YELLOW + "/corp invest <ticker> <acciones> " + ChatColor.GRAY + "- Invierte en acciones");
        player.sendMessage(ChatColor.YELLOW + "/corp dividend <ticker> <monto> " + ChatColor.GRAY + "- Paga dividendos a accionistas");
        player.sendMessage(ChatColor.YELLOW + "/corp list " + ChatColor.GRAY + "- Lista todas las empresas cotizadas");
    }

    private double parseDouble(String s, double fallback) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private int parseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
