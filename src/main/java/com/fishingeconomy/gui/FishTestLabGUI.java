package com.fishingeconomy.gui;

import com.fishingeconomy.FishingEconomy;
import com.fishingeconomy.fishing.DeepSeaTreasureSalvage;
import com.fishingeconomy.fishing.FishManager;
import com.fishingeconomy.fishing.GlobalFishingTournamentEngine;
import com.fishingeconomy.fishing.RodCraftingManager;
import com.fishingeconomy.multiblock.FishMultiblockType;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class FishTestLabGUI implements Listener {

    public static class LabHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final FishingEconomy plugin;

    public FishTestLabGUI(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new LabHolder(), 54, "§8🧪 §3Laboratorio de Pruebas: EconomyFish §8🧪");

        // Border panes
        ItemStack border = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta bMeta = border.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName(" ");
            border.setItemMeta(bMeta);
        }
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border);
            }
        }

        // --- FILA 1: Ítems Desplegables y Cañas Míticas ---
        // Slot 10: Dar Estación de Cebo
        inv.setItem(10, createBtn(Material.CAULDRON, "§a📦 Dar Ítem: Estación de Cebo",
                Arrays.asList("§7Otorga el bloque crafteable con PDC.", "§7Colócalo para erigir la estructura.", "§cRomperlo desmonta al 100% sin duplicar.", "", "§a▶ Haz clic para recibir")));

        // Slot 11: Dar Acuario Mágico
        inv.setItem(11, createBtn(Material.SEA_LANTERN, "§b📦 Dar Ítem: Acuario Mágico & Fileteado",
                Arrays.asList("§7Otorga el bloque crafteable con PDC.", "§7Colócalo para erigir el acuario y mesa.", "§cRomperlo desmonta al 100% sin duplicar.", "", "§b▶ Haz clic para recibir")));

        // Slot 12: Dar Caña Leviatán
        inv.setItem(12, createBtn(Material.BLAZE_ROD, "§6🔱 Dar Caña: Perdición del Leviatán",
                Arrays.asList("§7Caña Mítica con +40% de velocidad", "§7y +25% probabilidad de doble captura.", "", "§6▶ Haz clic para recibir")));

        // Slot 13: Dar Caña Astral del Vacío
        inv.setItem(13, createBtn(Material.ENDER_EYE, "§5🌌 Dar Caña: Astral del Vacío",
                Arrays.asList("§7Caña Mítica con +65% de velocidad,", "§7doble captura y atracción de Bóvedas.", "", "§5▶ Haz clic para recibir")));

        // Slot 14: Dar Caña Fénix Abisal
        inv.setItem(14, createBtn(Material.MAGMA_CREAM, "§c🔥 Dar Caña: Fénix Abisal",
                Arrays.asList("§7Caña Mítica con +55% de velocidad,", "§7auto-cocción y bono ígneo estelar.", "", "§c▶ Haz clic para recibir")));

        // Slot 15: Dar Caña Mareas Celestiales
        inv.setItem(15, createBtn(Material.HEART_OF_THE_SEA, "§9🌊 Dar Caña: Mareas Celestiales",
                Arrays.asList("§7Caña Mítica con +70% de velocidad", "§7y +150% multiplicador de venta.", "", "§9▶ Haz clic para recibir")));

        // Slot 16: Dar Paquete de Cebos Exóticos
        inv.setItem(16, createBtn(Material.DRAGON_BREATH, "§d🪱 Dar Paquete de Cebos Exóticos",
                Arrays.asList("§7Otorga x16 Sangre de Kraken, x16 Feromona", "§7de Sirena y x16 Escamas de Dragón.", "", "§d▶ Haz clic para recibir")));

        // Slot 17: Dar Caña Divina de Poseidón
        inv.setItem(17, createBtn(Material.PRISMARINE_SHARD, "§b🔱 Dar Caña Divina de Poseidón [NUEVA]",
                Arrays.asList("§7Caña legendaria con +85% velocidad.", "§b⚡ Clic Izquierdo desata Vórtice Marino.", "", "§b▶ Haz clic para recibir")));

        // Slot 18: Dar Muelle de Trampas de Cangrejos
        inv.setItem(18, createBtn(Material.BARREL, "§6📦 Dar Ítem: Muelle de Trampas de Cangrejos [NUEVO]",
                Arrays.asList("§7Otorga el bloque crafteable con PDC.", "§7Colócalo para erigir el muelle y trampas pasivas.", "§cRomperlo desmonta al 100% sin duplicar.", "", "§6▶ Haz clic para recibir")));

        // --- FILA 2: Despliegue en el Mundo & Mecánicas ---
        // Slot 19: Spawear Estación de Cebo en Mundo
        inv.setItem(19, createBtn(Material.BREWING_STAND, "§a🏗️ Desplegar: Estación de Cebo en Mundo",
                Arrays.asList("§7Construye la estructura completa 3D", "§7directamente frente a tu posición.", "", "§a▶ Haz clic para desplegar")));

        // Slot 20: Spawnear Acuario Mágico en Mundo
        inv.setItem(20, createBtn(Material.CYAN_STAINED_GLASS, "§b🏗️ Desplegar: Acuario Mágico en Mundo",
                Arrays.asList("§7Construye el tanque 3D y banco", "§7directamente frente a tu posición.", "", "§b▶ Haz clic para desplegar")));

        // Slot 21: Forzar Pez Mítico
        inv.setItem(21, createBtn(Material.NETHER_STAR, "§d⚡ Forzar Captura Mítica Inmediata",
                Arrays.asList("§7Tu siguiente captura con caña", "§7será 100% garantizada Mítica.", "", "§d▶ Haz clic para activar")));

        // Slot 22: Forzar Minijuego de Carrete
        inv.setItem(22, createBtn(Material.FISHING_ROD, "§b🎣 Forzar Minijuego de Carrete",
                Arrays.asList("§7Tu siguiente captura activará el", "§7minijuego de aguja interactiva.", "", "§b▶ Haz clic para activar")));

        // Slot 23: Invocar Megalodón
        inv.setItem(23, createBtn(Material.PRISMARINE_SHARD, "§c🦈 Invocar Megalodón Ancestral (Boss)",
                Arrays.asList("§7Spawnea al jefe mundial Megalodón", "§7en tu posición con 1,800 HP.", "", "§c▶ Haz clic para invocar")));

        // Slot 24: Invocar Kraken
        inv.setItem(24, createBtn(Material.INK_SAC, "§5🐙 Invocar Kraken Abisal (Boss)",
                Arrays.asList("§7Spawnea al jefe mundial Kraken en", "§7tu posición con ataques de tentáculos.", "", "§5▶ Haz clic para invocar")));

        // Slot 25: Torneo de Pesca
        inv.setItem(25, createBtn(Material.GOLDEN_HELMET, "§6🏆 Iniciar Torneo de Pesca Global",
                Arrays.asList("§7Comienza un evento de torneo con", "§7BossBar y premios para todo el servidor.", "", "§6▶ Haz clic para iniciar")));

        // Slot 26: Acuario Domo Abisal
        inv.setItem(26, createBtn(Material.DARK_PRISMARINE, "§3📦 Dar Ítem: Acuario Domo Abisal [NUEVO]",
                Arrays.asList("§7Otorga el bloque crafteable con PDC.", "§7Colócalo para erigir el domo cristalino.", "", "§3▶ Haz clic para recibir")));

        // --- FILA 3: Eventos Exóticos & Economía ---
        // Slot 28: Lluvia Meteorítica
        inv.setItem(28, createBtn(Material.FIREWORK_ROCKET, "§d☄️ Activar: Lluvia de Peces Meteoríticos",
                Arrays.asList("§7Dispara el evento exótico de 5 min con", "§7BossBar, partículas y capturas míticas x3.", "", "§d▶ Haz clic para activar")));

        // Slot 29: Marea del Leviatán Fantasma
        inv.setItem(29, createBtn(Material.SOUL_LANTERN, "§3👻 Activar: Marea del Leviatán Fantasma",
                Arrays.asList("§7Dispara la marea espectral con niebla,", "§7partículas de alma y ventas x2.5.", "", "§3▶ Haz clic para activar")));

        // Slot 30: Recargar Balance
        inv.setItem(30, createBtn(Material.EMERALD_BLOCK, "§a💰 Recargar Saldo de Economía",
                Arrays.asList("§7Deposita §a100,000 monedas §7en tu cuenta", "§7para pruebas de mercado y compras.", "", "§a▶ Haz clic para recargar")));

        // Slot 31: Maximizar Nivel de Caña
        inv.setItem(31, createBtn(Material.ANVIL, "§e⚡ Maximizar Nivel Marino de Caña",
                Arrays.asList("§7Sube la caña en tu mano al nivel marino 50,", "§7con 200 capturas y mejoras al máximo.", "", "§e▶ Haz clic para potenciar")));

        // Slot 32: Bóveda de Atlántida
        inv.setItem(32, createBtn(Material.ENDER_CHEST, "§5📦 Dar Bóveda de Atlántida (Salvage)",
                Arrays.asList("§7Entrega un cofre de tesoro legendario", "§7con minijuego de ganzúa submarina.", "", "§5▶ Haz clic para recibir")));

        // Slot 33: Plataforma de Acuicultura
        inv.setItem(33, createBtn(Material.DARK_PRISMARINE, "§2⚓ Plataforma de Acuicultura Offshore",
                Arrays.asList("§7Gestiona y despliega tu estación", "§7de recolección pasiva en alta mar.", "", "§2▶ Haz clic para abrir")));

        // Slot 34: Expedición a Naufragios
        inv.setItem(34, createBtn(Material.MAP, "§e🚢 Expediciones a Galeones Hundidos",
                Arrays.asList("§7Abre el mapa interactivo de buceo", "§7y rescate de tesoros sumergidos.", "", "§e▶ Haz clic para abrir")));

        // Slot 35: Cebos Ígneos y del Vacío
        inv.setItem(35, createBtn(Material.MAGMA_CREAM, "§6🔥 Dar Cebos Ígneos y Vacío [NUEVO]",
                Arrays.asList("§7Recibe cebos para lava y The End (x16 c/u).", "", "§6▶ Haz clic para recibir")));

        // --- FILA 4: Utilidades y Menús del Plugin ---
        // Slot 37: Mutación Genética
        inv.setItem(37, createBtn(Material.DRAGON_BREATH, "§d🧬 Laboratorio de Mutación Genética",
                Arrays.asList("§7Combina especies para crear híbridos.", "", "§d▶ Haz clic para abrir")));

        // Slot 38: Altar de Encantamientos
        inv.setItem(38, createBtn(Material.ENCHANTING_TABLE, "§9✨ Altar de Encantamientos Arcanos",
                Arrays.asList("§7Imbuye tu caña con encantamientos marinos.", "", "§9▶ Haz clic para abrir")));

        // Slot 39: Tienda de Traje de Buceo
        inv.setItem(39, createBtn(Material.TURTLE_HELMET, "§3🌊 Tienda de Traje de Buceo Abisal",
                Arrays.asList("§7Compra equipo de neopreno y oxígeno.", "", "§3▶ Haz clic para abrir")));

        // Slot 40: Codex
        inv.setItem(40, createBtn(Material.BOOK, "§e📜 Enciclopedia Codex de Peces",
                Arrays.asList("§7Consulta todas las especies y récords.", "", "§e▶ Haz clic para abrir")));

        // Slot 41: Submarino
        inv.setItem(41, createBtn(Material.MINECART, "§9🧭 Submarino Abisal Nautilus",
                Arrays.asList("§7Monta el vehículo sumergible de exploración.", "", "§9▶ Haz clic para montar")));

        // Slot 42: Museo
        inv.setItem(42, createBtn(Material.ARMOR_STAND, "§e🏛️ Museo de Taxidermia & Acuarios",
                Arrays.asList("§7Gestiona pedestales 3D de tus trofeos.", "", "§e▶ Haz clic para abrir")));

        // Slot 43: Recarga en Caliente
        inv.setItem(43, createBtn(Material.REDSTONE_TORCH, "§c⚡ Recarga en Caliente (Hot-Reload)",
                Arrays.asList("§7Recarga módulos y configuraciones en caliente.", "", "§c▶ Haz clic para recargar")));

        // Slot 44: Kit 4 Peces Exóticos
        inv.setItem(44, createBtn(Material.CONDUIT, "§d🐟 Dar Kit 4 Peces Exóticos [NUEVO]",
                Arrays.asList("§7Tiburón Magma, Mantarraya Vacío,", "§7Anguila Tempestad y Leviatán Abisal.", "", "§d▶ Haz clic para recibir")));

        // Slot 27: Dar Cebo de Polvo de Estrellas
        inv.setItem(27, createBtn(Material.AMETHYST_SHARD, "§d🪱 Dar Cebo de Polvo de Estrellas [NUEVO]",
                Arrays.asList("§7Recibe x16 Cebos de Polvo de Estrellas.", "§7Atrae criaturas de las estrellas.", "", "§d▶ Haz clic para recibir")));

        // Slot 36: Desplegar Biodomo de Arrecife en Mundo
        inv.setItem(36, createBtn(Material.PRISMARINE_BRICKS, "§b🏗️ Desplegar: Biodomo Arrecife en Mundo",
                Arrays.asList("§7Construye el biodomo de coral 3D", "§7directamente frente a tu posición.", "", "§b▶ Haz clic para desplegar")));

        // Slot 45: Dar Biodomo de Arrecife
        inv.setItem(45, createBtn(Material.PRISMARINE_BRICKS, "§b📦 Dar Ítem: Biodomo de Arrecife Coralino [NUEVO]",
                Arrays.asList("§7Otorga el bloque crafteable con PDC.", "§7Colócalo para erigir el biodomo coralino.", "", "§b▶ Haz clic para recibir")));

        // Slot 46: Dar Draga Abisal
        inv.setItem(46, createBtn(Material.HOPPER, "§9📦 Dar Ítem: Draga Abisal [NUEVO]",
                Arrays.asList("§7Otorga el bloque crafteable con PDC.", "§7Colócalo para erigir la grúa de dragado.", "", "§9▶ Haz clic para recibir")));

        // Slot 47: Dar Cebo de la Nebulosa
        inv.setItem(47, createBtn(Material.ECHO_SHARD, "§b🪱 Dar Cebo de la Nebulosa Cósmica [NUEVO]",
                Arrays.asList("§7Recibe x16 Cebos de la Nebulosa.", "§7Atrae peces cósmicos estelares.", "", "§b▶ Haz clic para recibir")));

        // Slot 48: Dar Caña Cósmica del Vacío
        inv.setItem(48, createBtn(Material.FISHING_ROD, "§5🌌 Dar Caña Cósmica del Vacío Estelar [NUEVA]",
                Arrays.asList("§7Caña legendaria con +95% velocidad.", "§5⚡ Clic Izquierdo abre Agujero Negro Marino.", "", "§5▶ Haz clic para recibir")));

        // Slot 49: Info Central
        inv.setItem(49, createBtn(Material.HEART_OF_THE_SEA, "§b§l✦ PANEL MAESTRO DE PRUEBAS",
                Arrays.asList("§7Laboratorio integral para verificación rápida.", "§7Haz clic en cualquier botón para disparar", "§7sistemas y mecánicas en 1 segundo.")));

        // Slot 50: Abrir Compendio
        inv.setItem(50, createBtn(Material.KNOWLEDGE_BOOK, "§b§l✦ Abrir Compendio / Guía Integral de Pesca ✦",
                Arrays.asList("§7Abre la enciclopedia de EconomyFish.", "", "§b▶ Haz clic para consultar la guía")));

        // Slot 51: Dar Vara del Eclipse Abisal
        inv.setItem(51, createBtn(Material.FISHING_ROD, "§5🌌 Dar Vara del Eclipse Abisal [NUEVA]",
                Arrays.asList("§7Caña suprema con +105% velocidad y +75% doble captura.", "§5⚡ Clic Izquierdo abre Agujero Negro Abisal.", "", "§5▶ Haz clic para recibir")));

        // Slot 52: Kit 4 Criaturas Cósmicas
        inv.setItem(52, createBtn(Material.AMETHYST_SHARD, "§d✨ Dar Kit 4 Criaturas Cósmicas [NUEVO]",
                Arrays.asList("§7Medusa Cósmica, Pez Rape Estelar,", "§7Cangrejo Magma y Serpiente Titán.", "", "§d▶ Haz clic para recibir")));

        // Slot 53: Kit 4 Peces del Arrecife y Abismo
        inv.setItem(53, createBtn(Material.TROPICAL_FISH, "§a🐟 Dar Kit 4 Peces del Arrecife y Abismo [NUEVO]",
                Arrays.asList("§7Raya Eléctrica, Tortuga Coralina,", "§7Calamar Ópalo y Tiburón Martillo.", "", "§a▶ Haz clic para recibir")));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.4f);
    }

    private ItemStack createBtn(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof LabHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();

        switch (slot) {
            case 10: // Dar Estación de Cebo
                if (plugin.getFishMultiblockManager() != null) {
                    player.getInventory().addItem(plugin.getFishMultiblockManager().createStructureItem(FishMultiblockType.BAIT_STATION));
                    player.sendMessage(ChatColor.GREEN + "✔ ¡Has recibido el bloque crafteable de la Estación de Cebo!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 11: // Dar Acuario Mágico
                if (plugin.getFishMultiblockManager() != null) {
                    player.getInventory().addItem(plugin.getFishMultiblockManager().createStructureItem(FishMultiblockType.MAGIC_AQUARIUM));
                    player.sendMessage(ChatColor.AQUA + "✔ ¡Has recibido el bloque crafteable del Acuario Mágico!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 12: // Caña Leviatán
                if (plugin.getRodCraftingManager() != null) {
                    player.getInventory().addItem(plugin.getRodCraftingManager().createCustomRod(RodCraftingManager.CustomRodType.LEVIATHAN_BANE));
                    player.sendMessage(ChatColor.GOLD + "✔ Caña Perdición del Leviatán añadida a tu inventario.");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 13: // Caña Astral
                if (plugin.getRodCraftingManager() != null) {
                    player.getInventory().addItem(plugin.getRodCraftingManager().createCustomRod(RodCraftingManager.CustomRodType.VOID_ASTRAL_ROD));
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "✔ Caña Astral del Vacío añadida a tu inventario.");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 14: // Caña Fénix
                if (plugin.getRodCraftingManager() != null) {
                    player.getInventory().addItem(plugin.getRodCraftingManager().createCustomRod(RodCraftingManager.CustomRodType.PHOENIX_ABYSSAL_ROD));
                    player.sendMessage(ChatColor.RED + "✔ Caña del Fénix Abisal añadida a tu inventario.");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 15: // Caña Mareas Celestiales
                if (plugin.getRodCraftingManager() != null) {
                    player.getInventory().addItem(plugin.getRodCraftingManager().createCustomRod(RodCraftingManager.CustomRodType.TIDAL_CELESTIAL_ROD));
                    player.sendMessage(ChatColor.BLUE + "✔ Caña de las Mareas Celestiales añadida a tu inventario.");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 16: // Dar Paquete Cebos Exóticos
                if (plugin.getBaitManager() != null) {
                    player.getInventory().addItem(plugin.getBaitManager().createBaitItem("kraken_blood", 16));
                    player.getInventory().addItem(plugin.getBaitManager().createBaitItem("siren_pheromone", 16));
                    player.getInventory().addItem(plugin.getBaitManager().createBaitItem("sea_dragon_scale", 16));
                    player.sendMessage(ChatColor.DARK_PURPLE + "✔ ¡Paquete de 3 cebos exóticos (x16 c/u) añadido a tu inventario!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 17: // Caña Tridente de Poseidón [NUEVO]
                if (plugin.getRodCraftingManager() != null) {
                    player.getInventory().addItem(plugin.getRodCraftingManager().createCustomRod(RodCraftingManager.CustomRodType.POSEIDON_TRIDENT_ROD));
                    player.sendMessage(ChatColor.AQUA + "✔ ¡Caña Tridente de Poseidón añadida a tu inventario!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 18: // Dar Muelle de Trampas de Cangrejos
                ItemStack crabTrap = plugin.getFishMultiblockManager().createStructureItem(FishMultiblockType.CRAB_TRAP_DOCK);
                player.getInventory().addItem(crabTrap);
                player.sendMessage(ChatColor.GOLD + "✦ [Lab] ¡Muelle de Trampas de Cangrejos entregado!");
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                break;

            case 19: // Desplegar Estación de Cebo en Mundo
                player.closeInventory();
                if (plugin.getFishMultiblockManager() != null) {
                    Location spawnLoc = player.getLocation().getBlock().getRelative(player.getFacing(), 2).getLocation();
                    plugin.getFishMultiblockManager().placeStructure(player, spawnLoc, FishMultiblockType.BAIT_STATION);
                    player.sendMessage(ChatColor.GREEN + "✔ ¡Estación de Cebo desplegada en el mundo frente a ti!");
                }
                break;

            case 20: // Desplegar Acuario Mágico en Mundo
                player.closeInventory();
                if (plugin.getFishMultiblockManager() != null) {
                    Location spawnLoc = player.getLocation().getBlock().getRelative(player.getFacing(), 2).getLocation();
                    plugin.getFishMultiblockManager().placeStructure(player, spawnLoc, FishMultiblockType.MAGIC_AQUARIUM);
                    player.sendMessage(ChatColor.AQUA + "✔ ¡Acuario Mágico desplegado en el mundo frente a ti!");
                }
                break;

            case 21: // Forzar Mítico
                plugin.getFishManager().setForceMythic(player.getUniqueId(), true);
                player.sendMessage(ChatColor.LIGHT_PURPLE + "✦ [Lab] ¡Tu próxima captura será 100% GARANTIZADA MÍTICA!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
                player.closeInventory();
                break;

            case 22: // Forzar Minijuego
                plugin.getFishManager().setForceMinigame(player.getUniqueId(), true);
                player.sendMessage(ChatColor.AQUA + "✦ [Lab] ¡Tu próxima captura activará el minijuego de aguja!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.5f);
                player.closeInventory();
                break;

            case 23: // Megalodón
                player.closeInventory();
                plugin.getOceanicBossRaid().spawnOceanicBoss(player.getLocation(), "megalodon");
                break;

            case 24: // Kraken
                player.closeInventory();
                plugin.getOceanicBossRaid().spawnOceanicBoss(player.getLocation(), "kraken");
                break;

            case 25: // Torneo
                player.closeInventory();
                if (plugin.getGlobalTournamentEngine() != null) {
                    plugin.getGlobalTournamentEngine().startTournament(GlobalFishingTournamentEngine.DerbyType.WEIGHT_CHAMPIONSHIP, 10);
                }
                break;

            case 26: // Dar Biodomo Acuario [NUEVO]
                if (plugin.getFishMultiblockManager() != null) {
                    player.getInventory().addItem(plugin.getFishMultiblockManager().createStructureItem(FishMultiblockType.BIODOME_AQUARIUM));
                    player.sendMessage(ChatColor.DARK_AQUA + "✔ ¡Has recibido el bloque crafteable del Biodomo Acuario Abisal!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 28: // Lluvia Meteorítica
                player.closeInventory();
                if (plugin.getExoticFishingEventManager() != null) {
                    plugin.getExoticFishingEventManager().startMeteorFishRain();
                }
                break;

            case 29: // Marea del Leviatán Fantasma
                player.closeInventory();
                if (plugin.getExoticFishingEventManager() != null) {
                    plugin.getExoticFishingEventManager().startGhostLeviathanTide();
                }
                break;

            case 30: // Recargar Balance
                plugin.getEconomyManager().depositPlayer(player, 100000.0);
                player.sendMessage(ChatColor.GREEN + "💰 ¡Se han depositado +" + plugin.getEconomyManager().format(100000.0) + " en tu cuenta de pruebas!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1.5f);
                break;

            case 31: // Maximizar Nivel de Caña
                ItemStack inHand = player.getInventory().getItemInMainHand();
                if (inHand.getType() == Material.FISHING_ROD && inHand.hasItemMeta()) {
                    ItemMeta meta = inHand.getItemMeta();
                    PersistentDataContainer pdc = meta.getPersistentDataContainer();
                    pdc.set(plugin.getCustomRodStatsGui().keyLevel, PersistentDataType.INTEGER, 50);
                    pdc.set(plugin.getCustomRodStatsGui().keyCatches, PersistentDataType.INTEGER, 200);
                    pdc.set(plugin.getCustomRodStatsGui().keyMaxWeight, PersistentDataType.DOUBLE, 150.0);
                    pdc.set(plugin.getCustomRodStatsGui().keyUpgradeSpeed, PersistentDataType.INTEGER, 5);
                    pdc.set(plugin.getCustomRodStatsGui().keyUpgradeTension, PersistentDataType.INTEGER, 5);
                    pdc.set(plugin.getCustomRodStatsGui().keyUpgradeLuck, PersistentDataType.INTEGER, 5);
                    pdc.set(plugin.getCustomRodStatsGui().keyUpgradeSell, PersistentDataType.INTEGER, 5);
                    inHand.setItemMeta(meta);
                    player.sendMessage(ChatColor.GOLD + "⚡ ¡Caña en mano potenciada a Nivel Marino 50 y Mejoras al Máximo (5/5)!");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.5f);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.8f);
                } else {
                    player.sendMessage(ChatColor.RED + "✖ Debes sostener una caña de pescar en tu mano principal para potenciarla.");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                }
                break;

            case 32: // Bóveda de Atlántida
                if (plugin.getDeepSeaTreasureSalvage() != null) {
                    player.getInventory().addItem(plugin.getDeepSeaTreasureSalvage().createSalvageItem(DeepSeaTreasureSalvage.SalvageTier.ATLANTIS_VAULT));
                    player.sendMessage(ChatColor.GOLD + "✔ Bóveda de Atlántida añadida a tu inventario.");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 33: // Rig
                if (plugin.getAquacultureRigManager() != null) {
                    plugin.getAquacultureRigManager().openRigGUI(player);
                }
                break;

            case 34: // Expedición Naufragios
                player.closeInventory();
                if (plugin.getShipwreckManager() != null) {
                    plugin.getShipwreckManager().openExpeditionMapGUI(player);
                }
                break;

            case 35: // Cebos Ígneos y Vacío [NUEVO]
                if (plugin.getBaitManager() != null) {
                    player.getInventory().addItem(plugin.getBaitManager().createBaitItem("infernal_bait", 16));
                    player.getInventory().addItem(plugin.getBaitManager().createBaitItem("void_stardust_bait", 16));
                    player.sendMessage(ChatColor.GOLD + "✔ ¡Recibiste Cebo Ígneo Infernal (x16) y Polvo Astral del Vacío (x16)!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 37: // Mutación Genética
                player.closeInventory();
                if (plugin.getFishMutationLab() != null) {
                    plugin.getFishMutationLab().openMutationGUI(player);
                }
                break;

            case 38: // Altar Encantamientos
                if (plugin.getRodEnchantManager() != null) {
                    plugin.getRodEnchantManager().openEnchantGUI(player);
                }
                break;

            case 39: // Traje de Buceo
                if (plugin.getDivingSuitManager() != null) {
                    plugin.getDivingSuitManager().openShopGUI(player);
                }
                break;

            case 40: // Codex
                if (plugin.getFishCodexManager() != null) {
                    plugin.getFishCodexManager().openCodexGUI(player);
                }
                break;

            case 41: // Submarino
                player.closeInventory();
                player.performCommand("submarine");
                break;

            case 42: // Museo
                player.closeInventory();
                if (plugin.getFishTaxidermyMuseum() != null) {
                    plugin.getFishTaxidermyMuseum().openMuseumGUI(player);
                }
                break;

            case 43: // Hot-Reload
                player.closeInventory();
                plugin.getUpdateManager().performLiveReload(player);
                break;

            case 44: // Dar Kit 4 Peces Exóticos [NUEVO]
                if (plugin.getFishManager() != null) {
                    String[] newFishIds = {"LAVA_SHARK", "VOID_RAY", "TEMPEST_EEL", "ABYSSAL_LEVIATHAN"};
                    for (String id : newFishIds) {
                        FishManager.CustomFish fish = plugin.getFishManager().getAllFish().get(id);
                        if (fish != null) {
                            player.getInventory().addItem(plugin.getFishManager().createFishItem(fish));
                        }
                    }
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "✔ ¡Has recibido el kit con los 4 nuevos peces exóticos!");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
                }
                break;

            case 46: // Draga Abisal
                if (plugin.getFishMultiblockManager() != null) {
                    player.getInventory().addItem(plugin.getFishMultiblockManager().createStructureItem(FishMultiblockType.DEEP_SEA_DREDGER));
                    player.sendMessage(ChatColor.BLUE + "✔ ¡Has recibido la Draga Abisal de Aguas Profundas (PDC)!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 47: // Cebo de la Nebulosa
                if (plugin.getBaitManager() != null) {
                    ItemStack nebulaBait = plugin.getBaitManager().createBaitItem("nebula_bait", 16);
                    if (nebulaBait != null) {
                        player.getInventory().addItem(nebulaBait);
                        player.sendMessage(ChatColor.AQUA + "✔ ¡Has recibido x16 Cebos de la Nebulosa Cósmica!");
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                    }
                }
                break;

            case 48: // Caña Cósmica del Vacío
                if (plugin.getRodCraftingManager() != null) {
                    ItemStack cosmicRod = plugin.getRodCraftingManager().createCustomRod(RodCraftingManager.CustomRodType.VOID_COSMIC_ROD);
                    player.getInventory().addItem(cosmicRod);
                    player.sendMessage(ChatColor.DARK_PURPLE + "✔ ¡Has recibido la Caña Cósmica del Vacío Estelar!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 50: // Abrir Compendio
                player.closeInventory();
                new FishContentGuideGUI(plugin).open(player);
                break;

            case 27: // Cebo de Polvo de Estrellas
                if (plugin.getBaitManager() != null) {
                    ItemStack stardust = plugin.getBaitManager().createBaitItem("stardust_bait", 16);
                    if (stardust != null) {
                        player.getInventory().addItem(stardust);
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "✔ ¡Has recibido x16 Cebos de Polvo de Estrellas!");
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                    }
                }
                break;

            case 36: // Desplegar Biodomo de Arrecife en Mundo
                player.closeInventory();
                if (plugin.getFishMultiblockManager() != null) {
                    Location spawnLoc = player.getLocation().getBlock().getRelative(player.getFacing(), 2).getLocation();
                    plugin.getFishMultiblockManager().placeStructure(player, spawnLoc, FishMultiblockType.CORAL_REEF_BIODOME);
                    player.sendMessage(ChatColor.AQUA + "✔ ¡Biodomo de Arrecife Coralino desplegado frente a ti!");
                }
                break;

            case 45: // Dar Biodomo de Arrecife
                if (plugin.getFishMultiblockManager() != null) {
                    player.getInventory().addItem(plugin.getFishMultiblockManager().createStructureItem(FishMultiblockType.CORAL_REEF_BIODOME));
                    player.sendMessage(ChatColor.AQUA + "✔ ¡Has recibido el Biodomo de Arrecife Coralino (PDC)!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 51: // Vara del Eclipse Abisal
                if (plugin.getRodCraftingManager() != null) {
                    ItemStack eclipseRod = plugin.getRodCraftingManager().createCustomRod(RodCraftingManager.CustomRodType.ABYSSAL_ECLIPSE_ROD);
                    player.getInventory().addItem(eclipseRod);
                    player.sendMessage(ChatColor.DARK_PURPLE + "✔ ¡Has recibido la Vara del Eclipse Abisal!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 52: // Kit 4 Criaturas Cósmicas
                if (plugin.getFishManager() != null) {
                    String[] cosmicFishIds = {"COSMIC_JELLYFISH", "STELLAR_ANGLERFISH", "MAGMA_CRAB", "TITAN_SEA_SERPENT"};
                    for (String id : cosmicFishIds) {
                        FishManager.CustomFish fish = plugin.getFishManager().getAllFish().get(id);
                        if (fish != null) {
                            player.getInventory().addItem(plugin.getFishManager().createFishItem(fish));
                        }
                    }
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "✔ ¡Has recibido el kit con las 4 nuevas criaturas cósmicas!");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
                }
                break;

            case 53: // Kit 4 Peces del Arrecife y Abismo
                if (plugin.getFishManager() != null) {
                    String[] newSpecies = {"STORM_ELECTRIC_RAY", "ANCIENT_CORAL_TURTLE", "OPAL_ABYSSAL_SQUID", "TITANIC_HAMMERHEAD"};
                    for (String id : newSpecies) {
                        FishManager.CustomFish fish = plugin.getFishManager().getAllFish().get(id);
                        if (fish != null) {
                            player.getInventory().addItem(plugin.getFishManager().createFishItem(fish));
                        }
                    }
                    player.sendMessage(ChatColor.AQUA + "✔ ¡Has recibido el kit con las 4 nuevas especies del arrecife y abismo!");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
                }
                break;
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof LabHolder) {
            event.setCancelled(true);
        }
    }
}
