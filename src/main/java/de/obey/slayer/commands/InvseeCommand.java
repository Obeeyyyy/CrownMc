package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       17.10.2022 / 21:57

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
@NonNull
public final class InvseeCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "invsee", true))
            return false;

        if (args.length == 1) {

            if (!messageUtil.isOnline(sender, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            if (target == player) {
                messageUtil.sendMessage(sender, "?");
                return false;
            }

            player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.4f, 0.4f);

            if (PermissionUtil.hasPermission(player, "invsee.edit", false)) {
                player.openInventory(target.getInventory());
                return false;
            }

            final Inventory inventory = Bukkit.createInventory(null, 9 * 4, "§aInv §7" + target.getName());

            inventory.setContents(target.getInventory().getContents());
            player.openInventory(inventory);

            return false;
        }

        messageUtil.sendSyntax(sender, "/invsee <spieler>");

        return false;
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().startsWith("§aInv "))
            event.setCancelled(true);
    }
}
