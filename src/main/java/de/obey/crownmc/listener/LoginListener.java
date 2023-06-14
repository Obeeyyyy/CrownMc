package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 23:00

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.commands.WhitelistCommand;
import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

@RequiredArgsConstructor
@NonNull
public final class LoginListener implements Listener {

    private final Initializer initializer;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(final PlayerLoginEvent event) {

        final Player player = event.getPlayer();

        if (initializer.isRestarting())
            return;

        final String host = event.getHostname().contains(":") ? event.getHostname().split(":")[0].replace(".", ",").toLowerCase() : event.getHostname().replace(".", ",").toLowerCase();

        initializer.getServerConfig().checkLoginDomain(host);

        if (initializer.getServerConfig().isWhitelist()) {
            if (!PermissionUtil.hasPermission(player, "team", false)
                    && !WhitelistCommand.tempList.contains(player.getName().toLowerCase())) {

                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§c§oDie Whitelist ist aktiviert§8, §cwir arbeiten am Server §8...");

                if (Bools.nowl)
                    initializer.getMessageUtil().broadcast(player.getName() + " hat versucht den Server zu betreten. §8(§e§o" + host.replace(",", ".") + "§8)");

                return;
            }
        }

        if(initializer.getServerConfig().isBetawhitelist()) {
            if (!PermissionUtil.hasPermission(player, "team", false)
                    && !PermissionUtil.hasPermission(player, "beta", false)
                    && !WhitelistCommand.tempList.contains(player.getName().toLowerCase())) {

                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§c§oDer Server ist in der Betaphase§8. \n\n§7Trete unserem Discord bei um einen §9Beta Key§7 zu erhalten§8!\n§7discord.SkySlayer.de");

                if (Bools.nowl)
                    initializer.getMessageUtil().broadcast(player.getName() + " hat versucht den Server zu betreten. §8(§e§o" + host.replace(",", ".") + "§8)");

                return;
            }
        }

        // Register player if not registered
        if (!initializer.getUserHandler().isRegistered(player))
            initializer.getUserHandler().register(player);
    }

}
