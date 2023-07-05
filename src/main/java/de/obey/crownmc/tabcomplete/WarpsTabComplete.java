package de.obey.crownmc.tabcomplete;
/*

    Author - Obey -> CrownMc
       05.07.2023 / 16:14

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.WarpHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor @NonNull
public final class WarpsTabComplete implements TabCompleter {

    private final WarpHandler warpHandler;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> warps = new ArrayList<>(warpHandler.getWarps().keySet());

        if (args.length > 0) {
            final String looking = args[args.length - 1];

            List<String> sorted = new ArrayList<>();

            for (String warp : warps) {
                if (warp.toLowerCase().startsWith(looking.toLowerCase()))
                    sorted.add(warp);
            }

            return sorted;
        }

        return warps;
    }
}
