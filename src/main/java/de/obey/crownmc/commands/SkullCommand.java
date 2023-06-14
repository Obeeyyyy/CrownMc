package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       20.10.2022 / 14:39

*/

import de.obey.crownmc.backend.user.UserCooldowns;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class SkullCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "skull", true))
            return false;

        if (args.length == 1) {

            if (!PermissionUtil.hasPermission(player, "team", false)) {
                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                    final UserCooldowns cd = user.getCooldowns();

                    if (!cd.isReady("skull")) {
                        messageUtil.sendMessage(player, "Du kannst diesen Command erst in §e" + MathUtil.getMinutesAndSecondsFromSeconds(cd.getRemainingMillis("skull") / 1000) + "§7 benutzen§8.");
                        return;
                    }

                    cd.setCooldown("skull", System.currentTimeMillis() + (1000 * 60 * 3));
                    player.getInventory().addItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setSkullOwner(args[0]).build());
                    messageUtil.sendMessage(player, "Du hast den Kopf von §e§o" + args[0] + "§7 bekommen§8.");

                });
            } else {

                player.getInventory().addItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setSkullOwner(args[0]).build());
                messageUtil.sendMessage(player, "Du hast den Kopf von §e§o" + args[0] + "§7 bekommen§8.");
                return false;

            }
            return false;
        }

        messageUtil.sendSyntax(sender, "/skull <spieler>");

        return false;
    }
}
