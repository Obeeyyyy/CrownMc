package de.obey.crownmc.tabcomplete;
/*

    Author - Obey -> SkySlayer-v4
       03.01.2023 / 21:01

*/

import de.obey.crownmc.commands.VanishCommand;
import de.obey.crownmc.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class OnlineListTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> name = new ArrayList<>();

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (sender instanceof Player && !PermissionUtil.hasPermission(((Player) sender).getPlayer(), "team", false)) {
                if (!VanishCommand.vanished.contains(all))
                    name.add(all.getName());
            } else {
                name.add(all.getName());
            }
        }
        for (Player all: Bukkit.getOnlinePlayers()) {
            if (sender instanceof Player && PermissionUtil.hasPermission(((Player) sender).getPlayer(), "team", false)) {
                if (VanishCommand.vanished.contains(all)) name.add(all.getName());
            }
        }

        if (args.length > 0) {
            final String looking = args[args.length - 1];

            List<String> sorted = new ArrayList<>();

            for (String s : name) {
                if (s.toLowerCase().startsWith(looking.toLowerCase()))
                    sorted.add(s);
            }

            name = sorted;
        }

        return name;
    }
}
