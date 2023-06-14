package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       06.12.2022 / 10:38

*/

import de.obey.slayer.util.InventoryUtil;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@NonNull
public final class HatCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "hat", true))
            return false;

        if (args.length == 0) {
            final ItemStack head = player.getInventory().getHelmet();

            if (!InventoryUtil.hasItemInHand(player, false)) {

                if (head == null || head.getType() == Material.AIR) {
                    messageUtil.sendMessage(sender, "Du hast kein Item auf deinem Kopf§8.");
                    return false;
                }

                player.getInventory().setHelmet(null);
                InventoryUtil.addItem(player, head);
                messageUtil.sendMessage(sender, "Du hast das Item auf deinem Kopf entfernt§8.");

                return false;
            }

            if (head != null && head.getType() != Material.AIR)
                InventoryUtil.addItem(player, head);

            player.getInventory().setHelmet(player.getItemInHand());
            player.setItemInHand(null);

            messageUtil.sendMessage(sender, "Du hast ein Item auf deinen Kopf gesetzt§8.");

            return false;
        }

        if (!PermissionUtil.hasPermission(player, "hat.others", true))
            return false;

        if (args.length == 1) {
            if (!messageUtil.isOnline(sender, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);
            final ItemStack head = target.getInventory().getHelmet();

            if (!InventoryUtil.hasItemInHand(player, false)) {
                if (head == null || head.getType() == Material.AIR) {
                    messageUtil.sendMessage(player, target.getName() + " hat kein Item auf seinem Kopf§8.");
                    return false;
                }

                InventoryUtil.addItem(target, head);
                target.getInventory().setHelmet(null);

                messageUtil.sendMessage(target, "Dir wurde das Item vom Kopf genommen§8.");
                messageUtil.sendMessage(player, "Du hast das Item auf dem Kopf von " + target.getName() + " entfernt§8.");

                return false;
            }

            if (head != null && head.getType() != Material.AIR)
                InventoryUtil.addItem(target, head);

            target.getInventory().setHelmet(player.getItemInHand());
            player.setItemInHand(null);

            messageUtil.sendMessage(target, "Dir wurde ein Item auf den Kopf gesetzt§8.");
            messageUtil.sendMessage(player, "Du hast " + target.getName() + " ein Item auf den Kopf gesetzt§8.");
        }

        return false;
    }
}
