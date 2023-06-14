// Made by Richard


package de.obey.slayer.commands;

import de.obey.slayer.backend.MySQL;
import de.obey.slayer.backend.ServerConfig;
import de.obey.slayer.backend.enums.DataType;
import de.obey.slayer.handler.BlockEventHandler;
import de.obey.slayer.handler.LocationHandler;
import de.obey.slayer.handler.UserHandler;
import de.obey.slayer.util.InventoryUtil;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor @NonNull
public class BlockEventCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final BlockEventHandler blockEventHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 0) {
            userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                messageUtil.sendMessage(player, "Du hast §ex" + messageUtil.formatLong(user.getLong(DataType.DESTROYEDEVENTBLOCKS)) + "§7 Blöcke in diesem Event abgebaut§8.");
            });
            return false;
        }

        if (!PermissionUtil.hasPermission(player, "*", true))
            return false;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("set")) {
                if (!InventoryUtil.hasItemInHand(player))
                    return false;

                final Material material = player.getItemInHand().getType();

                blockEventHandler.resetCounts();
                blockEventHandler.setEventBlock(material.name());
                blockEventHandler.setupArmorStands();
                messageUtil.sendMessage(player, "Du hast " + material.name() + " als Event Ziel gesetzt und alle Stände zurückgesetzt.");
                return true;
            }
        }

        messageUtil.sendSyntax(sender, "/blockevent set (item in der hand)");

        return false;
    }
}