package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       04.12.2022 / 11:44

*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class RainbowTabCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "rainbowtab", true))
            return false;

        if (args.length == 0) {

            final User user = userHandler.getUserInstant(player.getUniqueId());

            if (user == null)
                return false;

            if (user.is(DataType.RAINBOWTAB)) {
                user.setBoolean(DataType.RAINBOWTAB, false);
                messageUtil.sendMessage(player, "Du hast RainbowTab §c§odeaktiviert§8.");
                return false;
            }

            user.setBoolean(DataType.RAINBOWTAB, true);
            messageUtil.sendMessage(player, "Du hast RainbowTab §a§oaktiviert§8.");

            return false;
        }

        if (!PermissionUtil.hasPermission(player, "rainbowtab.others", true))
            return false;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("all")) {
                if (Bools.rainbowtab) {
                    Bools.rainbowtab = false;
                    messageUtil.broadcast("Rainbowtab wurde für alle Spieler §c§odeaktiviert§8.");
                    return false;
                }

                Bools.rainbowtab = true;
                messageUtil.broadcast("Rainbowtab wurde für alle Spieler §a§oaktiviert§8.");

                return false;
            }

            if (!messageUtil.hasPlayedBefore(sender, args[0]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                if (user.is(DataType.RAINBOWTAB)) {
                    user.setBoolean(DataType.RAINBOWTAB, false);
                    messageUtil.sendMessage(player, "Du hast RainbowTab für " + target.getName() + " §c§odeaktiviert§8.");
                } else {
                    user.setBoolean(DataType.RAINBOWTAB, true);
                    messageUtil.sendMessage(player, "Du hast RainbowTab  für " + target.getName() + " §a§oaktiviert§8.");
                }
            });
        }

        return false;
    }
}
