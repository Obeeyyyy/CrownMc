package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       02.01.2023 / 21:32

*/

import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class ClearlagCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission(((Player) sender).getPlayer(), "clearlag", true))
            return false;

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item || entity instanceof Creature)
                    entity.remove();
            }
        }

        messageUtil.broadcast(sender.getName() + " hat alle Items auf dem Boden gelöscht§8.");

        return false;
    }
}
