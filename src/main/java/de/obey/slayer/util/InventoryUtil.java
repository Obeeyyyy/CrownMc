package de.obey.slayer.util;
/*

    Author - Obey -> SkySlayer-v4
       20.10.2022 / 03:53

*/

import de.obey.slayer.SlayerMain;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public final class InventoryUtil {

    public void fillFromTo(final Inventory inventory, final ItemStack itemStack, int from, int to) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i >= from && i <= to)
                inventory.setItem(i, itemStack);
        }
    }

    public void fill(final Inventory inventory, final ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, itemStack);
    }

    public void fillSideRows(final Inventory inventory, final ItemStack itemStack) {

        if (inventory.getSize() >= 9) {
            inventory.setItem(0, itemStack);
            inventory.setItem(8, itemStack);
        }

        if (inventory.getSize() >= 18) {
            inventory.setItem(9, itemStack);
            inventory.setItem(17, itemStack);
        }

        if (inventory.getSize() >= 27) {
            inventory.setItem(18, itemStack);
            inventory.setItem(26, itemStack);
        }

        if (inventory.getSize() >= 36) {
            inventory.setItem(27, itemStack);
            inventory.setItem(35, itemStack);
        }

        if (inventory.getSize() >= 45) {
            inventory.setItem(36, itemStack);
            inventory.setItem(44, itemStack);
        }

        if (inventory.getSize() >= 54) {
            inventory.setItem(45, itemStack);
            inventory.setItem(53, itemStack);
        }

        if (inventory.getSize() >= 63) {
            inventory.setItem(54, itemStack);
            inventory.setItem(62, itemStack);
        }
    }

    public void addItem(final Player player, final ItemStack item) {
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item.clone());
            return;
        }

        player.getInventory().addItem(item);
    }

    public void addItem(final Player player, final ItemStack item, final int amount) {

        item.setAmount(amount);

        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item.clone());
            return;
        }

        player.getInventory().addItem(item.clone());
    }

    public int removeItem(final Player player, final ItemStack item) {
        int amount = 0;
        final ItemStack[] items = player.getInventory().getContents().clone();
        for (ItemStack content : items) {
            if (content != null && content.getType() == item.getType()) {
                amount += content.getAmount() / item.getAmount();
                content.setType(Material.AIR);
            }
        }

        player.getInventory().setContents(items);

        return amount;
    }

    // hier ist was falsch
    public int removeItem(final Player player, final ItemStack item, final int max) {
        int amount = 0;

        for(int i = 0; i < player.getInventory().getSize(); i++) {
            final ItemStack content = player.getInventory().getItem(i);

            if (amount >= max)
                return amount;

            if (content != null && content.getType() != Material.AIR && content.getType() == item.getType()) {
                if (amount + content.getAmount() / item.getAmount() > max) {
                    content.setAmount(content.getAmount() - (max - amount));
                    player.getInventory().setItem(i, content);
                    return max;
                }

                amount += content.getAmount() / item.getAmount();
                content.setType(Material.AIR);
                player.getInventory().setItem(i, content);
            }
        }

        return amount;
    }

    public void removeItemInHand(final Player player, final int amount) {
        if (player.getItemInHand().getAmount() <= amount) {
            player.setItemInHand(new ItemStack(Material.AIR));
        } else {
            final ItemStack item = player.getItemInHand().clone();
            
            item.setAmount(item.getAmount() - amount);
            player.setItemInHand(item);
        }
    }

    public boolean isItemInHandWithDisplayname(final Player player, final String displayname) {
        if (player.getItemInHand() == null || !player.getItemInHand().hasItemMeta() || !player.getItemInHand().getItemMeta().hasDisplayName())
            return false;

        return player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(displayname);
    }

    public boolean isItemInHandStartsWith(final Player player, final String displayname) {
        if (player.getItemInHand() == null || !player.getItemInHand().hasItemMeta() || !player.getItemInHand().getItemMeta().hasDisplayName())
            return false;

        return player.getItemInHand().getItemMeta().getDisplayName().startsWith(displayname);
    }

    public boolean hasItemInHand(final Player player) {
        final boolean state = !(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR);

        if (!state)
            SlayerMain.getInstance().getInitializer().getMessageUtil().sendMessage(player, "Du musst ein Item in der Hand halten§8!");

        return state;
    }

    public boolean hasItemInHand(final Player player, final boolean send) {
        final boolean state = !(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR);

        if (!state && send)
            SlayerMain.getInstance().getInitializer().getMessageUtil().sendMessage(player, "Du musst ein Item in der Hand halten§8!");

        return state;
    }

    public boolean isInventoryTitle(final Inventory inventory, final String title) {
        if (inventory == null)
            return false;

        if (inventory.getTitle() == null)
            return false;

        return inventory.getTitle().equalsIgnoreCase(title);
    }

    public boolean startsWithInventoryTitle(final Inventory inventory, final String title) {
        if (inventory == null)
            return false;

        if (inventory.getTitle() == null)
            return false;

        return inventory.getTitle().startsWith(title);
    }

}
