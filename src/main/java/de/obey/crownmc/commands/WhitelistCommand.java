package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 23:12

*/

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@RequiredArgsConstructor
public final class WhitelistCommand implements CommandExecutor {

    @NonNull
    private final ServerConfig serverConfig;

    @NonNull
    private final MessageUtil messageUtil;

    public static final ArrayList<String> tempList = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission(sender, "editwhitelist", true))
            return false;

        if (args.length == 0) {

            if (serverConfig.isWhitelist()) {
                serverConfig.setWhitelist(false);
                messageUtil.broadcast("Die Whitelist wurde deaktiviert. §8(§f§o" + sender.getName() + "§8)");
                return false;
            }

            serverConfig.setWhitelist(true);
            messageUtil.broadcast("Die Whitelist wurde aktiviert. §8(§f§o" + sender.getName() + "§8)");

            return false;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("beta")) {
                if (serverConfig.isBetawhitelist()) {
                    serverConfig.setBetawhitelist(false);
                    messageUtil.broadcast("Die Betawhitelist wurde deaktiviert. §8(§f§o" + sender.getName() + "§8)");
                    return false;
                }

                serverConfig.setBetawhitelist(true);
                messageUtil.broadcast("Die Betawhitelist wurde aktiviert. §8(§f§o" + sender.getName() + "§8)");

                return false;
            }

            if (tempList.contains(args[0].toLowerCase())) {
                tempList.remove(args[0].toLowerCase());
                messageUtil.sendMessage(sender, args[0] + " ist jetzt nicht mehr auf der temp whitelist§8.");
                return false;
            }

            tempList.add(args[0].toLowerCase());
            messageUtil.sendMessage(sender, args[0] + " ist jetzt auf der temp whitelist§8.");

            return false;
        }

        messageUtil.sendSyntax(sender,
                "/whitelist - ändert den status",
                "/whitelist beta - ändert den status der beta wl",
                "/whitelist <name> - um spieler auf die temporäre wl zu setzen");

        return false;
    }
}
