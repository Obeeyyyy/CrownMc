package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       13.10.2022 / 17:59

*/

import de.obey.slayer.backend.enums.DataType;
import de.obey.slayer.backend.user.User;
import de.obey.slayer.handler.EloHandler;
import de.obey.slayer.handler.UserHandler;
import de.obey.slayer.util.MathUtil;
import de.obey.slayer.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@NonNull
public final class StatsCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final EloHandler eloHandler;
    private final ExecutorService executorService;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0) {
            userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> sendStatsMessage(sender, user, command.getName().equalsIgnoreCase("pvpstats")));

            return false;
        }

        if (args.length == 1) {
            if (!messageUtil.hasPlayedBefore(sender, args[0]))
                return false;

            executorService.submit(() -> {
                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> sendStatsMessage(sender, user, command.getName().equalsIgnoreCase("pvpstats")));
            });
        }

        return false;
    }

    private void sendStatsMessage(final CommandSender sender, final User user, final boolean pvpstats) {
        sender.sendMessage("");
        sender.sendMessage("§8§l§m-----------------------------------");
        if (!pvpstats) {
            sender.sendMessage("");
            sender.sendMessage("§8▰§7▱ §6§lInformationen");
            sender.sendMessage("");
            sender.sendMessage("  §8- §7Name§8: §e§o" + user.getOfflinePlayer().getName() + " §8(§6§l#§e§o" + user.getInt(DataType.ID) + "§8)");
            sender.sendMessage("  §8- §7Level§8: §f§o" + user.getInt(DataType.LEVEL) + " §8(§f§o" + messageUtil.formatLong(user.getInt(DataType.XP)) + "xp§8)");
            sender.sendMessage("  §8- §7Balance§8: §e§o" + messageUtil.formatLong(user.getLong(DataType.MONEY)) + "§6§l$");
            sender.sendMessage("  §8- §7Bounty§8: §e§o" + messageUtil.formatLong(user.getLong(DataType.BOUNTY)) + "§6§l$");
            sender.sendMessage("  §8- §7Votes§8: §a§o" + messageUtil.formatLong(user.getInt(DataType.VOTES)));
            sender.sendMessage("  §8- §7Loginstreak§8: §a§o" + messageUtil.formatLong(user.getInt(DataType.VOTESTREAK)));
            sender.sendMessage("  §8- §7Beigetreten am§8: §f§o" + user.getString(DataType.FIRSTJOINDATE));
            sender.sendMessage("  §8- §7Status§8: " + (user.getOfflinePlayer().isOnline() ? "§a§oOnline §7seit§f§o " + MathUtil.getHoursAndMinutesAndSecondsFromSeconds((System.currentTimeMillis() - user.getLong(DataType.JOINED)) / 1000) : "§c§oOffline §7seit§f§o " + MathUtil.getHoursAndMinutesAndSecondsFromSeconds((System.currentTimeMillis() - user.getLong(DataType.LASTSEEN)) / 1000)));
            sender.sendMessage("  §8- §7Spielzeit§8:§f§o " + MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds(user.getPlaytime().getCurrentPlaytime()));
        }

        sender.sendMessage("");
        sender.sendMessage("§8▰§7▱ §6§lPvP Stats " + (pvpstats ? "§8( §e§o" + user.getOfflinePlayer().getName() + " §8)" : ""));
        sender.sendMessage("");

        final int kills = user.getInt(DataType.KILLS), deaths = user.getInt(DataType.DEATHS);

        sender.sendMessage("  §8- §7K/D§8: §a§o" + messageUtil.formatLong(kills) + "§8/§c§o" + messageUtil.formatLong(deaths) + " §8(§f§o" + formatKD(kills, deaths) + "§8)");
        sender.sendMessage("  §8- §7Elopunkte§8: §a§o" + messageUtil.formatLong(user.getInt(DataType.ELOPOINTS)) + " §8( " + eloHandler.getEloRangFromEloPoints(user.getInt(DataType.ELOPOINTS)) + " §8)");
        sender.sendMessage("  §8- §7Killstreak§8: §a§o" + messageUtil.formatLong(user.getInt(DataType.KILLSTREAK)) + " §7Rekord§8: §a§o" + messageUtil.formatLong(user.getInt(DataType.KILLSTREAKRECORD)));
        sender.sendMessage("");
        sender.sendMessage("§8§l§m-----------------------------------");
        sender.sendMessage("");
    }

    private String formatKD(final int kills, final int deaths) {
        final double kd = kills > 0 ? (deaths > 0 ? ((float)(kills / deaths)) + 0.0D : kills) : 0;
        final DecimalFormat format = new DecimalFormat("0.0#", new DecimalFormatSymbols(Locale.ENGLISH));
        return format.format(kd);
    }
}
