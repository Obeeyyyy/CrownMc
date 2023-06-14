package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       14.10.2022 / 15:21

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public final class MessageCommand implements CommandExecutor {

    private final static HashMap<UUID, UUID> lastMessaged = new HashMap<>();
    @NonNull
    private final Initializer initializer;
    private final String prefix = "§8[§3§lMSG§8] » §7";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("message")) {

            if (!(sender instanceof Player))
                return false;

            final Player player = (Player) sender;

            if (args.length < 2) {
                initializer.getMessageUtil().sendSyntax(sender, "/msg <spieler> <nachricht >");
                return false;
            }

            if (!initializer.getMessageUtil().isOnline(sender, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            if (target == player) {
                player.sendMessage("§8┃>§d§o ? LG Obey aus der Entwicklung :D");
                return false;
            }

            // TOGGLE MSG SOON

            final User targetUser = initializer.getUserHandler().getUserInstant(target.getUniqueId());

            if (targetUser == null)
                return false;

            if (!targetUser.is(DataType.MSGSTATE)) {
                if (!lastMessaged.containsKey(player.getUniqueId()) || lastMessaged.get(player.getUniqueId()) != target.getUniqueId()) {
                    player.sendMessage(prefix + target.getName() + " möchte nicht gestört werden.");
                    return false;
                }
            }

            String message = args[1];

            if (args.length > 2) {
                for (int i = 2; i < args.length; i++)
                    message = message + " " + args[i];
            }

            if (!initializer.getChatFilterHandler().runChatFilterCheck(player, message))
                return false;

            sendMessage(player, target, message);

            return false;
        }

        if (cmd.getName().equalsIgnoreCase("respond")) {
            if (!(sender instanceof Player))
                return false;

            if (args.length == 0) {
                initializer.getMessageUtil().sendSyntax(sender, "/respond <nachricht >");
                return false;
            }

            final Player player = (Player) sender;

            if (!lastMessaged.containsKey(player.getUniqueId())) {
                player.sendMessage(prefix + "Du hast niemanden zum antworten.");
                return false;
            }

            final Player target = Bukkit.getPlayer(lastMessaged.get(player.getUniqueId()));

            if (target == null || !target.isOnline() || VanishCommand.vanished.contains(target)) {
                initializer.getMessageUtil().sendMessage(sender, "Der Spieler ist nicht mehr online§8.");
                return false;
            }

            String message = args[0];

            if (args.length > 1) {
                for (int i = 1; i < args.length; i++)
                    message = message + " " + args[i];
            }

            if (!initializer.getChatFilterHandler().runChatFilterCheck(player, message))
                return false;

            sendMessage(player, target, message);

            return false;
        }

        return false;
    }

    private void sendMessage(final Player player, final Player target, final String message) {
        player.sendMessage(prefix + "Du §8-§7 " + target.getName() + "§8 » §e" + ChatColor.translateAlternateColorCodes('&', message));
        lastMessaged.put(player.getUniqueId(), target.getUniqueId());

        if (!initializer.getUserHandler().getUserInstant(target.getUniqueId()).getList(DataType.IGNORES).contains(player.getUniqueId().toString())) {
            target.sendMessage(prefix + player.getName() + " §8-§7 Dir §8 » §e" + ChatColor.translateAlternateColorCodes('&', message));
            lastMessaged.put(target.getUniqueId(), player.getUniqueId());
        }

        for (final Player teammember : Bukkit.getOnlinePlayers()) {
            if (PermissionUtil.hasPermission(teammember, "msgspy", false) && target != teammember && player != teammember) {
                if (initializer.getUserHandler().getUserInstant(teammember.getUniqueId()).is(DataType.MSGSPYSTATE))
                    teammember.sendMessage("§8[§3§lMSG-SPY§8] » §7" + player.getName() + " zu " + target.getName() + "§8 » §f" + message);
            }
        }
    }
}
