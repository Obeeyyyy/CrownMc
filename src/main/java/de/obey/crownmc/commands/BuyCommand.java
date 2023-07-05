package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       29.06.2023 / 19:56

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public final class BuyCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    @NonNull
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        if(command.getName().equalsIgnoreCase("buy")) {
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("crowns")) {
                    final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                    if (!messageUtil.hasPlayedBefore(sender, args[1]))
                        return false;

                    final int amount = Integer.parseInt(args[2]);

                    userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                        user.addLong(DataType.CROWNS, amount);

                        messageUtil.broadcast(target.getName() + " hat sich §e§o" + messageUtil.formatLong(amount) + "§7 Crowns in unserem §8§l/§6§lstore §7gekauft§8.");

                        if (target.isOnline())
                            target.getPlayer().playSound(target.getPlayer().getLocation(), Sound.WITHER_DEATH, 0.5f, 1);
                    });

                    return false;
                }
            }
            return false;
        }

        if(command.getName().equalsIgnoreCase("refund")) {
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("crowns")) {
                    final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                    if (!messageUtil.hasPlayedBefore(sender, args[1]))
                        return false;

                    final int amount = Integer.parseInt(args[2]);

                    userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {

                        messageUtil.sendMessage(sender, "§4§lREFUND §f" + target.getName() + " CROWNS " + amount);
                        if(user.getLong(DataType.CROWNS) < amount) {
                            messageUtil.sendMessage(sender, "§4§lWARN §fhat nicht genug crowns");
                            Bukkit.dispatchCommand(sender, "ban " + target.getName() + " chargeback");
                        }

                        user.removeLong(DataType.CROWNS, amount);

                        if (target.isOnline())
                            messageUtil.sendMessage(target.getPlayer(), "§c§oDeine Zahlung wurde zurückgezogen§8.");
                    });

                    return false;
                }
            }
            return false;
        }

        return false;
    }
}
