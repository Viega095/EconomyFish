package me.antigravity.fishingeconomy.gear;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class DivingSuitManager implements Listener {

    public static class SuitHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final FishingEconomy plugin;
    private final NamespacedKey suitKey;

    public DivingSuitManager(FishingEconomy plugin) {
        this.plugin = plugin;
        this.suitKey = new NamespacedKey(plugin, "abyssal_diving_suit");
    }

    public ItemStack createHelmet() {
        ItemStack item = new ItemStack(Material.TURTLE_HELMET);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b✦ Casco de Cristal Abisal ✦");
            meta.setLore(Arrays.asList(
                    "§7Forjado con cristales prismáticos y caparazón de tortuga.",
                    "",
                    "§6✦ Efectos Pasivos:",
                    "  §a• Visión Nocturna Submarina Infinita",
                    "  §a• Respiración Acuática Continua",
                    "  §e• +15% Probabilidad de Peces Raros en Profundidad",
                    "",
                    "§8[Equipo de Buceo Profesional]"
            ));
            meta.getPersistentDataContainer().set(suitKey, PersistentDataType.STRING, "HELMET");
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createChestplate() {
        ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b✦ Traje de Neopreno Reforzado ✦");
            meta.setLore(Arrays.asList(
                    "§7Aislante térmico de alta presión para aguas gélidas.",
                    "",
                    "§6✦ Efectos Pasivos:",
                    "  §a• Resistencia II contra Jefes Oceánicos",
                    "  §a• Inmunidad al ahogamiento",
                    "  §e• +20% Valor de Venta en Capturas",
                    "",
                    "§8[Equipo de Buceo Profesional]"
            ));
            meta.getPersistentDataContainer().set(suitKey, PersistentDataType.STRING, "CHESTPLATE");
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createBoots() {
        ItemStack item = new ItemStack(Material.NETHERITE_BOOTS);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b✦ Aletas de Impulso Abisal ✦");
            meta.setLore(Arrays.asList(
                    "§7Aletas aerodinámicas imbuídas con gracia del delfín.",
                    "",
                    "§6✦ Efectos Pasivos:",
                    "  §a• Gracia del Delfín II permanente bajo el agua",
                    "  §a• Velocidad de nado aumentada en un +40%",
                    "",
                    "§8[Equipo de Buceo Profesional]"
            ));
            meta.getPersistentDataContainer().set(suitKey, PersistentDataType.STRING, "BOOTS");
            item.setItemMeta(meta);
        }
        return item;
    }

    public void openShopGUI(Player player) {
        openSuitGUI(player);
    }

    public void openSuitGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new SuitHolder(), 36, "§8🌊 §3Equipo de Buceo Abisal §8🌊");

        ItemStack border = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 36; i++) inv.setItem(i, border);

        inv.setItem(11, createHelmet());
        inv.setItem(13, createChestplate());
        inv.setItem(15, createBoots());

        ItemStack info = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta iMeta = info.getItemMeta();
        if (iMeta != null) {
            iMeta.setDisplayName("§b§l✦ Bonificación de Set Completo");
            iMeta.setLore(Arrays.asList(
                    "§7Si equipas las 3 piezas del traje de buceo:",
                    "  §a✔ Poder del Conducto permanente",
                    "  §a✔ +35% de Doble Captura automática",
                    "  §e✔ Pesca submarina desbloqueada al nadar",
                    "",
                    "§eHaz clic en cada pieza para comprarla por $5,000 c/u"
            ));
            info.setItemMeta(iMeta);
        }
        inv.setItem(31, info);

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof SuitHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();

        double price = 5000.0;
        if (slot == 11) purchasePiece(player, createHelmet(), price);
        else if (slot == 13) purchasePiece(player, createChestplate(), price);
        else if (slot == 15) purchasePiece(player, createBoots(), price);
    }

    private void purchasePiece(Player player, ItemStack item, double cost) {
        if (plugin.getEconomyManager().has(player.getUniqueId(), cost)) {
            plugin.getEconomyManager().withdraw(player.getUniqueId(), cost);
            player.getInventory().addItem(item);
            player.sendMessage(ChatColor.GREEN + "✔ ¡Has adquirido una pieza del traje de buceo por " + plugin.getEconomyManager().format(cost) + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
        } else {
            player.sendMessage(ChatColor.RED + "✖ No tienes suficiente dinero. Necesitas " + plugin.getEconomyManager().format(cost));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isInWater()) {
            ItemStack helmet = player.getInventory().getHelmet();
            ItemStack chest = player.getInventory().getChestplate();
            ItemStack boots = player.getInventory().getBoots();

            boolean hasH = helmet != null && helmet.hasItemMeta() && helmet.getItemMeta().getPersistentDataContainer().has(suitKey, PersistentDataType.STRING);
            boolean hasC = chest != null && chest.hasItemMeta() && chest.getItemMeta().getPersistentDataContainer().has(suitKey, PersistentDataType.STRING);
            boolean hasB = boots != null && boots.hasItemMeta() && boots.getItemMeta().getPersistentDataContainer().has(suitKey, PersistentDataType.STRING);

            if (hasH) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 100, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0, false, false));
            }
            if (hasC) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false));
            }
            if (hasB) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 100, 1, false, false));
            }
            if (hasH && hasC && hasB) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 100, 0, false, false));
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof SuitHolder) {
            event.setCancelled(true);
        }
    }
}
