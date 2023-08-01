package de.obey.crownmc.commands;
/*

    Author - EntixOG -> SkySlayer-v4
       04.11.2022 / 15:50

*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.TradeHandler;
import de.obey.crownmc.handler.UserHandler;
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
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@NonNull
public final class TradeCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final TradeHandler tradeHandler;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 1) {

            if (!messageUtil.isOnline(player, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            if (target == player) {
                messageUtil.sendMessage(sender, "Du kannst nicht mit dir selber Traden§8.");
                return false;
            }

           final AtomicBoolean state = new AtomicBoolean(true);
            userHandler.getUser(target.getUniqueId()).thenAccept(user -> {
                if(!user.is(DataType.TRADEREQUESTS))
                    state.set(false);
            });

            if(!state.get()) {
                messageUtil.sendMessage(sender, target.getName() + " akzeptiert keine Trade Anfragen§8.");
                return false;
            }

            final List<UUID> requestedPlayers = tradeHandler.getRequests().getOrDefault(player, new ArrayList<>());
            if (requestedPlayers.contains(target.getUniqueId())) {
                messageUtil.sendMessage(player, "Du hast schon eine Tradeanfrage an §e§o" + target.getName() + "§7 gesendet§8.");
                return false;
            }

            requestedPlayers.add(target.getUniqueId());
            tradeHandler.getRequests().put(player, requestedPlayers);

            messageUtil.sendMessage(player, "Du hast §e§o" + target.getName() + "§7 eine Tradeanfrage gesendet§8.");
            messageUtil.sendMessage(target, "§e§o" + player.getName() + "§7 hat dir eine Tradeanfrage gesendet§8.");

            new MessageBuilder().addClickableCommand("§8» §7Klicke hier, um die Tradeanfrage §a§oanzunehmen§8.", "§a§oAkzeptieren", "/trade accept " + player.getName()).send(target);
            target.sendMessage("");
            new MessageBuilder().addClickableCommand("§8» §7Klicke hier, um die Tradeanfrage §c§oabzulehnen§8.", "§c§oAblehnen", "/trade deny " + player.getName()).send(target);

            return false;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("akzeptieren")) {
                if(!messageUtil.isOnline(sender, args[1]))
                    return false;

                Player target = Bukkit.getPlayer(args[1]);

                if (target == player) {
                    messageUtil.sendMessage(sender, "Du kannst nicht mit dir selber Traden§8.");
                    return false;
                }

                if (tradeHandler.getPlayerTrade(player) != null) {
                    messageUtil.sendMessage(player, "Du bist bereits am Traden§8.");
                    return false;
                }

                final List<UUID> requestedPlayers = tradeHandler.getRequests().getOrDefault(target, null);
                if (requestedPlayers == null || !requestedPlayers.contains(player.getUniqueId())) {
                    messageUtil.sendMessage(player, "Du hast keine Tradeanfrage von " + target.getName() + "§8.");
                    return false;
                }

                tradeHandler.getRequests().keySet().forEach(allRequester ->
                        tradeHandler.getRequests().get(allRequester).remove(player.getUniqueId()));

                tradeHandler.getRequests().remove(target);
                tradeHandler.openTrade(player, target);

                messageUtil.sendMessage(player, "Du Tradest jetzt mit " + target.getName());
                messageUtil.sendMessage(target, "Du Tradest jetzt mit " + player.getName());

                return false;
            }

            if (args[0].equalsIgnoreCase("deny") || args[0].equalsIgnoreCase("ablehnen")) {

                if (!messageUtil.isOnline(sender, args[1]))
                    return false;

                final Player target = Bukkit.getPlayer(args[1]);

                if (target == player) {
                    messageUtil.sendMessage(sender, "Du kannst nicht mit dir selber Traden§8.");
                    return false;
                }

                final List<UUID> requestedPlayers = tradeHandler.getRequests().getOrDefault(target, null);
                if (requestedPlayers == null || !requestedPlayers.contains(player.getUniqueId())) {
                    messageUtil.sendMessage(player, "Du hast keine Anfrage von " + target.getName() + "§8.");
                    return false;
                }

                requestedPlayers.remove(player.getUniqueId());

                messageUtil.sendMessage(player, "Du hast die Anfrage von §e§o" + target.getName() + "§c abgelehnt§8.");
                messageUtil.sendMessage(target , "§e§o" + player.getName() + "§7 hat deine Anfrage §cabgelehnt§8.");

                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/trade <spieler>",
                "/trade accept <spieler>",
                "/trade deny <spieler>"
        );

        return false;
    }
}
