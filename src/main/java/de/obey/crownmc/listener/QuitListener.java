package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       13.10.2022 / 17:46

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.commands.AfkCommand;
import de.obey.crownmc.commands.FreezeCommand;
import de.obey.crownmc.commands.VanishCommand;
import de.obey.crownmc.objects.gambling.CoinFlip;
import de.obey.crownmc.objects.pvp.Combat;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class QuitListener implements Listener {

    @NonNull
    private final Initializer initializer;

    @EventHandler
    public void on(final PlayerQuitEvent event) {
        event.setQuitMessage("");

        final Player player = event.getPlayer();

        // remove saved messages from chatfilter
        initializer.getChatFilterHandler().getChatFilterCache().remove(player.getUniqueId());

        // Check combattab
        final Combat combat = initializer.getCombatHandler().isInCombat(player);
        if (combat != null)
            combat.logout();

        if (initializer.getCoinflipHandler().isInCoinFlip(player)) {
            final CoinFlip coinFlip = initializer.getCoinflipHandler().getCoinflipFromPlayer(player);

            if (coinFlip.getState() == 0) {
                initializer.getCoinflipHandler().removeCoinflip(coinFlip);
            }
        }

        AfkCommand.afkList.remove(player);

        // Save data
        initializer.getUserHandler().getUser(event.getPlayer().getUniqueId()).thenAcceptAsync(user -> {
            user.setLong(DataType.LASTSEEN, System.currentTimeMillis());

            initializer.getUserHandler().saveData(user);

            if (user.getPacketReader() != null)
                user.getPacketReader().uninject();

            user.getPlaytime().updatePlaytime();

            if (initializer.isRestarting())
                initializer.getUserHandler().getUserCache().remove(user.getOfflinePlayer().getUniqueId());

            if (user.getString(DataType.LEAVEMESSAGE).length() > 0 && PermissionUtil.hasPermission(player, "leavemessage", false))
                initializer.getMessageUtil().broadcastNoPrefix("§e§lLEAVE§8 × §r" + ChatColor.translateAlternateColorCodes('&', user.getString(DataType.LEAVEMESSAGE)).replace("%name%", player.getName()));

            if(initializer.getServerConfig().isPlaytest()) {
                if(user.getPlaytime().getCurrentPlaytime() >= 60*60)
                    initializer.getPlaytestHandler().addTester(player.getUniqueId());
            }

            initializer.getMessageUtil().log("#> " + player.getName() + " LEFT ( " + initializer.getMessageUtil().formatLong(user.getLong(DataType.MONEY)) + "$, " + user.getLong(DataType.KILLS) + "/" + user.getLong(DataType.DEATHS) + " - " + initializer.getMessageUtil().formatLong(user.getLong(DataType.CROWNS)) + "C )");
        });

        // Update Scoreboard
        initializer.getScoreboardHandler().getTeams().remove(event.getPlayer().getScoreboard());
        initializer.getScoreboardHandler().updateEverythingForEveryone();

        // Remove from vanish list
        VanishCommand.vanished.remove(event.getPlayer());

        //Freezed stuff
        if (FreezeCommand.getFreezed().contains(player.getUniqueId()))
            initializer.getMessageUtil().sendMessageToTeamMembers("§3§lFREEZE§8 > §f§o" + player.getName() + "§7 hat den Server verlassen§8.");
    }

}
