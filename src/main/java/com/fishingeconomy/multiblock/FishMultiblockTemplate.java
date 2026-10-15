package com.fishingeconomy.multiblock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

public class FishMultiblockTemplate {

    public static class RelativeBlock {
        public final int dx;
        public final int dy;
        public final int dz;
        public final Material material;
        public final boolean isCore;

        public RelativeBlock(int dx, int dy, int dz, Material material, boolean isCore) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
            this.material = material;
            this.isCore = isCore;
        }
    }

    private final FishMultiblockType type;
    private final List<RelativeBlock> blocks = new ArrayList<>();
    private final double hologramOffsetY;

    public FishMultiblockTemplate(FishMultiblockType type, double hologramOffsetY) {
        this.type = type;
        this.hologramOffsetY = hologramOffsetY;
        initializeBlocks();
    }

    private void initializeBlocks() {
        if (type == FishMultiblockType.BAIT_STATION) {
            // Core
            blocks.add(new RelativeBlock(0, 0, 0, Material.CAULDRON, true));

            // Base at y = -1
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Material mat = (Math.abs(x) == 1 && Math.abs(z) == 1) ? Material.DARK_PRISMARINE : Material.PRISMARINE_BRICKS;
                    if (x == 0 && z == 0) mat = Material.SEA_LANTERN;
                    blocks.add(new RelativeBlock(x, -1, z, mat, false));
                }
            }

            // Barrels at 4 corners y = 0
            blocks.add(new RelativeBlock(-1, 0, -1, Material.BARREL, false));
            blocks.add(new RelativeBlock(1, 0, -1, Material.BARREL, false));
            blocks.add(new RelativeBlock(-1, 0, 1, Material.BARREL, false));
            blocks.add(new RelativeBlock(1, 0, 1, Material.BARREL, false));

            // Soul lanterns on top of barrels y = 1
            blocks.add(new RelativeBlock(-1, 1, -1, Material.SOUL_LANTERN, false));
            blocks.add(new RelativeBlock(1, 1, -1, Material.SOUL_LANTERN, false));
            blocks.add(new RelativeBlock(-1, 1, 1, Material.SOUL_LANTERN, false));
            blocks.add(new RelativeBlock(1, 1, 1, Material.SOUL_LANTERN, false));

        } else if (type == FishMultiblockType.MAGIC_AQUARIUM) {
            // Core lantern at 0, 0, 0
            blocks.add(new RelativeBlock(0, 0, 0, Material.SEA_LANTERN, true));

            // Base platform y = -1
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Material mat = (x == 0 && z == 0) ? Material.DARK_PRISMARINE : Material.PRISMARINE_BRICKS;
                    blocks.add(new RelativeBlock(x, -1, z, mat, false));
                }
            }

            // Filleting bench adjacent to core
            blocks.add(new RelativeBlock(1, 0, 0, Material.SMOKER, false));

            // Cyan glass tank enclosure around core
            blocks.add(new RelativeBlock(-1, 0, 0, Material.CYAN_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(0, 0, 1, Material.CYAN_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(0, 0, -1, Material.CYAN_STAINED_GLASS, false));

            // Tank chamber at y = 1
            blocks.add(new RelativeBlock(0, 1, 0, Material.WATER, false));
            blocks.add(new RelativeBlock(1, 1, 0, Material.CYAN_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(-1, 1, 0, Material.CYAN_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(0, 1, 1, Material.CYAN_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(0, 1, -1, Material.CYAN_STAINED_GLASS, false));

            // Top slab
            blocks.add(new RelativeBlock(0, 2, 0, Material.PRISMARINE_BRICK_SLAB, false));

        } else if (type == FishMultiblockType.BIODOME_AQUARIUM) {
            // Core Dark Prismarine at 0, 0, 0
            blocks.add(new RelativeBlock(0, 0, 0, Material.DARK_PRISMARINE, true));

            // Base platform 3x3 at y = -1
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Material mat = (x == 0 && z == 0) ? Material.SEA_LANTERN : Material.DARK_PRISMARINE;
                    blocks.add(new RelativeBlock(x, -1, z, mat, false));
                }
            }

            // Glass ring at y = 0
            blocks.add(new RelativeBlock(1, 0, 0, Material.TINTED_GLASS, false));
            blocks.add(new RelativeBlock(-1, 0, 0, Material.TINTED_GLASS, false));
            blocks.add(new RelativeBlock(0, 0, 1, Material.TINTED_GLASS, false));
            blocks.add(new RelativeBlock(0, 0, -1, Material.TINTED_GLASS, false));

            // Upper dome ring at y = 1
            blocks.add(new RelativeBlock(0, 1, 0, Material.WATER, false));
            blocks.add(new RelativeBlock(1, 1, 0, Material.LIGHT_BLUE_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(-1, 1, 0, Material.LIGHT_BLUE_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(0, 1, 1, Material.LIGHT_BLUE_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(0, 1, -1, Material.LIGHT_BLUE_STAINED_GLASS, false));

            // Apex at y = 2
            blocks.add(new RelativeBlock(0, 2, 0, Material.CONDUIT, false));

        } else if (type == FishMultiblockType.DEEP_SEA_DREDGER) {
            // Core hopper at 0, 0, 0
            blocks.add(new RelativeBlock(0, 0, 0, Material.HOPPER, true));

            // Platform at y = -1
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Material mat = (x == 0 && z == 0) ? Material.SEA_LANTERN : Material.DARK_PRISMARINE;
                    blocks.add(new RelativeBlock(x, -1, z, mat, false));
                }
            }

            // Industrial Corner Supports at y = 0
            blocks.add(new RelativeBlock(-1, 0, -1, Material.IRON_BARS, false));
            blocks.add(new RelativeBlock(1, 0, -1, Material.IRON_BARS, false));
            blocks.add(new RelativeBlock(-1, 0, 1, Material.IRON_BARS, false));
            blocks.add(new RelativeBlock(1, 0, 1, Material.IRON_BARS, false));

            // Crane Arch at y = 1
            blocks.add(new RelativeBlock(0, 1, 0, Material.DARK_PRISMARINE, false));
            blocks.add(new RelativeBlock(0, 1, -1, Material.CHAIN, false));
            blocks.add(new RelativeBlock(0, 1, 1, Material.CHAIN, false));

            // Derrick Beacon at y = 2
            blocks.add(new RelativeBlock(0, 2, 0, Material.SOUL_LANTERN, false));

        } else if (type == FishMultiblockType.CORAL_REEF_BIODOME) {
            // Core Prismarine Bricks at 0, 0, 0
            blocks.add(new RelativeBlock(0, 0, 0, Material.PRISMARINE_BRICKS, true));

            // Base platform 3x3 at y = -1
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Material mat = (x == 0 && z == 0) ? Material.SEA_LANTERN : Material.DARK_PRISMARINE;
                    blocks.add(new RelativeBlock(x, -1, z, mat, false));
                }
            }

            // Glass walls and corals at y = 0
            blocks.add(new RelativeBlock(1, 0, 0, Material.LIGHT_BLUE_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(-1, 0, 0, Material.LIGHT_BLUE_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(0, 0, 1, Material.LIGHT_BLUE_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(0, 0, -1, Material.LIGHT_BLUE_STAINED_GLASS, false));

            // Coral blocks at corners
            blocks.add(new RelativeBlock(-1, 0, -1, Material.BRAIN_CORAL_BLOCK, false));
            blocks.add(new RelativeBlock(1, 0, -1, Material.BUBBLE_CORAL_BLOCK, false));
            blocks.add(new RelativeBlock(-1, 0, 1, Material.FIRE_CORAL_BLOCK, false));
            blocks.add(new RelativeBlock(1, 0, 1, Material.HORN_CORAL_BLOCK, false));

            // Water dome interior at y = 1
            blocks.add(new RelativeBlock(0, 1, 0, Material.WATER, false));
            blocks.add(new RelativeBlock(1, 1, 0, Material.LIGHT_BLUE_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(-1, 1, 0, Material.LIGHT_BLUE_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(0, 1, 1, Material.LIGHT_BLUE_STAINED_GLASS, false));
            blocks.add(new RelativeBlock(0, 1, -1, Material.LIGHT_BLUE_STAINED_GLASS, false));

            // Crown at y = 2
            blocks.add(new RelativeBlock(0, 2, 0, Material.SEA_LANTERN, false));
        } else if (type == FishMultiblockType.CRAB_TRAP_DOCK) {
            // Core
            blocks.add(new RelativeBlock(0, 0, 0, Material.BARREL, true));

            // Wooden dock platform at y = -1 (3x3)
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    blocks.add(new RelativeBlock(x, -1, z, Material.MANGROVE_PLANKS, false));
                }
            }

            // Four corner posts at y = 0
            blocks.add(new RelativeBlock(-1, 0, -1, Material.MANGROVE_FENCE, false));
            blocks.add(new RelativeBlock(1, 0, -1, Material.MANGROVE_FENCE, false));
            blocks.add(new RelativeBlock(-1, 0, 1, Material.MANGROVE_FENCE, false));
            blocks.add(new RelativeBlock(1, 0, 1, Material.MANGROVE_FENCE, false));

            // Lanterns on corner posts at y = 1
            blocks.add(new RelativeBlock(-1, 1, -1, Material.LANTERN, false));
            blocks.add(new RelativeBlock(1, 1, -1, Material.LANTERN, false));
            blocks.add(new RelativeBlock(-1, 1, 1, Material.LANTERN, false));
            blocks.add(new RelativeBlock(1, 1, 1, Material.LANTERN, false));
        }
    }

    public boolean canBuildAt(Location origin) {
        World world = origin.getWorld();
        if (world == null) return false;

        for (RelativeBlock rb : blocks) {
            Location loc = origin.clone().add(rb.dx, rb.dy, rb.dz);
            if (loc.getBlockY() < world.getMinHeight() || loc.getBlockY() >= world.getMaxHeight()) {
                return false;
            }
            Block block = loc.getBlock();
            Material mat = block.getType();
            if (mat == Material.BEDROCK || mat == Material.BARRIER || mat == Material.END_PORTAL ||
                    mat == Material.END_PORTAL_FRAME || mat == Material.COMMAND_BLOCK ||
                    mat == Material.STRUCTURE_BLOCK) {
                return false;
            }
        }
        return true;
    }

    public Set<Location> build(Location origin) {
        World world = origin.getWorld();
        if (world == null) return Collections.emptySet();

        Set<Location> placedLocations = new HashSet<>();

        // Place non-water blocks first, then water
        for (RelativeBlock rb : blocks) {
            if (rb.material == Material.WATER) continue;
            Location loc = origin.clone().add(rb.dx, rb.dy, rb.dz);
            Block block = loc.getBlock();
            block.setType(rb.material, false);
            placedLocations.add(loc);
        }

        for (RelativeBlock rb : blocks) {
            if (rb.material != Material.WATER) continue;
            Location loc = origin.clone().add(rb.dx, rb.dy, rb.dz);
            Block block = loc.getBlock();
            block.setType(rb.material, false);
            placedLocations.add(loc);
        }

        return placedLocations;
    }

    public List<RelativeBlock> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public FishMultiblockType getType() {
        return type;
    }

    public double getHologramOffsetY() {
        return hologramOffsetY;
    }
}
