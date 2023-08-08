package de.obey.crownmc.util;
/*

    Author - Obey -> SkySlayer-v4
       20.10.2022 / 03:53

*/

import de.obey.crownmc.CrownMain;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public final class InventoryUtil {

    public Inventory getConfirmation(final String title) {
        final Inventory inv = Bukkit.createInventory(null, 9*3, title);

        fillSideRows(inv, new ItemStack(Material.IRON_FENCE));

        inv.setItem(4, new ItemStack(Material.IRON_FENCE));
        inv.setItem(13, new ItemStack(Material.IRON_FENCE));
        inv.setItem(22, new ItemStack(Material.IRON_FENCE));

        final ItemStack green = new ItemBuilder(Material.STAINED_CLAY,1 , (byte) 5)
                .setDisplayname("§a§lBestätigen")
                .build();

        final ItemStack red = new ItemBuilder(Material.STAINED_CLAY,1 , (byte) 14)
                .setDisplayname("§c§lAbbrechen")
                .build();

        inv.setItem(1, green); inv.setItem(2, green); inv.setItem(3, green);
        inv.setItem(10, green); inv.setItem(11, green); inv.setItem(12, green);
        inv.setItem(19, green); inv.setItem(20, green); inv.setItem(21, green);

        inv.setItem(5, red); inv.setItem(6, red); inv.setItem(7, red);
        inv.setItem(14, red); inv.setItem(15, red); inv.setItem(16, red);
        inv.setItem(23, red); inv.setItem(24, red); inv.setItem(25, red);

        return inv;
    }

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
                amount += content.getAmount() / (double)item.getAmount();
                content.setType(Material.AIR);
            }
        }

        player.getInventory().setContents(items);

        return amount;
    }

    // hier ist was falsch
    public int removeItem(final Player player, final ItemStack item, final int removeAmount) {
        int amount = 0;

        for(int i = 0; i < player.getInventory().getSize(); i++) {
            final ItemStack content = player.getInventory().getItem(i);

            if (amount >= removeAmount)
                return amount;

            if (content != null && content.getType() != Material.AIR && content.getType() == item.getType() && content.getAmount() >= item.getAmount()) {
                if (amount + (content.getAmount() / (double)item.getAmount()) > (double) removeAmount) {
                    content.setAmount(content.getAmount() - ((removeAmount - amount) * item.getAmount()));
                    player.getInventory().setItem(i, content);
                    return removeAmount;
                }

                amount++;
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
        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR|| !player.getItemInHand().hasItemMeta() || !player.getItemInHand().getItemMeta().hasDisplayName())
            return false;

        return player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(displayname);
    }

    public boolean isItemInHandStartsWith(final Player player, final String displayname) {
        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR|| !player.getItemInHand().hasItemMeta() || !player.getItemInHand().getItemMeta().hasDisplayName())
            return false;

        return player.getItemInHand().getItemMeta().getDisplayName().startsWith(displayname);
    }

    public boolean hasItemInHand(final Player player) {
        final boolean state = !(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR);

        if (!state)
            CrownMain.getInstance().getInitializer().getMessageUtil().sendMessage(player, "Du musst ein Item in der Hand halten§8!");

        return state;
    }

    public boolean hasItemInHand(final Player player, final boolean send) {
        final boolean state = !(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR);

        if (!state && send)
            CrownMain.getInstance().getInitializer().getMessageUtil().sendMessage(player, "Du musst ein Item in der Hand halten§8!");

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
