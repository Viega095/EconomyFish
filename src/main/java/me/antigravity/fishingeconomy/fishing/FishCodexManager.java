package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FishCodexManager implements Listener {

    private final FishingEconomy plugin;
    private final Map<UUID, Map<String, Integer>> playerCatches = new ConcurrentHashMap<>();
    private final File codexFile;
    private FileConfiguration codexConfig;

    public FishCodexManager(FishingEconomy plugin) {
        this.plugin = plugin;
        this.codexFile = new File(plugin.getDataFolder(), "codex_data.yml");
        loadData();
    }

    public void loadData() {
        if (!codexFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                codexFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("No se pudo crear codex_data.yml");
            }
        }
        codexConfig = YamlConfiguration.loadConfiguration(codexFile);

        for (String uuidStr : codexConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                Map<String, Integer> map = new HashMap<>();
                for (String fishKey : codexConfig.getConfigurationSection(uuidStr).getKeys(false)) {
                    map.put(fishKey, codexConfig.getInt(uuidStr + "." + fishKey));
                }
                playerCatches.put(uuid, map);
            } catch (Exception ignored) {}
        }
    }

    public void saveDataAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            synchronized (codexConfig) {
                for (Map.Entry<UUID, Map<String, Integer>> entry : playerCatches.entrySet()) {
                    String uuidStr = entry.getKey().toString();
                    for (Map.Entry<String, Integer> fishEntry : entry.getValue().entrySet()) {
                        codexConfig.set(uuidStr + "." + fishEntry.getKey(), fishEntry.getValue());
                    }
                }
                try {
                    codexConfig.save(codexFile);
                } catch (IOException e) {
                    plugin.getLogger().warning("Error al guardar codex_data.yml: " + e.getMessage());
                }
            }
        });
    }

    public void recordCatch(Player player, String fishId) {
        UUID uuid = player.getUniqueId();
        Map<String, Integer> catches = playerCatches.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        int current = catches.getOrDefault(fishId, 0);
        catches.put(fishId, current + 1);

        if (current == 0) {
            player.sendMessage(ChatColor.GOLD + "✦ [Enciclopedia Marina] ¡Nueva especie descubierta registrada en tu Codex: " + ChatColor.AQUA + fishId + ChatColor.GOLD + "!");
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.2f);
        }

        saveDataAsync();
    }

    public int getCatchCount(UUID uuid, String fishId) {
        Map<String, Integer> catches = playerCatches.get(uuid);
        if (catches == null) return 0;
        return catches.getOrDefault(fishId, 0);
    }

    public void openCodexGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new CodexHolder(), 54, "§8📖 Enciclopedia Marina & Codex");

        Map<String, FishManager.CustomFish> allFish = plugin.getFishManager().getAllFish();
        int slot = 10;
        int discovered = 0;

        for (FishManager.CustomFish fish : allFish.values()) {
            if (slot >= 44) break;
            if (slot % 9 == 8) slot += 2;

            int count = getCatchCount(player.getUniqueId(), fish.id);
            ItemStack item;
            if (count > 0) {
                discovered++;
                item = new ItemStack(fish.material);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', fish.name) + " §a✔");
                    List<String> lore = new ArrayList<>();
                    lore.add("§7Rareza: §f" + fish.rarity);
                    lore.add("§7Capturas registradas: §e" + count);
                    lore.add("§7Precio base: §a" + plugin.getEconomyManager().format(fish.price));
                    lore.add("");
                    lore.add("§a✔ Especie catalogada en tu enciclopedia.");
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            } else {
                item = new ItemStack(Material.GRAY_DYE);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("§8??? " + fish.rarity + " Desconocido");
                    meta.setLore(Arrays.asList(
                            "§7Esta criatura aún no ha sido",
                            "§7capturada por ti.",
                            "",
                            "§c✖ No descubierta todavía."
                    ));
                    item.setItemMeta(meta);
                }
            }

            inv.setItem(slot++, item);
        }

        // Progress summary item
        ItemStack summary = new ItemStack(Material.NETHER_STAR);
        ItemMeta sMeta = summary.getItemMeta();
        if (sMeta != null) {
            double percent = allFish.isEmpty() ? 0 : ((double) discovered / allFish.size()) * 100;
            sMeta.setDisplayName("§6§lProgreso Total de la Enciclopedia");
            sMeta.setLore(Arrays.asList(
                    "§7Especies descubiertas: §e" + discovered + " §7/ §e" + allFish.size(),
                    "§7Porcentaje completado: §a" + String.format("%.1f%%", percent),
                    "",
                    "§e¡Completa la enciclopedia para obtener el",
                    "§etítulo de Maestro Pescador de los Mares!"
            ));
            summary.setItemMeta(sMeta);
        }
        inv.setItem(49, summary);

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof CodexHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof CodexHolder) {
            event.setCancelled(true);
        }
    }

    public static class CodexHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
