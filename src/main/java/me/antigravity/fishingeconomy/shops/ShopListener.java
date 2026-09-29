package me.antigravity.fishingeconomy.shops;

import me.antigravity.fishingeconomy.FishingEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class ShopListener implements Listener {
    private final FishingEconomy plugin;

    public ShopListener(FishingEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String line0 = event.getLine(0);
        if (line0 != null && line0.equalsIgnoreCase("[Shop]")) {
            try {
                String line1 = event.getLine(1);
                String line2 = event.getLine(2);
                String line3 = event.getLine(3);

                int quantity = Integer.parseInt(line1);
                double price = Double.parseDouble(line2);
                Material material = Material.matchMaterial(line3);

                if (quantity <= 0 || price < 0 || material == null) {
                    event.getPlayer().sendMessage("§cInvalid shop format. Use:\n[Shop]\nQuantity\nPrice\nItemName");
                    event.setCancelled(true);
                    return;
                }

                event.setLine(0, "§9" + event.getPlayer().getName());
                event.setLine(1, String.valueOf(quantity));
                event.setLine(2, "B " + price);
                event.setLine(3, material.name());
                event.getPlayer().sendMessage("§aShop created successfully!");
            } catch (NumberFormatException | NullPointerException e) {
                event.getPlayer().sendMessage("§cInvalid number format.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Block block = event.getClickedBlock();
        if (block == null)
            return;

        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            String line0 = sign.getLine(0);
            String line2 = sign.getLine(2);

            if (line2.startsWith("B ")) {
                Player buyer = event.getPlayer();
                String ownerName = line0.replace("§9", "");
                OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerName);

                if (owner.getUniqueId().equals(buyer.getUniqueId())) {
                    buyer.sendMessage("§cYou cannot buy from your own shop.");
                    return;
                }

                int quantity = Integer.parseInt(sign.getLine(1));
                double price = Double.parseDouble(line2.substring(2));
                Material material = Material.matchMaterial(sign.getLine(3));

                if (material == null)
                    return;

                Block attached = block.getRelative(((Directional) block.getBlockData()).getFacing().getOppositeFace());
                if (!(attached.getState() instanceof Chest)) {
                    if (block.getType().toString().contains("WALL_SIGN")) {
                        // Already checked attached
                    } else {
                        attached = block.getRelative(0, -1, 0);
                    }
                }

                if (!(attached.getState() instanceof Chest)) {
                    buyer.sendMessage("§cShop is out of stock (No chest found).");
                    return;
                }

                Chest chest = (Chest) attached.getState();

                if (!plugin.getEconomyManager().has(buyer, price)) {
                    buyer.sendMessage("§cYou don't have enough money.");
                    return;
                }

                if (!chest.getInventory().containsAtLeast(new ItemStack(material), quantity)) {
                    buyer.sendMessage("§cShop is out of stock.");
                    return;
                }

                plugin.getEconomyManager().withdrawPlayer(buyer, price);
                plugin.getEconomyManager().depositPlayer(owner, price);

                chest.getInventory().removeItem(new ItemStack(material, quantity));
                buyer.getInventory().addItem(new ItemStack(material, quantity));

                buyer.sendMessage("§aBought " + quantity + " " + material.name() + " for "
                        + plugin.getEconomyManager().format(price));
                if (owner.isOnline()) {
                    owner.getPlayer().sendMessage("§a" + buyer.getName() + " bought " + quantity + " " + material.name()
                            + " from your shop.");
                }
            }
        }
    }
}
