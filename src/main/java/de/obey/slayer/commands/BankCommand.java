package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       02.01.2023 / 21:07

*/

import de.obey.slayer.backend.enums.DataType;
import de.obey.slayer.backend.user.UserBank;
import de.obey.slayer.handler.UserHandler;
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

@RequiredArgsConstructor
@NonNull
public final class BankCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("info")) {

                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

                    final UserBank bank = user.getBank();

                    messageUtil.sendMessage(player, "Du hast §e" + messageUtil.formatLong(bank.getBalance()) + "§6§l$ §7auf deinem Konto§8.");

                    if(bank.getMembers().isEmpty())
                        return;

                    messageUtil.sendMessage(player , "Diese Spieler haben Zugriff auf dein Konto§8.");

                    bank.getMembers().forEach(uuid -> {
                        final OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);

                        player.sendMessage("§8 - §7" + target.getName() + " " + (bank.isTrusted(target) ? "§8( §a§lTRUSTED §8)" : ""));
                    });
                });

                return false;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {

                if (!messageUtil.hasPlayedBefore(player, args[1]))
                    return false;

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(target -> {

                    if (!target.getBank().isMember(player) && !PermissionUtil.hasPermission(player, "bank.others", false)) {
                        messageUtil.sendMessage(player, "Du hast §c§okeinen§7 Zugriff auf die Bank von " + args[1] + "§8!");
                        return;
                    }

                    messageUtil.sendMessage(player, args[1] + " hat §e" + messageUtil.formatLong(target.getBank().getBalance()) + "§6§l$ §7auf seinem Konto§8.");

                });

                return false;
            }

            if (args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("pay")) {
                try {
                    long amount = 0;

                    try {
                        amount = Long.parseLong(args[1]);
                    } catch (final NumberFormatException exception) {
                        amount = MathUtil.getLongFromStringwithSuffix(args[1]);
                    }

                    if (amount <= 0) {
                        messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen. (k, m, mrd, b, brd, t)");
                        return false;
                    }

                    final long finalAmount = amount;

                    userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

                        if (!messageUtil.hasEnougthMoney(user, finalAmount))
                            return;

                        user.removeLong(DataType.MONEY, finalAmount);
                        user.getBank().deposit(player, finalAmount);
                    });


                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine gültige Zahl an§8.");
                }

                return false;
            }

            if (args[0].equalsIgnoreCase("withdraw") || args[0].equalsIgnoreCase("get")) {
                long amount = 0;

                try {
                    amount = Long.parseLong(args[1]);
                } catch (final NumberFormatException exception) {
                    amount = MathUtil.getLongFromStringwithSuffix(args[1]);
                }

                if (amount <= 0) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen. (k, m, mrd, b, brd, t)");
                    return false;
                }

                final long finalAmount = amount;

                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                    if (user.getBank().withdraw(player, finalAmount))
                        user.addLong(DataType.MONEY, finalAmount);
                });

                return false;
            }

            if(args[0].equalsIgnoreCase("add")) {

                if (!messageUtil.hasPlayedBefore(player, args[1]))
                    return false;

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

                    final UserBank bank = user.getBank();

                    if(bank.isMember(target)) {
                        messageUtil.sendMessage(player, "Der Spieler " + target.getName() + " hat bereits Zugriff auf dein Konto§8.");
                        return;
                    }

                    bank.addMember(target);

                });

                return false;
            }

            if(args[0].equalsIgnoreCase("remove")) {

                if (!messageUtil.hasPlayedBefore(player, args[1]))
                    return false;

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

                    final UserBank bank = user.getBank();

                    if(!bank.isMember(target)) {
                        messageUtil.sendMessage(player, "Der Spieler " + target.getName() + " hat keinen Zugriff auf dein Konto§8.");
                        return;
                    }

                    bank.removeMember(target);

                });

                return false;
            }

            if(args[0].equalsIgnoreCase("trust")) {

                if (!messageUtil.hasPlayedBefore(player, args[1]))
                    return false;

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

                    final UserBank bank = user.getBank();

                    if(bank.isTrusted(target)) {
                        messageUtil.sendMessage(player, "Der Spieler " + target.getName() + " is bereits getrusted§8.");
                        return;
                    }

                    bank.addTrusted(target);

                });

                return false;
            }

            if(args[0].equalsIgnoreCase("untrust")) {

                if (!messageUtil.hasPlayedBefore(player, args[1]))
                    return false;

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

                    final UserBank bank = user.getBank();

                    if(!bank.isTrusted(target)) {
                        messageUtil.sendMessage(player, "Der Spieler " + target.getName() + " ist nicht getrusted§8.");
                        return;
                    }

                    bank.removeTrusted(target);

                });

                return false;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("pay")) {

                if (!messageUtil.hasPlayedBefore(player, args[1]))
                    return false;

                long amount = 0;

                try {
                    amount = Long.parseLong(args[2]);
                } catch (final NumberFormatException exception) {
                    amount = MathUtil.getLongFromStringwithSuffix(args[2]);
                }

                if (amount <= 0) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen. (k, m, mrd, b, brd, t)");
                    return false;
                }

                final long finalAmount = amount;


                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

                    if (!messageUtil.hasEnougthMoney(user, finalAmount))
                        return;

                    userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(target -> {

                        if (!target.getBank().isMember(player) && !PermissionUtil.hasPermission(player, "bank.others", false)) {
                            messageUtil.sendMessage(player, "Du hast §c§okeinen§7 Zugriff auf die Bank von " + args[1] + "§8!");
                            return;
                        }

                        user.removeLong(DataType.MONEY, finalAmount);
                        target.getBank().deposit(player, finalAmount);

                    });
                });

                return false;
            }

            if (args[0].equalsIgnoreCase("withdraw") || args[0].equalsIgnoreCase("get")) {

                if (!messageUtil.hasPlayedBefore(player, args[1]))
                    return false;

                long amount = 0;

                try {
                    amount = Long.parseLong(args[2]);
                } catch (final NumberFormatException exception) {
                    amount = MathUtil.getLongFromStringwithSuffix(args[2]);
                }

                if (amount <= 0) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen. (k, m, mrd, b, brd, t)");
                    return false;
                }

                final long finalAmount = amount;


                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

                    userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(target -> {

                        if (!target.getBank().isTrusted(player) && !PermissionUtil.hasPermission(player, "bank.others", false)) {
                            messageUtil.sendMessage(player, "Du kannst §c§okein§7 Geld von " + args[1] + "'s abheben§8!");
                            return;
                        }

                        if (target.getBank().withdraw(player, finalAmount))
                            user.addLong(DataType.MONEY, finalAmount);

                    });
                });

                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/bank info",
                "/bank info <spieler>",
                "/bank withdraw <amount>",
                "/bank withdraw <spieler> <amount>",
                "/bank deposit <amount>",
                "/bank deposit <spieler> <amount>",
                "/bank add <spieler>",
                "/bank remove <spieler>",
                "/bank trust <spieler>",
                "/bank untrust <spieler>"
        );

        return false;
    }
}
