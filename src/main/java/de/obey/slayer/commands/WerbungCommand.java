package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       23.10.2022 / 03:38

*/

import de.obey.slayer.util.MathUtil;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public final class WerbungCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    private final HashMap<UUID, Long> timer = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "werbung", true))
            return false;

        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("reset")) {
                if (PermissionUtil.hasPermission(player, "*", false)) {
                    if (args.length == 2) {

                        if (!messageUtil.hasPlayedBefore(sender, args[1]))
                            return false;

                        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                        timer.remove(target.getUniqueId());

                        messageUtil.sendMessage(sender, "Timer für " + target.getName() + " wurde resettet.");

                        return false;
                    }

                    messageUtil.sendSyntax(sender, "/werbung reset <spieler>");

                    return false;
                }
            }

            if (timer.containsKey(player.getUniqueId())) {
                if (System.currentTimeMillis() - timer.get(player.getUniqueId()) < 1000 * 60 * 5) {
                    messageUtil.sendMessage(sender, "Du musst noch " + MathUtil.getMinutesAndSecondsFromSeconds(((timer.get(player.getUniqueId()) + 1000 * 60 * 5) - System.currentTimeMillis()) / 1000) + "§7warten.");
                    return false;
                }
            }

            String message = "";

            for (String arg : args)
                message = message + " " + arg;

            messageUtil.broadcast("§8§l§m------------------------------");
            messageUtil.broadcast("");
            messageUtil.broadcast("             Werbung von §e§o" + player.getName());
            messageUtil.broadcast(" §8┃>§7 " + message);
            messageUtil.broadcast("");
            messageUtil.broadcast("§8§l§m------------------------------");

            timer.put(player.getUniqueId(), System.currentTimeMillis());

            return false;
        }

        messageUtil.sendSyntax(sender, "/werbung <message>");

        return false;
    }
}
