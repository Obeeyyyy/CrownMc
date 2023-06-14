package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       07.01.2023 / 20:53

*/

import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @NonNull
public final class UuidCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player && !PermissionUtil.hasPermission(((Player) sender).getPlayer(), "uuid", true)) {
            return false;
        }

        if(args.length == 1) {

            if(!messageUtil.hasPlayedBefore(sender, args[0]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            messageUtil.sendMessage(sender,"UUID von " + target.getName() + "§8: §f§o" + target.getUniqueId().toString());

            return false;
        }

        messageUtil.sendSyntax(sender,"/uuid <name>");

        return false;
    }
}
