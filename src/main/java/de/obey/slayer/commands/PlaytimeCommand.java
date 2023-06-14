package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       04.12.2022 / 16:22

*/

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

@RequiredArgsConstructor
@NonNull
public final class PlaytimeCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0) {
            userHandler.getUser(player.getUniqueId())
                    .thenAcceptAsync(user -> messageUtil.sendMessage(sender, "Deine Spielzeit§8: §r" + MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds(user.getPlaytime().getCurrentPlaytime())));
            return false;
        }

        if (args.length == 1) {
            if (!messageUtil.hasPlayedBefore(sender, args[0]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            userHandler.getUser(target.getUniqueId())
                    .thenAcceptAsync(user -> messageUtil.sendMessage(sender, target.getName() + "'s Spielzeit§8: §r" + MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds(user.getPlaytime().getCurrentPlaytime())));

            return false;
        }

        return false;
    }
}
