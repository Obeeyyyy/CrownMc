package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       05.07.2023 / 18:14

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

@RequiredArgsConstructor
public final class ShieldCommand implements CommandExecutor, Listener {

    @NonNull
    private final MessageUtil messageUtil;

    private final ArrayList<Player> shielded = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(!PermissionUtil.hasPermission(player, "shield", true))
            return false;

        if(args.length == 0) {

            if(shielded.contains(player)) {
                shielded.remove(player);
                messageUtil.sendMessage(player, "Schild deaktiviert§8.");
                return false;
            }

            shielded.add(player);
            messageUtil.sendMessage(player, "Schild aktiviert§8.");

            return false;
        }

        if(!PermissionUtil.hasPermission(player, "shield.others", true))
            return false;

        if(args.length == 1) {

            if(!messageUtil.isOnline(player, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);


            if(shielded.contains(target)) {
                shielded.remove(target);
                messageUtil.sendMessage(player, "Schild von " + args[0] + " deaktiviert§8.");
                messageUtil.sendMessage(target, "Schild deaktiviert§8.");
                return false;
            }

            shielded.add(target);
            messageUtil.sendMessage(player, "Schild von " + args[0] + " aktiviert§8.");
            messageUtil.sendMessage(target, "Schild aktiviert§8.");

            return false;
        }

        return false;
    }

    @EventHandler
    public void on(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if(shielded.isEmpty())
            return;

        for (final Entity entity : player.getWorld().getEntities()) {
            if(!(entity instanceof Player))
                continue;

            final Player other = (Player) entity;

            if(player.getName().equalsIgnoreCase(other.getName()))
                continue;

            if(PermissionUtil.hasPermission(player, "shield.bypass", false))
                continue;

            if(!shielded.contains(other))
                return;

            if(player.getLocation().distance(other.getLocation()) > 5)
                continue;

            final Vector direction = player.getLocation().subtract(other.getLocation()).toVector();
            direction.setY(0.5);
            direction.normalize().multiply(1.2);
            player.setVelocity(direction);
        }
    }
}
