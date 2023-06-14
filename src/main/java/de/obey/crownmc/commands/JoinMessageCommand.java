package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       15.10.2022 / 17:31

*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.ChatFilterHandler;
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
public final class JoinMessageCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final ChatFilterHandler chatFilterHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "joinmessage", true))
            return false;

        userHandler.getUser(player.getUniqueId()).thenAccept(user -> {
            if (args.length > 0) {

                if (args[0].equalsIgnoreCase("reset")) {

                    if (args.length == 1) {
                        user.setString(DataType.JOINMESSAGE, "");
                        messageUtil.sendMessage(sender, "Deine JoinMessage wurde entfernt.");
                        return;
                    }

                    if (!PermissionUtil.hasPermission(player, "*", true))
                        return;

                    if (!messageUtil.hasPlayedBefore(sender, args[1]))
                        return;

                    final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                    userHandler.getUser(offlinePlayer.getUniqueId()).thenAcceptAsync(target -> {
                        target.setString(DataType.JOINMESSAGE, "");

                        messageUtil.sendMessage(sender, "Die JoinMessage von " + offlinePlayer.getName() + " wurde gelöscht.");
                    });

                    return;
                }

                if(args[0].equalsIgnoreCase("check")) {
                    if (PermissionUtil.hasPermission(player, "joinmessage.edit", true)) {
                        if (!messageUtil.hasPlayedBefore(sender, args[1]))
                            return;

                        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                        userHandler.getUser(offlinePlayer.getUniqueId()).thenAcceptAsync(target -> {
                            messageUtil.sendMessage(sender, "Die JoinMessage von " + offlinePlayer.getName() + " ist§8: §r" + target.getString(DataType.JOINMESSAGE));
                        });
                        return;
                    }
                }

                if(args[0].equalsIgnoreCase("help")) {
                    if (PermissionUtil.hasPermission(player, "joinmessage.edit", true)) {
                        messageUtil.sendSyntax(player, "/joinmessage check <spieler>" , "/joinmessage reset <spieler>");
                        return;
                    }
                }

                String message = args[0];

                if (args.length > 1) {
                    for (int i = 1; i < args.length; i++)
                        message = message + " " + args[i];
                }

                if(message.toLowerCase().contains("&k")) {
                    messageUtil.sendMessage(player, "Der Code §f&§fk §7ist verboten§8.");
                    return;
                }

                if (!message.contains("%name%")) {
                    messageUtil.sendMessage(player, "Du musst deinen Namen ( %name% ) angeben§8.");
                    return;
                }

                if(!chatFilterHandler.checkString(message)) {
                    messageUtil.sendMessage(player, "Die JoinMessage enthält unangebrachte Wörter ... bitte wähle eine andere§8.");
                    return;
                }

                user.setString(DataType.JOINMESSAGE, message);
                messageUtil.sendMessage(sender, "Deine JoinMessage wurde gesetzt.");
                messageUtil.sendMessage(sender, message);

                return;
            }

            if (user.getString(DataType.JOINMESSAGE).length() > 0) {
                messageUtil.sendMessage(player, "Deine joinmessage ist: " + ChatColor.translateAlternateColorCodes('&', user.getString(DataType.JOINMESSAGE).replace("%name%", player.getName())));
                messageUtil.sendMessage(sender, "/joinmessage reset - Um deine JoinMessage zu entfernen.");
                return;
            }

            messageUtil.sendMessage(player, "Du hast keine JoinMessage gesetzt.");
            messageUtil.sendMessage(sender, "/joinmessage <%name% ist gejoint> - Um deine Joinmessage zu setzten.");
        });

        return false;
    }
}
