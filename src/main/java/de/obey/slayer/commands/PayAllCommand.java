package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       26.12.2022 / 21:07

*/

import de.obey.slayer.backend.enums.DataType;
import de.obey.slayer.handler.UserHandler;
import de.obey.slayer.util.Bools;
import de.obey.slayer.util.MathUtil;
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
@NonNull
public final class PayAllCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "payall", true))
            return false;

        if (!Bools.pay) {
            messageUtil.sendMessage(sender, "Pay wurde kurzfristig deaktiviert.");
            return false;
        }

        if (Bukkit.getOnlinePlayers().size() <= 1) {
            messageUtil.sendMessage(player, "Es sind zu wengig Spieler online§8.");
            return false;
        }

        if (args.length == 1) {

            long amount = 0L;

            try {
                amount = Long.parseLong(args[0]);

                if (amount <= 0) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an die größer als 0 ist.");
                    return false;
                }

            } catch (final NumberFormatException exception) {

                amount = MathUtil.getLongFromStringwithSuffix(args[0]);

                if (amount <= 0) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen. (k, m, mrd, b, brd, t)");
                    return false;
                }
            }

            final long singeAmount = amount;
            final long finalAmount = amount * (Bukkit.getOnlinePlayers().size() - 1);

            userHandler.getUser(player.getUniqueId()).thenAccept(user -> {
                if (!messageUtil.hasEnougthMoney(user, finalAmount))
                    return;

                messageUtil.sendMessage(player, "Du hast §a§o" + messageUtil.formatLong(finalAmount) + "§6§l$§7 an alle Spieler gepayt§8.");
                user.removeLong(DataType.MONEY, finalAmount);

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    userHandler.getUser(onlinePlayer.getUniqueId()).thenAcceptAsync(targetUser -> {

                        if (targetUser.getLong(DataType.MONEY) + singeAmount < 0)
                            return;

                        targetUser.addLong(DataType.MONEY, singeAmount);

                        messageUtil.sendMessage(onlinePlayer.getPlayer(), player.getName() + " hat dir §e§o" + messageUtil.formatLong(singeAmount) + "§6§o$ §7überwiesen.");
                    });
                }
            });

            return false;
        }

        return false;
    }
}
