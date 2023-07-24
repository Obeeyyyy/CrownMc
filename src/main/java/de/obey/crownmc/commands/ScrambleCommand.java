package de.obey.crownmc.commands;

import de.obey.crownmc.handler.WordScrambleHandler;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor @NonNull
public class ScrambleCommand implements CommandExecutor {

    private final WordScrambleHandler wordScrumbleHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("start")) {

                wordScrumbleHandler.startNewScramble();

                return false;
            }
        }

        return false;
    }
}
