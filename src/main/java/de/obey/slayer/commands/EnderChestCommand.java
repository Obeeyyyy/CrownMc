package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       20.10.2022 / 13:04

*/

import de.obey.slayer.Initializer;
import de.obey.slayer.backend.user.User;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;


@RequiredArgsConstructor
public final class EnderChestCommand implements CommandExecutor {

    @NonNull
    private final Initializer initializer;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "enderchest", true))
            return false;

        if (args.length == 0) {
            initializer.getUserHandler().getUserInstant(player.getUniqueId()).getEnderchest().openEnderchestSite(player, 1);
            return false;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("trusted") || args[0].equalsIgnoreCase("list")) {

                final User user = initializer.getUserHandler().getUserInstant(player.getUniqueId());

                if (user.getEnderchest().getEnderchestTrusted().size() == 0) {
                    initializer.getMessageUtil().sendMessage(sender, "Niemand hat Zugriff auf deine Enderchest§8.");
                    return false;
                }

                initializer.getMessageUtil().sendMessage(sender, "Diese Spieler haben Zugriff auf deine Enderchest§8:");
                user.getEnderchest().getEnderchestTrusted().forEach(uuid -> player.sendMessage("§8  - §7" + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()));

                return false;
            }

            if (!initializer.getMessageUtil().hasPlayedBefore(sender, args[0]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            initializer.getUserHandler().getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                user.getEnderchest().openEnderchestSite(player, 1);
            });

            return false;
        }

        if (args.length == 2) {

            if (!initializer.getMessageUtil().hasPlayedBefore(sender, args[1]))
                return false;

            final User user = initializer.getUserHandler().getUserInstant(player.getUniqueId());
            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (args[0].equalsIgnoreCase("trust")) {

                if (user.getEnderchest().getEnderchestTrusted().contains(target.getUniqueId().toString())) {
                    initializer.getMessageUtil().sendMessage(sender, target.getName() + " hat bereits zugriff auf deine Enderchest§8.");
                    return false;
                }

                user.getEnderchest().getEnderchestTrusted().add(target.getUniqueId().toString());

                if (target.isOnline())
                    initializer.getMessageUtil().sendMessage(target.getPlayer(), "Du hast jetzt §aZugriff§7 auf die §5Enderchest§7 von §e" + player.getName() + "§8.");

                initializer.getMessageUtil().sendMessage(sender, target.getName() + " kann jetzt auf deine §5Enderchest §7zugreifen§8.");

                return false;
            }

            if (args[0].equalsIgnoreCase("untrust")) {

                if (!user.getEnderchest().getEnderchestTrusted().contains(target.getUniqueId().toString())) {
                    initializer.getMessageUtil().sendMessage(sender, target.getName() + " hat keinen zugriff auf deine Enderchest§8.");
                    return false;
                }

                user.getEnderchest().getEnderchestTrusted().remove(target.getUniqueId().toString());

                if (target.isOnline())
                    initializer.getMessageUtil().sendMessage(target.getPlayer(), "Du hast jetzt §ckeinen Zugriff§7 mehr auf die §5Enderchest§7 von §e" + player.getName() + "§8.");

                initializer.getMessageUtil().sendMessage(sender, target.getName() + " kann jetzt nicht mehr auf deine §5Enderchest §7zugreifen§8.");

                return false;
            }
        }

        initializer.getMessageUtil().sendSyntax(sender, "/ec <spieler>", "/ec trusted", "/ec trust <spieler>", "/ec untrust <spieler>");

        return false;
    }
}
