package me.antigravity.fishingeconomy.gui;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.fishing.DeepSeaTreasureSalvage;
import me.antigravity.fishingeconomy.fishing.RodCraftingManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        // Slot 10: Forzar Pez Mítico
        inv.setItem(10, createBtn(Material.NETHER_STAR, "§d⚡ Forzar Captura Mítica Inmediata",
                Arrays.asList("§7Tu siguiente lanzamiento con caña", "§7capturará garantizado un pez Mítico (Megalodón).", "", "§a▶ Haz clic para activar")));

        // Slot 11: Forzar Minijuego de Carrete
        inv.setItem(11, createBtn(Material.FISHING_ROD, "§b🎣 Forzar Minijuego de Carrete",
                Arrays.asList("§7Tu siguiente captura activará el", "§7minijuego de aguja interactiva direccional.", "", "§a▶ Haz clic para activar")));

        // Slot 12: Invocar Megalodón Ancestral
        inv.setItem(12, createBtn(Material.PRISMARINE_SHARD, "§c🦈 Invocar Megalodón Ancestral (Boss)",
                Arrays.asList("§7Spawnea al jefe mundial Megalodón", "§7en tu posición con 1,800 HP seguros.", "", "§c▶ Haz clic para invocar")));

        // Slot 13: Invocar Kraken Abisal
        inv.setItem(13, createBtn(Material.INK_SAC, "§5🐙 Invocar Kraken Abisal (Boss)",
                Arrays.asList("§7Spawnea al temible Kraken en", "§7tu posición con ataques de tentáculos.", "", "§5▶ Haz clic para invocar")));

        // Slot 14: Iniciar Torneo de Pesca Global
        inv.setItem(14, createBtn(Material.GOLDEN_HELMET, "§6🏆 Iniciar Torneo de Pesca Global",
                Arrays.asList("§7Comienza un evento de torneo de", "§7pesca para todo el servidor con premios.", "", "§6▶ Haz clic para iniciar")));

        // Slot 15: Iniciar Expedición Oceánica
        inv.setItem(15, createBtn(Material.SPYGLASS, "§e🚢 Iniciar Expedición de Altamar",
                Arrays.asList("§7Lanza la expedición hacia aguas", "§7profundas para cazar criaturas míticas.", "", "§e▶ Haz clic para iniciar")));

        // Slot 16: Dar Cañas Míticas
        inv.setItem(16, createBtn(Material.BLAZE_ROD, "§6🔱 Recibir Caña del Leviatán",
                Arrays.asList("§7Otorga la Caña Perdición del Leviatán", "§7con bono de doble captura a tu inventario.", "", "§6▶ Haz clic para recibir")));

        // Slot 19: Altar de Encantamientos de Caña
        inv.setItem(19, createBtn(Material.ENCHANTING_TABLE, "§9✨ Altar de Encantamientos Arcanos",
                Arrays.asList("§7Abre el altar para imbuir tu caña con", "§7Llamada Abisal, Bendición de Neptuno, etc.", "", "§9▶ Haz clic para abrir")));

        // Slot 20: Tienda de Traje de Buceo
        inv.setItem(20, createBtn(Material.TURTLE_HELMET, "§3🌊 Tienda de Traje de Buceo Abisal",
                Arrays.asList("§7Abre la tienda para comprar el casco,", "§7traje de neopreno y aletas de impulso.", "", "§3▶ Haz clic para abrir")));

        // Slot 21: Caldero de Cebos
        inv.setItem(21, createBtn(Material.CAULDRON, "§a🪱 Caldero de Elaboración de Cebos",
                Arrays.asList("§7Abre la estación alquímica para crear", "§7cebos luminiscentes y abisales.", "", "§a▶ Haz clic para abrir")));

        // Slot 22: Plataforma de Acuicultura
        inv.setItem(22, createBtn(Material.DARK_PRISMARINE, "§2⚓ Plataforma de Acuicultura Offshore",
                Arrays.asList("§7Gestiona y despliega tu plataforma", "§7de recolección pasiva de recursos marinos.", "", "§2▶ Haz clic para abrir")));

        // Slot 23: Enciclopedia Codex de Peces
        inv.setItem(23, createBtn(Material.BOOK, "§e📜 Enciclopedia Codex de Peces",
                Arrays.asList("§7Visualiza todas las especies marinas,", "§7pesos récord y recompensas de colección.", "", "§e▶ Haz clic para abrir")));

        // Slot 24: Estado de Mareas y Tormentas
        inv.setItem(24, createBtn(Material.WATER_BUCKET, "§b🌊 Ciclo de Mareas Oceánicas",
                Arrays.asList("§7Muestra el clima marino actual y", "§7multiplicadores de captura en vivo.", "", "§b▶ Haz clic para ver")));

        // Slot 25: Dar Cofre de Rescate de Atlántida
        inv.setItem(25, createBtn(Material.ENDER_CHEST, "§5📦 Dar Bóveda de Atlántida (Salvage)",
                Arrays.asList("§7Entrega una Bóveda de Atlántida de", "§7rescate marino legendario a tu bolsillo.", "", "§5▶ Haz clic para recibir")));

        // Slot 28: Mercado de Peces / Tienda
        inv.setItem(28, createBtn(Material.EMERALD, "§a💰 Mercado de Venta de Peces",
                Arrays.asList("§7Abre el mercado para vender capturas", "§7y consultar precios fluctuantes.", "", "§a▶ Haz clic para abrir")));

        // Slot 29: Astillero de Cañas
        inv.setItem(29, createBtn(Material.ANVIL, "§e🔨 Astillero de Cañas Personalizadas",
                Arrays.asList("§7Forja cañas con bonos especiales", "§7de velocidad y atracción marina.", "", "§e▶ Haz clic para abrir")));

        // Slot 30: Montar Submarino Nautilus
        inv.setItem(30, createBtn(Material.MINECART, "§9🧭 Submarino Abisal Nautilus",
                Arrays.asList("§7Monta un vehículo sumergible con", "§7visión submarina y oxígeno infinito.", "", "§9▶ Haz clic para montar")));

        // Slot 31: Gráfico Bursátil en Mapa
        inv.setItem(31, createBtn(Material.FILLED_MAP, "§6📈 Bolsa de Valores en Mapa",
                Arrays.asList("§7Genera un mapa renderizado en tiempo", "§7real con los gráficos de precios marinos.", "", "§6▶ Haz clic para obtener")));

        // Slot 32: Taxidermia y Trofeos
        inv.setItem(32, createBtn(Material.ITEM_FRAME, "§e🏆 Creador de Placas y Trofeos",
                Arrays.asList("§7Abre el menú para montar tus récords", "§7en placas decorativas de exhibición.", "", "§e▶ Haz clic para abrir")));

        // Slot 33: Depósito Bancario y Bonos
        inv.setItem(33, createBtn(Material.GOLD_INGOT, "§6🏦 Banco Central y Bonos",
                Arrays.asList("§7Accede a depósitos bancarios,", "§7intereses de pesca y bonos del tesoro.", "", "§6▶ Haz clic para abrir")));

        // Slot 34: Empresas Pesqueras
        inv.setItem(34, createBtn(Material.BEACON, "§d🏢 Corporaciones Pesqueras",
                Arrays.asList("§7Crea tu empresa pesquera, contrata", "§7flotas y gestiona acciones corporativas.", "", "§d▶ Haz clic para abrir")));

        // Slot 48: Auto-Update Check
        inv.setItem(48, createBtn(Material.EXPERIENCE_BOTTLE, "§a🔄 Probar Auto-Update en GitHub",
                Arrays.asList("§7Consulta la API de GitHub en busca de", "§7nuevas versiones y commits en caliente.", "", "§a▶ Haz clic para verificar")));

        // Slot 50: Live Hot-Reload
        inv.setItem(50, createBtn(Material.REDSTONE_TORCH, "§c⚡ Recarga en Caliente (Hot-Reload)",
                Arrays.asList("§7Recarga configs, módulos y mareas en", "§7<50ms sin reiniciar el servidor.", "", "§c▶ Haz clic para recargar")));

        // Center bottom info
        inv.setItem(49, createBtn(Material.HEART_OF_THE_SEA, "§b§l✦ PANEL MAESTRO DE PRUEBAS",
                Arrays.asList("§7Haz clic en cualquier funcionalidad", "§7para disparar eventos y mecánicas al instante.")));

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
            case 10: // Forzar Mítico
                plugin.getFishManager().setForceMythic(player.getUniqueId(), true);
                player.sendMessage(ChatColor.LIGHT_PURPLE + "✦ [Lab] ¡Tu próxima captura será 100% GARANTIZADA MÍTICA!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
                player.closeInventory();
                break;

            case 11: // Forzar Minijuego
                plugin.getFishManager().setForceMinigame(player.getUniqueId(), true);
                player.sendMessage(ChatColor.AQUA + "✦ [Lab] ¡Tu próxima captura activará el minijuego de aguja!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.5f);
                player.closeInventory();
                break;

            case 12: // Invocar Megalodón
                player.closeInventory();
                plugin.getOceanicBossRaid().spawnOceanicBoss(player.getLocation(), "megalodon");
                break;

            case 13: // Invocar Kraken
                player.closeInventory();
                plugin.getOceanicBossRaid().spawnOceanicBoss(player.getLocation(), "kraken");
                break;

            case 14: // Torneo
                player.closeInventory();
                if (plugin.getTournamentManager() != null) {
                    plugin.getTournamentManager().startTournament(me.antigravity.fishingeconomy.fishing.TournamentManager.TournamentType.HEAVIEST_FISH, 10);
                    player.sendMessage(ChatColor.GOLD + "🏆 [Lab] ¡Torneo de pesca de 10 minutos iniciado!");
                }
                break;

            case 15: // Expedición
                player.closeInventory();
                player.performCommand("expedition start");
                break;

            case 16: // Dar Caña Leviatán
                if (plugin.getRodCraftingManager() != null) {
                    player.getInventory().addItem(plugin.getRodCraftingManager().createCustomRod(RodCraftingManager.CustomRodType.LEVIATHAN_BANE));
                    player.sendMessage(ChatColor.GREEN + "✔ Caña Perdición del Leviatán añadida a tu inventario.");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 19: // Altar Encantamientos
                if (plugin.getRodEnchantManager() != null) {
                    plugin.getRodEnchantManager().openEnchantGUI(player);
                }
                break;

            case 20: // Tienda de Traje
                if (plugin.getDivingSuitManager() != null) {
                    plugin.getDivingSuitManager().openShopGUI(player);
                }
                break;

            case 21: // Caldero Cebos
                if (plugin.getBaitCraftingStation() != null) {
                    plugin.getBaitCraftingStation().openGUI(player);
                }
                break;

            case 22: // Rig
                if (plugin.getAquacultureRigManager() != null) {
                    plugin.getAquacultureRigManager().openRigGUI(player);
                }
                break;

            case 23: // Codex
                if (plugin.getFishCodexManager() != null) {
                    plugin.getFishCodexManager().openCodexGUI(player);
                }
                break;

            case 24: // Mareas
                if (plugin.getOceanWeatherAndTides() != null) {
                    plugin.getOceanWeatherAndTides().showTideStatus(player);
                }
                break;

            case 25: // Dar Bóveda de Atlántida
                if (plugin.getDeepSeaTreasureSalvage() != null) {
                    player.getInventory().addItem(plugin.getDeepSeaTreasureSalvage().createSalvageItem(DeepSeaTreasureSalvage.SalvageTier.ATLANTIS_VAULT));
                    player.sendMessage(ChatColor.GOLD + "✔ Bóveda de Atlántida añadida a tu inventario.");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.2f);
                }
                break;

            case 28: // Mercado
                player.closeInventory();
                player.performCommand("fishshop");
                break;

            case 29: // Astillero
                if (plugin.getCustomRodGui() != null) {
                    plugin.getCustomRodGui().open(player);
                }
                break;

            case 30: // Submarino
                player.closeInventory();
                player.performCommand("submarine");
                break;

            case 31: // Mapa bursátil
                player.closeInventory();
                player.performCommand("mapchart");
                break;

            case 32: // Trofeos
                player.closeInventory();
                player.performCommand("trophy");
                break;

            case 33: // Banco
                player.closeInventory();
                player.performCommand("bank");
                break;

            case 34: // Corporaciones
                player.closeInventory();
                player.performCommand("corp");
                break;

            case 48: // Update
                player.closeInventory();
                plugin.getUpdateManager().checkUpdate(player, true);
                break;

            case 50: // Reload
                player.closeInventory();
                plugin.getUpdateManager().performLiveReload(player);
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
