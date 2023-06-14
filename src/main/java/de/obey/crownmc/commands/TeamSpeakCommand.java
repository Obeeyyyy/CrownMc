package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       02.01.2023 / 20:02

*/

import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
@NonNull
public final class TeamSpeakCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        messageUtil.sendMessage(sender, "Wir benutzen ausschließlich Discord§8.");

        return false;
    }
}
