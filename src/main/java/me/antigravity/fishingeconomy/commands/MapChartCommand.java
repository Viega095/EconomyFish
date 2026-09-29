package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.market.MapStockRenderer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

public class MapChartCommand implements CommandExecutor {

    private final FishingEconomy plugin;

    public MapChartCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        Player player = (Player) sender;
        String symbol = (args.length >= 1) ? args[0].toUpperCase() : "AGY_STOCK";

        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) mapItem.getItemMeta();

        if (meta != null) {
            MapView view = Bukkit.createMap(player.getWorld());
            view.getRenderers().clear();
            view.addRenderer(new MapStockRenderer(symbol));
            meta.setMapView(view);
            meta.setDisplayName(ChatColor.GOLD + "📊 Gráfico Bursátil: " + ChatColor.YELLOW + symbol);
            mapItem.setItemMeta(meta);
        }

        player.getInventory().addItem(mapItem);
        player.sendMessage(ChatColor.GREEN + "✔ ¡Has recibido el gráfico bursátil interactivo para " + ChatColor.YELLOW + symbol + ChatColor.GREEN + "!");
        return true;
    }
}
