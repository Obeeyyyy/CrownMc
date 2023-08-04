package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       15.10.2022 / 14:13

*/

import de.obey.crownmc.handler.WorldProtectionHandler;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class LuckyFishingCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final WorldProtectionHandler worldProtectionHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "fly", true))
            return false;

        if (args.length == 0) {

            if (!PermissionUtil.hasPermission(player, "team", false)) {
                if (worldProtectionHandler.getWorldProtection(player.getWorld()) != null && !worldProtectionHandler.getWorldProtection(player.getWorld()).isFly()) {
                    messageUtil.sendMessage(player, "In dieser Welt darfst du nicht fliegen§8.");
                    return false;
                }
            }

            if (player.getAllowFlight()) {
                player.setAllowFlight(false);
                messageUtil.sendMessage(sender, "Du kannst jetzt nicht mehr fliegen.");
                return false;
            }

            player.setAllowFlight(true);
            messageUtil.sendMessage(sender, "Du kannst jetzt fliegen.");

            return false;
        }

        if (!PermissionUtil.hasPermission(player, "fly.others", true))
            return false;

        if (args.length == 1) {

            if (!messageUtil.isOnline(sender, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            if (target.getAllowFlight()) {
                target.setAllowFlight(false);
                messageUtil.sendMessage(target, "Du kannst jetzt nicht mehr fliegen.");
                messageUtil.sendMessage(sender, target.getName() + " kann jetzt nicht mehr fliegen.");
                return false;
            }

            target.setAllowFlight(true);
            messageUtil.sendMessage(target, "Du kannst jetzt fliegen.");
            messageUtil.sendMessage(sender, target.getName() + " kann jetzt fliegen.");

            return false;
        }

        messageUtil.sendSyntax(sender, "/fly", "/fly <spieler>");

        return false;
    }
}
