package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       13.10.2022 / 18:26

*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.enums.StoreType;
import de.obey.crownmc.handler.ScoreboardHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
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
public final class UserCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final ScoreboardHandler scoreboardHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "edituser", true))
            return false;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                messageUtil.sendMessage(sender, "Es sind §f§o" + userHandler.getUserCache().size() + "§7 User geladen§8.");

                userHandler.getUserCache().values().forEach(user -> {
                    sender.sendMessage("§8  - §7" + user.getOfflinePlayer().getName() + "§8 / §f§o" + user.getOfflinePlayer().getUniqueId());
                });
                return false;
            }

            if (args[0].equalsIgnoreCase("plotrand")) {
                messageUtil.sendSyntax(sender,
                        "/user plotrand get <spieler>",
                        "/user plotrand remove <spieler> <slot>");
                return false;
            }
        }

        if (args.length == 2) {

            if (!messageUtil.hasPlayedBefore(sender, args[1]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (args[0].equalsIgnoreCase("save")) {

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                    userHandler.saveData(user);
                    messageUtil.sendMessage(sender, user.getOfflinePlayer().getName() + " §8(§f§o" + user.getInt(DataType.ID) + "§8)§7 wurde gespeichert§8.");
                });

                return false;
            }

            if (args[0].equalsIgnoreCase("load")) {

                userHandler.loadData(target.getUniqueId(), true).thenAcceptAsync(user -> messageUtil.sendMessage(sender, user.getOfflinePlayer().getName() + " §8(§f§o" + user.getInt(DataType.ID) + "§8)§7 wurde neu geladen§8."));

                return false;
            }

            if (args[0].equalsIgnoreCase("getdata")) {

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                    messageUtil.sendMessage(sender, user.getOfflinePlayer().getName() + " §8(§f§o" + user.getInt(DataType.ID) + "§8)§7 :");

                    user.getData().keySet().forEach(data -> {
                        sender.sendMessage("§8- §7" + data.getSavedAs() + " §8(§e" + (data.getStoreType() == StoreType.CONFIG ? "CFG" : "SQL") + "§8) = §f§o" + user.getData().get(data));
                    });

                });

                return false;
            }

            if (args[0].equalsIgnoreCase("resetcd")) {

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                    if(target.isOnline())
                        messageUtil.sendMessage(target.getPlayer(), "Deine Cooldowns wurde zurückgesetzt§8.");

                    user.getCooldowns().clearCooldowns();
                    messageUtil.sendMessage(sender, "Du hast die Cooldowns von §e" + target.getName() + "§7 zurückgesetzt§8.");
                });

                return false;
            }

            if (args[0].equalsIgnoreCase("cd")) {

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                    if(user.getCooldowns().getCooldowns().isEmpty()) {
                        messageUtil.sendMessage(sender, target.getName() + " hat keine Cooldowns§8.");
                        return;
                    }

                    messageUtil.sendMessage(sender, "Cooldowns von§8: §e" + target.getName());

                    user.getCooldowns().getCooldowns().keySet().forEach(type -> {
                        sender.sendMessage("§8- §7" + type + " §8-> §f" + MathUtil.getMinutesAndSecondsFromSeconds(user.getCooldowns().getRemainingMillis(type)/1000));
                    });
                });

                return false;
            }

            if (args[0].equalsIgnoreCase("unregister")) {

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                    user.getPlayerFile().delete();
                    messageUtil.sendMessage(sender, "User " + target.getName() + " wurde unregistriert§8.");
                });

                return false;
            }
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("getlong")) {
                final DataType dataType = DataType.getTypeFromName(args[2]);

                if (dataType == null) {
                    messageUtil.sendMessage(sender, "Ungültiger typ.");
                    return false;
                }

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                try {

                    userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                        messageUtil.sendMessage(sender, target.getName() + " hat " + dataType.getSavedAs() + " = " + user.getLong(dataType));

                        if (user.getPlayer() != null)
                            scoreboardHandler.updateScoreboard(user.getPlayer());
                    });

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Ungültiger datentyp.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("getint")) {
                final DataType dataType = DataType.getTypeFromName(args[2]);

                if (dataType == null) {
                    messageUtil.sendMessage(sender, "Ungültiger typ.");
                    return false;
                }

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                try {

                    userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                        messageUtil.sendMessage(sender, target.getName() + " hat " + dataType.getSavedAs() + " = " + user.getInt(dataType));

                        if (user.getPlayer() != null)
                            scoreboardHandler.updateScoreboard(user.getPlayer());
                    });

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Ungültiger datentyp.");
                }

                return false;
            }

            if (args[0].equalsIgnoreCase("resetcd")) {

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                final String type = args[2];

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                    if(!user.getCooldowns().getCooldowns().containsKey(type)) {
                        messageUtil.sendMessage(sender, target.getName() + " hat keinen Cooldown §8(§e" + type + "§8).");
                        return;
                    }

                    if(target.isOnline())
                        messageUtil.sendMessage(target.getPlayer(), "Cooldown §8(§e" + type + "§8) wurde zurückgesetzt§8.");

                    user.getCooldowns().setCooldown(type, System.currentTimeMillis());
                    messageUtil.sendMessage(sender, "Du hast Cooldown §8(§e" + type + "§8) von §e" + target.getName() + "§7 zurückgesetzt§8.");
                });

                return false;
            }
        }

        if (args.length >= 3) {

            if (args[0].equalsIgnoreCase("setfirstjoin")) {

                if (!messageUtil.hasPlayedBefore(sender, args[1]))
                    return false;

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                String value = args[2];

                if (args.length > 3) {
                    for (int i = 3; i < args.length; i++) {
                        value = value + " " + args[i];
                    }
                }

                final String finalValue = value;

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                    user.setString(DataType.FIRSTJOINDATE, finalValue);
                    messageUtil.sendMessage(sender, "Du hast Firstjoin für " + target.getName() + " auf " + finalValue + " gesetzt§8.");
                });

                return false;
            }
        }

        if (args.length == 4) {

            if (!messageUtil.hasPlayedBefore(sender, args[1]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (args[0].equalsIgnoreCase("setlong")) {

                final DataType dataType = DataType.getTypeFromName(args[2]);

                if (dataType == null) {
                    messageUtil.sendMessage(sender, "Ungültiger typ.");
                    return false;
                }

                try {
                    final long amount = Long.parseLong(args[3]);

                    userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                        user.getData().put(dataType, amount);
                        messageUtil.sendMessage(sender, target.getName() + " set " + dataType.getSavedAs() + " = " + user.getLong(dataType));

                        if (user.getPlayer() != null)
                            scoreboardHandler.updateScoreboard(user.getPlayer());
                    });

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Ungültiger datentyp.");
                }

                return false;
            }

            if (args[0].equalsIgnoreCase("setint")) {

                final DataType dataType = DataType.getTypeFromName(args[2]);

                if (dataType == null) {
                    messageUtil.sendMessage(sender, "Ungültiger typ.");
                    return false;
                }

                try {
                    final int amount = Integer.parseInt(args[3]);

                    userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                        user.getData().put(dataType, amount);
                        messageUtil.sendMessage(sender, target.getName() + " set " + dataType.getSavedAs() + " = " + user.getInt(dataType));

                        if (user.getPlayer() != null)
                            scoreboardHandler.updateScoreboard(user.getPlayer());
                    });

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Ungültiger datentyp.");
                }

                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/user list",
                "/user load <spielername>",
                "/user save <spielername>",
                "/user unregister <spielername>",
                "/user getdata <spielername>",
                "/user cd <spielername>",
                "/user resetcd <spielername>",
                "/user resetcd <spielername> <type>",
                "/user getlong <spielername> <type>",
                "/user getint <spielername> <type>",
                "/user setlong <spielername> <type> <amount>",
                "/user setint <spielername> <type> <amount>",
                "/user setfirstjoin <spielername> <text>");

        return false;
    }
}
