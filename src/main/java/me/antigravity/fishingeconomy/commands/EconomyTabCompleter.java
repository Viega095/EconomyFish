package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EconomyTabCompleter implements TabCompleter {

    private final FishingEconomy plugin;

    public EconomyTabCompleter(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        String cmdName = command.getName().toLowerCase();

        if (cmdName.equals("eco")) {
            if (args.length == 1) {
                return filter(Arrays.asList("set", "add", "take", "gui"), args[0]);
            }
            if (args.length == 2 && !args[0].equalsIgnoreCase("gui")) {
                return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
            }
            if (args.length == 3 && !args[0].equalsIgnoreCase("gui")) {
                return Arrays.asList("100", "500", "1000", "5000", "10000");
            }
        } else if (cmdName.equals("pay")) {
            if (args.length == 1) {
                return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[0]);
            }
            if (args.length == 2) {
                return Arrays.asList("10", "50", "100", "500", "1000");
            }
        } else if (cmdName.equals("bounty")) {
            if (args.length == 1) {
                return filter(Arrays.asList("set", "list"), args[0]);
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
                return Arrays.asList("500", "1000", "5000");
            }
        } else if (cmdName.equals("corp")) {
            if (args.length == 1) {
                return filter(Arrays.asList("create", "invest", "dividend", "list"), args[0]);
            }
        } else if (cmdName.equals("bonds")) {
            if (args.length == 1) {
                return filter(Arrays.asList("buy", "claim"), args[0]);
            }
        } else if (cmdName.equals("property")) {
            if (args.length == 1) {
                return filter(Arrays.asList("list", "buy"), args[0]);
            }
        } else if (cmdName.equals("expedition")) {
            if (args.length == 1) {
                return filter(Arrays.asList("start"), args[0]);
            }
        } else if (cmdName.equals("ftournament")) {
            if (args.length == 1) {
                return filter(Arrays.asList("start", "status", "end"), args[0]);
            }
        } else if (cmdName.equals("merchant")) {
            if (args.length == 1) {
                return filter(Arrays.asList("list", "buy"), args[0]);
            }
        }

        return new ArrayList<>();
    }

    private List<String> filter(List<String> list, String query) {
        String q = query.toLowerCase();
        return list.stream().filter(s -> s.toLowerCase().startsWith(q)).collect(Collectors.toList());
    }
}
