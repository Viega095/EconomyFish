package com.fishingeconomy.multiblock;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FishMultiblockManager {

    private final FishingEconomy plugin;
    public final NamespacedKey STRUCTURE_PDC_KEY;

    private final Map<FishMultiblockType, FishMultiblockTemplate> templates = new EnumMap<>(FishMultiblockType.class);
    private final Map<UUID, ActiveFishMultiblock> activeStructures = new HashMap<>();
    private final Map<Location, ActiveFishMultiblock> blockToStructure = new HashMap<>();

    private File structuresFile;
    private FileConfiguration structuresConfig;

    public FishMultiblockManager(FishingEconomy plugin) {
        this.plugin = plugin;
        this.STRUCTURE_PDC_KEY = new NamespacedKey(plugin, "fish_structure");

        // Initialize templates
        templates.put(FishMultiblockType.BAIT_STATION, new FishMultiblockTemplate(FishMultiblockType.BAIT_STATION, 1.25));
        templates.put(FishMultiblockType.MAGIC_AQUARIUM, new FishMultiblockTemplate(FishMultiblockType.MAGIC_AQUARIUM, 2.35));
        templates.put(FishMultiblockType.BIODOME_AQUARIUM, new FishMultiblockTemplate(FishMultiblockType.BIODOME_AQUARIUM, 2.85));
        templates.put(FishMultiblockType.DEEP_SEA_DREDGER, new FishMultiblockTemplate(FishMultiblockType.DEEP_SEA_DREDGER, 2.50));
        templates.put(FishMultiblockType.CORAL_REEF_BIODOME, new FishMultiblockTemplate(FishMultiblockType.CORAL_REEF_BIODOME, 2.85));

        loadStructures();
        registerRecipes();
    }

    public ItemStack createStructureItem(FishMultiblockType type) {
        ItemStack item = new ItemStack(type.getItemMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(type.getDisplayName());
            meta.setLore(type.getLore());

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(STRUCTURE_PDC_KEY, PersistentDataType.STRING, type.name());

            item.setItemMeta(meta);
        }
        return item;
    }

    public FishMultiblockType getStructureTypeFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String val = pdc.get(STRUCTURE_PDC_KEY, PersistentDataType.STRING);
        if (val == null) return null;
        try {
            return FishMultiblockType.valueOf(val);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean canPlaceStructure(Location origin, FishMultiblockType type) {
        FishMultiblockTemplate template = templates.get(type);
        if (template == null) return false;
        return template.canBuildAt(origin);
    }

    public ActiveFishMultiblock placeStructure(Player player, Location origin, FishMultiblockType type) {
        FishMultiblockTemplate template = templates.get(type);
        if (template == null) return null;

        Location coreLoc = normalizeLocation(origin);
        UUID instanceId = UUID.randomUUID();
        ActiveFishMultiblock active = new ActiveFishMultiblock(instanceId, type, coreLoc, player != null ? player.getUniqueId() : null);

        // Build physical blocks
        Set<Location> placedLocs = template.build(coreLoc);
        for (Location loc : placedLocs) {
            Location norm = normalizeLocation(loc);
            active.addConstituentBlock(norm);
            blockToStructure.put(norm, active);
        }

        // Spawn Hologram Display
        World world = coreLoc.getWorld();
        if (world != null) {
            Location holoLoc = coreLoc.clone().add(0.5, template.getHologramOffsetY(), 0.5);
            ArmorStand stand = (ArmorStand) world.spawnEntity(holoLoc, org.bukkit.entity.EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setCanPickupItems(false);
            stand.setCustomName(type.getHologramTitle());
            stand.setCustomNameVisible(true);
            stand.setMarker(true);
            stand.setSmall(true);
            active.addEntityUuid(stand.getUniqueId());
        }

        activeStructures.put(instanceId, active);
        saveStructures();

        if (world != null) {
            world.playSound(coreLoc, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.2f);
            world.playEffect(coreLoc, Effect.ENDER_SIGNAL, 0);
        }

        return active;
    }

    public boolean dismantleStructure(Location brokenBlockLoc, Player player) {
        Location norm = normalizeLocation(brokenBlockLoc);
        ActiveFishMultiblock active = blockToStructure.get(norm);
        if (active == null) return false;

        // Remove all constituent blocks cleanly to AIR
        for (Location bLoc : active.getConstituentBlocks()) {
            if (bLoc.getWorld() != null) {
                bLoc.getBlock().setType(Material.AIR, false);
            }
            blockToStructure.remove(bLoc);
        }

        // Remove all hologram entities
        active.removeAllEntities();

        // Remove from tracking
        activeStructures.remove(active.getInstanceId());
        saveStructures();

        // Reconcile and refund EXACTLY ONE original craftable PDC item - ZERO DUPLICATION
        ItemStack refund = createStructureItem(active.getType());
        if (player != null && player.isOnline()) {
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(refund);
            if (!leftover.isEmpty() && active.getCoreLocation().getWorld() != null) {
                active.getCoreLocation().getWorld().dropItemNaturally(active.getCoreLocation(), refund);
            }
            player.sendMessage(ChatColor.YELLOW + "✦ Estructura " + active.getType().getDisplayName() +
                    ChatColor.YELLOW + " desmontada limpiamente. ¡Ítem reembolsado!");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 0.7f, 1.5f);
        } else if (active.getCoreLocation().getWorld() != null) {
            active.getCoreLocation().getWorld().dropItemNaturally(active.getCoreLocation(), refund);
        }

        return true;
    }

    public boolean isPartOfStructure(Location loc) {
        if (loc == null) return false;
        return blockToStructure.containsKey(normalizeLocation(loc));
    }

    public ActiveFishMultiblock getStructureAt(Location loc) {
        if (loc == null) return null;
        return blockToStructure.get(normalizeLocation(loc));
    }

    public Map<UUID, ActiveFishMultiblock> getActiveStructures() {
        return Collections.unmodifiableMap(activeStructures);
    }

    public Location normalizeLocation(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public void registerRecipes() {
        // Recipe 1: Estación de Cebo
        NamespacedKey baitStationKey = new NamespacedKey(plugin, "fish_recipe_bait_station");
        Bukkit.removeRecipe(baitStationKey);
        ShapedRecipe baitStationRecipe = new ShapedRecipe(baitStationKey, createStructureItem(FishMultiblockType.BAIT_STATION));
        baitStationRecipe.shape("PCP", "KHK", "BSB");
        baitStationRecipe.setIngredient('P', Material.PRISMARINE_CRYSTALS);
        baitStationRecipe.setIngredient('C', Material.CAULDRON);
        baitStationRecipe.setIngredient('K', Material.KELP);
        baitStationRecipe.setIngredient('H', Material.HEART_OF_THE_SEA);
        baitStationRecipe.setIngredient('B', Material.PRISMARINE_BRICKS);
        baitStationRecipe.setIngredient('S', Material.SOUL_LANTERN);
        Bukkit.addRecipe(baitStationRecipe);

        // Recipe 2: Acuario Mágico / Banco de Fileteado
        NamespacedKey aquariumKey = new NamespacedKey(plugin, "fish_recipe_magic_aquarium");
        Bukkit.removeRecipe(aquariumKey);
        ShapedRecipe aquariumRecipe = new ShapedRecipe(aquariumKey, createStructureItem(FishMultiblockType.MAGIC_AQUARIUM));
        aquariumRecipe.shape("GGG", "GHG", "BSB");
        aquariumRecipe.setIngredient('G', Material.CYAN_STAINED_GLASS);
        aquariumRecipe.setIngredient('H', Material.HEART_OF_THE_SEA);
        aquariumRecipe.setIngredient('B', Material.PRISMARINE_BRICKS);
        aquariumRecipe.setIngredient('S', Material.SMOKER);
        Bukkit.addRecipe(aquariumRecipe);

        // Recipe 3: Acuario Domo Abisal
        NamespacedKey biodomeKey = new NamespacedKey(plugin, "fish_recipe_biodome_aquarium");
        Bukkit.removeRecipe(biodomeKey);
        ShapedRecipe biodomeRecipe = new ShapedRecipe(biodomeKey, createStructureItem(FishMultiblockType.BIODOME_AQUARIUM));
        biodomeRecipe.shape("TCT", "PHP", "DSD");
        biodomeRecipe.setIngredient('T', Material.TINTED_GLASS);
        biodomeRecipe.setIngredient('C', Material.CONDUIT);
        biodomeRecipe.setIngredient('P', Material.DARK_PRISMARINE);
        biodomeRecipe.setIngredient('H', Material.HEART_OF_THE_SEA);
        biodomeRecipe.setIngredient('D', Material.DIAMOND);
        biodomeRecipe.setIngredient('S', Material.SEA_LANTERN);
        Bukkit.addRecipe(biodomeRecipe);

        // Recipe 4: Draga Abisal de Aguas Profundas
        NamespacedKey dredgerKey = new NamespacedKey(plugin, "fish_recipe_deep_sea_dredger");
        Bukkit.removeRecipe(dredgerKey);
        ShapedRecipe dredgerRecipe = new ShapedRecipe(dredgerKey, createStructureItem(FishMultiblockType.DEEP_SEA_DREDGER));
        dredgerRecipe.shape("ICI", "PHP", "BSB");
        dredgerRecipe.setIngredient('I', Material.IRON_BLOCK);
        dredgerRecipe.setIngredient('C', Material.CHAIN);
        dredgerRecipe.setIngredient('P', Material.DARK_PRISMARINE);
        dredgerRecipe.setIngredient('H', Material.HOPPER);
        dredgerRecipe.setIngredient('B', Material.PRISMARINE_BRICKS);
        dredgerRecipe.setIngredient('S', Material.SOUL_LANTERN);
        Bukkit.addRecipe(dredgerRecipe);

        // Recipe 5: Biodomo de Arrecife Coralino
        NamespacedKey reefKey = new NamespacedKey(plugin, "fish_recipe_coral_reef_biodome");
        Bukkit.removeRecipe(reefKey);
        ShapedRecipe reefRecipe = new ShapedRecipe(reefKey, createStructureItem(FishMultiblockType.CORAL_REEF_BIODOME));
        reefRecipe.shape("GCG", "PBP", "DSD");
        reefRecipe.setIngredient('G', Material.LIGHT_BLUE_STAINED_GLASS);
        reefRecipe.setIngredient('C', Material.BRAIN_CORAL_BLOCK);
        reefRecipe.setIngredient('P', Material.PRISMARINE_BRICKS);
        reefRecipe.setIngredient('B', Material.HEART_OF_THE_SEA);
        reefRecipe.setIngredient('D', Material.DIAMOND);
        reefRecipe.setIngredient('S', Material.SEA_LANTERN);
        Bukkit.addRecipe(reefRecipe);
    }

    public void loadStructures() {
        structuresFile = new File(plugin.getDataFolder(), "structures.yml");
        if (!structuresFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                structuresFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("No se pudo crear structures.yml: " + e.getMessage());
            }
        }

        structuresConfig = YamlConfiguration.loadConfiguration(structuresFile);
        ConfigurationSection sec = structuresConfig.getConfigurationSection("structures");
        if (sec == null) return;

        blockToStructure.clear();
        activeStructures.clear();

        for (String idStr : sec.getKeys(false)) {
            try {
                UUID id = UUID.fromString(idStr);
                String typeName = sec.getString(idStr + ".type");
                FishMultiblockType type = FishMultiblockType.valueOf(typeName);

                String worldName = sec.getString(idStr + ".world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) continue;

                int cx = sec.getInt(idStr + ".coreX");
                int cy = sec.getInt(idStr + ".coreY");
                int cz = sec.getInt(idStr + ".coreZ");
                Location coreLoc = new Location(world, cx, cy, cz);

                String ownerStr = sec.getString(idStr + ".owner");
                UUID owner = ownerStr != null ? UUID.fromString(ownerStr) : null;

                ActiveFishMultiblock active = new ActiveFishMultiblock(id, type, coreLoc, owner);

                List<String> blockList = sec.getStringList(idStr + ".blocks");
                for (String bStr : blockList) {
                    String[] parts = bStr.split(",");
                    if (parts.length == 3) {
                        int bx = Integer.parseInt(parts[0]);
                        int by = Integer.parseInt(parts[1]);
                        int bz = Integer.parseInt(parts[2]);
                        Location bLoc = new Location(world, bx, by, bz);
                        active.addConstituentBlock(bLoc);
                        blockToStructure.put(bLoc, active);
                    }
                }

                List<String> entityList = sec.getStringList(idStr + ".entities");
                for (String eStr : entityList) {
                    active.addEntityUuid(UUID.fromString(eStr));
                }

                activeStructures.put(id, active);
            } catch (Exception e) {
                plugin.getLogger().warning("Error cargando estructura " + idStr + ": " + e.getMessage());
            }
        }
    }

    public void saveStructures() {
        if (structuresConfig == null || structuresFile == null) return;

        structuresConfig.set("structures", null);

        for (Map.Entry<UUID, ActiveFishMultiblock> entry : activeStructures.entrySet()) {
            String path = "structures." + entry.getKey().toString();
            ActiveFishMultiblock active = entry.getValue();

            structuresConfig.set(path + ".type", active.getType().name());
            structuresConfig.set(path + ".world", active.getCoreLocation().getWorld().getName());
            structuresConfig.set(path + ".coreX", active.getCoreLocation().getBlockX());
            structuresConfig.set(path + ".coreY", active.getCoreLocation().getBlockY());
            structuresConfig.set(path + ".coreZ", active.getCoreLocation().getBlockZ());
            if (active.getOwnerUuid() != null) {
                structuresConfig.set(path + ".owner", active.getOwnerUuid().toString());
            }

            List<String> blockList = new ArrayList<>();
            for (Location loc : active.getConstituentBlocks()) {
                blockList.add(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
            }
            structuresConfig.set(path + ".blocks", blockList);

            List<String> entityList = new ArrayList<>();
            for (UUID eId : active.getEntityUuids()) {
                entityList.add(eId.toString());
            }
            structuresConfig.set(path + ".entities", entityList);
        }

        try {
            structuresConfig.save(structuresFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Error guardando structures.yml: " + e.getMessage());
        }
    }
}
