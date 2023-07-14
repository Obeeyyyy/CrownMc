package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       14.07.2023 / 02:22

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.BanHandler;
import de.obey.crownmc.objects.punishment.BanReason;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @NonNull
public final class BanReasonCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final BanHandler banHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {

                if(banHandler.getReasons().isEmpty()) {
                    messageUtil.sendMessage(player, "Es existieren noch keine ban Gründe§8.");
                    return false;
                }

                messageUtil.sendMessage(player, "Es existieren §8(§f" + banHandler.getReasons().size() + "§8) §7Gründe§8:");

                for (int i : banHandler.getReasons().keySet()) {
                    final BanReason reason = banHandler.getReadsonFromID(i);
                    player.sendMessage("§8  - §7ID: §4§l" + i);
                    player.sendMessage("§8     -> §7Name§8: §f" + reason.getName());
                    player.sendMessage("§8     -> §7Perm§8: §f" + reason.getPermission());
                    player.sendMessage("§8     -> §7Duration§8: §f" + reason.getDuration() + "§8 (§f§o " + MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds(reason.getDuration()/1000) + " §8)");
                    player.sendMessage("");
                }

                return false;
            }
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("create")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(banHandler.getReasons().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es existiert bereits ein Grund mit der ID " + id + "§8.");
                        return false;
                    }

                    banHandler.createNewReason(id);
                    messageUtil.sendMessage(player, "Ban Grund erstellt§8. (§f" + id + "§8)");

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("delete")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!banHandler.getReasons().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es existiert kein Grund mit der ID " + id + "§8.");
                        return false;
                    }

                    banHandler.deleteReason(id);
                    messageUtil.sendMessage(player, "Ban Grund gelöscht§8. (§f" + id + "§8)");

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("setn")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!banHandler.getReasons().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es existiert kein Grund mit der ID " + id + "§8.");
                        return false;
                    }

                    final String value = args[2];

                    banHandler.getReadsonFromID(id).setName(value);
                    messageUtil.sendMessage(player, "Ban Grund bearbeitet§8. (§f" + id + "§8)");
                    messageUtil.sendMessage(player, "name -> " + value);

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("setp")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!banHandler.getReasons().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es existiert kein Grund mit der ID " + id + "§8.");
                        return false;
                    }

                    final String value = args[2];

                    banHandler.getReadsonFromID(id).setPermission(value);
                    messageUtil.sendMessage(player, "Ban Grund bearbeitet§8. (§f" + id + "§8)");
                    messageUtil.sendMessage(player, "permission -> " + value);

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("setd")) {


                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!banHandler.getReasons().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es existiert kein Grund mit der ID " + id + "§8.");
                        return false;
                    }

                    final long value = Long.parseLong(args[2]);

                    banHandler.getReadsonFromID(id).setDuration(value);
                    messageUtil.sendMessage(player, "Ban Grund bearbeitet§8. (§f" + id + "§8)");
                    messageUtil.sendMessage(player, "duration -> " + value + " §8(§f" + MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds(value/1000) + "§8)");

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/banr list",
                "/banr create <id>",
                "/banr delete <id>",
                "/banr setn <id> <name>",
                "/banr setp <id> <perms>",
                "/banr setd <id> <duration>"
                );

        return false;
    }
}
