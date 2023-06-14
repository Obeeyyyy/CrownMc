package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       25.10.2022 / 13:38

*/

import de.obey.slayer.objects.SupportChat;
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
public final class SupportCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    public static final HashMap<UUID, SupportChat> openChats = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "team", false)) {

            if (args.length > 0) {

                final SupportChat supportChat = isInSupportChat(player.getUniqueId());

                if (args[0].equalsIgnoreCase("close")) {

                    if (supportChat == null) {
                        messageUtil.sendMessage(sender, "Du hast keine offene Supportanfrage§8.");
                        return false;
                    }

                    supportChat.close(player);

                    return false;
                }

                if (supportChat != null) {
                    messageUtil.sendMessage(sender, "Du hast bereits eine offene Supportanfrage§8.");
                    messageUtil.sendMessage(sender, "Nutze /support close um diese zu schließen§8.");
                    return false;
                }

                String grund = "";

                for (String arg : args) {
                    grund = grund + " " + arg;
                }

                openChats.put(player.getUniqueId(), new SupportChat(player, grund));

                return false;
            }

            messageUtil.sendSyntax(sender, "/support <grund>", "/support close");

            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {

                if (openChats.isEmpty()) {
                    messageUtil.sendMessage(sender, "§c§oKeine offenen Supportanfragen§8.");
                    return false;
                }

                messageUtil.sendMessage(sender, "Alle Supportanfragen§8:");

                openChats.values().forEach(supportChat -> {
                    messageUtil.sendMessage(sender, supportChat.getOwner().getName() + " §8| §7Grund: " + supportChat.getGrund() + " §8| §7State: " + supportChat.getState());
                });

                return false;
            }

            if (args[0].equalsIgnoreCase("leave")) {

                final SupportChat supportChat = isInSupportChat(player.getUniqueId());

                if (supportChat == null) {
                    messageUtil.sendMessage(sender, "Du bist in keinem Supportchat§8.");
                    return false;
                }

                supportChat.leave(player);

                return false;
            }
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("join")) {

                if (!messageUtil.isOnline(sender, args[1]))
                    return false;

                final Player target = Bukkit.getPlayer(args[1]);
                final SupportChat supportChat = isInSupportChat(target.getUniqueId());

                if (supportChat == null) {
                    messageUtil.sendMessage(sender, target.getName() + " hat keine offene Supportanfrage§8.");
                    return false;
                }

                if (isInSupportChat(player.getUniqueId()) != null) {
                    messageUtil.sendMessage(sender, "Du bist bereits in einer Supportanfrage§8.");
                    messageUtil.sendMessage(sender, "Nutze /support close um diese zu schließen§8.");
                    return false;
                }

                supportChat.join(player);
                openChats.put(player.getUniqueId(), supportChat);

                return false;
            }

            if (args[0].equalsIgnoreCase("close")) {

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                final SupportChat supportChat = isInSupportChat(target.getUniqueId());

                if (supportChat == null) {
                    messageUtil.sendMessage(sender, target.getName() + " hat keine offene Supportanfrage§8.");
                    return false;
                }

                supportChat.close(player);
                messageUtil.sendMessage(sender, target.getName() + "'s Supportanfrage wurde geschlossen§8.");

                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/support list", "/support leave", "/support join <spieler>", "/support close <spieler>");

        return false;
    }

    public static SupportChat isInSupportChat(final UUID uuid) {
        return openChats.get(uuid);
    }
}
