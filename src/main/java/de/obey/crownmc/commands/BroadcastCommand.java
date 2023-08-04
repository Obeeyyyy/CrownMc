package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       18.10.2022 / 19:35

*/

import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class BroadcastCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission(sender, "broadcast", true))
            return false;

        if (args.length >= 1) {

            String message = args[0];

            if (args.length > 1) {
                for (int i = 1; i < args.length; i++)
                    message = message + " " + args[i];
            }

            messageUtil.broadcast(ChatColor.translateAlternateColorCodes('&', message));

            return false;
        }

        messageUtil.sendSyntax(sender, "/bc <message>");

        return false;
    }
}
