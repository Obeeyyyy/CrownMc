package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       24.10.2022 / 18:29

*/

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
public final class PrivateChatClearCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "pcc", true))
            return false;

        for (int i = 0; i < 150; i++)
            player.sendMessage("");

        messageUtil.sendMessage(sender, "Du hast deinen Chat geleert.");

        return false;
    }
}
