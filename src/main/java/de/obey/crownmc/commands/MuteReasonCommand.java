package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       14.07.2023 / 02:22

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.MuteHandler;
import de.obey.crownmc.objects.punishment.BanReason;
import de.obey.crownmc.objects.punishment.MuteReason;
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
public final class MuteReasonCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final MuteHandler muteHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                muteHandler.loadReasons();
                messageUtil.sendMessage(sender, "Mutereasons neu geladen§8.");
                return false;
            }

            if(args[0].equalsIgnoreCase("list")) {

                if(muteHandler.getReasons().isEmpty()) {
                    messageUtil.sendMessage(player, "Es existieren noch keine mute Gründe§8.");
                    return false;
                }

                messageUtil.sendMessage(player, "Es existieren §8(§f" + muteHandler.getReasons().size() + "§8) §7Gründe§8:");

                for (int i : muteHandler.getReasons().keySet()) {
                    final MuteReason reason = muteHandler.getReadsonFromID(i);
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

                    if(muteHandler.getReasons().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es existiert bereits ein Grund mit der ID " + id + "§8.");
                        return false;
                    }

                    muteHandler.createNewReason(id);
                    messageUtil.sendMessage(player, "Ban Grund erstellt§8. (§f" + id + "§8)");

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("delete")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!muteHandler.getReasons().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es existiert kein Grund mit der ID " + id + "§8.");
                        return false;
                    }

                    muteHandler.deleteReason(id);
                    messageUtil.sendMessage(player, "Ban Grund gelöscht§8. (§f" + id + "§8)");

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }
        }

        if(args.length >= 3) {
            if(args[0].equalsIgnoreCase("setn")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!muteHandler.getReasons().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es existiert kein Grund mit der ID " + id + "§8.");
                        return false;
                    }

                    String value = args[2];

                    if(args.length > 3) {
                        for (int i = 3; i < args.length; i++)
                            value = value + " " + args[i];
                    }

                    muteHandler.getReadsonFromID(id).setName(value);
                    messageUtil.sendMessage(player, "Ban Grund bearbeitet§8. (§f" + id + "§8)");
                    messageUtil.sendMessage(player, "name -> " + value);

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("setp")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!muteHandler.getReasons().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es existiert kein Grund mit der ID " + id + "§8.");
                        return false;
                    }

                    final String value = args[2];

                    muteHandler.getReadsonFromID(id).setPermission(value);
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

                    if(!muteHandler.getReasons().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es existiert kein Grund mit der ID " + id + "§8.");
                        return false;
                    }

                    final long value = Long.parseLong(args[2]);

                    muteHandler.getReadsonFromID(id).setDuration(value);
                    messageUtil.sendMessage(player, "Ban Grund bearbeitet§8. (§f" + id + "§8)");
                    messageUtil.sendMessage(player, "duration -> " + value + " §8(§f" + MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds(value/1000) + "§8)");

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/muter list",
                "/muter reload",
                "/muter create <id>",
                "/muter delete <id>",
                "/muter setn <id> <name>",
                "/muter setp <id> <perms>",
                "/muter setd <id> <duration>"
                );

        return false;
    }
}
