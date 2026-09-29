package me.antigravity.fishingeconomy.fishing;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BreedingTankManager {

    private final FishingEconomy plugin;

    public enum HybridBreed {
        RAINBOW_TROUT("§d§l✦ Trucha Prisma Arcoíris ✦", 15000.0, Material.TROPICAL_FISH,
                List.of("§7Pez híbrido mutado genéticamente.", "§dRareza: MÍTICA EXÓTICA", "§eValor de venta x5.0")),
        ABYSSAL_EEL("§5§l✦ Anguila Abisal del Vacío ✦", 22000.0, Material.PUFFERFISH,
                List.of("§7Genera descargas eléctricas abisales.", "§5Rareza: CÓSMICA", "§eValor de venta x7.5")),
        CELESTIAL_KOI("§b§l✦ Koi Astral Luminiscente ✦", 40000.0, Material.SALMON,
                List.of("§7Brilla con la bendición de las constelaciones.", "§bRareza: DIVINA", "§eValor de venta x12.0"));

        public final String name;
        public final double marketValue;
        public final Material material;
        public final List<String> lore;

        HybridBreed(String name, double marketValue, Material material, List<String> lore) {
            this.name = name;
            this.marketValue = marketValue;
            this.material = material;
            this.lore = lore;
        }
    }

    public BreedingTankManager(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public boolean breedFish(Player player, ItemStack fishA, ItemStack fishB) {
        if (fishA == null || fishB == null || fishA.getType().isAir() || fishB.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "✖ Debes colocar 2 peces válidos en el tanque de cría.");
            return false;
        }

        HybridBreed[] hybrids = HybridBreed.values();
        HybridBreed offspring = hybrids[ThreadLocalRandom.current().nextInt(hybrids.length)];

        ItemStack hybridItem = new ItemStack(offspring.material);
        ItemMeta meta = hybridItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(offspring.name);
            meta.setLore(offspring.lore);
            hybridItem.setItemMeta(meta);
        }

        fishA.setAmount(fishA.getAmount() - 1);
        fishB.setAmount(fishB.getAmount() - 1);

        player.getInventory().addItem(hybridItem);

        player.sendMessage(ChatColor.GOLD + "🧬 [Acuicultura Genética] ¡El cruce ha producido: " + offspring.name + ChatColor.GOLD + "!");
        player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1f, 1.3f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.8f);
        return true;
    }
}
