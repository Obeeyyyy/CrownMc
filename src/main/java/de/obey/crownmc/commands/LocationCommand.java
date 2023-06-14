package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       17.10.2022 / 16:21

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class LocationCommand implements CommandExecutor {

    @NonNull
    private final Initializer initializer;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "location", true))
            return false;

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("list")) {

                player.sendMessage("§oLocations ->");

                initializer.getLocationHandler().getLocations().keySet().forEach(name -> {
                    final Location location = initializer.getLocationHandler().getLocation(name);
                    player.sendMessage("§8    <=> §f" + name + "§8 - (§e§o " + location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ() + " §8)");
                });

                return false;
            }

        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {

                initializer.getLocationHandler().setLocation(args[1], player.getLocation());
                initializer.getMessageUtil().sendMessage(sender, "Location " + args[1] + " wurde gesetzt.");

                return false;
            }

            if (args[0].equalsIgnoreCase("delete")) {

                initializer.getLocationHandler().deleteLocation(args[1]);
                initializer.getMessageUtil().sendMessage(sender, "Location " + args[1] + " wurde gelöscht.");

                return false;
            }

            if (args[0].equalsIgnoreCase("tp")) {

                if (!initializer.getLocationHandler().getLocations().containsKey(args[1])) {
                    initializer.getMessageUtil().sendMessage(sender, "Die Location " + args[1] + " existiert nicht.");
                    return false;
                }

                initializer.getLocationHandler().teleportToLocationNameInstant(player, args[1]);

                return false;
            }
        }

        initializer.getMessageUtil().sendSyntax(sender, "/location list",
                "/location set <name>",
                "/location delete <name>",
                "/location tp <name>");

        return false;
    }
}
