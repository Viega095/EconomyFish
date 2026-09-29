package me.antigravity.fishingeconomy.gambling;

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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SlotsGui implements Listener, InventoryHolder {
    private final FishingEconomy plugin;
    private final Inventory inventory;
    private final Random random = new Random();
    private final Material[] symbols = {
            Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT, Material.IRON_INGOT, Material.COAL,
            Material.REDSTONE
    };

    public SlotsGui(FishingEconomy plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 27, "§6Slots Machine");
        setupGui();
    }

    private void setupGui() {
        ItemStack spin = new ItemStack(Material.LEVER);
        ItemMeta meta = spin.getItemMeta();
        meta.setDisplayName("§a§lSPIN! ($100)");
        spin.setItemMeta(meta);
        inventory.setItem(22, spin);

        ItemStack bg = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bgMeta = bg.getItemMeta();
        bgMeta.setDisplayName(" ");
        bg.setItemMeta(bgMeta);

        for (int i = 0; i < 27; i++) {
            if (i != 22 && (i < 9 || i > 17)) {
                inventory.setItem(i, bg);
            }
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this)
            return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR)
            return;

        if (clicked.getType() == Material.LEVER) {
            if (!plugin.getEconomyManager().has(player, 100)) {
                player.sendMessage("§cYou need $100 to spin!");
                return;
            }

            plugin.getEconomyManager().withdrawPlayer(player, 100);
            spin(player);
        }
    }

    private void spin(Player player) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 20) { // Stop after 1 second
                    cancel();
                    calculateResult(player);
                    return;
                }

                // Animate slots
                inventory.setItem(11, new ItemStack(symbols[random.nextInt(symbols.length)]));
                inventory.setItem(13, new ItemStack(symbols[random.nextInt(symbols.length)]));
                inventory.setItem(15, new ItemStack(symbols[random.nextInt(symbols.length)]));
                player.updateInventory();
                ticks += 2;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void calculateResult(Player player) {
        ItemStack s1 = inventory.getItem(11);
        ItemStack s2 = inventory.getItem(13);
        ItemStack s3 = inventory.getItem(15);

        if (s1 != null && s2 != null && s3 != null) {
            if (s1.getType() == s2.getType() && s2.getType() == s3.getType()) {
                // Jackpot!
                double prize = 0;
                switch (s1.getType()) {
                    case DIAMOND:
                        prize = 5000;
                        break;
                    case EMERALD:
                        prize = 2500;
                        break;
                    case GOLD_INGOT:
                        prize = 1000;
                        break;
                    case IRON_INGOT:
                        prize = 500;
                        break;
                    default:
                        prize = 250;
                        break;
                }
                plugin.getEconomyManager().depositPlayer(player, prize);
                player.sendMessage("§6§lJACKPOT! §aYou won " + plugin.getEconomyManager().format(prize) + "!");
                Bukkit.broadcastMessage("§6" + player.getName() + " won the Slots Jackpot!");
            } else if (s1.getType() == s2.getType() || s2.getType() == s3.getType() || s1.getType() == s3.getType()) {
                // Small win
                plugin.getEconomyManager().depositPlayer(player, 50);
                player.sendMessage("§aSmall win! You got $50 back.");
            } else {
                player.sendMessage("§cYou lost. Try again!");
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
