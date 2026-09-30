package me.antigravity.fishingeconomy.commands;

import me.antigravity.fishingeconomy.FishingEconomy;
import me.antigravity.fishingeconomy.fishing.RodCraftingManager;
import me.antigravity.fishingeconomy.fishing.DeepSeaTreasureSalvage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FishGuideCommand implements CommandExecutor, TabCompleter {

    private final FishingEconomy plugin;

    public FishGuideCommand(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("guide") || args[0].equalsIgnoreCase("help")) {
            sendInteractiveGuide(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("rod") || args[0].equalsIgnoreCase("rods")) {
            plugin.getCustomRodGui().open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("codex") || args[0].equalsIgnoreCase("encyclopedia")) {
            plugin.getFishCodexManager().openCodexGUI(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("bait") || args[0].equalsIgnoreCase("cauldron")) {
            plugin.getBaitCraftingStation().openGUI(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("suit") || args[0].equalsIgnoreCase("suits") || args[0].equalsIgnoreCase("gear")) {
            plugin.getDivingSuitManager().openShopGUI(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("enchant") || args[0].equalsIgnoreCase("enchants") || args[0].equalsIgnoreCase("altar")) {
            plugin.getRodEnchantManager().openEnchantGUI(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("update") || args[0].equalsIgnoreCase("autoupdate")) {
            if (!player.hasPermission("fishingeconomy.admin")) {
                player.sendMessage(ChatColor.RED + "No tienes permisos de administrador.");
                return true;
            }
            if (args.length >= 2 && (args[1].equalsIgnoreCase("apply") || args[1].equalsIgnoreCase("download"))) {
                plugin.getUpdateManager().applyAutoUpdate(player);
            } else {
                plugin.getUpdateManager().checkUpdate(player, true);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("fishingeconomy.admin")) {
                player.sendMessage(ChatColor.RED + "No tienes permisos de administrador.");
                return true;
            }
            plugin.getUpdateManager().performLiveReload(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("tides") || args[0].equalsIgnoreCase("tide") || args[0].equalsIgnoreCase("weather")) {
            plugin.getOceanWeatherAndTides().showTideStatus(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("salvage")) {
            if (!player.hasPermission("fishingeconomy.admin")) {
                player.sendMessage(ChatColor.YELLOW + "Pesca en aguas profundas o usa cebo rastreador para rescatar cajas de tesoros del fondo marino.");
                return true;
            }
            String tierStr = args.length >= 2 ? args[1].toUpperCase() : "WOODEN_CHEST";
            try {
                DeepSeaTreasureSalvage.SalvageTier tier = DeepSeaTreasureSalvage.SalvageTier.valueOf(tierStr);
                player.getInventory().addItem(plugin.getDeepSeaTreasureSalvage().createSalvageItem(tier));
                player.sendMessage(ChatColor.GREEN + "✓ Has recibido un " + tier.getDisplayName());
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Tier inválido: WOODEN_CHEST, ANCIENT_LOCKBOX, ATLANTIS_VAULT");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("rig")) {
            if (args.length >= 2 && args[1].equalsIgnoreCase("deploy")) {
                plugin.getAquacultureRigManager().deployRig(player);
            } else if (args.length >= 2 && args[1].equalsIgnoreCase("collect")) {
                plugin.getAquacultureRigManager().collectRig(player);
            } else {
                plugin.getAquacultureRigManager().openRigGUI(player);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("boss")) {
            if (!player.hasPermission("fishingeconomy.admin")) {
                player.sendMessage(ChatColor.RED + "No tienes permisos para invocar jefes.");
                return true;
            }
            String type = args.length >= 2 ? args[1] : "kraken";
            boolean success = plugin.getOceanicBossRaid().spawnOceanicBoss(player.getLocation(), type);
            if (!success) {
                player.sendMessage(ChatColor.RED + "¡Ya hay un jefe oceánico activo en el servidor!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("admin")) {
            if (!player.hasPermission("fishingeconomy.admin")) {
                player.sendMessage(ChatColor.RED + "No tienes permisos de administrador.");
                return true;
            }

            if (args.length < 2) {
                sendAdminGuide(player);
                return true;
            }

            String sub = args[1].toLowerCase();
            if (sub.equals("forcemythic")) {
                plugin.getFishManager().setForceMythic(player.getUniqueId(), true);
                player.sendMessage(ChatColor.LIGHT_PURPLE + "✦ [Admin Debug] ¡Tu próxima captura será 100% GARANTIZADA MÍTICA!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
                return true;
            }

            if (sub.equals("forcemini")) {
                plugin.getFishManager().setForceMinigame(player.getUniqueId(), true);
                player.sendMessage(ChatColor.AQUA + "✦ [Admin Debug] ¡Tu próxima captura activará el minijuego de carrete interactivo!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.5f);
                return true;
            }

            if (sub.equals("giverod") && args.length >= 3) {
                try {
                    RodCraftingManager.CustomRodType type = RodCraftingManager.CustomRodType.valueOf(args[2].toUpperCase());
                    player.getInventory().addItem(plugin.getRodCraftingManager().createCustomRod(type));
                    player.sendMessage(ChatColor.GREEN + "✓ Caña " + type.name() + " agregada a tu inventario.");
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "Tipo inválido: LEVIATHAN_BANE, MAGMA_FISHER, SIREN_WEAVER");
                }
                return true;
            }

            if (sub.equals("spawnboss")) {
                String type = args.length >= 3 ? args[2] : "kraken";
                plugin.getOceanicBossRaid().spawnOceanicBoss(player.getLocation(), type);
                return true;
            }

            sendAdminGuide(player);
            return true;
        }

        sendInteractiveGuide(player);
        return true;
    }

    private void sendInteractiveGuide(Player player) {
        player.sendMessage(ChatColor.AQUA + "╔════════════════════════════════════════════════╗");
        player.sendMessage(ChatColor.AQUA + "║     " + ChatColor.GOLD + "🎣 GUÍA MAESTRA DE ECONOMY FISH" + ChatColor.AQUA + "     ║");
        player.sendMessage(ChatColor.AQUA + "╚════════════════════════════════════════════════╝");
        player.sendMessage(ChatColor.GRAY + "Haz clic en cualquier botón interactivo para probarlo:");

        sendClickable(player, "§6▶ §eAltar de Encantamientos de Caña §7(/fish enchant)", "/fish enchant", "§aAplicar encantamientos marinos especiales a tu caña");
        sendClickable(player, "§6▶ §eEquipo y Traje de Buceo Abisal §7(/fish suit)", "/fish suit", "§aComprar y equipar armadura de inmersión profunda");
        sendClickable(player, "§6▶ §eCaldero Alquímico de Cebos §7(/fish bait)", "/fish bait", "§aElaborar cebos biológicos y míticos");
        sendClickable(player, "§6▶ §eEstado de Mareas Oceánicas §7(/fish tides)", "/fish tides", "§aVer el clima marino y multiplicadores");
        sendClickable(player, "§6▶ §eEnciclopedia Codex de Peces §7(/fish codex)", "/fish codex", "§aVer todas las especies descubiertas");
        sendClickable(player, "§6▶ §ePlataforma de Acuicultura Offshore §7(/fish rig)", "/fish rig", "§aGestionar plataforma de pesca pasiva");
        sendClickable(player, "§6▶ §eAstilleros de Cañas Míticas §7(/customrod)", "/customrod", "§aVer y forjar cañas con bonos especiales");
        sendClickable(player, "§6▶ §eMercado de Peces y Venta §7(/fishshop)", "/fishshop", "§aVender peces especiales y ver precios");
        sendClickable(player, "§6▶ §eSubmarino Abisal Nautilus §7(/submarine)", "/submarine", "§aMontar submarino con respiración acuática");
        sendClickable(player, "§6▶ §eTorneo de Pesca en Vivo §7(/ftournament status)", "/ftournament status", "§aVer el estado del torneo actual");
        sendClickable(player, "§6▶ §eTaxidermia y Trofeos §7(/trophy)", "/trophy", "§aCrear placas decorativas con tus récords");
        sendClickable(player, "§6▶ §eBolsa de Valores en Mapa §7(/mapchart)", "/mapchart", "§aObtener gráfico bursátil en tiempo real");

        if (player.hasPermission("fishingeconomy.admin")) {
            player.sendMessage("");
            sendClickable(player, "§d⚡ §d[PANEL DE TESTING DE ADMINISTRADOR]", "/fish admin", "§cAcceder a comandos de forzado y depuración");
        }
        player.sendMessage(ChatColor.AQUA + "══════════════════════════════════════════════════");
    }

    private void sendAdminGuide(Player player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "╔════════════════════════════════════════════════╗");
        player.sendMessage(ChatColor.DARK_PURPLE + "║     " + ChatColor.LIGHT_PURPLE + "⚡ HERRAMIENTAS DE PRUEBA DE ADMIN" + ChatColor.DARK_PURPLE + "    ║");
        player.sendMessage(ChatColor.DARK_PURPLE + "╚════════════════════════════════════════════════╝");
        sendClickable(player, "§d• Forzar Próxima Captura Mítica", "/fish admin forcemythic", "§eTu siguiente pez será Mítico (Megalodon)");
        sendClickable(player, "§d• Forzar Minijuego de Pesca", "/fish admin forcemini", "§eActiva el minijuego de tensión de carrete");
        sendClickable(player, "§d• Obtener Caña Leviatán", "/fish admin giverod LEVIATHAN_BANE", "§eRecibir Caña Perdición del Leviatán");
        sendClickable(player, "§d• Obtener Caña Magmática", "/fish admin giverod MAGMA_FISHER", "§eRecibir Caña de Ignición Magmática");
        sendClickable(player, "§d• Invocar Kraken Abisal", "/fish boss kraken", "§eInvocar Jefe Mundial Kraken");
        sendClickable(player, "§d• Invocar Megalodón Ancestral", "/fish boss megalodon", "§eInvocar Jefe Mundial Megalodón");
        sendClickable(player, "§d• Iniciar Torneo de Pesca", "/ftournament start", "§eIniciar torneo global inmediatamente");
        sendClickable(player, "§d• Iniciar Expedición Kraken", "/expedition start", "§eIniciar expedición a aguas profundas");
        sendClickable(player, "§a• Auto-Update / Verificar GitHub", "/fish update", "§eVerificar y descargar actualizaciones de GitHub");
        sendClickable(player, "§a• Recarga en Caliente (Hot-Reload)", "/fish reload", "§eRecargar configuración y módulos sin reiniciar");
        player.sendMessage(ChatColor.DARK_PURPLE + "══════════════════════════════════════════════════");
    }

    private void sendClickable(Player player, String text, String command, String hover) {
        TextComponent component = new TextComponent(text);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        player.spigot().sendMessage(component);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> subs = new ArrayList<>(Arrays.asList("guide", "help", "rod", "codex", "rig", "bait", "tides", "suit", "enchant"));
            if (sender.hasPermission("fishingeconomy.admin")) {
                subs.add("admin");
                subs.add("boss");
                subs.add("salvage");
                subs.add("update");
                subs.add("reload");
            }
            return filter(subs, args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("update") && sender.hasPermission("fishingeconomy.admin")) {
            return filter(Arrays.asList("check", "apply", "download"), args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("salvage") && sender.hasPermission("fishingeconomy.admin")) {
            return filter(Arrays.asList("WOODEN_CHEST", "ANCIENT_LOCKBOX", "ATLANTIS_VAULT"), args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("rig")) {
            return filter(Arrays.asList("gui", "deploy", "collect"), args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("boss") && sender.hasPermission("fishingeconomy.admin")) {
            return filter(Arrays.asList("kraken", "megalodon"), args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("admin") && sender.hasPermission("fishingeconomy.admin")) {
            return filter(Arrays.asList("forcemythic", "forcemini", "giverod", "spawnboss"), args[1]);
        }
        if (args.length == 3 && args[1].equalsIgnoreCase("giverod") && sender.hasPermission("fishingeconomy.admin")) {
            return filter(Arrays.asList("LEVIATHAN_BANE", "MAGMA_FISHER", "SIREN_WEAVER"), args[2]);
        }
        if (args.length == 3 && args[1].equalsIgnoreCase("spawnboss") && sender.hasPermission("fishingeconomy.admin")) {
            return filter(Arrays.asList("kraken", "megalodon"), args[2]);
        }
        return new ArrayList<>();
    }

    private List<String> filter(List<String> list, String query) {
        String q = query.toLowerCase();
        return list.stream().filter(s -> s.toLowerCase().startsWith(q)).collect(Collectors.toList());
    }
}
