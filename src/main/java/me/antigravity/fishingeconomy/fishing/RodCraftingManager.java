package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class RodCraftingManager {

    private final FishingEconomy plugin;
    public final NamespacedKey ROD_TYPE_KEY;
    public final NamespacedKey REEL_SPEED_KEY;
    public final NamespacedKey DOUBLE_CATCH_KEY;

    public enum CustomRodType {
        LEVIATHAN_BANE("§6§l✦ Caña Perdición del Leviatán ✦", Material.FISHING_ROD, 0.40, 0.25, 50000.0,
                List.of("§7Forjada en las profundidades abisales.", "§e+40% Velocidad de Pesca", "§a+25% Probabilidad de Doble Captura", "§5Atracción de Criaturas Míticas")),
        MAGMA_FISHER("§c§l✦ Caña de Ignición Magmática ✦", Material.FISHING_ROD, 0.25, 0.15, 25000.0,
                List.of("§7Permite extraer tesoros ígneos.", "§e+25% Velocidad de Pesca", "§a+15% Probabilidad de Doble Captura", "§6Auto-cocción de peces")),
        SIREN_WEAVER("§b§l✦ Hiladora de Sirenas ✦", Material.FISHING_ROD, 0.50, 0.35, 120000.0,
                List.of("§7Encantada con cantos de sirena milenarios.", "§e+50% Velocidad de Pesca", "§a+35% Probabilidad de Doble Captura", "§d+100% Valor de venta de captura"));

        public final String displayName;
        public final Material material;
        public final double reelSpeedBonus;
        public final double doubleCatchBonus;
        public final double cost;
        public final List<String> lore;

        CustomRodType(String displayName, Material material, double reelSpeedBonus, double doubleCatchBonus, double cost, List<String> lore) {
            this.displayName = displayName;
            this.material = material;
            this.reelSpeedBonus = reelSpeedBonus;
            this.doubleCatchBonus = doubleCatchBonus;
            this.cost = cost;
            this.lore = lore;
        }
    }

    public RodCraftingManager(FishingEconomy plugin) {
        this.plugin = plugin;
        this.ROD_TYPE_KEY = new NamespacedKey(plugin, "custom_rod_type");
        this.REEL_SPEED_KEY = new NamespacedKey(plugin, "rod_reel_speed");
        this.DOUBLE_CATCH_KEY = new NamespacedKey(plugin, "rod_double_catch");
    }

    public ItemStack createCustomRod(CustomRodType type) {
        ItemStack item = new ItemStack(type.material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(type.displayName);
            meta.setLore(type.lore);
            meta.setUnbreakable(true);

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(ROD_TYPE_KEY, PersistentDataType.STRING, type.name());
            pdc.set(REEL_SPEED_KEY, PersistentDataType.DOUBLE, type.reelSpeedBonus);
            pdc.set(DOUBLE_CATCH_KEY, PersistentDataType.DOUBLE, type.doubleCatchBonus);

            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean purchaseRod(Player player, CustomRodType type) {
        if (!plugin.getEconomyManager().has(player, type.cost)) {
            player.sendMessage(ChatColor.RED + "✖ Fondos insuficientes para forjar esta caña (" +
                    plugin.getEconomyManager().format(type.cost) + ")");
            return false;
        }

        plugin.getEconomyManager().withdraw(player, type.cost);
        ItemStack rod = createCustomRod(type);
        player.getInventory().addItem(rod);

        player.sendMessage(ChatColor.GOLD + "🎣 [Astilleros de Pesca] ¡Has forjado exitosamente: " + type.displayName + ChatColor.GOLD + "!");
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.2f);
        return true;
    }

    public boolean isCustomRod(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(ROD_TYPE_KEY, PersistentDataType.STRING);
    }

    public double getReelSpeedBonus(ItemStack item) {
        if (!isCustomRod(item)) return 0.0;
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(REEL_SPEED_KEY, PersistentDataType.DOUBLE, 0.0);
    }

    public double getDoubleCatchBonus(ItemStack item) {
        if (!isCustomRod(item)) return 0.0;
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(DOUBLE_CATCH_KEY, PersistentDataType.DOUBLE, 0.0);
    }
}
