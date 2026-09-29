package me.antigravity.fishingeconomy.jobs;

import me.antigravity.fishingeconomy.FishingEconomy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        Inventory gui = Bukkit.createInventory(new JobsHolder(), 27, "§8Select a Job");

        addJobItem(gui, 10, Material.IRON_PICKAXE, "§eMiner", "§7Earn money by mining ores.");
        addJobItem(gui, 11, Material.GOLDEN_HOE, "§eFarmer", "§7Earn money by farming crops.");
        addJobItem(gui, 12, Material.IRON_SWORD, "§eHunter", "§7Earn money by killing mobs.");
        addJobItem(gui, 13, Material.FISHING_ROD, "§eFisherman", "§7Earn money by fishing.");
        addJobItem(gui, 14, Material.IRON_AXE, "§eWoodcutter", "§7Earn money by chopping wood.");

        // Quit Job
        ItemStack quit = new ItemStack(Material.BARRIER);
        ItemMeta meta = quit.getItemMeta();
        meta.setDisplayName("§cQuit Job");
        quit.setItemMeta(meta);
        gui.setItem(26, quit);

        player.openInventory(gui);
    }

    private void addJobItem(Inventory gui, int slot, Material mat, String name, String desc) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(desc, "§aClick to join!"));
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
                player.sendMessage("§cYou quit your job.");
                player.closeInventory();
                return;
            default:
                return;
        }

        if (job != JobsManager.JobType.NONE) {
            plugin.getJobsManager().setJob(player, job);
            player.sendMessage("§aYou joined the " + job.name() + " job!");
            player.closeInventory();
        }
    }

    private static class JobsHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
