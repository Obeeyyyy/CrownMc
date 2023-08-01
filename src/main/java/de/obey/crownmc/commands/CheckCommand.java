package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       31.07.2023 / 21:04

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.user.UserPunishment;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor @NonNull
public final class CheckCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            if (!PermissionUtil.hasPermission(sender, "check", true))
                return false;

            if(args.length == 1) {

                if(!messageUtil.hasPlayedBefore(sender, args[0]))
                    return false;

                userHandler.getUser(Bukkit.getOfflinePlayer(args[0]).getUniqueId()).thenAcceptAsync(user -> {
                    final UserPunishment punishment = user.getPunishment();

                    messageUtil.sendMessage(sender, "§8  - §f§lCheck §7- §e" + args[0]);
                    punishment.sendMuteInfo(sender);
                    punishment.sendBanInfo(sender);
                    sender.sendMessage("");
                });

                return false;
            }

        return false;
    }
}
