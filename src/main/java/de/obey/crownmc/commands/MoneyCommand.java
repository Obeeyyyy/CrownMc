package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       26.10.2022 / 22:42

*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
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
@NonNull
public final class MoneyCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

        if (args.length == 0) {

            if (!(sender instanceof Player)) {
                messageUtil.sendSyntax(sender, "/money <spieler>");
                return false;
            }

            final User user = userHandler.getUserInstant(((Player) sender).getUniqueId());
            messageUtil.sendMessage(sender, "Du hast §e§o" + messageUtil.formatLong(user.getLong(DataType.MONEY)) + "§6§l$§8.");

            return false;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("help")) {
                if (!PermissionUtil.hasPermission(sender, "money.edit", true))
                    return false;

                messageUtil.sendSyntax(sender,
                        "/money add <spieler> <amount>",
                        "/money remove <spieler> <amount>",
                        "/money set <spieler> <amount>"
                );
            }

            if (!messageUtil.hasPlayedBefore(sender, args[0]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> messageUtil.sendMessage(sender, target.getName() + " hat §e§o" + messageUtil.formatLong(user.getLong(DataType.MONEY)) + "§6§l$§8."));

            return false;
        }

        if (!PermissionUtil.hasPermission(sender, "edit.money", true))
            return false;

        if (args.length == 3) {

            if (!messageUtil.hasPlayedBefore(sender, args[1]))
                return false;

            long amount = 0;

            try {
                amount = Long.parseLong(args[2]);
            } catch (final NumberFormatException exception) {
                amount = MathUtil.getLongFromStringwithSuffix(args[2]);
            }

            if (amount < 0) {
                messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen. (k, m, mrd, b, brd, t)");
                return false;
            }

            long finalAmount = amount;

            if (args[0].equalsIgnoreCase("add")) {

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(user -> {
                    user.addLong(DataType.MONEY, finalAmount);
                    messageUtil.sendMessage(sender, args[1] + " hat §e§o" + messageUtil.formatLong(finalAmount) + "§6$ §7bekommen§8.");
                });
                return false;
            }

            if (args[0].equalsIgnoreCase("remove")) {

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(user -> {
                    user.removeLong(DataType.MONEY, finalAmount);
                    messageUtil.sendMessage(sender, args[1] + " hat §e§o" + messageUtil.formatLong(finalAmount) + "§6$ §7verloren§8.");
                });

                return false;
            }

            if (args[0].equalsIgnoreCase("set")) {

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(user -> {
                    user.setLong(DataType.MONEY, finalAmount);
                    messageUtil.sendMessage(sender, args[1] + " hat jetzt §e§o" + messageUtil.formatLong(finalAmount) + "§6$§8.");
                });

                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/money add <spieler> <amount>",
                "/money remove <spieler> <amount>",
                "/money set <spieler> <amount>"
        );

        return false;
    }
}
