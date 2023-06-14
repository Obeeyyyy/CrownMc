package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       16.10.2022 / 17:34

*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
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
public final class PayCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        if (!Bools.pay) {
            messageUtil.sendMessage(sender, "Pay wurde kurzfristig deaktiviert.");
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 2) {

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            if (!messageUtil.hasPlayedBefore(sender, args[0]))
                return false;

            if (player == target) {
                messageUtil.sendMessage(sender, "Du kannst dir kein Geld senden.");
                return false;
            }

            long amount = 0L;

            try {
                amount = Long.parseLong(args[1]);

                if (amount <= 0) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an die größer als 0 ist.");
                    return false;
                }

            } catch (final NumberFormatException exception) {

                amount = MathUtil.getLongFromStringwithSuffix(args[1]);

                if (amount <= 0) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen. (k, m, mrd, b, brd, t)");
                    return false;
                }
            }

            final long finalAmount = amount;

            userHandler.getUser(player.getUniqueId()).thenAccept(user -> {
                if (!messageUtil.hasEnougthMoney(user, finalAmount))
                    return;

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(targetUser -> {

                    if (user.getLong(DataType.MONEY) + finalAmount < 0) {
                        messageUtil.sendMessage(sender, "Der Spieler " + target.getName() + " hat zu viel Geld.");
                        return;
                    }

                    targetUser.addLong(DataType.MONEY, finalAmount);
                    user.removeLong(DataType.MONEY, finalAmount);

                    messageUtil.sendMessage(sender, "Du hast " + targetUser.getOfflinePlayer().getName() + " §e§o" + messageUtil.formatLong(finalAmount) + "§6§o$ §7überwiesen.");

                    if (target.isOnline())
                        messageUtil.sendMessage(target.getPlayer(), player.getName() + " hat dir §e§o" + messageUtil.formatLong(finalAmount) + "§6§o$ §7überwiesen.");
                });
            });

            return false;
        }

        messageUtil.sendSyntax(sender, "/pay <spieler> <anzahl>");

        return false;
    }
}
