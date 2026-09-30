package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
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

public class BaitCraftingStation implements Listener {

    public enum BaitType {
        ABYSSAL_CHUM("§5Engodo Abisal Concentrado", Material.FERMENTED_SPIDER_EYE, 2500.0,
                Arrays.asList("§7Aumenta +40% la probabilidad de peces Míticos", "§7y Jefes Oceánicos.")),
        BIOLUMINESCENT_KRILL("§bKrill Bioluminiscente", Material.GLOW_INK_SAC, 1500.0,
                Arrays.asList("§7Atrae peces legendarios durante la noche", "§7y aumenta velocidad de carrete +35%.")),
        GOLDEN_LARVA("§6Larva Dorada de Coral", Material.GOLD_NUGGET, 3000.0,
                Arrays.asList("§7Duplica el valor económico en venta (x2)", "§7de los peces capturados con este cebo.")),
        TREASURE_SCENT("§eExtracto de Rastreador de Tesoros", Material.PRISMARINE_CRYSTALS, 2000.0,
                Arrays.asList("§7Aumenta +60% la probabilidad de rescatar", "§7Cofres y Reliquias Hundidas del Abismo."));

        private final String displayName;
        private final Material icon;
        private final double cost;
        private final List<String> lore;

        BaitType(String displayName, Material icon, double cost, List<String> lore) {
            this.displayName = displayName;
            this.icon = icon;
            this.cost = cost;
            this.lore = lore;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Material getIcon() {
            return icon;
        }

        public double getCost() {
            return cost;
        }

        public List<String> getLore() {
            return lore;
        }
    }

    private final FishingEconomy plugin;
    private final NamespacedKey baitKey;

    public static class BaitHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public BaitCraftingStation(FishingEconomy plugin) {
        this.plugin = plugin;
        this.baitKey = new NamespacedKey(plugin, "custom_bait_type");
    }

    public ItemStack createBaitItem(BaitType type, int amount) {
        ItemStack item = new ItemStack(type.getIcon(), amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(type.getDisplayName());
            List<String> lore = new ArrayList<>(type.getLore());
            lore.add("");
            lore.add("§8[Cebo Biológico Especial]");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(baitKey, PersistentDataType.STRING, type.name());
            item.setItemMeta(meta);
        }
        return item;
    }

    public BaitType getBaitFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        String val = item.getItemMeta().getPersistentDataContainer().get(baitKey, PersistentDataType.STRING);
        if (val == null) return null;
        try {
            return BaitType.valueOf(val);
        } catch (Exception e) {
            return null;
        }
    }

    public void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new BaitHolder(), 27, "§8🧪 Caldero Alquímico de Cebos");

        ItemStack border = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 27; i++) inv.setItem(i, border);

        int[] slots = {10, 12, 14, 16};
        BaitType[] baits = BaitType.values();

        for (int i = 0; i < baits.length && i < slots.length; i++) {
            BaitType bait = baits[i];
            ItemStack item = new ItemStack(bait.getIcon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(bait.getDisplayName());
                List<String> lore = new ArrayList<>(bait.getLore());
                lore.add("");
                lore.add("§7Precio de elaboración: §a" + plugin.getEconomyManager().format(bait.getCost()));
                lore.add("§e▶ Haz clic para sintetizar x4 unidades");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slots[i], item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof BaitHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getRawSlot();
        BaitType selected = null;
        if (slot == 10) selected = BaitType.ABYSSAL_CHUM;
        else if (slot == 12) selected = BaitType.BIOLUMINESCENT_KRILL;
        else if (slot == 14) selected = BaitType.GOLDEN_LARVA;
        else if (slot == 16) selected = BaitType.TREASURE_SCENT;

        if (selected != null) {
            if (!plugin.getEconomyManager().has(player, selected.getCost())) {
                player.sendMessage(ChatColor.RED + "✖ Necesitas " + plugin.getEconomyManager().format(selected.getCost()) + " para elaborar este cebo.");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.7f);
                return;
            }

            plugin.getEconomyManager().withdrawPlayer(player, selected.getCost());
            ItemStack baitItem = createBaitItem(selected, 4);

            if (!player.getInventory().addItem(baitItem).isEmpty()) {
                player.getWorld().dropItem(player.getLocation(), baitItem);
            }

            player.sendMessage(ChatColor.GREEN + "🧪 ¡Has elaborado x4 " + selected.getDisplayName() + ChatColor.GREEN + " con éxito!");
            player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1f, 1.2f);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof BaitHolder) {
            event.setCancelled(true);
        }
    }
}
