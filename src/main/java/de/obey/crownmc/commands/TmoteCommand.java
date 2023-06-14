package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       23.10.2022 / 02:30

*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.ScoreboardHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class TmoteCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final ScoreboardHandler scoreboardHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "*", true))
            return false;

        final Player player = (Player) sender;

        if (args.length == 1) {

            if (!messageUtil.hasPlayedBefore(sender, args[0]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                if (user.getString(DataType.TMOTE).length() > 0) {
                    messageUtil.sendMessage(sender, target.getName() + "'s TMOTE§8:§r " + ChatColor.translateAlternateColorCodes('&', user.getString(DataType.TMOTE)));
                    return;
                }

                messageUtil.sendMessage(sender, target.getName() + " hat keinen TMOTE.");
            });

            return false;
        }

        if (args.length == 2) {
            if (!messageUtil.hasPlayedBefore(sender, args[0]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {

                if (args[1].equalsIgnoreCase("reset")) {

                    user.setString(DataType.TMOTE, "");

                    if (target.isOnline())
                        scoreboardHandler.setTablistName(target.getPlayer());

                    messageUtil.sendMessage(sender, "TMOTE von " + target.getName() + " wurde resettet.");
                    return;
                }

                user.setString(DataType.TMOTE, args[1]);

                if (target.isOnline())
                    scoreboardHandler.setTablistName(target.getPlayer());

                messageUtil.sendMessage(sender, "TMOTE von " + target.getName() + " wurde auf " + ChatColor.translateAlternateColorCodes('&', args[1]) + " §7gesetzt.");
            });
            return false;
        }

        messageUtil.sendSyntax(sender, "/tmote <spieler>", "/tmote <spieler> reset", "/tmote <spieler> <tmote>");

        return false;
    }
}
