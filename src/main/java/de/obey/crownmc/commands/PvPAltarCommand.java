package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       11.07.2023 / 04:23

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.PvPAltarHandler;
import de.obey.crownmc.objects.pvp.PvPAltar;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@RequiredArgsConstructor @NonNull
public final class PvPAltarCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final PvPAltarHandler pvPAltarHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        final Player player = (Player) sender;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {

                if(pvPAltarHandler.getPvpAltarMap().isEmpty()) {
                    messageUtil.sendMessage(player, "Es existieren noch keine PvPAltare§8.");
                    return false;
                }

                messageUtil.sendMessage(player, "Es existieren " + pvPAltarHandler.getPvpAltarMap().size() + " PvPAltare§8:");

                for (final int id : pvPAltarHandler.getPvpAltarMap().keySet()) {
                    final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);
                    player.sendMessage("");
                    player.sendMessage("§8 - §7ID§8: §f" + id + "§8 ( " + altar.getPrefix() + " §8)");
                    player.sendMessage("§7   -§8> §7MoneyReward§8: §e" + messageUtil.formatLong(altar.getMoneyReward()));
                    player.sendMessage("§7   -§8> §7EloReward§8: §d" + messageUtil.formatLong(altar.getEloReward()));
                    player.sendMessage("§7   -§8> §7XpReward§8: §a" + messageUtil.formatLong(altar.getXpReward()));
                    player.sendMessage("§7   -§8> §7Items§8: §a" + altar.getItemRewards().size());
                    player.sendMessage("§7   -§8> §7MoneyPunish§8: §a" + messageUtil.formatLong(altar.getMoneyPunish()));
                    player.sendMessage("§7   -§8> §7EloPunish§8: §a" + messageUtil.formatLong(altar.getEloPunish()));
                    player.sendMessage("");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("reload")) {
                pvPAltarHandler.shutdown();
                pvPAltarHandler.loadAllPvPAltars();
                messageUtil.sendMessage(player, "Altare neu geladen§8.");
                return false;
            }
        }

        if(args.length == 2){
            if(args[0].equalsIgnoreCase("unblock")) {
                if(!messageUtil.isOnline(sender, args[1]))
                    return false;

                pvPAltarHandler.unBlock(Bukkit.getPlayer(args[1]));
                messageUtil.sendMessage(sender, "Done.");

                return false;
            }

            if(args[0].equalsIgnoreCase("create")) {
                try {
                    final int id = Integer.parseInt(args[1]);

                    if(pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert bereits ein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    pvPAltarHandler.createPvPAltar(id, player.getLocation().clone().add(0, -1.25, 0));
                    messageUtil.sendMessage(player, "Altar §8(§f" + id + "§8)§7 erstellt§8.");

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("delete")) {
                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert kein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    pvPAltarHandler.deletePvPAltar(id);
                    messageUtil.sendMessage(player, "Altar §8(§f" + id + "§8)§7 gelöscht§8.");

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("respawn")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert kein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);

                    altar.shutdown();
                    altar.spawnAltar();

                    messageUtil.sendMessage(player, "Altar §8(§f" + id + "§8)§7 respawnt§8.");

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("setitems")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert kein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);
                    final Inventory inv = Bukkit.createInventory(null, 9*6, "Altar " + id);

                    if(!altar.getItemRewards().isEmpty()) {
                        int slot = 0;
                        for (ItemStack itemReward : altar.getItemRewards()) {
                            inv.setItem(slot, itemReward);
                            slot++;
                        }
                    }

                    player.openInventory(inv);

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("setloc")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert kein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);

                    altar.shutdown();
                    altar.setLocation(player.getLocation().clone().add(0, -1.25, 0));
                    altar.spawnAltar();

                    messageUtil.sendMessage(player, "Altar §8(§f" + id + "§8)§7 respawnt§8.");

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }

                return false;
            }
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("settime")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert kein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);
                    final long millis = MathUtil.getMillisFromString(args[2]);

                    altar.setTimeToCapture(millis);
                    altar.shutdown();
                    altar.spawnAltar();

                    messageUtil.sendMessage(player, "Altar TimeToCapture -> " + MathUtil.getMinutesAndSecondsFromSeconds(millis/1000));

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }


                return false;
            }

            if(args[0].equalsIgnoreCase("setcd")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert kein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);
                    final long millis = MathUtil.getMillisFromString(args[2]);

                    altar.setCooldownMillis(millis);
                    altar.shutdown();
                    altar.spawnAltar();

                    messageUtil.sendMessage(player, "Altar Cooldown -> " + MathUtil.getMinutesAndSecondsFromSeconds(millis/1000));

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }


                return false;
            }

            if(args[0].equalsIgnoreCase("setMoney")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert kein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);
                    final long amount = Long.parseLong(args[2]);

                    altar.setMoneyReward(amount);

                    messageUtil.sendMessage(player, "Altar Money -> " + messageUtil.formatLong(amount));

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }


                return false;
            }


            if(args[0].equalsIgnoreCase("setXp")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert kein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);
                    final long amount = Long.parseLong(args[2]);

                    altar.setXpReward(amount);

                    messageUtil.sendMessage(player, "Altar XP -> " + messageUtil.formatLong(amount));

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }


                return false;
            }

            if(args[0].equalsIgnoreCase("setElo")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert kein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);
                    final long amount = Long.parseLong(args[2]);

                    altar.setEloReward(amount);

                    messageUtil.sendMessage(player, "Altar Elo -> " + messageUtil.formatLong(amount));

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("setElo-")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert kein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);
                    final long amount = Long.parseLong(args[2]);

                    altar.setEloPunish(amount);

                    messageUtil.sendMessage(player, "Altar Elo- -> " + messageUtil.formatLong(amount));

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("setMoney-")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert kein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);
                    final long amount = Long.parseLong(args[2]);

                    altar.setMoneyPunish(amount);

                    messageUtil.sendMessage(player, "Altar Money- -> " + messageUtil.formatLong(amount));

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }

                return false;
            }
        }

        if(args.length >= 3) {
            if(args[0].equalsIgnoreCase("setprefix")) {

                try {
                    final int id = Integer.parseInt(args[1]);

                    if(!pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert kein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);

                    String prefix = args[2];

                    if(args.length > 3) {
                        for (int i = 3; i < args.length; i++)
                            prefix = prefix + " " + args[i];
                    }

                    altar.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
                    altar.shutdown();
                    altar.spawnAltar();

                    messageUtil.sendMessage(player, "Altar Prefix -> " + prefix);

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gib eine Zahl an§8.");
                }

                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/pvpaltar list",
                "/pvpaltar reload",
                "/pvpaltar unblock <name>",
                "/pvpaltar respawn <id>",
                "/pvpaltar settime <id> <10m>",
                "/pvpaltar setcd <id> <10m>",
                "/pvpaltar setloc <id>",
                "/pvpaltar setprefix <id> <string>",
                "/pvpaltar setMoney <id> <long>",
                "/pvpaltar setXP <id> <long>",
                "/pvpaltar setElo <id> <long>",
                "/pvpaltar setMoney- <id> <long>",
                "/pvpaltar setElo- <id> <long>",
                "/pvpaltar setItems <id>",
                "/pvpaltar create <id>",
                "/pvpaltar delete <id>"
                );

        return false;
    }

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if(!InventoryUtil.startsWithInventoryTitle(event.getInventory(), "Altar"))
            return;

        final int id = Integer.parseInt(event.getInventory().getTitle().split(" ")[1]);
        final PvPAltar altar = pvPAltarHandler.getPvpAltarMap().get(id);

        final ArrayList<ItemStack> temp = new ArrayList<>();

        for (final ItemStack item : event.getInventory().getContents()) {
            if(item != null && item.getType() != Material.AIR) {
                temp.add(item);
            }
        }

        altar.setItemRewards(temp);
    }

    @EventHandler
    public void on(final SignChangeEvent event) {
        if(!PermissionUtil.hasPermission(event.getPlayer(), "admin", false))
            return;

        if(event.getLine(0).startsWith("altar-")) {

            final int id = Integer.parseInt(event.getLine(0).split("-")[1]);

            event.setLine(0, "§8▰§7▱ §f§lAltar §7▱§8▰");
            event.setLine(1, "ID§8:§f " + id);
            event.setLine(2, "Klicke hier um");
            event.setLine(3,"zu starten§8.");
        }
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(!event.getClickedBlock().getType().name().contains("SIGN"))
            return;

        final Sign sign = (Sign) event.getClickedBlock().getState();

        if(sign.getLine(0).equalsIgnoreCase("§8▰§7▱ §f§lAltar §7▱§8▰")) {

            if(VanishCommand.vanished.contains(event.getPlayer())) {
                messageUtil.sendMessage(event.getPlayer(), "§c§oDu bist noch im vanish§8.");
                return;
            }

            try {
                final int id = Integer.parseInt(sign.getLine(1).split(" ")[1]);

                pvPAltarHandler.startPvPAltar(id, event.getPlayer());

            } catch (final NumberFormatException ignored) {}
        }
    }

}
