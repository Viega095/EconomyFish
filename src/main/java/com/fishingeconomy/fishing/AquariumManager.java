package com.fishingeconomy.fishing;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AquariumManager implements Listener {

    public static class AquariumHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final FishingEconomy plugin;
    private final Map<UUID, List<ItemStack>> playerAquariums = new HashMap<>();

    public AquariumManager(FishingEconomy plugin) {
        this.plugin = plugin;
        startTourismRevenueTask();
    }

    public List<ItemStack> getAquariumFish(UUID uuid) {
        return playerAquariums.computeIfAbsent(uuid, k -> new ArrayList<>());
    }

    public void addFishToAquarium(UUID uuid, ItemStack fishItem) {
        List<ItemStack> list = getAquariumFish(uuid);
        if (list.size() < 18) {
            list.add(fishItem.clone());
        }
    }

    private void startTourismRevenueTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<UUID, List<ItemStack>> entry : playerAquariums.entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    double totalValue = 0;
                    for (ItemStack item : entry.getValue()) {
                        FishManager.CustomFish fish = plugin.getFishManager().getFishFromItem(item);
                        if (fish != null) {
                            totalValue += fish.price;
                        }
                    }
                    if (totalValue > 0) {
                        double passiveIncome = totalValue * 0.005; // 0.5% per tick period
                        plugin.getEconomyManager().deposit(player, passiveIncome);
                        player.sendMessage(ChatColor.AQUA + "🏛 [Acuario] " + ChatColor.GREEN +
                                "¡Has recibido +" + plugin.getEconomyManager().format(passiveIncome) +
                                " de ingresos por turismo de tu acuario!");
                    }
                }
            }
        }, 1200L, 1200L); // Every 60 seconds
    }

    public void openAquariumGUI(Player player) {
        Inventory inv = Bukkit.createInventory(new AquariumHolder(), 27, ChatColor.DARK_BLUE + "🐠 Tu Gran Acuario");
        List<ItemStack> fishList = getAquariumFish(player.getUniqueId());

        for (int i = 0; i < fishList.size() && i < 18; i++) {
            inv.setItem(i, fishList.get(i).clone());
        }

        // Fill bottom row with info & control glass
        ItemStack filler = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }
        for (int i = 18; i < 27; i++) {
            inv.setItem(i, filler);
        }

        ItemStack info = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.AQUA + "✦ Estadísticas del Acuario ✦");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Peces en exhibición: " + ChatColor.YELLOW + fishList.size() + "/18");
            lore.add(ChatColor.GRAY + "Genera ingresos pasivos cada 60s");
            lore.add(ChatColor.GRAY + "basado en el valor de tus especies.");
            lore.add("");
            lore.add(ChatColor.GREEN + "▶ Clic en un pez para retirarlo.");
            lore.add(ChatColor.YELLOW + "▶ Shift-Clic en pez de tu mochila para exhibirlo.");
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inv.setItem(22, info);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof AquariumHolder)) return;

        if (!(event.getWhoClicked() instanceof Player player)) return;

        int rawSlot = event.getRawSlot();
        Inventory topInv = event.getView().getTopInventory();

        // Prevent dragging/moving border items or clicking them
        if (rawSlot >= 18 && rawSlot < 27) {
            event.setCancelled(true);
            return;
        }

        // Clicking top inventory fish display (slots 0-17)
        if (rawSlot >= 0 && rawSlot < 18) {
            event.setCancelled(true);
            ItemStack clickedItem = topInv.getItem(rawSlot);
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                // Withdraw fish from aquarium
                List<ItemStack> fishList = getAquariumFish(player.getUniqueId());
                if (rawSlot < fishList.size()) {
                    ItemStack removed = fishList.remove(rawSlot);
                    HashMap<Integer, ItemStack> left = player.getInventory().addItem(removed);
                    if (!left.isEmpty()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), removed);
                    }
                    player.sendMessage(ChatColor.YELLOW + "✔ Has retirado un pez del acuario a tu inventario.");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.3f);
                    openAquariumGUI(player); // Refresh
                }
            }
            return;
        }

        // Shift-clicking from player's bottom inventory (slots >= 27)
        if (rawSlot >= 27) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

                FishManager.CustomFish fish = plugin.getFishManager().getFishFromItem(clickedItem);
                if (fish == null) {
                    player.sendMessage(ChatColor.RED + "✖ Solo puedes colocar peces capturados especiales en el acuario.");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    return;
                }

                List<ItemStack> fishList = getAquariumFish(player.getUniqueId());
                if (fishList.size() >= 18) {
                    player.sendMessage(ChatColor.RED + "✖ Tu acuario ya ha alcanzado el límite máximo de 18 especies.");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    return;
                }

                ItemStack toAdd = clickedItem.clone();
                toAdd.setAmount(1);
                addFishToAquarium(player.getUniqueId(), toAdd);

                clickedItem.setAmount(clickedItem.getAmount() - 1);
                player.sendMessage(ChatColor.AQUA + "✔ ¡Pez " + fish.name + ChatColor.AQUA + " añadido a tu acuario!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1f, 1.2f);
                openAquariumGUI(player); // Refresh
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof AquariumHolder) {
            if (event.getRawSlots().stream().anyMatch(slot -> slot < 27)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof AquariumHolder) {
            // Safely verified
        }
    }
}
