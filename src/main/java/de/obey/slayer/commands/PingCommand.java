package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       17.10.2022 / 17:38

*/

import de.obey.slayer.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class PingCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0) {
            messageUtil.sendMessage(sender, "Dein Ping ist§8: " + getPing(player) + "§f§oms");
            return false;
        }

        if (args.length == 1) {
            if (!messageUtil.isOnline(sender, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            messageUtil.sendMessage(sender, target.getName() + "'s Ping ist§8: " + getPing(target) + "§f§oms");

            return false;
        }

        messageUtil.sendSyntax(sender, "/ping", "/ping <spieler>");

        return false;
    }

    public static String getPing(final Player player) {
        final int ping = ((CraftPlayer) player).getHandle().ping;
        return String.valueOf(ping < 20 ? "§2" : (ping < 44 ? "§a" : (ping < 90 ? "§e" : "§c"))) + ping;
    }
}
