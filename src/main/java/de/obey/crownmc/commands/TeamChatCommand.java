package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       14.10.2022 / 15:25

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
public final class TeamChatCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "team", true))
            return false;

        if (args.length > 0) {

            String message = args[0];

            if (args.length > 1) {
                for (int i = 1; i < args.length; i++)
                    message = message + " " + args[i];
            }

            messageUtil.sendMessageToTeamMembers("§8[§6§lTEAMCHAT§8] §f§o" + player.getName() + "§8 »§d " + ChatColor.translateAlternateColorCodes('&', message));

            return false;
        }

        messageUtil.sendMessage(sender, "/tc <nachricht>");

        return false;
    }
}
