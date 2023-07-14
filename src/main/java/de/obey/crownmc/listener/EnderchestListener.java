package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       20.10.2022 / 04:04

*/

import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
@NonNull
public final class EnderchestListener implements Listener {

    private final UserHandler userHandler;

    @EventHandler
    public void on(final PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);

                userHandler.getUserInstant(player.getUniqueId()).getEnderchest().openEnderchestSite(player, 1);

                return;
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (InventoryUtil.isItemInHandWithDisplayname(player, "§8» §5§lEnderchest §8× §7Seite")) {
                event.setCancelled(true);
                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> user.getEnderchest().addEnderchestSite());
                return;
            }

            if (InventoryUtil.isItemInHandWithDisplayname(player, "§8» §5§lEnderchest §8× §7Zeile")) {
                event.setCancelled(true);
                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> user.getEnderchest().addEnderchestZeile());
            }
        }
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {

        final Player player = (Player) event.getWhoClicked();

        if (InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§5§lEC§7 ")) {

            final OfflinePlayer chestOwner = Bukkit.getOfflinePlayer(event.getInventory().getTitle().split(" ")[1]);
            final User user = userHandler.getUserInstant(chestOwner.getUniqueId());

            if (!player.getName().equalsIgnoreCase(chestOwner.getName())) {
                if (!PermissionUtil.hasPermission(event.getWhoClicked(), "enderchest.other.edit", false)) {
                    if (!user.getEnderchest().getEnderchestTrusted().contains(player.getUniqueId().toString()))
                        event.setCancelled(true);
                }
            }
        }

        if (InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§5§lEC§7 ")) {

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER) {
                event.setCancelled(true);
                return;
            }

            final OfflinePlayer chestOwner = Bukkit.getOfflinePlayer(event.getInventory().getTitle().split(" ")[1]);
            final User user = userHandler.getUserInstant(chestOwner.getUniqueId());

            if (!chestOwner.getName().equalsIgnoreCase(player.getName())) {
                if (!PermissionUtil.hasPermission(player, "enderchest.other.edit", false)) {
                    if (!user.getEnderchest().getEnderchestTrusted().contains(player.getUniqueId().toString())) {
                        event.setCancelled(true);
                    }
                }
            }

            if (InventoryUtil.startsWithInventoryTitle(event.getClickedInventory(), "§5§lEC§7 ")) {
                final int site = Integer.parseInt(event.getClickedInventory().getTitle().split(" ")[2]);

                if (event.getSlot() >= 9 * user.getEnderchest().getEnderchestInformation().get(site + ".rows")) {
                    event.setCancelled(true);

                    if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.SKULL_ITEM) {

                        if (event.getSlot() == 48 && chestOwner.getUniqueId() == player.getUniqueId()) {

                            final boolean trustedLocked = user.getEnderchest().getEnderchestInformation().get(site + ".locked.trusted") != 0;
                            final boolean allLocked = user.getEnderchest().getEnderchestInformation().get(site + ".locked.all") != 0;

                            if (event.isLeftClick()) {

                                if (allLocked) {
                                    user.getEnderchest().getEnderchestInformation().put(site + ".locked.all", 0);
                                } else {
                                    user.getEnderchest().getEnderchestInformation().put(site + ".locked.all", 1);
                                }

                            } else if (event.isRightClick()) {

                                if (trustedLocked) {
                                    user.getEnderchest().getEnderchestInformation().put(site + ".locked.trusted", 0);
                                } else {
                                    user.getEnderchest().getEnderchestInformation().put(site + ".locked.trusted", 1);
                                }

                            }

                            user.getEnderchest().setLastEcRow(event.getInventory(), site);
                            player.playSound(player.getLocation(), Sound.CLICK, 0.5f, 1);

                            return;
                        }

                        if (event.getSlot() == 52) {
                            user.getEnderchest().openEnderchestSite(player, site + 1);
                        } else if (event.getSlot() == 46) {
                            user.getEnderchest().openEnderchestSite(player, site - 1);
                        }
                    }
                }
            }
        }
    }

}
