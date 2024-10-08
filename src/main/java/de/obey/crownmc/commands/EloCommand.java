package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       03.11.2022 / 16:33

*/

import de.obey.crownmc.handler.EloHandler;
import de.obey.crownmc.objects.pvp.EloRang;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class EloCommand implements CommandExecutor {

    private final EloHandler eloHandler;
    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        player.sendMessage("§8§l§m-----------------------------------");
        player.sendMessage("");
        player.sendMessage("§8▰§7▱ §9§lRang §8┃ §9§lPunkte");
        player.sendMessage("");

        for (final EloRang rang : EloRang.values())
            player.sendMessage("§8 - " + rang.getPrefix() + " §8┃ §f§o" + messageUtil.formatLong(rang.getEloPoints()));

        player.sendMessage("");
        player.sendMessage("§8§l§m-----------------------------------");

        return false;
    }
}
