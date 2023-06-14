package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       13.10.2022 / 16:21

*/

import de.obey.slayer.Initializer;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class SetPrefixCommand implements CommandExecutor {

    @NonNull
    private final Initializer initializer;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "*", true))
            return false;

        if (args.length >= 1) {

            String newPrefix = args[0];

            if (args.length > 1) {
                for (int i = 1; i < args.length; i++) {
                    newPrefix = newPrefix + " " + args[i];
                }
            }

            initializer.getServerConfig().setPrefix(ChatColor.translateAlternateColorCodes('&', newPrefix));
            initializer.getMessageUtil().sendMessage(sender, "Neuer prefix: " + newPrefix + "testmessage");

            return false;
        }

        initializer.getMessageUtil().sendSyntax(sender, "/setprefix <prefix>");

        return false;
    }
}
