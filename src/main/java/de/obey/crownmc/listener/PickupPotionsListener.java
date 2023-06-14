package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       14.10.2022 / 17:47

*/

import de.obey.crownmc.CrownMain;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public final class PickupPotionsListener implements Listener {

    @EventHandler
    public void itemPiuckup(final PlayerPickupItemEvent e) {
        final Player player = e.getPlayer();

        if (e.getItem().getItemStack().getType() == Material.POTION || e.getItem().getItemStack().getType() == Material.ENDER_PEARL) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    stackItems(player);
                }
            }.runTaskLater(CrownMain.getInstance(), 1);
        }
    }


    public static void stackItems(final Player player) {
        final ItemStack[] contents = player.getInventory().getContents();

        int changed = 0;
        int i = 0;
        while (i < contents.length) {
            ItemStack current = contents[i];

            if (current != null && current.getType() != null && current.getType() != Material.AIR) {

                int maxStack = current.getType() == Material.POTION ? 3 : 64;

                if (current.getAmount() < maxStack && current.getType() == Material.POTION || current.getType() == Material.ENDER_PEARL) {
                    int needed = maxStack - current.getAmount();
                    int i2 = i + 1;
                    while (i2 < contents.length) {
                        if (contents[i2] != null && contents[i2].getType() != Material.AIR && contents[i2].getAmount() > 0 && current.getType() == contents[i2].getType()) {
                            final ItemStack nextCurrent = contents[i2].clone();
                            if (current.getDurability() == nextCurrent.getDurability() && (current.getItemMeta() == null && nextCurrent.getItemMeta() == null || current.getItemMeta() != null && current.getItemMeta().equals((Object) nextCurrent.getItemMeta()))) {
                                if (nextCurrent.getAmount() > needed) {
                                    current.setAmount(maxStack);
                                    contents[i2].setAmount(nextCurrent.getAmount() - needed);
                                    ++changed;
                                } else {
                                    contents[i2].setType(Material.AIR);
                                    current.setAmount(current.getAmount() + nextCurrent.getAmount());
                                    needed = maxStack - current.getAmount();
                                    ++changed;
                                }
                            }
                        }
                        ++i2;
                    }
                }
            }
            ++i;
        }

        if (changed > 0) {
            player.getInventory().setContents(contents);
            player.updateInventory();
            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 5, 5);
        }
    }

}
