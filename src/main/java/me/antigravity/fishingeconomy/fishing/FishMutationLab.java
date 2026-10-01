package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FishMutationLab implements Listener {

    public static class LabHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final FishingEconomy plugin;

    public FishMutationLab(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void openMutationGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new LabHolder(), 36, "§8🧬 §5Laboratorio de Mutación Genética §8🧬");

        for (int i = 0; i < 36; i++) {
            inv.setItem(i, createPane(Material.PURPLE_STAINED_GLASS_PANE));
        }

        // Slot 11: Muestra Genética A
        inv.setItem(11, createBtn(Material.TROPICAL_FISH, "§b🧬 Muestra Genética A",
                Arrays.asList("§7Coloca un pez en tu inventario", "§7para fusionar sus genes.", "", "§a▶ Inserta pez base")));

        // Slot 13: Catalizador Arcane
        inv.setItem(13, createBtn(Material.DRAGON_BREATH, "§d⚗️ Catalizador Mutagénico",
                Arrays.asList("§7Otorga +100% a +300% de valor extra", "§7y partículas elementales.", "", "§eCosto: $500")));

        // Slot 15: Muestra Genética B
        inv.setItem(15, createBtn(Material.PUFFERFISH, "§e🧬 Muestra Genética B",
                Arrays.asList("§7Coloca el segundo pez", "§7para combinar rasgos.", "", "§a▶ Inserta pez secundario")));

        // Slot 31: Botón de Fusión
        inv.setItem(31, createBtn(Material.NETHER_STAR, "§6⚡ SINTETIZAR HÍBRIDO MUTANTE",
                Arrays.asList("§7Fusiona los especímenes para", "§7crear un pez mutante legendario.", "", "§6▶ Haz clic para sintetizar")));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 0.8f, 1.2f);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof LabHolder) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;

            int slot = event.getRawSlot();
            if (slot == 31) {
                if (plugin.getEconomyManager().getBalance(player) < 500) {
                    player.sendMessage(ChatColor.RED + "No tienes los $500 requeridos para el catalizador mutagénico.");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.7f, 1f);
                    return;
                }

                plugin.getEconomyManager().withdrawPlayer(player, 500);

                ItemStack hybrid = createMutatedFish();
                player.getInventory().addItem(hybrid);

                player.closeInventory();
                player.sendTitle("§d🧬 ¡MUTACIÓN EXITOSA!", "§aHas creado un Híbrido Quimérico", 10, 50, 15);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.8f);
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.8f, 1.4f);
                player.sendMessage(ChatColor.DARK_PURPLE + "🧬 [Laboratorio] ¡El experimento fue un éxito rotundo! Has recibido una especie mutada.");
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof LabHolder) {
            event.setCancelled(true);
        }
    }

    private ItemStack createMutatedFish() {
        ItemStack fish = new ItemStack(Material.COD);
        ItemMeta meta = fish.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d✦ QUIMERA ABISAL MUTADA ✦");
            List<String> lore = new ArrayList<>();
            lore.add("§8Especie Híbrida Sintetizada");
            lore.add("");
            lore.add("§7Rareza: §d§lMUTANTE MÍTICO");
            lore.add("§7Rasgo 1: §eEscamas de Oro Puro (x2.5 Valor)");
            lore.add("§7Rasgo 2: §bAura Bioluminiscente Abisal");
            lore.add("§7Rasgo 3: §cFlama de Lava Marina");
            lore.add("");
            lore.add("§aValor Estimado de Venta: §6$15,000");
            meta.setLore(lore);
            fish.setItemMeta(meta);
        }
        return fish;
    }

    private ItemStack createPane(Material material) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            pane.setItemMeta(meta);
        }
        return pane;
    }

    private ItemStack createBtn(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
