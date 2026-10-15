package com.fishingeconomy.jobs;

import com.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class JobsGui implements Listener {
    private final FishingEconomy plugin;

    public JobsGui(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    public void openJobsGui(Player player) {
        Inventory gui = Bukkit.createInventory(new JobsHolder(), 27, "§8Seleccionar Trabajo");

        addJobItem(gui, 10, Material.IRON_PICKAXE, "§eMinero", "§7Gana dinero extrayendo minerales.");
        addJobItem(gui, 11, Material.GOLDEN_HOE, "§eGranjero", "§7Gana dinero cosechando cultivos.");
        addJobItem(gui, 12, Material.IRON_SWORD, "§eCazador", "§7Gana dinero derrotando criaturas.");
        addJobItem(gui, 13, Material.FISHING_ROD, "§ePescador", "§7Gana dinero pescando especies.");
        addJobItem(gui, 14, Material.IRON_AXE, "§eLeñador", "§7Gana dinero talando madera.");

        // Quit Job
        ItemStack quit = new ItemStack(Material.BARRIER);
        ItemMeta meta = quit.getItemMeta();
        meta.setDisplayName("§cDejar Trabajo");
        quit.setItemMeta(meta);
        gui.setItem(26, quit);

        player.openInventory(gui);
    }

    private void addJobItem(Inventory gui, int slot, Material mat, String name, String desc) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(desc, "§a¡Haz clic para unirte!"));
        item.setItemMeta(meta);
        gui.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        if (event.getClickedInventory() == null)
            return;
        if (!(event.getView().getTopInventory().getHolder() instanceof JobsHolder))
            return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR)
            return;

        JobsManager.JobType job = JobsManager.JobType.NONE;

        switch (clicked.getType()) {
            case IRON_PICKAXE:
                job = JobsManager.JobType.MINER;
                break;
            case GOLDEN_HOE:
                job = JobsManager.JobType.FARMER;
                break;
            case IRON_SWORD:
                job = JobsManager.JobType.HUNTER;
                break;
            case FISHING_ROD:
                job = JobsManager.JobType.FISHERMAN;
                break;
            case IRON_AXE:
                job = JobsManager.JobType.WOODCUTTER;
                break;
            case BARRIER:
                plugin.getJobsManager().setJob(player, JobsManager.JobType.NONE);
                player.sendMessage("§cHas dejado tu trabajo actual.");
                player.closeInventory();
                return;
            default:
                return;
        }

        if (job != JobsManager.JobType.NONE) {
            plugin.getJobsManager().setJob(player, job);
            player.sendMessage("§a¡Te has unido exitosamente al trabajo de " + job.name() + "!");
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof JobsHolder) {
            event.setCancelled(true);
        }
    }

    public static class JobsHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
