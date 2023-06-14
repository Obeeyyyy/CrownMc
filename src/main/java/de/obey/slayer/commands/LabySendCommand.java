package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       31.12.2022 / 14:59

*/

import de.obey.slayer.util.LabyUtil;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class LabySendCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "*", true))
            return false;

        if (args.length == 0) {
            messageUtil.sendMessage(sender, "/labysend <message>");
            return false;
        }

        String message = args[0];

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                message = message + " " + args[i];
            }
        }

        messageUtil.sendMessage(player, "Du hast " + message + "§7 gesendet§8.");

        for (final Player online : Bukkit.getOnlinePlayers())
            LabyUtil.sendCurrentPlayingGamemode(online, ChatColor.translateAlternateColorCodes('&', message));

        return false;
    }
}
