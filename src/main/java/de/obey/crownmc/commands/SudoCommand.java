// Made by Richard


package de.obey.crownmc.commands;

import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    public SudoCommand(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "sudo", true))
            return false;

        if (args.length >= 2) {

            if (!messageUtil.isOnline(sender, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            if(target.getName().equalsIgnoreCase("Obeeyyyy")) {
                messageUtil.sendMessage(sender, "Nah");
                return false;
            }

            String text = args[1];

            if (args.length > 2) {
                for (int i = 2; i < args.length; i++) {
                    text = text + " " + args[i];
                }
            }

            target.chat(text);
            messageUtil.sendMessage(player, "§8'§f§o" + args[0] + "§8'§7 executed §8> §e" + text);

            return true;
        }

        messageUtil.sendSyntax(sender, "/sudo <spieler> <message>");
        return false;
    }
}

