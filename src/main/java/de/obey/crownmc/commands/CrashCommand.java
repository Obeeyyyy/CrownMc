package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       16.12.2022 / 22:19

*/

import de.obey.crownmc.handler.CrashHandler;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class CrashCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final CrashHandler crashHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("leave")) {
                crashHandler.getCrash().leaveCrash(player);
                return false;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("join")) {

                long amount = 0L;

                try {
                    amount = Long.parseLong(args[1]);

                    if (amount <= 0) {
                        messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an die größer als 0 ist.");
                        return false;
                    }

                } catch (final NumberFormatException exception) {

                    amount = MathUtil.getLongFromStringwithSuffix(args[1]);

                    if (amount <= 0) {
                        messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen. (k, m, mrd, b, brd, t)");
                        return false;
                    }
                }

                if (amount > 100000000) {
                    messageUtil.sendMessage(player, "Bitte gebe einen Betrag an der keiner als 100 Millionen ist§8.");
                    return false;
                }

                crashHandler.getCrash().joinCrash(player, amount);

                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/crash join <amount>", "/crash leave");

        return false;
    }
}
