package de.obey.crownmc.commands;
/*

    Author - EntixOG -> SkySlayer-v4
       04.11.2022 / 15:30

*/

import de.obey.crownmc.handler.ChatFilterHandler;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class ChatFilterCommand implements CommandExecutor {

    private final ChatFilterHandler chatFilterHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "*", true))
            return false;

        if (args.length == 0 || args.length > 2) {
            sendHelp(sender, label);
            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (chatFilterHandler.getWordFilterList().isEmpty()) {
                    sender.sendMessage("§cEs stehen derzeit keine Wörter auf der Liste.");
                    return false;
                }

                StringBuilder stringBuilder = new StringBuilder();
                for (String blockedWord : chatFilterHandler.getWordFilterList()) {
                    stringBuilder.append("§a").append(blockedWord).append("§8, ");
                }

                String wordListString = stringBuilder.substring(0, stringBuilder.length() - 4);
                sender.sendMessage("§7Es stehen folgende Wörter auf der Liste:");
                sender.sendMessage(wordListString);
            } else {
                sendHelp(sender, label);
                return false;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                String word = args[1];
                if (chatFilterHandler.getWordFilterList().contains(word)) {
                    sender.sendMessage("§cDas Wort steht bereits auf der Liste.");
                    return false;
                }

                chatFilterHandler.getWordFilterList().add(word);
                sender.sendMessage("§7Das Wort §e" + word + " §7wurde zu Liste hinzugefügt.");

                chatFilterHandler.save();
            } else if (args[0].equalsIgnoreCase("remove")) {
                String word = args[1];
                if (!chatFilterHandler.getWordFilterList().contains(word)) {
                    sender.sendMessage("§cDas Wort ist in der Liste nicht enthalten.");
                    return false;
                }

                chatFilterHandler.getWordFilterList().remove(word);
                sender.sendMessage("§7Das Wort §e" + word + " §7wurde von der Liste entfernt.");

                chatFilterHandler.save();
            } else {
                sendHelp(sender, label);
                return false;
            }
        }
        return false;
    }

    public void sendHelp(CommandSender sender, String lable) {
        sender.sendMessage("§b/" + lable + " list");
        sender.sendMessage("§b/" + lable + " add <Wort>");
        sender.sendMessage("§b/" + lable + " remove <Wort>");
    }
}
