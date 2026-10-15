package com.fishingeconomy.multiblock;

import org.bukkit.Material;

import java.util.List;

public enum FishMultiblockType {
    BAIT_STATION(
            "BAIT_STATION",
            "§a§lEstación de Cebo Alquímica",
            Material.CAULDRON,
            List.of(
                    "§7Estructura marina desplegable de alta alquimia.",
                    "§7Coloca este bloque para erigir la estación en el mundo.",
                    "§c✦ Al romper cualquier bloque se desmonta al 100%",
                    "§c  y se reembolsa este ítem original sin pérdidas.",
                    "§a✦ Clic derecho en el caldero para sintetizar cebos marinos."
            ),
            "§a§l✦ Estación de Cebo Alquímica ✦"
    ),
    MAGIC_AQUARIUM(
            "MAGIC_AQUARIUM",
            "§b§lAcuario Mágico y Banco de Fileteado",
            Material.SEA_LANTERN,
            List.of(
                    "§7Estructura marina desplegable de exhibición y corte.",
                    "§7Coloca este bloque para erigir el tanque y mesa de corte.",
                    "§c✦ Al romper cualquier bloque se desmonta al 100%",
                    "§c  y se reembolsa este ítem original sin pérdidas.",
                    "§b✦ Clic derecho para gestionar tu acuario y filetear capturas."
            ),
            "§b§l✦ Acuario Mágico & Banco de Fileteado ✦"
    ),
    BIODOME_AQUARIUM(
            "BIODOME_AQUARIUM",
            "§3§lAcuario Domo Abisal",
            Material.DARK_PRISMARINE,
            List.of(
                    "§7Estructura marina de investigación y exhibición abisal.",
                    "§7Coloca este bloque para erigir el domo cristalino.",
                    "§c✦ Al romper cualquier bloque se desmonta al 100%",
                    "§c  y se reembolsa este ítem original sin pérdidas.",
                    "§3✦ Genera ingresos pasivos y perlas de experiencia marina."
            ),
            "§3§l✦ Acuario Domo Abisal ✦"
    ),
    DEEP_SEA_DREDGER(
            "DEEP_SEA_DREDGER",
            "§9§lDraga Abisal de Aguas Profundas",
            Material.HOPPER,
            List.of(
                    "§7Estructura marina de dragado y extracción oceánica.",
                    "§7Coloca este bloque para erigir la grúa de dragado marina.",
                    "§c✦ Al romper cualquier bloque se desmonta al 100%",
                    "§c  y se reembolsa este ítem original sin pérdidas.",
                    "§9✦ Draga automáticamente reliquias y tesoros del fondo abisal."
            ),
            "§9§l✦ Draga Abisal de Aguas Profundas ✦"
    ),
    CORAL_REEF_BIODOME(
            "CORAL_REEF_BIODOME",
            "§b§lBiodomo de Arrecife Coralino",
            Material.PRISMARINE_BRICKS,
            List.of(
                    "§7Santuario viviente de corales y fauna tropical.",
                    "§7Coloca este bloque para erigir el biodomo cristalino.",
                    "§c✦ Al romper cualquier bloque se desmonta al 100%",
                    "§c  y se reembolsa este ítem original sin pérdidas.",
                    "§b✦ Clic derecho otorga bendición oceánica de pesca acelerada."
            ),
            "§b§l✦ Biodomo de Arrecife Coralino ✦"
    ),
    CRAB_TRAP_DOCK(
            "CRAB_TRAP_DOCK",
            "§6§lMuelle de Trampas de Cangrejos",
            Material.BARREL,
            List.of(
                    "§7Estructura costera de captura y recolección marina.",
                    "§7Coloca este bloque para erigir el muelle y las nasas.",
                    "§c✦ Al romper cualquier bloque se desmonta al 100%",
                    "§c  y se reembolsa este ítem original sin pérdidas.",
                    "§6✦ Clic derecho recolecta cangrejos, ostras y perlas marinas."
            ),
            "§6§l✦ Muelle de Trampas de Cangrejos ✦"
    );

    private final String id;
    private final String displayName;
    private final Material itemMaterial;
    private final List<String> lore;
    private final String hologramTitle;

    FishMultiblockType(String id, String displayName, Material itemMaterial, List<String> lore, String hologramTitle) {
        this.id = id;
        this.displayName = displayName;
        this.itemMaterial = itemMaterial;
        this.lore = lore;
        this.hologramTitle = hologramTitle;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getHologramTitle() {
        return hologramTitle;
    }
}
