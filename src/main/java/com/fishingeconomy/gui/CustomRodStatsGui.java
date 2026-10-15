package com.fishingeconomy.gui;

import com.fishingeconomy.FishingEconomy;
import com.fishingeconomy.fishing.BaitManager;
import com.fishingeconomy.fishing.FishManager;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CustomRodStatsGui implements Listener {

    public static class RodStatsHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final FishingEconomy plugin;
    public final NamespacedKey keyCatches;
    public final NamespacedKey keyMaxWeight;
    public final NamespacedKey keyLevel;
    public final NamespacedKey keyExp;
    public final NamespacedKey keyUpgradeSpeed;
    public final NamespacedKey keyUpgradeTension;
    public final NamespacedKey keyUpgradeLuck;
    public final NamespacedKey keyUpgradeSell;

    public CustomRodStatsGui(FishingEconomy plugin) {
        this.plugin = plugin;
        this.keyCatches = new NamespacedKey(plugin, "rod_catches_count");
        this.keyMaxWeight = new NamespacedKey(plugin, "rod_max_weight");
        this.keyLevel = new NamespacedKey(plugin, "rod_marine_level");
        this.keyExp = new NamespacedKey(plugin, "rod_marine_exp");
        this.keyUpgradeSpeed = new NamespacedKey(plugin, "rod_upgrade_reel_speed");
        this.keyUpgradeTension = new NamespacedKey(plugin, "rod_upgrade_tension");
        this.keyUpgradeLuck = new NamespacedKey(plugin, "rod_upgrade_luck");
        this.keyUpgradeSell = new NamespacedKey(plugin, "rod_upgrade_sell_bonus");
    }

    public void updateRodStats(ItemStack rod, FishManager.CustomFish caughtFish) {
        if (rod == null || rod.getType() != Material.FISHING_ROD || caughtFish == null) return;
        ItemMeta meta = rod.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        int catches = pdc.getOrDefault(keyCatches, PersistentDataType.INTEGER, 0) + 1;
        double currentMax = pdc.getOrDefault(keyMaxWeight, PersistentDataType.DOUBLE, 0.0);
        double fishWeight = (caughtFish.price * 0.12) + (Math.random() * 4.5);
        double maxWeight = Math.max(currentMax, fishWeight);
        int exp = pdc.getOrDefault(keyExp, PersistentDataType.INTEGER, 0) + (int) (caughtFish.price / 10) + 5;
        int level = pdc.getOrDefault(keyLevel, PersistentDataType.INTEGER, 1);

        int requiredExp = level * 100;
        if (exp >= requiredExp) {
            level++;
            exp -= requiredExp;
        }

        pdc.set(keyCatches, PersistentDataType.INTEGER, catches);
        pdc.set(keyMaxWeight, PersistentDataType.DOUBLE, maxWeight);
        pdc.set(keyExp, PersistentDataType.INTEGER, exp);
        pdc.set(keyLevel, PersistentDataType.INTEGER, level);

        rod.setItemMeta(meta);
    }

    public void open(Player player) {
        ItemStack rod = getPlayerRod(player);
        if (rod == null) {
            player.sendMessage(ChatColor.RED + "✖ Debes tener una caña de pescar en la mano para ver y mejorar sus atributos.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        Inventory inv = Bukkit.createInventory(new RodStatsHolder(), 54, "§8🎣 §bEstadísticas & Mejoras de Caña §8🎣");

        // Border
        ItemStack pane = createPane(Material.CYAN_STAINED_GLASS_PANE);
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, pane);
            }
        }

        ItemMeta meta = rod.getItemMeta();
        int catches = 0;
        double maxWeight = 0.0;
        int level = 1;
        int exp = 0;
        int upSpeed = 0;
        int upTension = 0;
        int upLuck = 0;
        int upSell = 0;

        if (meta != null) {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            catches = pdc.getOrDefault(keyCatches, PersistentDataType.INTEGER, 0);
            maxWeight = pdc.getOrDefault(keyMaxWeight, PersistentDataType.DOUBLE, 0.0);
            level = pdc.getOrDefault(keyLevel, PersistentDataType.INTEGER, 1);
            exp = pdc.getOrDefault(keyExp, PersistentDataType.INTEGER, 0);
            upSpeed = pdc.getOrDefault(keyUpgradeSpeed, PersistentDataType.INTEGER, 0);
            upTension = pdc.getOrDefault(keyUpgradeTension, PersistentDataType.INTEGER, 0);
            upLuck = pdc.getOrDefault(keyUpgradeLuck, PersistentDataType.INTEGER, 0);
            upSell = pdc.getOrDefault(keyUpgradeSell, PersistentDataType.INTEGER, 0);
        }

        int reqExp = level * 100;

        // Check equipped bait in offhand
        ItemStack offhand = player.getInventory().getItemInOffHand();
        BaitManager.BaitType equippedBait = plugin.getBaitManager() != null ? plugin.getBaitManager().getBaitFromItem(offhand) : null;
        String baitStatus = equippedBait != null ? "§a" + ChatColor.translateAlternateColorCodes('&', equippedBait.displayName) + " §7(x" + offhand.getAmount() + ")" : "§cNinguno equipado en mano secundaria";

        // Slot 13: Central Rod Display
        ItemStack displayRod = rod.clone();
        ItemMeta dMeta = displayRod.getItemMeta();
        if (dMeta != null) {
            List<String> lore = dMeta.hasLore() && dMeta.getLore() != null ? new ArrayList<>(dMeta.getLore()) : new ArrayList<>();
            lore.add("");
            lore.add("§6══════════ §eEstadísticas de la Caña §6══════════");
            lore.add("§7✦ Nivel Marino de Caña: §eNivel " + level);
            lore.add("§7✦ Experiencia de Caña: §b" + exp + "/" + reqExp + " EXP");
            lore.add("§7✦ Peces Totales Capturados: §a" + catches + " capturas");
            lore.add("§7✦ Mayor Récord de Peso: §d" + String.format(Locale.US, "%.2f", maxWeight) + " kg");
            lore.add("§7✦ Cebo en Mano Secundaria: " + baitStatus);
            lore.add("§6─────── §eMejoras Activas §6───────");
            lore.add("§7⚡ Velocidad de Carrete: §eNivel " + upSpeed + "/5");
            lore.add("§7🧵 Tensión del Hilo: §eNivel " + upTension + "/5");
            lore.add("§7🌊 Suerte del Abismo: §eNivel " + upLuck + "/5");
            lore.add("§7💰 Multiplicador de Venta: §eNivel " + upSell + "/5");
            lore.add("§6══════════════════════════════════════");
            dMeta.setLore(lore);
            displayRod.setItemMeta(dMeta);
        }
        inv.setItem(13, displayRod);

        // Bait Fast-Loader Buttons: Slots 19, 20, 21, 23, 24, 25
        inv.setItem(19, createBaitBtn("golden_worm", "§6🪱 Gusano Dorado", Arrays.asList("§7Rareza: §a+50% Suerte", "§7Velocidad: §e+30% Pique", "", "§e▶ Clic para equipar x16 en mano secundaria")));
        inv.setItem(20, createBaitBtn("abyssal_chum", "§5🪱 Engodo Abisal", Arrays.asList("§7Rareza: §d+120% Prob. Mítica", "§7Atracción de Abismos", "", "§e▶ Clic para equipar x16 en mano secundaria")));
        inv.setItem(21, createBaitBtn("starlight_krill", "§b🪱 Krill Luz Estelar", Arrays.asList("§7Velocidad: §b+100% Pique Rápido", "§7Economía: §a+80% Valor", "", "§e▶ Clic para equipar x16 en mano secundaria")));
        inv.setItem(23, createBaitBtn("kraken_blood", "§4🪱 Sangre de Kraken", Arrays.asList("§7Titanes: §c+150% Prob. Jefes", "§7Atracción de Megalodón", "", "§e▶ Clic para equipar x16 en mano secundaria")));
        inv.setItem(24, createBaitBtn("siren_pheromone", "§d🪱 Feromona de Sirena", Arrays.asList("§7Armonía: §dMitad tiempo de carrete", "§7Pique: §b+120% Velocidad", "", "§e▶ Clic para equipar x16 en mano secundaria")));
        inv.setItem(25, createBaitBtn("sea_dragon_scale", "§3🪱 Escama de Dragón", Arrays.asList("§7Calidad: §6Peces Épico+ Garantizados", "§7Economía: §e+150% Multiplicador", "", "§e▶ Clic para equipar x16 en mano secundaria")));

        // Upgrade Interactive Nodes: Slots 37, 39, 41, 43
        inv.setItem(37, createUpgradeBtn(Material.BLAZE_POWDER, "§e⚡ Mejora: Velocidad de Carrete",
                upSpeed, (upSpeed + 1) * 5000.0, "§7Aumenta la rapidez del tirón del hilo (+10% por nivel)."));

        inv.setItem(39, createUpgradeBtn(Material.LEAD, "§a🧵 Mejora: Tensión del Hilo",
                upTension, (upTension + 1) * 6000.0, "§7Mayor tolerancia ante tirones bruscos (+15% estabilidad)."));

        inv.setItem(41, createUpgradeBtn(Material.PRISMARINE_CRYSTALS, "§b🌊 Mejora: Suerte del Abismo",
                upLuck, (upLuck + 1) * 8000.0, "§7Incrementa la probabilidad de peces míticos y cósmicos (+12%)."));

        inv.setItem(43, createUpgradeBtn(Material.GOLD_INGOT, "§6💰 Mejora: Multiplicador de Venta",
                upSell, (upSell + 1) * 10000.0, "§7Aumenta el valor de mercado al vender capturas (+20% por nivel)."));

        // Shortcuts
        inv.setItem(47, createBtn(Material.ANVIL, "§e🔨 Astillero de Cañas", Arrays.asList("§7Forja cañas superiores con", "§7habilidades pasivas permanentes.", "", "§e▶ Clic para abrir")));
        inv.setItem(49, createBtn(Material.ENCHANTING_TABLE, "§d✨ Altar de Encantamientos", Arrays.asList("§7Imbuye tu caña con encantamientos:", "§7Llamada Abisal, Sonar, Resonancia.", "", "§d▶ Clic para abrir")));
        inv.setItem(51, createBtn(Material.CAULDRON, "§a🧪 Caldero de Cebos", Arrays.asList("§7Crea y fabrica nuevos cebos", "§7alquímicos con ingredientes marinos.", "", "§a▶ Clic para abrir")));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 1.2f);
    }

    private ItemStack getPlayerRod(Player player) {
        ItemStack main = player.getInventory().getItemInMainHand();
        if (main.getType() == Material.FISHING_ROD) return main;
        ItemStack off = player.getInventory().getItemInOffHand();
        if (off.getType() == Material.FISHING_ROD) return off;
        return null;
    }

    private ItemStack createUpgradeBtn(Material mat, String name, int currentLevel, double cost, String desc) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>();
            lore.add(desc);
            lore.add("");
            lore.add("§7Nivel actual: §e" + currentLevel + "/5");
            if (currentLevel >= 5) {
                lore.add("§a✦ ¡NIVEL MÁXIMO ALCANZADO!");
            } else {
                lore.add("§7Costo de mejora: §a" + plugin.getEconomyManager().format(cost));
                lore.add("§e▶ Haz clic para mejorar a Nivel " + (currentLevel + 1));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createBaitBtn(String baitId, String name, List<String> lore) {
        BaitManager.BaitType type = plugin.getBaitManager() != null ? plugin.getBaitManager().createBaitItem(baitId, 1) != null ? plugin.getBaitManager().getBaitFromItem(plugin.getBaitManager().createBaitItem(baitId, 1)) : null : null;
        Material mat = type != null ? type.material : Material.GLOW_BERRIES;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createBtn(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createPane(Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof RodStatsHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getRawSlot();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, 1f);

        // Equip Baits
        if (slot == 19) equipBaitToPlayer(player, "golden_worm");
        else if (slot == 20) equipBaitToPlayer(player, "abyssal_chum");
        else if (slot == 21) equipBaitToPlayer(player, "starlight_krill");
        else if (slot == 23) equipBaitToPlayer(player, "kraken_blood");
        else if (slot == 24) equipBaitToPlayer(player, "siren_pheromone");
        else if (slot == 25) equipBaitToPlayer(player, "sea_dragon_scale");

        // Upgrades
        else if (slot == 37) handleUpgrade(player, keyUpgradeSpeed, "Velocidad de Carrete", 5000.0);
        else if (slot == 39) handleUpgrade(player, keyUpgradeTension, "Tensión del Hilo", 6000.0);
        else if (slot == 41) handleUpgrade(player, keyUpgradeLuck, "Suerte del Abismo", 8000.0);
        else if (slot == 43) handleUpgrade(player, keyUpgradeSell, "Multiplicador de Venta", 10000.0);

        // Shortcuts
        else if (slot == 47) {
            player.closeInventory();
            plugin.getCustomRodGui().open(player);
        } else if (slot == 49) {
            player.closeInventory();
            plugin.getRodEnchantManager().openEnchantGUI(player);
        } else if (slot == 51) {
            player.closeInventory();
            plugin.getBaitCraftingStation().openGUI(player);
        }
    }

    private void handleUpgrade(Player player, NamespacedKey key, String name, double baseCost) {
        ItemStack rod = getPlayerRod(player);
        if (rod == null) return;
        ItemMeta meta = rod.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        int currentLevel = pdc.getOrDefault(key, PersistentDataType.INTEGER, 0);

        if (currentLevel >= 5) {
            player.sendMessage(ChatColor.YELLOW + "✦ ¡Esta mejora ya ha alcanzado el nivel máximo (5/5)!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        double cost = (currentLevel + 1) * baseCost;
        if (!plugin.getEconomyManager().has(player, cost)) {
            player.sendMessage(ChatColor.RED + "✖ Fondos insuficientes para mejorar " + name + " (" + plugin.getEconomyManager().format(cost) + ").");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.7f);
            return;
        }

        plugin.getEconomyManager().withdrawPlayer(player, cost);
        int nextLevel = currentLevel + 1;
        pdc.set(key, PersistentDataType.INTEGER, nextLevel);
        rod.setItemMeta(meta);

        player.sendMessage(ChatColor.GREEN + "⚡ ¡Has mejorado " + ChatColor.GOLD + name + ChatColor.GREEN + " al Nivel " + nextLevel + "!");
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.4f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.8f);

        open(player); // Refresh
    }

    private void equipBaitToPlayer(Player player, String baitId) {
        if (plugin.getBaitManager() == null) return;
        ItemStack baitItem = plugin.getBaitManager().createBaitItem(baitId, 16);
        if (baitItem != null) {
            player.getInventory().setItemInOffHand(baitItem);
            player.sendMessage(ChatColor.GREEN + "🪱 ¡Cebo " + ChatColor.GOLD + baitId + ChatColor.GREEN + " equipado en tu mano secundaria (x16)!");
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1f, 1.4f);
            open(player);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof RodStatsHolder) {
            event.setCancelled(true);
        }
    }
}
