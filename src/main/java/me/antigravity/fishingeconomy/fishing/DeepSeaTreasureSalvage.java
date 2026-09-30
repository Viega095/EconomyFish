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
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class DeepSeaTreasureSalvage implements Listener {

    public enum SalvageTier {
        WOODEN_CHEST("§6📦 Cofre Naufragado de Madera", Material.CHEST, 5000.0, 15000.0),
        ANCIENT_LOCKBOX("§b🧰 Caja Fuerte de Hierro Ancestral", Material.IRON_BLOCK, 15000.0, 35000.0),
        ATLANTIS_VAULT("§d👑 Bóveda Dorada de la Atlántida", Material.GOLD_BLOCK, 40000.0, 100000.0);

        private final String displayName;
        private final Material icon;
        private final double minReward;
        private final double maxReward;

        SalvageTier(String displayName, Material icon, double minReward, double maxReward) {
            this.displayName = displayName;
            this.icon = icon;
            this.minReward = minReward;
            this.maxReward = maxReward;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Material getIcon() {
            return icon;
        }

        public double rollReward() {
            return minReward + (Math.random() * (maxReward - minReward));
        }
    }

    private final FishingEconomy plugin;
    private final NamespacedKey salvageKey;

    public static class SalvageHolder implements InventoryHolder {
        public final SalvageTier tier;
        public SalvageHolder(SalvageTier tier) {
            this.tier = tier;
        }
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public DeepSeaTreasureSalvage(FishingEconomy plugin) {
        this.plugin = plugin;
        this.salvageKey = new NamespacedKey(plugin, "deepsea_salvage_tier");
    }

    public ItemStack createSalvageItem(SalvageTier tier) {
        ItemStack item = new ItemStack(tier.getIcon());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(tier.getDisplayName());
            meta.setLore(Arrays.asList(
                    "§7Reliquia rescatada de las profundidades marinas.",
                    "§e▶ Haz clic derecho para abrir y desenterrar su tesoro."
            ));
            meta.getPersistentDataContainer().set(salvageKey, PersistentDataType.STRING, tier.name());
            item.setItemMeta(meta);
        }
        return item;
    }

    public SalvageTier getTierFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        String val = item.getItemMeta().getPersistentDataContainer().get(salvageKey, PersistentDataType.STRING);
        if (val == null) return null;
        try {
            return SalvageTier.valueOf(val);
        } catch (Exception e) {
            return null;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack inHand = event.getItem();
            SalvageTier tier = getTierFromItem(inHand);
            if (tier != null) {
                event.setCancelled(true);
                inHand.setAmount(inHand.getAmount() - 1);
                openSalvageGUI(event.getPlayer(), tier);
            }
        }
    }

    public void openSalvageGUI(Player player, SalvageTier tier) {
        Inventory inv = Bukkit.createInventory(new SalvageHolder(tier), 27, "§8🔱 Desbloqueo de Tesoro Hundido");

        ItemStack filler = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta fMeta = filler.getItemMeta();
        if (fMeta != null) {
            fMeta.setDisplayName(" ");
            filler.setItemMeta(fMeta);
        }
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        ItemStack unlock = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta uMeta = unlock.getItemMeta();
        if (uMeta != null) {
            uMeta.setDisplayName("§e§l✦ FORZAR CERRADURA DE PRECISIÓN ✦");
            uMeta.setLore(Arrays.asList(
                    "§7Contenedor: " + tier.getDisplayName(),
                    "",
                    "§a▶ Haz clic en la cerradura para romper el sello y reclamar el botín!"
            ));
            unlock.setItemMeta(uMeta);
        }
        inv.setItem(13, unlock);

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1f, 1f);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof SalvageHolder holder) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;

            if (event.getRawSlot() == 13) {
                double reward = holder.tier.rollReward();
                plugin.getEconomyManager().depositPlayer(player, reward);

                // Rare items
                if (holder.tier == SalvageTier.ATLANTIS_VAULT) {
                    player.getInventory().addItem(new ItemStack(Material.NETHERITE_INGOT, 1));
                    player.getInventory().addItem(new ItemStack(Material.HEART_OF_THE_SEA, 1));
                } else if (holder.tier == SalvageTier.ANCIENT_LOCKBOX) {
                    player.getInventory().addItem(new ItemStack(Material.DIAMOND, 3));
                }

                player.closeInventory();
                player.sendMessage(ChatColor.GOLD + "╔════════════════════════════════════════════════╗");
                player.sendMessage(ChatColor.GOLD + "║      " + ChatColor.YELLOW + "🔱 ¡TESORO SUBMARINO RESCATADO!" + ChatColor.GOLD + "      ║");
                player.sendMessage(ChatColor.GOLD + "╚════════════════════════════════════════════════╝");
                player.sendMessage(ChatColor.GREEN + "✓ Has obtenido: §a" + plugin.getEconomyManager().format(reward));
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof SalvageHolder) {
            event.setCancelled(true);
        }
    }
}
