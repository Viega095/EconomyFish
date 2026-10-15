package com.fishingeconomy.multiblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.*;

public class ActiveFishMultiblock {

    private final UUID instanceId;
    private final FishMultiblockType type;
    private final Location coreLocation;
    private final Set<Location> constituentBlocks = new HashSet<>();
    private final List<UUID> entityUuids = new ArrayList<>();
    private final UUID ownerUuid;
    private final long placedTimestamp;

    public ActiveFishMultiblock(UUID instanceId, FishMultiblockType type, Location coreLocation, UUID ownerUuid) {
        this.instanceId = instanceId;
        this.type = type;
        this.coreLocation = coreLocation;
        this.ownerUuid = ownerUuid;
        this.placedTimestamp = System.currentTimeMillis();
    }

    public UUID getInstanceId() {
        return instanceId;
    }

    public FishMultiblockType getType() {
        return type;
    }

    public Location getCoreLocation() {
        return coreLocation;
    }

    public Set<Location> getConstituentBlocks() {
        return constituentBlocks;
    }

    public void addConstituentBlock(Location loc) {
        constituentBlocks.add(loc);
    }

    public List<UUID> getEntityUuids() {
        return entityUuids;
    }

    public void addEntityUuid(UUID uuid) {
        entityUuids.add(uuid);
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public long getPlacedTimestamp() {
        return placedTimestamp;
    }

    public void removeAllEntities() {
        for (UUID uid : entityUuids) {
            Entity ent = Bukkit.getEntity(uid);
            if (ent != null && ent.isValid()) {
                ent.remove();
            }
        }
        entityUuids.clear();
    }
}
