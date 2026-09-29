package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BreedingCommand implements CommandExecutor {

    private final FishingEconomy plugin;

    public BreedingCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        if (mainHand == null || offHand == null || mainHand.getType().isAir() || offHand.getType().isAir()) {
            player.sendMessage(ChatColor.YELLOW + "Sostén un pez en tu mano principal y otro en tu mano secundaria (offhand) y usa /breedfish para cruzarlos.");
            return true;
        }

        plugin.getBreedingTankManager().breedFish(player, mainHand, offHand);
        return true;
    }
}
