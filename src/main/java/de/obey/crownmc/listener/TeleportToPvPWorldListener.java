package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       08.12.2022 / 14:53

*/

import de.obey.crownmc.handler.LocationHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
@NonNull
public final class TeleportToPvPWorldListener implements Listener {

    private final LocationHandler locationHandler;

    @EventHandler
    public void onMove(final PlayerMoveEvent event) {

        final Location spawn = locationHandler.getLocation("spawn");

        if (spawn == null)
            return;

        final Location pvp = locationHandler.getLocation("pvp");

        if (pvp == null)
            return;

        final Player player = event.getPlayer();

        if (player.getWorld() != spawn.getWorld())
            return;

        if (player.getLocation().getBlock().getType() == Material.ENDER_PORTAL) {
            event.setCancelled(true);
            locationHandler.teleportToLocationNameInstant(player, "pvp");
            player.sendTitle("§4§l⚠ §c§lAchtung §4§l⚠", "§7Du bist nun in der §c§oPvP§8-§c§oZone§8.");

            return;
        }

        if (player.getLocation().getY() < 100) {
            event.setCancelled(true);
            locationHandler.teleportToLocationNameInstant(player, "pvp");
            player.sendTitle("§4§l⚠ §c§lAchtung §4§l⚠", "§7Du bist nun in der §c§oPvP§8-§c§oZone§8.");
        }

    }

}
