package me.antigravity.fishingeconomy.gui;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class FishingMasteryTreeGUI implements Listener {

    public static class MasteryHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final FishingEconomy plugin;
    private final NamespacedKey keyPoints;
    private final NamespacedKey keyMagnetic;
    private final NamespacedKey keyStorm;
    private final NamespacedKey keyDoubleHook;
    private final NamespacedKey keySonar;
    private final NamespacedKey keyLegends;

    public FishingMasteryTreeGUI(FishingEconomy plugin) {
        this.plugin = plugin;
        this.keyPoints = new NamespacedKey(plugin, "mastery_points");
        this.keyMagnetic = new NamespacedKey(plugin, "mastery_magnetic");
        this.keyStorm = new NamespacedKey(plugin, "mastery_storm");
        this.keyDoubleHook = new NamespacedKey(plugin, "mastery_double_hook");
        this.keySonar = new NamespacedKey(plugin, "mastery_sonar");
        this.keyLegends = new NamespacedKey(plugin, "mastery_legends");
    }

    public int getMasteryPoints(Player player) {
        return player.getPersistentDataContainer().getOrDefault(keyPoints, PersistentDataType.INTEGER, 0);
    }

    public void addMasteryPoints(Player player, int points) {
        int current = getMasteryPoints(player);
        player.getPersistentDataContainer().set(keyPoints, PersistentDataType.INTEGER, current + points);
    }

    public boolean hasMastery(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, 0) > 0;
    }

    public void unlockMastery(Player player, NamespacedKey key, int cost) {
        int points = getMasteryPoints(player);
        if (points < cost) {
            player.sendMessage(ChatColor.RED + "✖ No tienes suficientes Puntos de Maestría. Requiere " + cost + " pts.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        player.getPersistentDataContainer().set(keyPoints, PersistentDataType.INTEGER, points - cost);
        player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        player.sendMessage(ChatColor.GREEN + "✨ ¡Maestría Marina Desbloqueada con Éxito!");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.4f);
        open(player);
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new MasteryHolder(), 45, "§8🔱 §bÁrbol de Maestrías de Pesca §8🔱");

        // Background
        ItemStack border = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 45; i++) inv.setItem(i, border);

        int points = getMasteryPoints(player);

        // Header Info (Slot 4)
        ItemStack info = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta iMeta = info.getItemMeta();
        if (iMeta != null) {
            iMeta.setDisplayName("§b§l✦ MAESTRÍAS OCEÁNICAS ✦");
            iMeta.setLore(Arrays.asList(
                    "§7Gana Puntos de Maestría capturando peces épicos y míticos.",
                    "",
                    "§6✦ Tus Puntos Disponibles: §e" + points + " pts",
                    "§7Haz clic en las ramas para desbloquear talentos permanentes."
            ));
            info.setItemMeta(iMeta);
        }
        inv.setItem(4, info);

        // Mastery 1: Carrete Magnético (Slot 20)
        inv.setItem(20, createMasteryItem(
                Material.COMPASS, "§e🧲 Carrete Magnético",
                "§7Atrae automáticamente reliquias y cajas de rescate marino al pescar.",
                1, hasMastery(player, keyMagnetic)
        ));

        // Mastery 2: Resistencia a Tormentas (Slot 22)
        inv.setItem(22, createMasteryItem(
                Material.WATER_BUCKET, "§b🌊 Resonancia de Tormenta",
                "§7+40% de velocidad de pique durante días de lluvia o tormenta oceánica.",
                2, hasMastery(player, keyStorm)
        ));

        // Mastery 3: Doble Anzuelo (Slot 24)
        inv.setItem(24, createMasteryItem(
                Material.TRIPWIRE_HOOK, "§a⚡ Doble Anzuelo Experto",
                "§715% de probabilidad de enganchar 2 peces en un único lanzamiento.",
                3, hasMastery(player, keyDoubleHook)
        ));

        // Mastery 4: Sonar Bioluminiscente (Slot 30)
        inv.setItem(30, createMasteryItem(
                Material.GLOW_INK_SAC, "§d👁️ Sonar de Aguas Profundas",
                "§7Aumenta en un +60% la probabilidad de atraer especies míticas del abismo.",
                4, hasMastery(player, keySonar)
        ));

        // Mastery 5: Pescador de Leyendas (Slot 32)
        inv.setItem(32, createMasteryItem(
                Material.NETHER_STAR, "§6🔱 Bendición de los Océanos",
                "§7+25% de valor económico de venta en todas tus capturas legendarias y míticas.",
                5, hasMastery(player, keyLegends)
        ));

        player.openInventory(inv);
    }

    private ItemStack createMasteryItem(Material mat, String name, String desc, int cost, boolean unlocked) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName((unlocked ? "§a✔ " : "§e") + name);
            List<String> lore = new ArrayList<>();
            lore.add(desc);
            lore.add("");
            if (unlocked) {
                lore.add("§a[MAESTRÍA DESBLOQUEADA]");
            } else {
                lore.add("§6Costo: §e" + cost + " Puntos de Maestría");
                lore.add("§e▶ Haz clic para desbloquear");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof MasteryHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getRawSlot();
        if (slot == 20 && !hasMastery(player, keyMagnetic)) unlockMastery(player, keyMagnetic, 1);
        else if (slot == 22 && !hasMastery(player, keyStorm)) unlockMastery(player, keyStorm, 2);
        else if (slot == 24 && !hasMastery(player, keyDoubleHook)) unlockMastery(player, keyDoubleHook, 3);
        else if (slot == 30 && !hasMastery(player, keySonar)) unlockMastery(player, keySonar, 4);
        else if (slot == 32 && !hasMastery(player, keyLegends)) unlockMastery(player, keyLegends, 5);
    }
}
