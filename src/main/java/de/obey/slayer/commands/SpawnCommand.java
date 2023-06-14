package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       16.10.2022 / 15:52

*/

import de.obey.slayer.Initializer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class SpawnCommand implements CommandExecutor {

    @NonNull
    private final Initializer initializer;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        initializer.getLocationHandler().teleportToLocationName(player, "spawn");

        return false;
    }
}
