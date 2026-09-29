package me.antigravity.fishingeconomy.gui;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.fishing.FishManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiManager implements Listener {
    private final FishingEconomy plugin;

    public GuiManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void openMainGui(Player player) {
        Inventory gui = Bukkit.createInventory(new EcoHolder("main"), 27, "§8Economy Menu");

        // Balance Item
        ItemStack balanceItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta balMeta = balanceItem.getItemMeta();
        balMeta.setDisplayName("§eYour Balance");
        balMeta.setLore(Arrays.asList(
                "§7Current: §a" + plugin.getEconomyManager().format(plugin.getEconomyManager().getBalance(player))));
        balanceItem.setItemMeta(balMeta);
        gui.setItem(11, balanceItem);

        // Top Rich Item
        ItemStack topItem = new ItemStack(Material.EMERALD);
        ItemMeta topMeta = topItem.getItemMeta();
        topMeta.setDisplayName("§aTop Richest");
        topMeta.setLore(Arrays.asList("§7Click to view the", "§7richest players."));
        topItem.setItemMeta(topMeta);
        gui.setItem(13, topItem);

        // Fish Shop Item
        ItemStack shopItem = new ItemStack(Material.FISHING_ROD);
        ItemMeta shopMeta = shopItem.getItemMeta();
        shopMeta.setDisplayName("§bFish Shop");
        shopMeta.setLore(Arrays.asList("§7Click to sell your", "§7special fish."));
        shopItem.setItemMeta(shopMeta);
        gui.setItem(15, shopItem);

        // Fill empty slots
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < 27; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }

        player.openInventory(gui);
    }

    public void openTopRichGui(Player player) {
        Inventory gui = Bukkit.createInventory(new EcoHolder("top"), 27, "§8Top Richest Players");

        List<Map.Entry<UUID, Double>> top = plugin.getEconomyManager().getTopRich(10);
        int[] slots = { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21 }; // Just some slots

        for (int i = 0; i < top.size() && i < slots.length; i++) {
            Map.Entry<UUID, Double> entry = top.get(i);
            OfflinePlayer p = Bukkit.getOfflinePlayer(entry.getKey());

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (p.getName() != null) {
                meta.setOwningPlayer(p);
            }
            meta.setDisplayName("§6#" + (i + 1) + " §e" + (p.getName() != null ? p.getName() : "Unknown"));
            meta.setLore(Arrays.asList("§7Balance: §a" + plugin.getEconomyManager().format(entry.getValue())));
            head.setItemMeta(meta);

            gui.setItem(slots[i], head);
        }

        // Back Button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§cBack");
        back.setItemMeta(backMeta);
        gui.setItem(26, back);

        player.openInventory(gui);
    }

    public void openFishShopGui(Player player) {
        Inventory gui = Bukkit.createInventory(new EcoHolder("shop"), 54, "§8Fish Shop");

        // Sell All Button
        ItemStack sellAll = new ItemStack(Material.HOPPER);
        ItemMeta sellMeta = sellAll.getItemMeta();
        sellMeta.setDisplayName("§aSell All Special Fish");
        sellMeta.setLore(Arrays.asList("§7Click to sell all special", "§7fish in your inventory."));
        sellAll.setItemMeta(sellMeta);
        gui.setItem(49, sellAll);

        // Back Button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§cBack");
        back.setItemMeta(backMeta);
        gui.setItem(53, back);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        if (event.getClickedInventory() == null)
            return;
        if (!(event.getView().getTopInventory().getHolder() instanceof EcoHolder))
            return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        EcoHolder holder = (EcoHolder) event.getView().getTopInventory().getHolder();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR)
            return;

        // Simple check for back button, assuming name is correct.
        if (clicked.getType() == Material.ARROW) {
            // We can check display name if we really want, or just assume arrow in these
            // GUIs is back.
            // For safety let's just check type for now to avoid component parsing issues.
            openMainGui(player);
            return;
        }

        if (holder.id.equals("main")) {
            if (clicked.getType() == Material.EMERALD) {
                openTopRichGui(player);
            } else if (clicked.getType() == Material.FISHING_ROD) {
                openFishShopGui(player);
            }
        } else if (holder.id.equals("shop")) {
            if (clicked.getType() == Material.HOPPER) {
                sellAllFish(player);
            }
        }
    }

    private void sellAllFish(Player player) {
        double total = 0;
        int count = 0;
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            FishManager.CustomFish fish = plugin.getFishManager().getFishFromItem(item);
            if (fish != null) {
                total += fish.price * item.getAmount();
                count += item.getAmount();
                inv.setItem(i, null); // Remove item
            }
        }

        if (count > 0) {
            plugin.getEconomyManager().addBalance(player, total);
            player.sendMessage(plugin.getConfigManager().getMessage("fishing.sold-all")
                    .replace("%count%", String.valueOf(count))
                    .replace("%total%", plugin.getEconomyManager().format(total)));
            player.closeInventory();
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("fishing.no-fish-to-sell"));
        }
    }

    private static class EcoHolder implements InventoryHolder {
        private final String id;

        public EcoHolder(String id) {
            this.id = id;
        }

        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
