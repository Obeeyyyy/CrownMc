package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       15.10.2022 / 18:44

*/

import de.obey.slayer.Initializer;
import de.obey.slayer.backend.enums.DataType;
import de.obey.slayer.backend.user.User;
import de.obey.slayer.util.MathUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class BountyCommand implements CommandExecutor {

    @NonNull
    private final Initializer initializer;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 2) {
            if (!initializer.getMessageUtil().hasPlayedBefore(sender, args[0]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            try {
                long amount = 0L;

                try {
                    amount = Long.parseLong(args[1]);

                    if (amount <= 10) {
                        initializer.getMessageUtil().sendMessage(sender, "Bitte gebe eine Zahl an die größer als 10 ist§8.");
                        return false;
                    }

                } catch (final NumberFormatException exception) {

                    amount = MathUtil.getLongFromStringwithSuffix(args[1]);

                    if (amount <= 0) {
                        initializer.getMessageUtil().sendMessage(sender, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen§7. §7(k, m, mrd, b, brd, t)");
                        return false;
                    }
                }

                final long finalAmount = amount;
                final User user = initializer.getUserHandler().getUserInstant(player.getUniqueId());

                if (!initializer.getMessageUtil().hasEnougthMoney(user, finalAmount))
                    return false;

                user.removeLong(DataType.MONEY, finalAmount);

                initializer.getScoreboardHandler().updateScoreboard(player);
                initializer.getUserHandler().getUser(target.getUniqueId()).thenAcceptAsync(targetUser -> {
                    targetUser.addLong(DataType.BOUNTY, finalAmount);

                    if (target.isOnline())
                        initializer.getMessageUtil().sendMessage(target.getPlayer(), "Jemand hat §e§o" + initializer.getMessageUtil().formatLong(finalAmount) + "§6§o$§7 auf deinen Kopf gesetzt§8.");

                    if (finalAmount >= 10000) {
                        initializer.getMessageUtil().broadcast(player.getName() + " hat §e§o" + initializer.getMessageUtil().formatLong(finalAmount) + "§6§o$§7 auf den Kopf von §e§o" + target.getName() + " §7gesetzt§8.");
                    } else {
                        initializer.getMessageUtil().sendMessage(player, "Du hast §e§o" + initializer.getMessageUtil().formatLong(finalAmount) + "§6§o$§7 auf den Kopf von §e§o" + target.getName() + " §7gesetzt§8.");
                    }
                });

            } catch (final NumberFormatException exception) {
                initializer.getMessageUtil().sendMessage(sender, "Bitte gebe eine Zahl an.");
            }

            return false;
        }

        if(args.length == 1) {
            if (!initializer.getMessageUtil().hasPlayedBefore(sender, args[0]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            initializer.getUserHandler().getUser(target.getUniqueId()).thenAcceptAsync(targetUser -> {
                initializer.getMessageUtil().sendMessage(player, target.getName() + " hat ein Kopfgeld in höhe von §e§o" + initializer.getMessageUtil().formatLong(targetUser.getLong(DataType.BOUNTY))+ "§6§l$§8.");
            });
        }

        initializer.getMessageUtil().sendSyntax(sender, "/bounty <spieler>", "/bounty <spieler> <anzahl>", "Bei mehr als 10k wird die Nachticht gebroadcastet.");

        return false;
    }
}
