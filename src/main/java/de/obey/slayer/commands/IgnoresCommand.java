// Made by Richard


package de.obey.slayer.commands;

import de.obey.slayer.backend.enums.DataType;
import de.obey.slayer.backend.user.User;
import de.obey.slayer.handler.UserHandler;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class IgnoresCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    public IgnoresCommand(MessageUtil messageUtil, UserHandler userHandler) {
        this.messageUtil = messageUtil;
        this.userHandler = userHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "ignores", true))
            return false;

        if (args.length != 0) {
            messageUtil.sendSyntax(sender, "/ignores");
            return true;
        }

        final User user = userHandler.getUserInstant(player.getUniqueId());
        final List<String> ignores = user.getList(DataType.IGNORES);

        if (ignores.size() < 1) {
            messageUtil.sendMessage(player, "Du ignorierst niemanden.");
            return true;
        }
        String message = "Du ignorierst§8: §e" + String.join("§7, §e", ignores.stream().map(ignore -> Bukkit.getOfflinePlayer(ignore).getName()).collect(Collectors.toList()));
        messageUtil.sendMessage(player, message.substring(0, message.length()));
        return false;
    }
}