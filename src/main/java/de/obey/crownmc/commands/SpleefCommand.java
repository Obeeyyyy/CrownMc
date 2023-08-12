package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       12.08.2023 / 12:07

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.SpleefHandler;
import de.obey.crownmc.objects.events.SpleefRound;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor @NonNull
public final class SpleefCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final SpleefHandler spleefHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("start")) {
                if(!PermissionUtil.hasPermission(sender, "admin", true))
                    return false;

                if(!spleefHandler.start(player.getLocation())) {
                    messageUtil.sendMessage(player, "Es läuft bereits eine Spleef Runde§8.");
                    return false;
                }

                return false;
            }

            if(spleefHandler.getSpleefRound() == null) {
                messageUtil.sendMessage(player, "Es läuft aktuell keine Spleef Runde§8.");
                return false;
            }

            if (args[0].equalsIgnoreCase("join")) {
                spleefHandler.getSpleefRound().playerJoin(player);
                return false;
            }
        }

        return false;
    }

    @EventHandler
    public void on(final PlayerMoveEvent event) {
        final SpleefRound round = spleefHandler.getSpleefRound();

        if(round == null)
            return;

        if(!round.getTeilnehmer().contains(event.getPlayer()))
            return;

        if(round.getState() != 1)
            return;

        final Player player = event.getPlayer();

        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFoodLevel(20);
        player.setHealth(20);

        round.getLastMoved().put(player, System.currentTimeMillis());

        final Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

        if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {

            Block temp = block.getRelative(BlockFace.NORTH);

            if(temp.getType() == Material.SNOW_BLOCK) {
                if(!round.getBlocks().containsKey(temp))
                    round.getBlocks().put(temp, System.currentTimeMillis() + 100);
            }

            temp = block.getRelative(BlockFace.EAST);

            if(temp.getType() == Material.SNOW_BLOCK) {
                if(!round.getBlocks().containsKey(temp))
                    round.getBlocks().put(temp, System.currentTimeMillis() + 100);
            }

            temp = block.getRelative(BlockFace.WEST);

            if(temp.getType() == Material.SNOW_BLOCK) {
                if(!round.getBlocks().containsKey(temp))
                    round.getBlocks().put(temp, System.currentTimeMillis() + 100);
            }

            temp = block.getRelative(BlockFace.SOUTH);

            if(temp.getType() == Material.SNOW_BLOCK) {
                if(!round.getBlocks().containsKey(temp))
                    round.getBlocks().put(temp, System.currentTimeMillis() + 100);
            }

            return;
        }

        if(player.getLocation().getBlock().getType() == Material.WATER ||
            player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
            round.quit(player);
            return;
        }


        if(block.getType() != Material.SNOW_BLOCK)
            return;

        if(round.getBlocks().containsKey(block))
            return;

        round.getBlocks().put(block, System.currentTimeMillis() + 100);
    }
}
