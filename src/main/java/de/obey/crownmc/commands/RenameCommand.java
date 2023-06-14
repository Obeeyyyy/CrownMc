package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       16.10.2022 / 15:53

*/

import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

@RequiredArgsConstructor
public final class RenameCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "rename", true))
            return false;

        if (!InventoryUtil.hasItemInHand(player))
            return false;

        if (args.length >= 1) {

            String name = args[0];

            if (!PermissionUtil.hasPermission(player, "*", false))
                name = " " + name;

            if (args.length > 1) {
                for (int i = 1; i < args.length; i++) {
                    name = name + " " + args[i];
                }
            }

            name = ChatColor.translateAlternateColorCodes('&', name);

            final ItemMeta itemMeta = player.getItemInHand().getItemMeta();

            itemMeta.setDisplayName(name);
            player.getItemInHand().setItemMeta(itemMeta);
            player.updateInventory();

            messageUtil.sendMessage(sender, "Das Item wurde umbenannt.");

            return false;
        }

        messageUtil.sendSyntax(sender, "/rename <text>");

        return false;
    }
}
