package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       05.07.2023 / 18:57

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.MessageBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public final class FriedenCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    @NonNull
    private final UserHandler userHandler;

    private final HashMap<Player, ArrayList<Player>> peaceRequests = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                    final int friedeListSize = user.getUserPeace().getPeaceList().size();
                    if (friedeListSize == 0) {
                        messageUtil.sendMessage(player, "Du hast mit niemandem Frieden§8.");
                        return;
                    }

                    messageUtil.sendMessage(player, "Du hast Frieden mit §8(§f" + friedeListSize + "§8)§7 Spielern§8 :");
                    for (final String uuid : user.getUserPeace().getPeaceList()) {
                        player.sendMessage("§8  - §7" + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                    }
                });

                return false;
            }

            if (!messageUtil.hasPlayedBefore(sender, args[0]))
                return false;

            if (!messageUtil.isOnline(sender, args[0]))
                return false;

            if(args[0].equalsIgnoreCase(player.getName())) {
                messageUtil.sendMessage(player, "Du liebst dich selber§8.");
                return false;
            }

            final Player target = Bukkit.getPlayer(args[0]);

            final AtomicBoolean state = new AtomicBoolean(true);
            userHandler.getUser(target.getUniqueId()).thenAccept(user -> {
                if(!user.is(DataType.PEACEREQUESTS))
                    state.set(false);
            });

            if(!state.get()) {
                messageUtil.sendMessage(sender, target.getName() + " akzeptiert keine Friedensangebote§8.");
                return false;
            }

            userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                if (user.getUserPeace().hasPeaceWith(target)) {
                    messageUtil.sendMessage(player, "Du hast bereits Frieden mit §f§o" + target.getName() + "§8.");
                    new MessageBuilder("§8» §7Klicke hier, um den Frieden mit §f§o" + target.getName() + "§7 zu §c§obeenden§8.").addClickable(ClickEvent.Action.RUN_COMMAND, "/friede remove " + player.getName()).send(player);
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return;
                }

                if (peaceRequests.containsKey(player) && peaceRequests.get(player).contains(target)) {
                    messageUtil.sendMessage(player, "Du hast §f§o" + target.getName() + " §7schon eine Friedensanfrage gesendet§8.");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return;
                }

                final ArrayList<Player> requests = peaceRequests.get(player) == null ? new ArrayList<>() : peaceRequests.get(player);

                requests.add(target);
                peaceRequests.put(player, requests);

                messageUtil.sendMessage(player, "Du hast §f§o" + target.getName() + "§7 ein Friedensangebot gesendet§8.");
                messageUtil.sendMessage(target, "§f§o" + player.getName() + "§7 hat dir ein Friedensangebot gesendet§8.");

                target.sendMessage("");
                new MessageBuilder("§8» §7Klicke hier, um das Friedensangebot §a§oanzunehmen§8.").addClickable(ClickEvent.Action.RUN_COMMAND, "/friede accept " + player.getName()).send(target);
                target.sendMessage("");
                new MessageBuilder("§8» §7Klicke hier, um das Friedensangebot §c§oabzulehnen§8.").addClickable(ClickEvent.Action.RUN_COMMAND, "/friede deny " + player.getName()).send(target);

            });

            return false;
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("remove")) {

                if (!messageUtil.hasPlayedBefore(sender, args[1]))
                    return false;

                final OfflinePlayer target = Bukkit.getPlayer(args[1]);

                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                    if (!user.getUserPeace().hasPeaceWith(target)) {
                        messageUtil.sendMessage(player, "Du hast keinen Frieden mit §f§o" + target.getName() + "§8.");
                        player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                        return;
                    }

                    user.getUserPeace().getPeaceList().remove(target.getUniqueId().toString());
                    messageUtil.sendMessage(player, "Du hast den Frieden mit §f§o" + target.getName() + "§7 beendet§8.");

                    userHandler.getUser(target.getUniqueId()).thenAcceptAsync(tagetUser -> {
                       tagetUser.getUserPeace().getPeaceList().remove(player.getUniqueId().toString());

                       if(target.isOnline())
                           messageUtil.sendMessage(target.getPlayer(), "§f§o" + player.getName() + "§7 hat den Frieden mit dir beendet§8.");
                    });

                });

                return false;
            }

            if(args[0].equalsIgnoreCase("accept")) {

                if (!messageUtil.hasPlayedBefore(sender, args[1]))
                    return false;

                if (!messageUtil.isOnline(sender, args[1]))
                    return false;

                final Player target = Bukkit.getPlayer(args[1]);
                final ArrayList<Player> requests = peaceRequests.containsKey(target) ? peaceRequests.get(target) : new ArrayList<>();

                if (!requests.contains(player)) {
                    messageUtil.sendMessage(player, "Du hast kein Friedensangebot von §f§o" + target.getName() + "§8.");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return false;
                }

                requests.remove(player);
                peaceRequests.put(target, requests);

                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                    user.getUserPeace().makePeaceWith(target);
                    messageUtil.sendMessage(player, "Du hast Frieden mit §f§o" + target.getName() + "§7 geschlossen§8.");
                    player.playSound(player.getLocation(), Sound.VILLAGER_YES, 0.5f, 1);
                });

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(targetUser -> {
                    targetUser.getUserPeace().makePeaceWith(player);
                    messageUtil.sendMessage(target, "§f§o" + player.getName() + "§7 hat dein Friedensangebot §a§oangenommen§8.");
                    target.playSound(target.getLocation(), Sound.VILLAGER_YES, 0.5f, 1);
                });

                return false;
            }

            if(args[0].equalsIgnoreCase("deny")) {

                if (!messageUtil.hasPlayedBefore(sender, args[1]))
                    return false;

                if (!messageUtil.isOnline(sender, args[1]))
                    return false;

                final Player target = Bukkit.getPlayer(args[1]);
                final ArrayList<Player> requests = peaceRequests.containsKey(target) ? peaceRequests.get(target) : new ArrayList<>();

                if(!requests.contains(player)) {
                    messageUtil.sendMessage(player, "Du hast kein Friedensangebot von §f§o" + target.getName() + "§8.");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return false;
                }

                requests.remove(player);
                peaceRequests.put(target, requests);

                messageUtil.sendMessage(player, "Du hast das Friedensangebot von §f§o" + target.getName() + "§7 §c§oabgelehnt§8.");
                player.playSound(player.getLocation(), Sound.VILLAGER_HIT, 0.5f, 1);

                messageUtil.sendMessage(target, "§f§o" + target.getName() + "§7 hat dein Friedensangebot §c§oabgelehnt§8.");
                target.playSound(target.getLocation(), Sound.VILLAGER_HIT, 0.5f, 1);

                return false;
            }
        }

        messageUtil.sendSyntax(player,
                "/frieden <spieler>",
                "/frieden list",
                "/frieden remove <spieler>," +
                        "/frieden accept <spieler>",
                        "/frieden deny <spieler>");

        return false;
    }

}
