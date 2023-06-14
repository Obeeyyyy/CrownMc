package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       06.01.2023 / 17:25

*/

import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor @NonNull
public final class GiveCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player && !PermissionUtil.hasPermission(((Player) sender).getPlayer(), "give", true))
            return false;

        if(args.length == 1 || args.length == 2 || args.length == 3) {
            if(args.length > 1) {
                if (!messageUtil.isOnline(sender, args[0]))
                    return false;
            }

            final Player target = args.length > 1 ? Bukkit.getPlayer(args[0]) : (Player) sender;

            final String value = args.length > 1 ? args[1] : args[0];

            ItemStack item = new ItemStack(Material.AIR);

            if(value.contains(":")) {
                final String[] matString = value.split(":");

                try {
                    item.setType(Material.getMaterial(Integer.parseInt(matString[0])));
                } catch ( final NumberFormatException exception) {
                    try {
                        item.setType(Material.getMaterial(matString[0].toUpperCase()));
                    } catch (final IllegalArgumentException exception1) {
                        messageUtil.sendMessage(sender, "Bitte gebe ein gültiges Material an§8.");
                        return false;
                    }
                }

                try {
                    item.setDurability(((byte) Integer.parseInt(matString[1])));
                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Bitte gebe ein gültiges Material an§8.");
                    return false;
                }
            } else {
                try {
                    item.setType(Material.getMaterial(args.length > 1 ? Integer.parseInt(args[1]) : Integer.parseInt(args[0])));
                } catch ( final NumberFormatException exception) {
                    try {
                        item.setType(Material.getMaterial(args.length > 1 ? args[1].toUpperCase() : args[0].toUpperCase()));
                    } catch (final IllegalArgumentException exception1) {
                        messageUtil.sendMessage(sender, "Bitte gebe ein gültiges Material an§8.");
                        return false;
                    }
                }
            }

            int amount = 1;

            if(args.length == 3) {
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender,"Bitte gebe eine gültige Zahl an§8.");
                }
            }
            if(item.getType() == Material.AIR) {
                messageUtil.sendMessage(sender, "Bitte gebe ein gültiges Material an§8.");
                return false;
            }

            InventoryUtil.addItem(target, item, amount);
            messageUtil.sendMessage(sender, target.getName() + " hat x" + amount + " " + item.getType().name() + " erhalten§8.");

            return false;
        }

        messageUtil.sendSyntax(sender, "/give <spieler> <item> <anzahl>");

        return false;
    }
}
