package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       23.10.2022 / 14:42

*/

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.handler.ScoreboardHandler;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class ConfigCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final ServerConfig serverConfig;
    private final ScoreboardHandler scoreboardHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "*", true))
            return false;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("info")) {

                messageUtil.sendMessage(sender, "ServerConfig:");
                messageUtil.sendMessage(sender, " - Playercount: " + serverConfig.getPlayerCount());
                messageUtil.sendMessage(sender, " - killMoneyReward: " + serverConfig.getKillMoneyReward());
                messageUtil.sendMessage(sender, " - killXpReward: " + serverConfig.getKillXPReward());
                messageUtil.sendMessage(sender, " - killEloReward: " + serverConfig.getKillEloReward());
                messageUtil.sendMessage(sender, " - deathMoneyLose: " + serverConfig.getDeathMoneyLose());
                messageUtil.sendMessage(sender, " - deathEloLose: " + serverConfig.getDeathEloLose());
                messageUtil.sendMessage(sender, " - votes: " + serverConfig.getVotes());
                messageUtil.sendMessage(sender, " - voteparty: " + serverConfig.getVoteparty());
                messageUtil.sendMessage(sender, " - bcdelay: " + serverConfig.getAutoBroadcastDelay());
                sender.sendMessage("");

                messageUtil.sendMessage(sender, "Blocked Commands:");
                serverConfig.getBlockedCommands().forEach(value -> {
                    messageUtil.sendMessage(sender, " - " + value);
                });
                sender.sendMessage("");
                messageUtil.sendMessage(sender, "Blocked in Combat:");
                serverConfig.getBlockedCombatCommands().forEach(value -> {
                    messageUtil.sendMessage(sender, " - " + value);
                });
                sender.sendMessage("");
                messageUtil.sendMessage(sender, "Autobroadcast messages:");
                serverConfig.getAutoBroadcastMessages().forEach(value -> {
                    messageUtil.sendMessage(sender, " - " + value);
                });
                sender.sendMessage("");

                return false;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {

                try {
                    final int amount = Integer.parseInt(args[2]);

                    if (amount < 0) {
                        messageUtil.sendMessage(sender, "Die Zahl müss größer als -1 sein.");
                        return false;
                    }

                    if (args[1].equalsIgnoreCase("killMoneyReward"))
                        serverConfig.setKillMoneyReward(amount);

                    if (args[1].equalsIgnoreCase("killxpreward"))
                        serverConfig.setKillXPReward(amount);

                    if (args[1].equalsIgnoreCase("deathmoneylose"))
                        serverConfig.setDeathMoneyLose(amount);

                    if (args[1].equalsIgnoreCase("killEloReward"))
                        serverConfig.setKillEloReward(amount);

                    if (args[1].equalsIgnoreCase("deathelolose"))
                        serverConfig.setDeathEloLose(amount);

                    if (args[1].equalsIgnoreCase("voteparty"))
                        serverConfig.setVoteparty(amount);

                    if (args[1].equalsIgnoreCase("votes"))
                        serverConfig.setVotes(amount);

                    if (args[1].equalsIgnoreCase("bcdelay"))
                        serverConfig.setAutoBroadcastDelay(amount);

                    scoreboardHandler.updateEverythingForEveryone();

                    messageUtil.sendMessage(sender, "UPDATED " + args[1] + " -> " + amount);
                    return false;

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an.");
                    return false;
                }
            }
        }

        if (args.length >= 3) {
            String cmd = args[2];

            for (int i = 3; i < args.length; i++) {
                cmd = cmd + " " + args[i];
            }

            if (args[0].equalsIgnoreCase("add")) {
                if (args[1].equalsIgnoreCase("blocked")) {
                    serverConfig.getBlockedCommands().add(cmd.toLowerCase());
                    messageUtil.sendMessage(sender, cmd + " ist jetzt für alle Spieler geblockt§8.");
                    return false;
                }

                if (args[1].equalsIgnoreCase("combat")) {
                    serverConfig.getBlockedCombatCommands().add(cmd.toLowerCase());
                    messageUtil.sendMessage(sender, cmd + " ist jetzt für alle Spieler im Kampf geblockt§8.");
                    return false;
                }

                if (args[1].equalsIgnoreCase("bc")) {
                    serverConfig.getAutoBroadcastMessages().add(cmd);
                    messageUtil.sendMessage(sender, cmd + " §7wird jetzt auch gebroadcastet§8.");
                    return false;
                }
            }

            if (args[0].equalsIgnoreCase("remove")) {
                if (args[1].equalsIgnoreCase("blocked")) {
                    serverConfig.getBlockedCommands().remove(cmd.toLowerCase());
                    messageUtil.sendMessage(sender, cmd + " ist jetzt für alle Spieler freigeschaltet§8.");
                    return false;
                }

                if (args[1].equalsIgnoreCase("combat")) {
                    serverConfig.getBlockedCombatCommands().remove(cmd.toLowerCase());
                    messageUtil.sendMessage(sender, cmd + " ist jetzt für alle Spieler im Kampf freigeschaltet§8.");
                    return false;
                }

                if (args[1].equalsIgnoreCase("bc")) {
                    try {
                        final int nr = Integer.parseInt(cmd);
                        if (serverConfig.getAutoBroadcastMessages().size() < nr) {
                            messageUtil.sendMessage(sender, "Die line " + nr + " existiert nicht§8.");
                            return false;
                        }

                        messageUtil.sendMessage(sender, serverConfig.getAutoBroadcastMessages().get(nr - 1) + "§7 wird jetzt nicht mehr gebroadcastet§8.");
                        serverConfig.getAutoBroadcastMessages().remove(nr - 1);
                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an§8.");
                    }
                    return false;
                }
            }
        }

        messageUtil.sendSyntax(sender, "/config info",
                "/config add <blocked, combat, bc> <message>",
                "/config remove <blocked, combat, bc> <nummer>",
                "/config set <killMoneyReward, killXPReward, killEloReward, deathMoneyLose, deathEloLose, voteparty, votes, bcdelay> <amount>");

        return false;
    }
}
