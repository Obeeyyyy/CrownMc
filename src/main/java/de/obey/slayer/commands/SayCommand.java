package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       09.01.2023 / 19:10

*/

import de.obey.slayer.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @NonNull
public final class SayCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            messageUtil.sendMessage(sender, "Dieser Command ist nicht für dich§8.");
            return false;
        }

        String text = "";

        if(args.length > 0) {
            text = args[0];

            if(args.length > 1) {
                for (int i = 1; i < args.length; i++) {
                    text = text + " " + args[i];
                }
            }
        }

        Bukkit.broadcastMessage("§8[§4§lConsole§8] (§b§lObey§8) -> §f" + ChatColor.translateAlternateColorCodes('&', text));

        return false;
    }
}
