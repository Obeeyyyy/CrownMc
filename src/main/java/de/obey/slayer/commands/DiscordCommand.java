package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       28.12.2022 / 19:49

*/

import de.obey.slayer.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
@NonNull
public final class DiscordCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        messageUtil.sendMessage(sender, "Unser Discord§8:§f§o http://discord.crownmc.de");

        return false;
    }
}
