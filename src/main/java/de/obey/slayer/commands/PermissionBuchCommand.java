package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       06.12.2022 / 11:56

*/

import de.obey.slayer.util.InventoryUtil;
import de.obey.slayer.util.ItemBuilder;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
@NonNull
public final class PermissionBuchCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "admin", true))
            return false;

        if (args.length > 1) {

            final String prefix = ChatColor.translateAlternateColorCodes('&', args[0]);

            String perm = "";

            for (int i = 1; i < args.length; i++)
                perm = perm + " " + args[i];

            InventoryUtil.addItem(player, new ItemBuilder(Material.BOOK)
                    .setDisplayname("§8» §c§lPERMISSION §8┃ §r" + prefix)
                    .setLore("",
                            "§8▰§7▱ §cRechtsklick",
                            "§8 - §7Löst den gutschein ein§8.",
                            "",
                            "§8▰§7▱ §cPermission",
                            "§8 -§7 " + perm,
                            "")
                    .build());

            return false;
        }

        messageUtil.sendSyntax(sender, "/permissionbuch <prefix> <permission>");

        return false;
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        final Player player = event.getPlayer();

        if (!InventoryUtil.isItemInHandStartsWith(player, "§8» §c§lPERMISSION §8┃ §r"))
            return;

        event.setCancelled(true);

        final String permission = player.getItemInHand().getItemMeta().getLore().get(5).split(" ")[3];

        if (PermissionUtil.hasPermission(player, permission, false)) {
            messageUtil.sendMessage(player, "Du hast diese Permission bereits§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
            return;
        }

        messageUtil.sendMessage(player, "Du hast §8'" + player.getItemInHand().getItemMeta().getDisplayName() + "§8'§7 eingelöst§8.");
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

        InventoryUtil.removeItemInHand(player, 1);
        PermissionUtil.addPermission(player, permission);
    }
}
