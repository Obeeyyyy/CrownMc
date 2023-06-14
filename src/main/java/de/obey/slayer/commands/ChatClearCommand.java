package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       15.10.2022 / 17:54

*/

import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class ChatClearCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "chatclear", true))
            return false;

        Bukkit.getOnlinePlayers().forEach(all -> {
            if (!PermissionUtil.hasPermission(all, "team", false)) {
                for (int i = 0; i < 250; i++) {
                    all.sendMessage("");
                }
            }
        });

        messageUtil.sendMessageToTeamMembers("§c§oDer Chat wurde von " + sender.getName() + " geleert.");

        return false;
    }
}
