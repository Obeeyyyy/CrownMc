package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       26.06.2023 / 19:38

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class CrownCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    @NonNull
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 0) {
            userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                messageUtil.sendMessage(sender, "Du hast §e§o" + messageUtil.formatLong(user.getInt(DataType.CROWNS)) + "§7 Crowns§8.");
            });

            return false;
        }

        if(args.length == 1) {
            if (!messageUtil.hasPlayedBefore(sender, args[0]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> messageUtil.sendMessage(sender, target.getName() + " hat §e§o" + messageUtil.formatLong(user.getInt(DataType.CROWNS)) + "§7 Crowns§8."));

            return false;
        }

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        if (args.length == 3) {

            if (!messageUtil.hasPlayedBefore(sender, args[1]))
                return false;

            int amount = 0;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (final NumberFormatException exception) {
                messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an.");
                return false;

            }
            if (amount < 0) {
                messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an.");
                return false;
            }

            int finalAmount = amount;

            if (args[0].equalsIgnoreCase("add")) {

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(user -> {
                    user.addInt(DataType.CROWNS, finalAmount);
                    messageUtil.sendMessage(sender, args[1] + " hat §e§o" + messageUtil.formatLong(finalAmount) + "§7 Crowns bekommen§8.");
                });
                return false;
            }

            if (args[0].equalsIgnoreCase("remove")) {

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(user -> {
                    user.removeInt(DataType.CROWNS, finalAmount);
                    messageUtil.sendMessage(sender, args[1] + " hat §e§o" + messageUtil.formatLong(finalAmount) + "§7 Crowns verloren§8.");
                });

                return false;
            }

            if (args[0].equalsIgnoreCase("set")) {

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(user -> {
                    user.setInt(DataType.CROWNS, finalAmount);
                    messageUtil.sendMessage(sender, args[1] + " hat jetzt §e§o" + messageUtil.formatLong(finalAmount) + "§7 Crowns§8.");
                });

                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/crowns add <spieler> <amount>",
                "/crowns remove <spieler> <amount>",
                "/crowns set <spieler> <amount>"
        );

        return false;
    }
}
