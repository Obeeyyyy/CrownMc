// Made by Richard


package de.obey.crownmc.commands;

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class IgnoreCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    public IgnoreCommand(MessageUtil messageUtil, UserHandler userHandler) {
        this.messageUtil = messageUtil;
        this.userHandler = userHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "ignore", true))
            return false;

        if (args.length != 1) {
            messageUtil.sendSyntax(sender, "/ignore <spieler>");
            return true;
        }

        if (!messageUtil.hasPlayedBefore(sender, args[0]))
            return false;

        if (!messageUtil.isOnline(sender, args[0]))
            return false;

        final Player target = Bukkit.getPlayer(args[0]);

        if (target.getUniqueId().equals(player.getUniqueId())) {
            messageUtil.sendMessage(sender, "Richard sagt§8: §8'§fBisschen zuviel Selbsthass§8.', §7obey stimmt zu§8.");
            return false;
        }

        if (PermissionUtil.hasPermission(target, "team", false)) {
            messageUtil.sendMessage(sender, "Du kannst Teammitglieder nicht ignoren§8.");
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 0.4f, 0.4f);
            return false;
        }

        player.playSound(player.getLocation(), Sound.VILLAGER_YES, 0.4f, 0.4f);

        final User user = userHandler.getUserInstant(player.getUniqueId());
        final List<String> ignores = user.getList(DataType.IGNORES);

        if (!ignores.contains(target.getUniqueId().toString())) {
            ignores.add(target.getUniqueId().toString());
            messageUtil.sendMessage(player, "Du ignorierst §e" + target.getName() + "§7 jetzt§8. §7Gebe diesen Command erneut ein§8,§7 um es rückgängig zu machen§8.");
            user.setList(DataType.IGNORES, ignores);
            return true;
        }

        ignores.remove(target.getUniqueId().toString());
        user.setList(DataType.IGNORES, ignores);
        messageUtil.sendMessage(player, "Du ignorierst §e" + target.getName() + "§7 nicht mehr§8.");
        return false;
    }
}