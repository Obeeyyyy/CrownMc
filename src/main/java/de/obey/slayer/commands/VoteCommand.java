package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       27.12.2022 / 19:24

*/

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class VoteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        player.sendMessage("§8§l§m---------------------------------------------");
        player.sendMessage("");
        player.sendMessage(" §8» ┃ §a§l Vote 1 §8:§f§o https://minecraft-server.eu/vote/index/1FB6F/" + player.getName());
        player.sendMessage("");
        player.sendMessage("§8§l§m---------------------------------------------");

        return false;
    }
}
