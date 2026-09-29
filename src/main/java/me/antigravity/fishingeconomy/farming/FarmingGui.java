package me.antigravity.fishingeconomy.farming;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FarmingGui implements Listener, InventoryHolder {
    private final FishingEconomy plugin;
    private final Inventory inventory;

    public FarmingGui(FishingEconomy plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 27, "§aFarming Market");
        setupGui();
    }

    private void setupGui() {
        // Load crops from CropsManager and display them
        // For now, hardcoded example based on crops.yml structure assumption
        // Ideally we iterate through CropsManager data

        int slot = 0;
        for (Map.Entry<Material, Double> entry : plugin.getCropsManager().getCropPrices().entrySet()) {
            if (slot >= 27)
                break;

            ItemStack item = new ItemStack(entry.getKey());
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7Sell Price: §a" + plugin.getEconomyManager().format(entry.getValue()));
            lore.add("§eClick to Sell All");
            meta.setLore(lore);
            item.setItemMeta(meta);

            inventory.setItem(slot++, item);
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this)
            return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR)
            return;

        Material material = clicked.getType();
        double price = plugin.getCropsManager().getPrice(material);

        if (price <= 0)
            return;

        int amount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                amount += item.getAmount();
                player.getInventory().remove(item);
            }
        }

        if (amount > 0) {
            double total = amount * price;
            plugin.getEconomyManager().depositPlayer(player, total);
            player.sendMessage(
                    "§aSold " + amount + " " + material.name() + " for " + plugin.getEconomyManager().format(total));
        } else {
            player.sendMessage("§cYou don't have any " + material.name() + " to sell.");
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
