package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       01.08.2023 / 02:59

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.StatTrackHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @NonNull
public final class StatTrackCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final StatTrackHandler statTrackHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        if(!PermissionUtil.hasPermission(sender, "stattrack", true))
            return false;

        final Player player = (Player) sender;

        if(args.length == 0) {

            if(!InventoryUtil.hasItemInHand(player))
                return false;

            if(statTrackHandler.isStatTrack(player.getItemInHand())) {
                messageUtil.sendMessage(sender, "Dieses Item ist bereits Stattrack§8.");
                return false;
            }

            statTrackHandler.setStatTrack(player.getItemInHand());
            player.updateInventory();
            player.playSound(player.getLocation(), Sound.ANVIL_USE, 0.5f, 1);

            return false;
        }

        if(!PermissionUtil.hasPermission(sender, "admin", false)) {
            messageUtil.sendSyntax(sender, "/stattrack");
            return false;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reset")) {
                if(!InventoryUtil.hasItemInHand(player))
                    return false;

                if(!statTrackHandler.isStatTrack(player.getItemInHand())) {
                    messageUtil.sendMessage(sender, "Dieses Item ist nicht Stattrack§8.");
                    return false;
                }

                statTrackHandler.reset(player.getItemInHand());
                player.playSound(player.getLocation(), Sound.ANVIL_USE, 0.5f, 1);
                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/stattrack", "/stattrack reset");

        return false;
    }
}
