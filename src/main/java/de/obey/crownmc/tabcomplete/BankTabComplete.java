package de.obey.crownmc.tabcomplete;
/*

    Author - Obey -> SkySlayer-v4
       03.01.2023 / 21:01

*/

import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.commands.VanishCommand;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor @NonNull
public final class BankTabComplete implements TabCompleter {

    private final UserHandler userHandler;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if(args.length == 1) {
            List<String> args1 = new ArrayList<>();

            args1.add("info");
            args1.add("withdraw");
            args1.add("deposit");
            args1.add("add");
            args1.add("remove");
            args1.add("trust");
            args1.add("untrust");

            final String looking = args[args.length - 1];

            List<String> sorted = new ArrayList<>();

            for (String s : args1) {
                if (s.toLowerCase().startsWith(looking.toLowerCase()))
                    sorted.add(s);
            }

            args1 = sorted;

            return args1;
        }

        if(args.length >= 2) {
            if (args[0].equalsIgnoreCase("add") ||
                    args[0].equalsIgnoreCase("info") ||
                    args[0].equalsIgnoreCase("trust")) {
                final List<String> name = new ArrayList<>();

                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (sender instanceof Player && !PermissionUtil.hasPermission(((Player) sender).getPlayer(), "team", false)) {
                        if (!VanishCommand.vanished.contains(all))
                            name.add(all.getName());
                    } else {
                        name.add(all.getName());
                    }
                }

                if (args.length > 2) {
                    final String looking = args[2];
                    final List<String> sorted = new ArrayList<>();

                    for (String s : name) {
                        if (s.toLowerCase().startsWith(looking.toLowerCase()))
                            sorted.add(s);
                    }

                    return sorted;
                }

                return name;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                final User user = userHandler.getUserInstant(((Player) sender).getUniqueId());

                final List<String> name = new ArrayList<>();
                for (UUID uuid : user.getBank().getMembers()) {
                    name.add(Bukkit.getOfflinePlayer(uuid).getName());
                }

                if(args.length > 2) {
                    final String looking = args[2];
                    final List<String> sorted = new ArrayList<>();

                    for (String s : name) {
                        if (s.toLowerCase().startsWith(looking.toLowerCase()))
                            sorted.add(s);
                    }

                    return sorted;
                }

                return name;
            }

            if (args[0].equalsIgnoreCase("untrust")) {
                final User user = userHandler.getUserInstant(((Player) sender).getUniqueId());

                final List<String> name = new ArrayList<>();
                for (UUID uuid : user.getBank().getTrusted()) {
                    name.add(Bukkit.getOfflinePlayer(uuid).getName());
                }

                if(args.length > 2) {
                    final String looking = args[2];
                    final List<String> sorted = new ArrayList<>();

                    for (String s : name) {
                        if (s.toLowerCase().startsWith(looking.toLowerCase()))
                            sorted.add(s);
                    }

                    return sorted;
                }

                return name;
            }
        }

        return null;
    }
}
