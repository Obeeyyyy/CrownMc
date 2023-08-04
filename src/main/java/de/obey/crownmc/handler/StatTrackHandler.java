package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       01.08.2023 / 03:25

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.util.InventoryUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class StatTrackHandler {

    public void setStatTrack(final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        lore.add("");
        lore.add("§6§l    ☼ §e§oStattrack");

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public void addCount(final Player player) {

        if(!InventoryUtil.hasItemInHand(player, false) || !isStatTrack(player.getItemInHand()))
            return;

        final ItemStack item = player.getItemInHand();
        final ItemMeta meta = item.getItemMeta();
        final List<String> lore = meta.getLore();

        boolean found = false;
        for (int i = 0; i < lore.size(); i++) {
            if(lore.get(i).startsWith("§8➥ §7" + player.getName())) {
                final int kills = Integer.parseInt(lore.get(i).split(" ")[3]) + 1;
                lore.set(i, "§8➥ §7" + player.getName() + " §8♦§a§o " + kills + " §7" + (kills > 1 ? "Kills" : "Kill"));
                found = true;
                break;
            }
        }

        if(!found)
            lore.add("§8➥ §7" + player.getName() + " §8♦§a§o 1 §7Kill");

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public void reset(final ItemStack item)  {
        final ItemMeta meta = item.getItemMeta();
        final List<String> lore = meta.getLore();

        for (int i = 0; i < lore.size(); i++) {
            if(lore.get(i).startsWith("§8➥ §7") || lore.get(i).startsWith("§6§l    ☼ §e§oStattrack")) {
                lore.remove(i);
                i--;
                break;
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public boolean isStatTrack(final ItemStack item) {
        if(!item.hasItemMeta() || !item.getItemMeta().hasLore())
            return false;

        final List<String> lore = item.getItemMeta().getLore();

        for (final String line : lore) {
            if(line.equalsIgnoreCase("§6§l    ☼ §e§oStattrack"))
                return true;
        }

        return false;

    }

}
