package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AquariumManager {

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
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_BLUE + "🐠 Tu Gran Acuario");
        List<ItemStack> fishList = getAquariumFish(player.getUniqueId());

        for (int i = 0; i < fishList.size() && i < 18; i++) {
            inv.setItem(i, fishList.get(i));
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
            lore.add(ChatColor.GRAY + "basado en la rareza de tus especies.");
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inv.setItem(22, info);

        player.openInventory(inv);
    }
}
