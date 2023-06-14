package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       17.01.2023 / 09:48

*/

import de.obey.slayer.backend.user.UserPunishment;
import de.obey.slayer.handler.UserHandler;
import de.obey.slayer.util.MathUtil;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @NonNull
public final class MuteCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("mute")) {

            if (sender instanceof Player && !PermissionUtil.hasPermission(sender, "mute", true))
                return false;

            if(args.length >= 3) {
                if(!messageUtil.hasPlayedBefore(sender, args[0]))
                    return false;

                String timeValue = args[2];

                if(args.length > 3) {
                    for (int i = 3; i < args.length; i++)
                        timeValue = timeValue + " " + args[i];
                }

                try {

                    final long millis = MathUtil.getMillisFromString(timeValue);

                    if (millis <= 0) {
                        messageUtil.sendMessage(sender, "Bitte nutze <1h 10m 20s>");
                        return false;
                    }

                    userHandler.getUser(Bukkit.getOfflinePlayer(args[0]).getUniqueId()).thenAcceptAsync(user -> {
                        user.getPunishment().mute(sender, args[1], millis);
                    });

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Bitte nutze <1h 10m 20s>");
                }

                return false;
            }

        }

        if(command.getName().equalsIgnoreCase("unmute")) {

            if (sender instanceof Player && !PermissionUtil.hasPermission(sender, "unmute", true))
                return false;

            if(args.length == 1) {

                if(!messageUtil.hasPlayedBefore(sender, args[0]))
                    return false;

                userHandler.getUser(Bukkit.getOfflinePlayer(args[0]).getUniqueId()).thenAcceptAsync(user -> {
                    user.getPunishment().unMute(sender);
                });

                return false;
            }
        }

        if(command.getName().equalsIgnoreCase("check")) {
            if (sender instanceof Player && !PermissionUtil.hasPermission(sender, "check", true))
                return false;

            if(args.length == 1) {

                if(!messageUtil.hasPlayedBefore(sender, args[0]))
                    return false;

                userHandler.getUser(Bukkit.getOfflinePlayer(args[0]).getUniqueId()).thenAcceptAsync(user -> {
                    final UserPunishment punishment = user.getPunishment();

                    if(punishment.getMutes().isEmpty()){
                        messageUtil.sendMessage(sender, "Der Spieler §8'§e" + args[0] + "§8'§7 wurde noch nie gemutet§8.");
                        return;
                    }

                    punishment.sendMuteInfo(sender);
                });

                return false;
            }
        }


        messageUtil.sendSyntax(sender, "/mute <spieler> <grund> <1h 10m 20s>",
                "/unmute <spieler>",
                "/check <spieler>");

        return false;
    }
}
