package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       22.10.2022 / 01:55

*/

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

@RequiredArgsConstructor
@NonNull
public final class MotdCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final ServerConfig serverConfig;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "motd", true))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0) {
            messageUtil.sendMessage(sender, "Die aktuelle MOTD:");
            messageUtil.sendMessage(sender, "Line1: " + serverConfig.getMotd1());
            messageUtil.sendMessage(sender, "Line2: " + serverConfig.getMotd2());
            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {

                final YamlConfiguration cfg = serverConfig.getCfg();

                if (cfg.contains("motd.1"))
                    serverConfig.setMotd1(cfg.getString("motd.1"));

                if (cfg.contains("motd.2"))
                    serverConfig.setMotd1(cfg.getString("motd.2"));

                messageUtil.sendMessage(sender, "MOTD neu geladen.");

                return false;
            }
        }

        if (args.length >= 2) {

            final StringBuilder line = new StringBuilder(args[1].replace(",", " "));

            if (args.length > 2) {
                for (int i = 2; i < args.length; i++) {
                    line.append(" ").append(args[i].replace("#", " "));
                }
            }

            if (args[0].equalsIgnoreCase("line1")) {
                serverConfig.setMotd1(line.toString());
                messageUtil.sendMessage(sender, "Line1 §8 > §r" + line);
                return false;
            }

            if (args[0].equalsIgnoreCase("line2")) {
                serverConfig.setMotd2(line.toString());
                messageUtil.sendMessage(sender, "Line2 §8 > §r" + line);
                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/motd reload", "/motd line1 <motd>", "/motd line2 <motd>", "Nutze # um lücken darzustellen.");

        return false;
    }

    @EventHandler
    public void on(final ServerListPingEvent event) {
        final String line1 = ChatColor.translateAlternateColorCodes('&', serverConfig.getMotd1());
        final String line2 = ChatColor.translateAlternateColorCodes('&', serverConfig.getMotd2());

        String newLine1 = "", newLine2 = "";

        for (int i = 0; i < (70 - line1.length()) / 2; i++) {
            newLine1 = newLine1 + " ";
        }

        for (int i = 0; i < (60 - line2.length()) / 2; i++) {
            newLine2 = newLine2 + " ";
        }

        newLine1 = newLine1 + line1;
        newLine2 = newLine2 + line2;

        event.setMotd(newLine1 + "\n§r" + newLine2);
    }
}
