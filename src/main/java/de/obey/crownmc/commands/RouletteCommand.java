package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       03.07.2023 / 13:40

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.RouletteHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor @NonNull
public final class RouletteCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final RouletteHandler rouletteHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("respawn")) {

                rouletteHandler.respawnTables();
                messageUtil.sendMessage(player, "Alle Tische neu gespawnt§8.");

                return false;
            }

            if(args[0].equalsIgnoreCase("tables")) {

                if(rouletteHandler.getTables().isEmpty()) {
                    messageUtil.sendMessage(sender, "Es existieren noch keine Tische§7.");
                    return false;
                }

                messageUtil.sendMessage(sender, "Alle Tische (§f" + rouletteHandler.getTables().size() + "§8) §8:");
                for (Integer id : rouletteHandler.getTables().keySet()) {
                    player.sendMessage("§8 - §7Tisch §f" + id);
                    player.sendMessage("     > State§8: §f" + rouletteHandler.getTable(id).getState());
                }

                return false;
            }
        }

        if(args.length == 2) {
            if(args[0].contains("create")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(rouletteHandler.getTable(id) != null) {
                        messageUtil.sendMessage(player, "Der Tisch " + id + " exisiert bereits§8.");
                        return false;
                    }

                    rouletteHandler.createNewTable(id, player.getLocation());
                    messageUtil.sendMessage(player, "Roulette Tisch " + id + " erstellt§8.");

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }

            if (args[0].equalsIgnoreCase("delete")) {
                try {
                    final int id = Integer.parseInt(args[1]);

                    if(rouletteHandler.getTable(id) == null) {
                        messageUtil.sendMessage(player, "Der Tisch " + id + " exisiert nicht§8.");
                        return false;
                    }

                    rouletteHandler.deleteTable(id);
                    messageUtil.sendMessage(player, "Roulette Tisch " + id + " gelöscht§8.");

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }

            if (args[0].equalsIgnoreCase("setyaw")) {
                try {
                    final float yaw = Float.parseFloat(args[1]);

                    rouletteHandler.getTable(1).setYaw(yaw);

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/roulette tables",
                "/roulette delete <id>",
                "/roulette create <id>",
                "/roulette respawn",
                "/roulette setyaw <yaw>");

        return false;
    }

    @EventHandler
    public void on(final SignChangeEvent event) {
        if(!PermissionUtil.hasPermission(event.getPlayer(), "admin", false))
            return;

        if(event.getLine(0).startsWith("roulette-")) {

            final int id = Integer.parseInt(event.getLine(0).split("-")[1]);

            event.setLine(0, "§8▰§7▱ §fRoulette §7▱§8▰");
            event.setLine(1, "Tisch§8:§f " + id);
            event.setLine(2, "Klicke hier um");
            event.setLine(3,"beizutreten§8.");
        }
    }


    @EventHandler
    public void on(final PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(!event.getClickedBlock().getType().name().contains("SIGN"))
            return;

        final Sign sign = (Sign) event.getClickedBlock().getState();

        if(sign.getLine(0).equalsIgnoreCase("§8▰§7▱ §fRoulette §7▱§8▰")) {
            try {
                final int id = Integer.parseInt(sign.getLine(1).split(" ")[1]);

                rouletteHandler.openTable(id, event.getPlayer());

            } catch (final NumberFormatException ignored) {}
        }
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if(!InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§f§lTisch§7 "))
            return;

        event.setCancelled(true);

        if(!InventoryUtil.startsWithInventoryTitle(event.getClickedInventory(), "§f§lTisch§7 "))
            return;

        final int table = Integer.parseInt(event.getInventory().getName().split(" ")[1]);
        final Player player = (Player) event.getWhoClicked();


        if(event.getSlot() == 29 || event.getSlot() == 31 || event.getSlot() == 33) {
            // rot
            if(event.getSlot() == 29)
                rouletteHandler.joiningRoulette.put(player, "red");

            //black
            if(event.getSlot() == 31)
                rouletteHandler.joiningRoulette.put(player, "black");

            //green
            if(event.getSlot() == 33)
                rouletteHandler.joiningRoulette.put(player, "green");

            rouletteHandler.joinedTable.put(player, table);

            player.closeInventory();

            messageUtil.sendMessage(player, "Schreibe den §a§ogewünschten Betrag§7 in den Chat§8.");
            messageUtil.sendMessage(player, "Schreibe §c§ocancel§7 um den Vorgang abzubrechen§8.");
        }

    }


}
