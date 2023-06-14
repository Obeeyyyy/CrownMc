package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       20.10.2022 / 17:28

*/

import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor
public final class GodCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    public static final ArrayList<UUID> godmode = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "team", true))
            return false;

        if (args.length == 0) {

            if (godmode.contains(player.getUniqueId())) {
                godmode.remove(player.getUniqueId());
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);
                messageUtil.sendMessage(sender, "Godmode §c§odeaktivert§7.");
                return false;
            }

            godmode.add(player.getUniqueId());
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);
            messageUtil.sendMessage(sender, "Godmode §a§oaktiviert§7.");

            return false;
        }

        if (!PermissionUtil.hasPermission(player, "godmode.others", true))
            return false;

        if (args.length == 1) {

            if (!messageUtil.isOnline(sender, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            if (godmode.contains(target.getUniqueId())) {
                godmode.remove(target.getUniqueId());
                target.playSound(target.getLocation(), Sound.LEVEL_UP, 0.5f, 1);
                messageUtil.sendMessage(sender, "Godmode für " + target.getName() + " §c§odeaktiviert§7.");
                messageUtil.sendMessage(target, "Godmode §c§odeaktivert§7.");
                return false;
            }

            godmode.add(target.getUniqueId());
            target.playSound(target.getLocation(), Sound.LEVEL_UP, 0.5f, 1);
            messageUtil.sendMessage(sender, "Godmode für " + target.getName() + " §a§oaktiviert§7.");
            messageUtil.sendMessage(target, "Godmode §a§oaktiviert§7.");

            return false;
        }

        messageUtil.sendSyntax(sender, "/god", "/god <spieler>");
        return false;
    }
}
