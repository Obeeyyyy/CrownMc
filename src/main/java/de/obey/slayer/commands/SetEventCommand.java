package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       31.12.2022 / 18:24

*/

import de.obey.slayer.backend.ServerConfig;
import de.obey.slayer.handler.ScoreboardHandler;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class SetEventCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final ServerConfig serverConfig;
    private final ScoreboardHandler scoreboardHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            if (!PermissionUtil.hasPermission(((Player) sender).getPlayer(), "setevent", true))
                return false;
        }

        if (args.length == 0) {
            messageUtil.sendSyntax(sender, "/setevent <text>");
            return false;
        }

        String text = args[0];

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                text = text + " " + args[i];
            }
        }

        if (text.length() > 16) {
            messageUtil.sendMessage(sender, "Der Text ist zu lang§8.");
            return false;
        }

        serverConfig.setEvent(ChatColor.translateAlternateColorCodes('&', text));
        messageUtil.sendMessage(sender, "Du hast Event geupdated§8. §r" + text);
        scoreboardHandler.updateEverythingForEveryone();

        return false;
    }
}
