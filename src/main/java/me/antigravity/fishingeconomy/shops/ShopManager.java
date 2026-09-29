package me.antigravity.fishingeconomy.shops;

import me.antigravity.fishingeconomy.FishingEconomy;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class ShopManager {
    public ShopManager(FishingEconomy plugin) {
    }

    public boolean isShopSign(Sign sign) {
        return sign.getLine(0).equals("§9[Shop]");
    }

    public void handlePurchase(Player buyer, Sign sign) {
        // String ownerName = sign.getLine(3);
        // In a real plugin, we'd store UUIDs, but for simplicity we use names or assume
        // online
        // For this phase, let's assume the sign stores the name.

        // int amount;
        // double price;
        // Material material;

        // try {
        // amount = Integer.parseInt(sign.getLine(1));
        // price = Double.parseDouble(sign.getLine(2).replace("$", ""));
        // material = Material.matchMaterial(sign.getLine(3)); // Wait, line 3 is item?
        // } catch (NumberFormatException | NullPointerException e) {
        // buyer.sendMessage("§cInvalid shop sign.");
        // return;
        // }

        // Wait, standard format:
        // [Shop]
        // Quantity
        // Price
        // Item
        // But we need to know the OWNER. Usually chest shops put owner on line 0 or 3.
        // Let's change format:
        // Line 0: [Shop] (Auto changes to Owner Name)
        // Line 1: Quantity
        // Line 2: Price
        // Line 3: Item

        // Actually, let's stick to a simple format where the sign is placed ON the
        // chest.
        // We can store the owner in the chest's persistent data or just assume the sign
        // creator is the owner if we track it.
        // But without a database of shops, we can't easily know the owner if it's just
        // a sign.
        // Standard ChestShop uses:
        // Line 0: Name
        // Line 1: Quantity
        // Line 2: B <Price> : S <Price>
        // Line 3: Item

        // Let's implement a simplified version:
        // Line 0: [Shop] -> Becomes Owner Name
        // Line 1: Quantity
        // Line 2: Price
        // Line 3: Item
    }
}
