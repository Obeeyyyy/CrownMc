package de.obey.crownmc.commands;
/*

    Author - EntixOG -> SkySlayer-v4
       04.11.2022 / 15:50

*/

import de.obey.crownmc.handler.TradeHandler;
import de.obey.crownmc.util.MessageBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@NonNull
public final class TradeCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final TradeHandler tradeHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0 || args.length > 2) {
            player.sendMessage("§cAnwendung: §b/trade <spieler>");
            return false;
        }

        if (args.length == 1) {

            if (!messageUtil.isOnline(player, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            if (target == player) {
                player.sendMessage("§cDu kannst dir selbst keine Handelsanfrage schicken.");
                return false;
            }


            final List<UUID> requestedPlayers = tradeHandler.getRequests().getOrDefault(player, new ArrayList<>());
            if (requestedPlayers.contains(target.getUniqueId())) {
                player.sendMessage("§cDu hast schon eine Anfrage an §a" + target.getName() + " §7gesendet.");
                return false;
            }

            requestedPlayers.add(target.getUniqueId());
            tradeHandler.getRequests().put(player, requestedPlayers);

            player.sendMessage("§7Du hast §a" + target.getName() + " §7eine Anfrage gesendet.");

            target.sendMessage("§7Du hast eine Anfrage von §a" + player.getName() + " §7erhalten.");
            new MessageBuilder("§8►§a► §a§oKlicke, um die Handelsanfrage anzunehmen.").addClickable(ClickEvent.Action.RUN_COMMAND, "/trade accept " + player.getName()).send(target);
            new MessageBuilder("§8►§4► §c§oKlicke, um die Handelsanfrage abzulehnen.").addClickable(ClickEvent.Action.RUN_COMMAND, "/trade deny " + player.getName()).send(target);
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("akzeptieren")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§cDer Spieler §a" + args[1] + " §cist nicht online.");
                    return false;
                }

                if (target == player) {
                    player.sendMessage("§cDu kannst mit dir selbst nicht Handeln.");
                    return false;
                }

                if (tradeHandler.getPlayerTrade(player) != null) {
                    player.sendMessage("§cDu bist aktuell schon am Handeln, versuche es später erneut.");
                    return false;
                }

                final List<UUID> requestedPlayers = tradeHandler.getRequests().getOrDefault(target, null);
                if (requestedPlayers == null || !requestedPlayers.contains(player.getUniqueId())) {
                    player.sendMessage("§a" + target.getName() + " §7hat dir keine Handelsanfrage geschickt.");
                    return false;
                }

                tradeHandler.getRequests().keySet().forEach(allRequester ->
                        tradeHandler.getRequests().get(allRequester).remove(player.getUniqueId()));

                tradeHandler.getRequests().remove(target);
                tradeHandler.openTrade(player, target);

                player.sendMessage("§7Du bist nun im Handel mit §a" + target.getName());
                target.sendMessage("§7Du bist nun im Handel mit §a" + player.getName());
            } else if (args[0].equalsIgnoreCase("deny") || args[0].equalsIgnoreCase("ablehnen")) {

                if (!messageUtil.isOnline(sender, args[1]))
                    return false;

                final Player target = Bukkit.getPlayer(args[1]);

                if (target == player) {
                    player.sendMessage("§cDu kannst mit dir selbst nicht Handeln.");
                    return false;
                }

                final List<UUID> requestedPlayers = tradeHandler.getRequests().getOrDefault(target, null);
                if (requestedPlayers == null || !requestedPlayers.contains(player.getUniqueId())) {
                    player.sendMessage("§a" + target.getName() + " §7hat dir keine Handelsanfrage geschickt.");
                    return false;
                }

                requestedPlayers.remove(player.getUniqueId());
                player.sendMessage("§7Du hast die Handelsanfrage von §a" + target.getName() + " §7abgelehnt.");
                target.sendMessage("§a" + player.getName() + " §chat die Handelsanfrage abgelehnt.");
            } else {
                player.sendMessage("§cAnwendung: §b/trade <spieler>");
                return false;
            }
        }
        return false;
    }
}
