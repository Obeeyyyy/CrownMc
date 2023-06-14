package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       07.11.2022 / 19:00

*/

import de.obey.slayer.handler.WarpHandler;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@RequiredArgsConstructor
@NonNull
public final class WarpCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final WarpHandler warpHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("warp")) {

            if (args.length == 0) {
                warpHandler.openWarpInventory(player);
                return false;
            }

            if (args.length == 1) {
                final String warpName = args[0].toLowerCase();

                warpHandler.teleportToWarp(player, warpName);

                return false;
            }

            if (!PermissionUtil.hasPermission(player, "edit.warps", true))
                return false;

            // warp create name
            // warp setslot name
            // warp setprefix name
            // warp setitem name
            // warp delete name

            if (args.length == 2) {
                final String warpName = args[1].toLowerCase();

                if (args[0].equalsIgnoreCase("create")) {
                    warpHandler.createWarp(player, warpName);
                    return false;
                }

                if (args[0].equalsIgnoreCase("delete")) {
                    warpHandler.deleteWarp(player, warpName);
                    return false;
                }

                if (args[0].equalsIgnoreCase("setitem")) {

                    if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                        messageUtil.sendMessage(player, "Du musst ein Item in der Hand halten §8.");
                        return false;
                    }

                    warpHandler.setItem(player, warpName, player.getItemInHand());

                    return false;
                }
            }

            if (args.length == 3) {
                final String warpName = args[1].toLowerCase();

                if (args[0].equalsIgnoreCase("setslot")) {

                    try {
                        final int newSlot = Integer.parseInt(args[2]);
                        warpHandler.setSlot(player, warpName, newSlot);
                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                    }

                    return false;
                }
            }

            if (args[0].equalsIgnoreCase("setprefix")) {

                String prefix = args[2].toLowerCase();

                for (int i = 3; i < args.length; i++) {
                    prefix = prefix + " " + args[i];
                }

                warpHandler.setPrefix(player, args[1], prefix);

                return false;
            }

            messageUtil.sendSyntax(sender, "/warp create <name>",
                    "/warp delete <name>",
                    "/warp setslot <name> <slot>",
                    "/warp setprefix <name> <prefix>",
                    "/warp setitem <name>"
            );

            return false;
        }

        if (command.getName().equalsIgnoreCase("warps")) {
            if (warpHandler.getWarps().isEmpty()) {
                messageUtil.sendMessage(player, "Es existieren noch keine Warps§8.");
                return false;
            }

            messageUtil.sendMessage(player, "Nutze /warp <name> um dich zu Teleportieren§8:");
            warpHandler.getWarps().keySet().forEach(name -> player.sendMessage("§8 - §f§o" + name));
        }

        return false;
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if (!InventoryUtil.isInventoryTitle(event.getInventory(), "§e§lWARPS"))
            return;

        event.setCancelled(true);

        if (!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§e§lWARPS"))
            return;

        warpHandler.getWarps().values().forEach(warp -> {
            if (warp.getSlot() == event.getSlot())
                warpHandler.teleportToWarp((Player) event.getWhoClicked(), warp.getWarpName());
        });
    }
}
