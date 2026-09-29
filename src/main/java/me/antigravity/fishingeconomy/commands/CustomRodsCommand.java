package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.fishing.RodCraftingManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomRodsCommand implements CommandExecutor, TabCompleter {

    private final FishingEconomy plugin;

    public CustomRodsCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("gui")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Solo jugadores pueden abrir la interfaz gráfica.");
                return true;
            }
            plugin.getCustomRodGui().open(player);
            return true;
        }

        RodCraftingManager rods = plugin.getRodCraftingManager();

        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(ChatColor.GOLD + "=== 🎣 " + ChatColor.YELLOW + "ASTILLERO DE CAÑAS MÍTICAS" + ChatColor.GOLD + " ===");
            for (RodCraftingManager.CustomRodType type : RodCraftingManager.CustomRodType.values()) {
                sender.sendMessage(ChatColor.AQUA + "• " + type.displayName + " §8| §a" + plugin.getEconomyManager().format(type.cost));
                for (String line : type.lore) {
                    sender.sendMessage(ChatColor.DARK_GRAY + "   " + line);
                }
            }
            sender.sendMessage(ChatColor.GRAY + "Para comprar una caña: " + ChatColor.YELLOW + "/customrod buy <leviathan|magma|siren>");
            return true;
        }

        if (args[0].equalsIgnoreCase("buy") && args.length >= 2) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Solo jugadores.");
                return true;
            }

            RodCraftingManager.CustomRodType selected = parseRodType(args[1]);
            if (selected == null) {
                player.sendMessage(ChatColor.RED + "Caña desconocida. Opciones: LEVIATHAN_BANE, MAGMA_FISHER, SIREN_WEAVER.");
                return true;
            }

            rods.purchaseRod(player, selected);
            return true;
        }

        if (args[0].equalsIgnoreCase("give") && args.length >= 3) {
            if (!sender.hasPermission("fishingeconomy.admin")) {
                sender.sendMessage(ChatColor.RED + "No tienes permiso para otorgar cañas personalizadas.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Jugador no encontrado: " + args[1]);
                return true;
            }

            RodCraftingManager.CustomRodType selected = parseRodType(args[2]);
            if (selected == null) {
                sender.sendMessage(ChatColor.RED + "Tipo de caña inválido.");
                return true;
            }

            ItemStack rod = rods.createCustomRod(selected);
            target.getInventory().addItem(rod);
            target.sendMessage(ChatColor.GOLD + "✦ [Administración] ¡Has recibido: " + selected.displayName + ChatColor.GOLD + "!");
            target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
            sender.sendMessage(ChatColor.GREEN + "Has otorgado " + selected.name() + " a " + target.getName());
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Uso: /customrod <gui|list|buy <tipo>|give <jugador> <tipo>>");
        return true;
    }

    private RodCraftingManager.CustomRodType parseRodType(String input) {
        String name = input.toLowerCase();
        if (name.contains("leviathan")) return RodCraftingManager.CustomRodType.LEVIATHAN_BANE;
        if (name.contains("magma")) return RodCraftingManager.CustomRodType.MAGMA_FISHER;
        if (name.contains("siren")) return RodCraftingManager.CustomRodType.SIREN_WEAVER;
        try {
            return RodCraftingManager.CustomRodType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> subs = new ArrayList<>(Arrays.asList("gui", "list", "buy"));
            if (sender.hasPermission("fishingeconomy.admin")) {
                subs.add("give");
            }
            return filter(subs, args[0]);
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("buy")) {
                return filter(Arrays.asList("leviathan", "magma", "siren"), args[1]);
            }
            if (args[0].equalsIgnoreCase("give") && sender.hasPermission("fishingeconomy.admin")) {
                return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
            }
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give") && sender.hasPermission("fishingeconomy.admin")) {
            return filter(Arrays.asList("LEVIATHAN_BANE", "MAGMA_FISHER", "SIREN_WEAVER"), args[2]);
        }
        return new ArrayList<>();
    }

    private List<String> filter(List<String> list, String query) {
        String q = query.toLowerCase();
        return list.stream().filter(s -> s.toLowerCase().startsWith(q)).collect(Collectors.toList());
    }
}
