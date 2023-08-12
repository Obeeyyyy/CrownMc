package de.obey.crownmc.tabcomplete;
/*

    Author - Obey -> SkySlayer-v4
       03.01.2023 / 21:01

*/

import de.obey.crownmc.commands.VanishCommand;
import de.obey.crownmc.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class ProtectionTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> name = new ArrayList<>();

        if(args.length == 0) {
            name.add("kill");
            name.add("info");
            name.add("toggle");
        }

        if(args.length == 1) {
            name.add("kill");
            name.add("info");
            name.add("toggle");

            final String looking = args[args.length - 1];

            List<String> sorted = new ArrayList<>();

            for (String s : name) {
                if (s.toLowerCase().startsWith(looking.toLowerCase()))
                    sorted.add(s);
            }

            name = sorted;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
            for (World world : Bukkit.getWorlds()) {
                name.add(world.getName());
            }

            final String looking = args[1];

            List<String> sorted = new ArrayList<>();

            for (String s : name) {
                if (s.toLowerCase().startsWith(looking.toLowerCase()))
                    sorted.add(s);
            }

            name = sorted;
        }

        if(args.length >= 3 && args[0].equalsIgnoreCase("toggle")) {
            name.add("pvp");
            name.add("build");
            name.add("ep");
            name.add("itemdrops");
            name.add("fly");
            name.add("pve");
            name.add("mobspawn");
            name.add("projectiles");
            name.add("interact");
            name.add("blockexplosion");

            final String looking = args[2];

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
