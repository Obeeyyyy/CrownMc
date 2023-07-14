package de.obey.crownmc.listener;
/*

    Author - Obey -> CrownMc
       13.07.2023 / 18:40

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.LocationHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.LocationUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class WarpAugeListener implements Listener {

    @NonNull
    private final MessageUtil messageUtil;

    @NonNull
    private final LocationHandler locationHandler;

    private final ArrayList<Player> settingLocation = new ArrayList<>();

    @EventHandler
    public void on(final PlayerInteractEvent event) {
        if(!InventoryUtil.isItemInHandWithDisplayname(event.getPlayer(), "§5§lWarp Auge"))
            return;

        event.setCancelled(true);

        final Player player = event.getPlayer();

        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            messageUtil.sendMessage(player, "Schreibe einen Namen für die Position in den Chat");
            messageUtil.sendMessage(player, "Schreibe §c§ocancel§7 um den Vorgang abzubrechen§8.");

            if(!settingLocation.contains(player))
                settingLocation.add(player);

            return;
        }

        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {

            final ItemStack item = player.getItemInHand();
            final ItemMeta meta = item.getItemMeta();
            final List<String> lore = meta.getLore();

            if (lore.get(13).length() < 3) {
                messageUtil.sendMessage(player, "Dieses §5§lWarp Auge§7 wurde noch nicht Kalibriert§8.");
                return;
            }

            final Location location = LocationUtil.decode(ChatColor.stripColor(lore.get(13)));

            if(location == null) {
                messageUtil.sendMessage(player, "Dieses §5§lWarp Auge§7 wurde noch nicht Kalibriert§8.");
                return;
            }

            if(location.getWorld() != player.getWorld()) {
                messageUtil.sendMessage(player, "Du musst in der selben Welt sein§8, §7um das Warp Auge nutzen zu können§8.");
                return;
            }

            final int uses = Integer.parseInt(lore.get(11).split(" ")[2]) - 1;

            if(uses > 0) {
                lore.set(11, "§8» §7Energie§8:§f " + uses);
                meta.setLore(lore);
                player.updateInventory();
            } else {
                InventoryUtil.removeItemInHand(player, 1);
            }

            locationHandler.teleportToLocation(player, location);
        }
    }

    public boolean isSettingLocation(final Player player, final String message) {

        if(settingLocation.contains(player)) {

            if (message.equalsIgnoreCase("cancel")) {
                settingLocation.remove(player);
                messageUtil.sendMessage(player, "Vorgang wurde abgebrochen§8.");
                return true;
            }

            if(!InventoryUtil.isItemInHandWithDisplayname(player, "§5§lWarp Auge")) {
                messageUtil.sendMessage(player, "Du musst das §5§lWarp Auge§7 in der Hand halten§8.");
                return true;
            }

            settingLocation.remove(player);

            final ItemStack item = player.getItemInHand();
            final ItemMeta meta = item.getItemMeta();
            final List<String> lore = meta.getLore();

            lore.set(12, "§8» §7Position Name§8:§f " + message);
            lore.set(13, "§0" + LocationUtil.encode(player.getLocation()));

            meta.setLore(lore);
            item.setItemMeta(meta);
            player.updateInventory();

            messageUtil.sendMessage(player, "Location §8'§f§o" + message + "§8'§7 gespeichert§8.");
            player.playSound(player.getLocation(), Sound.ENDERMAN_IDLE, 0.1f, 1);

            return true;
        }

        return false;
    }
}
