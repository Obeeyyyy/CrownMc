package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       17.10.2022 / 14:03

*/

import de.obey.crownmc.util.Bools;
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
public final class GlobalMuteCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "globalmute", true))
            return false;

        if (Bools.chat) {
            Bools.chat = false;
            messageUtil.broadcast("Globalmute wurde von §e§o" + sender.getName() + " §aaktiviert§7.");
            return false;
        }

        Bools.chat = true;
        messageUtil.broadcast("Globalmute wurde von §e§o" + sender.getName() + " §cdeaktiviert§7.");

        return false;
    }
}
