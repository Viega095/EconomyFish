package com.fishingeconomy.gui;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
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

import java.util.Arrays;
import java.util.List;

public class FishContentGuideGUI implements Listener {

    public static class GuideHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final FishingEconomy plugin;

    public FishContentGuideGUI(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new GuideHolder(), 54, "§3§lEconomyFish §8| §0Guía & Compendio");

        ItemStack border = createItem(Material.CYAN_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border);
            }
        }

        // Slot 4: Cabecera Principal
        inv.setItem(4, createItem(Material.HEART_OF_THE_SEA, "§b§l✦ ENCICLOPEDIA MARINA DE ECONOMYFISH ✦",
                Arrays.asList(
                        "§7Guía integral con todas las mecánicas,",
                        "§7cañas míticas, cebos, especies acuáticas,",
                        "§7multiblocks y economía de pesca en el servidor.",
                        "",
                        "§e▶ Haz clic en cualquier sección para aprender."
                )));

        // Slot 10: Cañas Míticas & Habilidades Activas
        inv.setItem(10, createItem(Material.FISHING_ROD, "§6🔱 Cañas de Pesca Legendarias & Activas",
                Arrays.asList(
                        "§7Forja cañas incomparables en los Astilleros (/rods):",
                        "§8▪ §6Perdición del Leviatán §7(+40% vel, doble captura).",
                        "§8▪ §cIgnición Magmática §7(Pesca en lava y auto-cocción).",
                        "§8▪ §bHiladora de Sirenas §7(+100% valor de venta).",
                        "§8▪ §5Astral del Vacío §7(Atracción de Bóvedas de Atlántida).",
                        "§8▪ §cFénix Abisal §7(+55% vel, llamas y perlas).",
                        "§8▪ §9Mareas Celestiales §7(+150% multiplicador de venta).",
                        "§8▪ §bCaña Divina de Poseidón §7(Clic Izq: Vórtice Marino Mítico).",
                        "§8▪ §5Caña Cósmica del Vacío §7(Clic Izq: Agujero Negro Marino)."
                )));

        // Slot 12: Cebos Alquímicos & Engodos
        inv.setItem(12, createItem(Material.FERMENTED_SPIDER_EYE, "§e🪱 Cebos Marinos Alquímicos",
                Arrays.asList(
                        "§7Equípalos en tu mano secundaria para activarlos:",
                        "§8▪ §6Gusano Dorado §7- +50% rareza y +30% velocidad.",
                        "§8▪ §5Engodo Abisal §7- +120% probabilidad Mítica/Legendaria.",
                        "§8▪ §bKrill Estelar §7- +100% vel nocturna y +80% valor.",
                        "§8▪ §4Sangre de Kraken §7- Atrae titanes marinos colosales.",
                        "§8▪ §dFeromona de Sirena §7- Pique casi instantáneo.",
                        "§8▪ §3Escamas de Dragón §7- Garantiza tier Épico o superior.",
                        "§8▪ §6Cebo Ígneo §7- Pesca en lava y ríos del Nether.",
                        "§8▪ §dPolvo de Estrellas §7- Pesca en The End y el vacío.",
                        "§8▪ §bCebo de la Nebulosa §7- Atrae Medusas Cósmicas y seres estelares."
                )));

        // Slot 14: Especies Acuáticas & Récords
        inv.setItem(14, createItem(Material.TROPICAL_FISH, "§a🐟 Especies Acuáticas & Biomas",
                Arrays.asList(
                        "§7Gran variedad de peces clasificados por rareza:",
                        "§8▪ §fComunes, Poco Comunes y Raros §7(Ríos y costas).",
                        "§8▪ §5Épicos y Legendarios §7(Océanos profundos y tormentas).",
                        "§8▪ §dMíticos y Reliquias §7(Leviatanes, Megalodón, Poseidón).",
                        "§8▪ §d✨ Medusa Cósmica Celestial §7[NUEVO].",
                        "§8▪ §6⭐ Pez Rape de la Fosa Estelar §7[NUEVO].",
                        "§8▪ §5🦀 Cangrejo del Magma Abisal §7[NUEVO].",
                        "§8▪ §d✦ Serpiente Marina Titán del Abismo §7[NUEVO]."
                )));

        // Slot 16: Estructuras Multiblock Desplegables
        inv.setItem(16, createItem(Material.BEACON, "§3🏗️ Multiblocks 3D Desplegables",
                Arrays.asList(
                        "§7Estructuras físicas crafteables con PDC único:",
                        "§8▪ §aEstación de Cebo Alquímica §7- Caldero con síntesis rápida.",
                        "§8▪ §bAcuario Mágico & Fileteado §7- Tanque y mesa de corte.",
                        "§8▪ §3Acuario Domo Abisal §7- Cúpula de exhibición con ingresos pasivos.",
                        "§8▪ §9Draga Abisal §7[NUEVA] - Grúa marina de extracción profunda.",
                        "",
                        "§d✦ Desmontaje 100% Limpio: §7Romper cualquier bloque",
                        "§7elimina la estructura sin duplicar materiales y",
                        "§7reembolsa el bloque crafteable original."
                )));

        // Slot 28: Banco de Fileteado & Procesamiento
        inv.setItem(28, createItem(Material.SMOKER, "§6🔪 Banco de Fileteado & Cocina Marina",
                Arrays.asList(
                        "§7Transforma tus capturas en recursos de alto valor:",
                        "§8▪ §7Filetea peces para obtener filetes selectos y aceite.",
                        "§8▪ §7Extrae perlas de escamas y dientes para el mercado.",
                        "§8▪ §7Cocina pescado en los ahumadores para buff alimenticio."
                )));

        // Slot 30: Bóvedas de Atlántida & Torneos
        inv.setItem(30, createItem(Material.CONDUIT, "§e🏆 Bóvedas Sumergidas & Torneos Globales",
                Arrays.asList(
                        "§7Desafíos competitivos del océano:",
                        "§8▪ §5Bóvedas de Atlántida: §7Cofres rescatados con",
                        "  §7minijuego interactivo de ganzúa por tesoros míticos.",
                        "§8▪ §6Torneo de Pesca del Servidor: §7Eventos automáticos",
                        "  §7donde el pez más pesado gana el pozo global.",
                        "§8▪ §cBosses Mundiales: §7Megalodón y Kraken marino."
                )));

        // Slot 32: Comandos del Jugador
        inv.setItem(32, createItem(Material.WRITABLE_BOOK, "§f📜 Guía de Comandos de Pesca",
                Arrays.asList(
                        "§7Comandos rápidos disponibles:",
                        "§8▪ §e/fish §7- Menú general de pesca.",
                        "§8▪ §e/fish guia §7o §e/fish contenido §7- Abre esta guía.",
                        "§8▪ §e/fish test §7- Laboratorio de pruebas completas.",
                        "§8▪ §e/fish codex §7- Enciclopedia de especies capturadas.",
                        "§8▪ §e/rods §7- Astillero y forja de cañas personalizadas.",
                        "§8▪ §e/fish stats §7- Estadísticas de nivel y capturas.",
                        "§8▪ §e/mercado §7- Bolsa de valores y venta de peces."
                )));

        // Slot 34: Blindaje y Seguridad
        inv.setItem(34, createItem(Material.SHIELD, "§2🛡️ Seguridad de Inventario & Zero-Dupe",
                Arrays.asList(
                        "§7Blindaje del sistema de pesca:",
                        "§8▪ §aCero duplicación al romper bloques multiblock.",
                        "§8▪ §aProtección estricta anti-robo en todas las interfaces.",
                        "§8▪ §aGuardado atómico de estructuras y niveles de caña."
                )));

        // Slot 40: Botón de Acceso al Laboratorio de Test
        inv.setItem(40, createItem(Material.BREWING_STAND, "§a§l🧪 ABRIR LABORATORIO DE PRUEBAS 🧪",
                Arrays.asList(
                        "§7Abre el panel maestro de 1-clic de EconomyFish:",
                        "§7obtén cañas míticas, cebos, spawnea jefes marinos,",
                        "§7recibe los multiblocks y prueba todo el contenido.",
                        "",
                        "§a▶ Haz clic para abrir el Laboratorio de Test"
                )));

        // Slot 49: Botón de Cerrar
        inv.setItem(49, createItem(Material.BARRIER, "§c§l✖ Cerrar Compendio",
                Arrays.asList("§7Haz clic para salir del menú.")));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1.2f);
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createItem(Material material, String name) {
        return createItem(material, name, null);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuideHolder)) return;

        event.setCancelled(true); // Complete anti-theft GUI protection

        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getRawSlot();
        if (slot >= 54) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (slot == 40) {
            player.closeInventory();
            new FishTestLabGUI(plugin).open(player);
        } else if (slot == 49) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1f, 1f);
        } else {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, 1.2f);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof GuideHolder) {
            event.setCancelled(true);
        }
    }
}
