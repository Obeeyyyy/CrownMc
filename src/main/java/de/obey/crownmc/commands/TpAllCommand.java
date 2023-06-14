package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       14.10.2022 / 17:51

*/

import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class TpAllCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "tpall", true))
            return false;

        Bukkit.getOnlinePlayers().forEach(all -> all.teleport(player));
        messageUtil.sendMessage(sender, "Alle Spieler wurden zu dir teleportiert.");

        return false;
    }

}
