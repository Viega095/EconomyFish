package me.antigravity.fishingeconomy.gui;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.fishing.RodCraftingManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomRodGui implements Listener {

    private final FishingEconomy plugin;

    public CustomRodGui(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new RodGuiHolder(), 27, "§8Astilleros de Cañas Míticas");

        ItemStack border = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, border);
        }

        RodCraftingManager rods = plugin.getRodCraftingManager();
        int[] slots = { 11, 13, 15 };
        RodCraftingManager.CustomRodType[] types = RodCraftingManager.CustomRodType.values();

        for (int i = 0; i < types.length && i < slots.length; i++) {
            RodCraftingManager.CustomRodType type = types[i];
            ItemStack rod = rods.createCustomRod(type);
            ItemMeta meta = rod.getItemMeta();
            if (meta != null) {
                List<String> lore = new ArrayList<>(meta.getLore() != null ? meta.getLore() : List.of());
                lore.add("");
                lore.add("§7Precio de forja: §a" + plugin.getEconomyManager().format(type.cost));
                lore.add("§e▶ ¡Haz clic para forjar esta caña!");
                meta.setLore(lore);
                rod.setItemMeta(meta);
            }
            inv.setItem(slots[i], rod);
        }

        // Info item
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta iMeta = info.getItemMeta();
        if (iMeta != null) {
            iMeta.setDisplayName("§6§lInformación del Astillero");
            iMeta.setLore(Arrays.asList(
                    "§7Las cañas personalizadas otorgan",
                    "§7bonificaciones pasivas permanentes:",
                    "§a• Mayor velocidad de captura",
                    "§e• Probabilidad de doble pez",
                    "§d• Atracción de peces míticos"
            ));
            info.setItemMeta(iMeta);
        }
        inv.setItem(22, info);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof RodGuiHolder) {
            event.setCancelled(true);

            if (!(event.getWhoClicked() instanceof Player player)) return;
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            int slot = event.getRawSlot();
            RodCraftingManager.CustomRodType[] types = RodCraftingManager.CustomRodType.values();

            if (slot == 11 && types.length > 0) {
                plugin.getRodCraftingManager().purchaseRod(player, types[0]);
                open(player);
            } else if (slot == 13 && types.length > 1) {
                plugin.getRodCraftingManager().purchaseRod(player, types[1]);
                open(player);
            } else if (slot == 15 && types.length > 2) {
                plugin.getRodCraftingManager().purchaseRod(player, types[2]);
                open(player);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof RodGuiHolder) {
            event.setCancelled(true);
        }
    }

    public static class RodGuiHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
