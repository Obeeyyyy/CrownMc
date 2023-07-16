package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 23:00

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.backend.user.UserPunishment;
import de.obey.crownmc.commands.WhitelistCommand;
import de.obey.crownmc.objects.punishment.Ban;
import de.obey.crownmc.objects.punishment.BanReason;
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

        initializer.getUserHandler().getUser(event.getPlayer().getUniqueId()).thenAccept(user -> {
            final UserPunishment punishment = user.getPunishment();

            if(user.getPunishment().isBanned()) {
                final Ban ban = punishment.getBans().get(punishment.getBans().size() - 1);
                final BanReason reason = ban.getBanReason();
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, initializer.getBanHandler().getKickMessage(reason.getId(), punishment.getRemainingBanMillis(), ban.getAuthor(), false));
            }
        });

        if(event.getResult() == PlayerLoginEvent.Result.KICK_OTHER)
            return;

        final String host = event.getHostname().contains(":") ? event.getHostname().split(":")[0].replace(".", ",").toLowerCase() : event.getHostname().replace(".", ",").toLowerCase();

        initializer.getServerConfig().checkLoginDomain(host);

        if (initializer.getServerConfig().isWhitelist()) {
            if (!PermissionUtil.hasPermission(player, "team", false)
                    && !WhitelistCommand.tempList.contains(player.getName().toLowerCase())) {

                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "\n§c§oDie Whitelist ist aktiviert§8, §cwir arbeiten am Server §8...\n\n§7discord.crownmc.de");

                if (Bools.nowl)
                    initializer.getMessageUtil().broadcast(player.getName() + " hat versucht den Server zu betreten. §8(§e§o" + host.replace(",", ".") + "§8)");

                return;
            }
        }

        if(initializer.getServerConfig().isBetawhitelist()) {
            if (!PermissionUtil.hasPermission(player, "team", false)
                    && !PermissionUtil.hasPermission(player, "beta", false)
                    && !WhitelistCommand.tempList.contains(player.getName().toLowerCase())) {

                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "\n§c§oDie Whitelist ist aktiviert§8, §cwir arbeiten am Server §8...\n\n§7discord.crownmc.de");

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
