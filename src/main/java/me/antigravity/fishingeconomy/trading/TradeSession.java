package me.antigravity.fishingeconomy.trading;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class TradeSession implements InventoryHolder {
    private final FishingEconomy plugin;
    private final Player p1;
    private final Player p2;
    private final Inventory inventory;
    private boolean p1Ready = false;
    private boolean p2Ready = false;
    private boolean finished = false;

    public TradeSession(FishingEconomy plugin, Player p1, Player p2) {
        this.plugin = plugin;
        this.p1 = p1;
        this.p2 = p2;
        this.inventory = Bukkit.createInventory(this, 54,
                "§8Trade: " + p1.getName() + " vs " + p2.getName());
        setupGui();
    }

    private void setupGui() {
        ItemStack divider = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = divider.getItemMeta();
        meta.setDisplayName(" ");
        divider.setItemMeta(meta);

        for (int i = 0; i < 6; i++) {
            inventory.setItem(i * 9 + 4, divider);
        }

        updateStatusButtons();
    }

    private void updateStatusButtons() {
        ItemStack p1Button = new ItemStack(p1Ready ? Material.LIME_WOOL : Material.RED_WOOL);
        ItemMeta p1Meta = p1Button.getItemMeta();
        p1Meta.setDisplayName(
                p1Ready ? "§a" + p1.getName() + " Ready" : "§c" + p1.getName() + " Not Ready");
        p1Button.setItemMeta(p1Meta);
        inventory.setItem(45, p1Button);

        ItemStack p2Button = new ItemStack(p2Ready ? Material.LIME_WOOL : Material.RED_WOOL);
        ItemMeta p2Meta = p2Button.getItemMeta();
        p2Meta.setDisplayName(
                p2Ready ? "§a" + p2.getName() + " Ready" : "§c" + p2.getName() + " Not Ready");
        p2Button.setItemMeta(p2Meta);
        inventory.setItem(53, p2Button);

        // Accept button in middle
        ItemStack accept = new ItemStack(Material.NETHER_STAR);
        ItemMeta acceptMeta = accept.getItemMeta();
        acceptMeta.setDisplayName("§6Click to Toggle Ready");
        accept.setItemMeta(acceptMeta);
        inventory.setItem(49, accept);
    }

    public void open() {
        p1.openInventory(inventory);
        p2.openInventory(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null)
            return;

        Player clicker = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        // Allow clicking in own inventory
        if (slot >= 54) {
            event.setCancelled(false);
            // If they shift click, we need to handle it carefully or block it for
            // simplicity
            if (event.isShiftClick())
                event.setCancelled(true);
            return;
        }

        // Allow placing items in own side
        boolean isP1 = clicker.equals(p1);
        if (isP1) {
            if ((slot % 9) < 4 && slot < 45) {
                event.setCancelled(false); // Allow interaction in P1's area
                p1Ready = false;
                p2Ready = false; // Reset ready on change
                updateStatusButtons();
            }
        } else {
            if ((slot % 9) > 4 && slot < 54 && slot != 53) {
                event.setCancelled(false); // Allow interaction in P2's area
                p1Ready = false;
                p2Ready = false; // Reset ready on change
                updateStatusButtons();
            }
        }

        // Handle Ready Button
        if (slot == 49) {
            if (isP1)
                p1Ready = !p1Ready;
            else
                p2Ready = !p2Ready;
            updateStatusButtons();
            checkFinish();
        }
    }

    private void checkFinish() {
        if (p1Ready && p2Ready) {
            finished = true;
            p1.closeInventory();
            p2.closeInventory();

            // Give items
            for (int i = 0; i < 45; i++) {
                if ((i % 9) < 4) { // P1 items -> P2
                    ItemStack item = inventory.getItem(i);
                    if (item != null)
                        p2.getInventory().addItem(item);
                } else if ((i % 9) > 4) { // P2 items -> P1
                    ItemStack item = inventory.getItem(i);
                    if (item != null)
                        p1.getInventory().addItem(item);
                }
            }

            p1.sendMessage("§aTrade completed!");
            p2.sendMessage("§aTrade completed!");
            plugin.getTradeManager().endTrade(this);
        }
    }

    public void handleClose(InventoryCloseEvent event) {
        if (finished)
            return;

        // Cancel trade, return items
        finished = true;
        if (event.getPlayer().equals(p1))
            p2.closeInventory();
        else
            p1.closeInventory();

        for (int i = 0; i < 45; i++) {
            if ((i % 9) < 4) { // P1 items -> P1
                ItemStack item = inventory.getItem(i);
                if (item != null)
                    p1.getInventory().addItem(item);
            } else if ((i % 9) > 4) { // P2 items -> P2
                ItemStack item = inventory.getItem(i);
                if (item != null)
                    p2.getInventory().addItem(item);
            }
        }

        p1.sendMessage("§cTrade cancelled.");
        p2.sendMessage("§cTrade cancelled.");
        plugin.getTradeManager().endTrade(this);
    }

    public Player getP1() {
        return p1;
    }

    public Player getP2() {
        return p2;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
