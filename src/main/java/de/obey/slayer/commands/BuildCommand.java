package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       29.10.2022 / 18:14

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

import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor
public final class BuildCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    public static final ArrayList<UUID> buildMode = new ArrayList<UUID>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "build", true))
            return false;

        if (args.length == 0) {

            if (buildMode.contains(player.getUniqueId())) {
                buildMode.remove(player.getUniqueId());
                messageUtil.sendMessage(sender, "Du kannst jetzt nicht mehr bauen.");
            } else {
                buildMode.add(player.getUniqueId());
                messageUtil.sendMessage(sender, "Du kannst jetzt bauen.");
            }

            return false;
        }

        if (args.length == 1) {

            if (!messageUtil.isOnline(sender, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            if (buildMode.contains(target.getUniqueId())) {
                buildMode.remove(target.getUniqueId());
                messageUtil.sendMessage(target, "Du kannst jetzt nicht mehr bauen.");
                messageUtil.sendMessage(sender, target.getName() + " kann jetzt nicht mehr bauen.");
            } else {
                buildMode.add(target.getUniqueId());
                messageUtil.sendMessage(sender, target.getName() + " kann jetzt bauen.");
            }

            return false;
        }

        messageUtil.sendSyntax(sender, "/build", "/build <spieler>");

        return false;
    }
}
