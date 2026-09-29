package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FishTrophyManager {

    private final FishingEconomy plugin;

    public FishTrophyManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public boolean createTrophy(Player player, ItemStack fishItem, double weightKg) {
        if (fishItem == null || fishItem.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "✖ Sostén un pez en tu mano para taxidermizarlo.");
            return false;
        }

        double fee = 5000.0;
        if (!plugin.getEconomyManager().has(player, fee)) {
            player.sendMessage(ChatColor.RED + "✖ El servicio de taxidermia cuesta " + plugin.getEconomyManager().format(fee));
            return false;
        }

        plugin.getEconomyManager().withdraw(player, fee);
        fishItem.setAmount(fishItem.getAmount() - 1);

        ItemStack plaque = new ItemStack(Material.OAK_HANGING_SIGN);
        ItemMeta meta = plaque.getItemMeta();
        if (meta != null) {
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            meta.setDisplayName("§6§l🏆 Trofeo de Pesca: " + ChatColor.YELLOW + player.getName());
            meta.setLore(List.of(
                    "§7Capturado por: §f" + player.getName(),
                    "§7Peso Oficial: §a" + String.format("%.2f", weightKg) + " kg",
                    "§7Fecha de Registro: §e" + date,
                    "§d✦ Placa Conmemorativa de Alta Mar ✦"
            ));
            plaque.setItemMeta(meta);
        }

        player.getInventory().addItem(plaque);
        player.sendMessage(ChatColor.GOLD + "🏆 [Taxidermia Marina] ¡Has recibido tu placa de trofeo conmemorativa!");
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PLACE, 1f, 1.2f);
        return true;
    }
}
