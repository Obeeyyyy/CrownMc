package de.obey.crownmc.commands;

import de.obey.crownmc.objects.vote.VoteKick;
import de.obey.crownmc.util.MessageBuilder;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public class VoteKickCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;

    private final HashMap<UUID, VoteKick> voteKicks = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player =(Player) sender;

        if(args.length == 2) {

            if (!messageUtil.hasPlayedBefore(sender, args[1]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (!voteKicks.containsKey(target.getUniqueId())) {
                messageUtil.sendMessage(player, "Es läuft kein VoteKick gegen " + target.getName() + "§8.");
                return false;
            }

            final boolean state = (args[0].equalsIgnoreCase("ja"));

            final VoteKick voteKick = voteKicks.get(target.getUniqueId());

            if (voteKick.vote(player, state)) {
                messageUtil.sendMessage(sender, "Du hast für §f" + args[0] + " §7gestimmt§8.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);
                return false;
            }

            messageUtil.sendMessage(sender, "§c§o Du hast deine Stimme bereits abgegeben§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.1f, 1);

            return false;
        }

        if(!PermissionUtil.hasPermission(sender, "votekick", true))
            return false;

        if(args.length == 1) {
            if (!messageUtil.isOnline(sender, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            if (voteKicks.containsKey(target.getUniqueId())) {
                messageUtil.sendMessage(sender, "Es läuft bereits ein VoteKick gegen " + target.getName() + "§8.");
                return false;
            }

            voteKicks.put(target.getUniqueId(), new VoteKick(target.getUniqueId(), sender.getName()));

            messageUtil.broadcast(sender.getName() + " möchte §e" + target.getName() + "§7 vom Server kicken§8.");
            messageUtil.broadcast("Die Abstimmung endet in 20 Sekunden§8.");

            for (final Player all : Bukkit.getOnlinePlayers()) {
                new MessageBuilder().addClickableCommand("§8» §7Kicke hier um §adafür§7 zu stimmen§8.", "§a§oDafür stimmen", "/votekick ja " + target.getName()).send(all);
                new MessageBuilder().addClickableCommand("§8» §7Kicke hier um §cdagegen§7 zu stimmen§8.", "§c§oDagegen stimmen", "/votekick nein " + target.getName()).send(all);
            }

            return false;
        }

        messageUtil.sendSyntax(sender, "/votekick <spieler>");

        return false;
    }

    @EventHandler
    public void on(final PlayerQuitEvent event) {
        if(voteKicks.containsKey(event.getPlayer().getUniqueId()))
            voteKicks.remove(event.getPlayer().getUniqueId());
    }
}
