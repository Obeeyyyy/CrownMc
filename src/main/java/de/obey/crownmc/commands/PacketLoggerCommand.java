package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       25.10.2022 / 00:59

*/

import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class PacketLoggerCommand implements CommandExecutor {

    @NonNull
    private final UserHandler userHandler;

    @NonNull
    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "*", true))
            return false;

        if (args.length == 1) {

            if (!messageUtil.isOnline(sender, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            final User user = userHandler.getUserInstant(target.getUniqueId());

            if (user.getPacketReader().isLogger()) {
                user.getPacketReader().setLogger(false);
                messageUtil.sendMessage(sender, "PacketLogger für " + target.getName() + " deaktiviert§8.");
                return false;
            }

            user.getPacketReader().setLogger(true);
            messageUtil.sendMessage(sender, "PacketLogger für " + target.getName() + " aktiviert§8.");

            return false;
        }

        messageUtil.sendSyntax(sender, "/packetlogger <spieler>");

        return false;
    }
}
