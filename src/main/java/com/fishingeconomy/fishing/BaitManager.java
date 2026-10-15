package com.fishingeconomy.fishing;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaitManager {

    private final FishingEconomy plugin;
    private final NamespacedKey baitKey;
    private final Map<String, BaitType> registeredBaits = new HashMap<>();

    public BaitManager(FishingEconomy plugin) {
        this.plugin = plugin;
        this.baitKey = new NamespacedKey(plugin, "fishing_bait_type");
        registerDefaultBaits();
    }

    private void registerDefaultBaits() {
        registeredBaits.put("golden_worm", new BaitType(
                "golden_worm",
                "&6Gusano Dorado",
                Material.GLOW_BERRIES,
                1.5,
                1.3,
                List.of("&7Un cebo brillante que atrae", "&7peces de mayor rareza.", "&a+50% Suerte de Rareza", "&e+30% Velocidad de Pique")
        ));

        registeredBaits.put("abyssal_chum", new BaitType(
                "abyssal_chum",
                "&5Engodo Abisal",
                Material.FERMENTED_SPIDER_EYE,
                2.2,
                1.1,
                List.of("&7Mezcla oscura impregnada del vacío.", "&d+120% Probabilidad Mítica/Legendaria", "&cAtrae peces abisales feroces")
        ));

        registeredBaits.put("starlight_krill", new BaitType(
                "starlight_krill",
                "&bKrill de Luz Estelar",
                Material.PRISMARINE_CRYSTALS,
                1.8,
                2.0,
                List.of("&7Brilla en aguas profundas y de noche.", "&b+100% Velocidad de Pique", "&a+80% Valor de Mercado")
        ));

        registeredBaits.put("kraken_blood", new BaitType(
                "kraken_blood",
                "&4Sangre de Kraken Abisal",
                Material.REDSTONE,
                2.8,
                1.5,
                List.of("&7Esencia purpúrea extraída de un titán oceánico.", "&c+150% Probabilidad de Criaturas Titánicas", "&4Atrae monstruos legendarios del fondo")
        ));

        registeredBaits.put("siren_pheromone", new BaitType(
                "siren_pheromone",
                "&dFeromona de Sirena Mística",
                Material.DRAGON_BREATH,
                2.0,
                2.5,
                List.of("&7Fragancia mágica cargada de armonía marina.", "&dReduce a la mitad la espera de pique", "&b+120% Velocidad de Carrete")
        ));

        registeredBaits.put("sea_dragon_scale", new BaitType(
                "sea_dragon_scale",
                "&3Extracto de Escamas de Dragón",
                Material.PRISMARINE_SHARD,
                3.5,
                1.8,
                List.of("&7Concentrado alquímico de escamas ancestrales.", "&6Garantiza capturas de tier ÉPICO o superior", "&e+150% Multiplicador de Economía")
        ));

        registeredBaits.put("infernal_bait", new BaitType(
                "infernal_bait",
                "&6Cebo Ígneo Volcánico",
                Material.MAGMA_CREAM,
                3.0,
                2.0,
                List.of("&7Imbuido con magma condensado.", "&cPermite pescar en lava y fuentes ígneas", "&6+200% Atracción de Tiburones de Magma")
        ));

        registeredBaits.put("void_stardust_bait", new BaitType(
                "void_stardust_bait",
                "&dCebo de Polvo de Estrellas",
                Material.AMETHYST_SHARD,
                3.8,
                2.2,
                List.of("&7Polvo cósmico brillante del vacío.", "&5Permite pescar criaturas de The End", "&d+250% Probabilidad de Mantarrayas del Vacío")
        ));

        registeredBaits.put("nebula_bait", new BaitType(
                "nebula_bait",
                "&b&lCebo de la Nebulosa Cósmica",
                Material.ECHO_SHARD,
                4.5,
                3.0,
                List.of("&7Condensación de gas estelar y energía abisal.", "&d+350% Atracción de Medusas y Peces Cósmicos", "&bPesca instantánea con vórtice estelar")
        ));
    }

    public ItemStack createBaitItem(String id, int amount) {
        BaitType type = registeredBaits.get(id);
        if (type == null) return null;

        ItemStack item = new ItemStack(type.material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', type.displayName));
            List<String> lore = new ArrayList<>();
            for (String line : type.lore) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "Colócalo en tu mano secundaria para usarlo");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(baitKey, PersistentDataType.STRING, id);
            item.setItemMeta(meta);
        }
        return item;
    }

    public BaitType getBaitFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        String id = item.getItemMeta().getPersistentDataContainer().get(baitKey, PersistentDataType.STRING);
        if (id == null) return null;
        return registeredBaits.get(id);
    }

    public BaitType consumeEquippedBait(Player player) {
        ItemStack offhand = player.getInventory().getItemInOffHand();
        BaitType type = getBaitFromItem(offhand);
        if (type != null) {
            offhand.setAmount(offhand.getAmount() - 1);
            return type;
        }
        return null;
    }

    public Map<String, BaitType> getRegisteredBaits() {
        return Collections.unmodifiableMap(registeredBaits);
    }

    public static class BaitType {
        public final String id;
        public final String displayName;
        public final Material material;
        public final double rarityMultiplier;
        public final double biteSpeedMultiplier;
        public final List<String> lore;

        public BaitType(String id, String displayName, Material material, double rarityMultiplier, double biteSpeedMultiplier, List<String> lore) {
            this.id = id;
            this.displayName = displayName;
            this.material = material;
            this.rarityMultiplier = rarityMultiplier;
            this.biteSpeedMultiplier = biteSpeedMultiplier;
            this.lore = lore;
        }
    }
}
