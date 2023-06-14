package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       14.10.2022 / 17:49

*/

import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TeleportCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "tp", true))
            return false;

        if (args.length == 0) {
            messageUtil.sendSyntax(sender, "/tp <spieler>", "/tp <wer> <wem>");
            return false;
        }

        if (!messageUtil.isOnline(sender, args[0]))
            return false;

        final Player target = Bukkit.getPlayer(args[0]);

        if (args.length == 1) {
            if (target == player) {
                player.sendMessage("§8┃>§d§o ? LG Obey aus der Entwicklung :D");
                return false;
            }

            player.teleport(target);

            return false;
        }

        if (args.length == 2) {
            if (!PermissionUtil.hasPermission(player, "tp.others", true))
                return false;

            if (!messageUtil.isOnline(sender, args[1]))
                return false;

            final Player target2 = Bukkit.getPlayer(args[1]);
            target.teleport(target2);
        }

        return false;
    }
}
