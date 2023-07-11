package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       11.07.2023 / 04:23

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.PvPAltarHandler;
import de.obey.crownmc.objects.pvp.PvPAltar;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @NonNull
public final class PvPAltarCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final PvPAltarHandler pvPAltarHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
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
                }

                return false;
            }
        }

        if(args.length == 2){
            if(args[0].equalsIgnoreCase("create")) {
                try {
                    final int id = Integer.parseInt(args[1]);

                    if(pvPAltarHandler.getPvpAltarMap().containsKey(id)) {
                        messageUtil.sendMessage(player, "Es exisiert bereits ein Altar mit der ID " + id + "§8.");
                        return false;
                    }

                    pvPAltarHandler.createPvPAltar(id, player.getLocation());
                    messageUtil.sendMessage(player, "Altar §8(§f" + id + "§8)§7 erstellt§8.");

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
        }

        messageUtil.sendSyntax(sender,
                "/pvpaltar list",
                "/pvpaltar respawn <id>",
                "/pvpaltar create <id>",
                "/pvpaltar delete <id>"
                );

        return false;
    }
}
