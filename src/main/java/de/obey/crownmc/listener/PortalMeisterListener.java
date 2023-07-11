package de.obey.crownmc.listener;
/*

    Author - Obey -> CrownMc
       01.07.2023 / 22:31

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.LocationHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

@RequiredArgsConstructor @NonNull
public final class PortalMeisterListener implements Listener {

    private final MessageUtil messageUtil;
    private final ServerConfig serverConfig;
    private final UserHandler userHandler;
    private final LocationHandler locationHandler;

    @EventHandler
    public void on(final PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand))
            return;

        final Entity entity = event.getRightClicked();

        if (entity == null || entity.getCustomName() == null)
            return;

        if (entity.isCustomNameVisible())
            event.setCancelled(true);

        if (!entity.getCustomName().equalsIgnoreCase("§5§lPortal §7Meister"))
            return;

        openPortalInventory(event.getPlayer());
    }

    private void openPortalInventory(final Player player) {
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.2f, 1);

        final Inventory inventory = Bukkit.createInventory(null, 9*3, "§7Wähle eine Dimension");

        InventoryUtil.fillSideRows(inventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7.").build());

        inventory.setItem(11, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setTextur("NmY3ZGZmNTM5ZGE3NGMxMmI3ZTFiOGQyYzA3MzQ5OGM0ZThmNzc2ZWEyYzBkM2Y2ZDEyZmUwMmNmNDc1ZTUxMiJ9fX0=", UUID.randomUUID())
                        .setDisplayname("§8» §c§lNether §7Dimension")
                        .setLore("",
                                "§c§lINFORMATIONEN",
                                "§8  - §7Preis§8: §f§o" + messageUtil.formatLong(serverConfig.getNetherPrice()) + "§6§l$",
                                "§8  - §7PvP§8: §a§oAktiviert",
                                "")
                .build());

        inventory.setItem(15, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZTgzODNlZGRjZmFiOTExMDJhYjRhZTEwYjM0YjZlMjI3YzY4NTljOTRkMjgwNDk3N2VhMDFiMTZiOTk2MGNkMyJ9fX0=", UUID.randomUUID())
                .setDisplayname("§8» §f§lEnd §7Dimension")
                .setLore("",
                        "§f§lINFORMATIONEN",
                        "§8  - §7Preis§8: §f§o" + messageUtil.formatLong(serverConfig.getEndPrice()) + "§6§l$",
                        "§8  - §7PvP§8: §a§oAktiviert",
                        "")
                .build());

        player.openInventory(inventory);
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if(!InventoryUtil.isInventoryTitle(event.getInventory(), "§7Wähle eine Dimension"))
            return;

        event.setCancelled(true);

        if(!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§7Wähle eine Dimension"))
            return;

        final Player player = (Player) event.getWhoClicked();

        if(event.getSlot() == 11) {

            final Location nether = locationHandler.getLocation("nether");

            if(nether == null) {
                messageUtil.sendMessage(player, "Location existiert noch nicht§8.");
                return;
            }

            final User user = userHandler.getUserInstant(player.getUniqueId());

            if(!messageUtil.hasEnougthMoney(user, (long) serverConfig.getNetherPrice()))
                return;

            user.removeLong(DataType.MONEY, serverConfig.getNetherPrice());
            player.teleport(nether);
            player.playSound(player.getLocation(), Sound.PORTAL_TRAVEL, 0.5f, 1);

            return;
        }

        if(event.getSlot() == 15) {

            final Location end = locationHandler.getLocation("end");

            if(end == null) {
                messageUtil.sendMessage(player, "Location existiert noch nicht§8.");
                return;
            }

            final User user = userHandler.getUserInstant(player.getUniqueId());

            if(!messageUtil.hasEnougthMoney(user, (long) serverConfig.getEndPrice()))
                return;

            user.removeLong(DataType.MONEY, serverConfig.getEndPrice());
            player.teleport(end);
            player.playSound(player.getLocation(), Sound.PORTAL_TRAVEL, 0.2f, 1);
        }
    }

}
