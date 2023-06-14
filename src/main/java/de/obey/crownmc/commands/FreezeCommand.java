package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       15.11.2022 / 17:39

*/

import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor
public final class FreezeCommand implements CommandExecutor, Listener {

    @NonNull
    private final MessageUtil messageUtil;

    @Getter
    private static final ArrayList<UUID> freezed = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "freeze", true))
            return false;

        if (args.length == 1) {

            if (!messageUtil.isOnline(sender, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            if (PermissionUtil.hasPermission(target, "nofreeze", false)) {
                messageUtil.sendMessage(sender, "Du kannst " + target.getName() + " nicht freezen§8.");
                return false;
            }

            if (freezed.contains(target.getUniqueId())) {
                freezed.remove(target.getUniqueId());
                messageUtil.sendMessage(target, "Du kannst dich wieder bewegen§8.");
                messageUtil.sendMessage(sender, "Du hast " + target.getName() + " unfreezed§8.");
                return false;
            }

            freezed.add(target.getUniqueId());
            target.playSound(target.getLocation(), Sound.SPLASH, 1, 1);
            target.sendTitle("§f§lFREEZE", "§c§oBetrete den Discord support Warteraum !");
            messageUtil.sendMessage(target, "Du wurdest gefreezed§8,§7 bitte melde dich im Discord§8.");
            messageUtil.sendMessage(sender, "Du hast " + target.getName() + " gefreezed§8.");

            return false;
        }

        return false;
    }

    @EventHandler
    public void on(final PlayerMoveEvent event) {
        if (freezed.contains(event.getPlayer().getUniqueId())) {
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ())
                event.setCancelled(true);
        }
    }
}
