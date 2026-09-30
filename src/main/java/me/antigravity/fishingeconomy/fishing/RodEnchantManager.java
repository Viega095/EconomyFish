package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RodEnchantManager implements Listener {

    public static class EnchantHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public enum RodEnchant {
        ABYSSAL_CALL("§5Llamada Abisal", Material.AMETHYST_SHARD, 3000.0, "§7+20% Probabilidad de enganchar peces Míticos o Legendarios"),
        NEPTUNE_BLESSING("§6Bendición de Neptuno", Material.PRISMARINE_CRYSTALS, 2500.0, "§7+30% Valor de venta en todas tus capturas"),
        MAGNETIC_PULL("§bImán de Mareas", Material.NAUTILUS_SHELL, 2000.0, "§7Las capturas se entregan directamente a tu inventario"),
        BIOLUMINESCENT_SONAR("§eSonar Bioluminiscente", Material.GLOWSTONE_DUST, 3500.0, "§7+15% Probabilidad de pescar Cofres y Bóvedas del Fondo Marino");

        public final String name;
        public final Material icon;
        public final double cost;
        public final String description;

        RodEnchant(String name, Material icon, double cost, String description) {
            this.name = name;
            this.icon = icon;
            this.cost = cost;
            this.description = description;
        }
    }

    private final FishingEconomy plugin;
    private final NamespacedKey enchantKey;

    public RodEnchantManager(FishingEconomy plugin) {
        this.plugin = plugin;
        this.enchantKey = new NamespacedKey(plugin, "custom_rod_enchants");
    }

    public void openEnchantGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new EnchantHolder(), 45, "§8🔱 §bAltar de Encantamientos de Cañas §8🔱");

        ItemStack border = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 45; i++) inv.setItem(i, border);

        // Center slot: Player's fishing rod preview
        ItemStack rodInHand = player.getInventory().getItemInMainHand();
        boolean isRod = rodInHand != null && rodInHand.getType() == Material.FISHING_ROD;

        ItemStack preview = isRod ? rodInHand.clone() : new ItemStack(Material.BARRIER);
        ItemMeta pMeta = preview.getItemMeta();
        if (pMeta != null && !isRod) {
            pMeta.setDisplayName("§c✖ No tienes una caña en la mano principal");
            pMeta.setLore(Arrays.asList("§7Sostén una Caña de Pescar en la mano principal", "§7para imbuirla con encantamientos arcanos."));
            preview.setItemMeta(pMeta);
        }
        inv.setItem(13, preview);

        // Enchant Options (Slots 29, 31, 33, 35)
        RodEnchant[] enchants = RodEnchant.values();
        int[] slots = {29, 31, 33, 35};
        for (int i = 0; i < enchants.length && i < slots.length; i++) {
            RodEnchant ench = enchants[i];
            ItemStack eItem = new ItemStack(ench.icon);
            ItemMeta eMeta = eItem.getItemMeta();
            if (eMeta != null) {
                eMeta.setDisplayName(ench.name);
                eMeta.setLore(Arrays.asList(
                        ench.description,
                        "",
                        "§ePrecio: §a" + plugin.getEconomyManager().format(ench.cost),
                        isRod ? "§a▶ Haz clic para encantar tu caña" : "§c✖ Sostén una caña primero"
                ));
                eItem.setItemMeta(eMeta);
            }
            inv.setItem(slots[i], eItem);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof EnchantHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack rod = player.getInventory().getItemInMainHand();
        if (rod == null || rod.getType() != Material.FISHING_ROD) {
            player.sendMessage(ChatColor.RED + "✖ Debes sostener una caña de pescar en la mano principal.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        int slot = event.getRawSlot();
        RodEnchant selected = null;
        if (slot == 29) selected = RodEnchant.ABYSSAL_CALL;
        else if (slot == 31) selected = RodEnchant.NEPTUNE_BLESSING;
        else if (slot == 33) selected = RodEnchant.MAGNETIC_PULL;
        else if (slot == 35) selected = RodEnchant.BIOLUMINESCENT_SONAR;

        if (selected != null) {
            applyEnchant(player, rod, selected);
        }
    }

    private void applyEnchant(Player player, ItemStack rod, RodEnchant ench) {
        if (!plugin.getEconomyManager().has(player.getUniqueId(), ench.cost)) {
            player.sendMessage(ChatColor.RED + "✖ No tienes suficiente dinero. Necesitas " + plugin.getEconomyManager().format(ench.cost));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        ItemMeta meta = rod.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        for (String line : lore) {
            if (line.contains(ench.name)) {
                player.sendMessage(ChatColor.YELLOW + "⚠ Tu caña ya posee el encantamiento: " + ench.name);
                return;
            }
        }

        plugin.getEconomyManager().withdraw(player.getUniqueId(), ench.cost);

        lore.add(ChatColor.DARK_AQUA + "✦ Encantamiento: " + ench.name);
        meta.setLore(lore);

        String current = meta.getPersistentDataContainer().getOrDefault(enchantKey, PersistentDataType.STRING, "");
        meta.getPersistentDataContainer().set(enchantKey, PersistentDataType.STRING, current + ";" + ench.name());
        rod.setItemMeta(meta);

        player.sendMessage(ChatColor.GOLD + "✨ ¡Has imbuído tu caña con " + ench.name + ChatColor.GOLD + "!");
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1.2f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.6f);

        Location loc = player.getLocation().add(0, 1, 0);
        loc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc, 35, 0.5, 0.5, 0.5, 0.2);

        openEnchantGUI(player);
    }

    public boolean hasEnchant(ItemStack item, RodEnchant ench) {
        if (item == null || !item.hasItemMeta()) return false;
        String val = item.getItemMeta().getPersistentDataContainer().get(enchantKey, PersistentDataType.STRING);
        return val != null && val.contains(ench.name());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof EnchantHolder) {
            event.setCancelled(true);
        }
    }
}
