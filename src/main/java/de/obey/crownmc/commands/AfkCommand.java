package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       09.01.2023 / 12:47

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.ScoreboardHandler;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor @NonNull
public final class AfkCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final ScoreboardHandler scoreboardHandler;

    public static final ArrayList<Player> afkList = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 0) {

            if(afkList.contains(player)) {
                afkList.remove(player);
                scoreboardHandler.updateEverythingForEveryone();
                messageUtil.sendMessage(player, "Du bist jetzt nicht mehr AFK§8.");
                return false;
            }

            afkList.add(player);
            scoreboardHandler.updateEverythingForEveryone();
            messageUtil.sendMessage(player, "Du bist jetzt AFK§8.");

            return false;
        }

        messageUtil.sendSyntax(sender, "/afk");

        return false;
    }

    private static final HashMap<UUID, Long> lastaction = new HashMap<UUID, Long>();

    public static void checkAllIfAfk()  {
        if(Bukkit.getOnlinePlayers().isEmpty())
            return;

        for (Player all : Bukkit.getOnlinePlayers()) {
            if(!lastaction.containsKey(all.getUniqueId()))
                lastaction.put(all.getUniqueId(), System.currentTimeMillis());

            if(!afkList.contains(all)) {
                if (System.currentTimeMillis() - lastaction.get(all.getUniqueId()) >= 1000 * 60 * 5) {
                    afkList.add(all);
                    CrownMain.getInstance().getInitializer().getScoreboardHandler().updateEverythingForEveryone();
                }
            }
        }
    }

    @EventHandler
    public void on(final AsyncPlayerChatEvent event) {
        if(afkList.contains(event.getPlayer())) {
            afkList.remove(event.getPlayer());
            messageUtil.sendMessage(event.getPlayer(), "Du bist jetzt nicht mehr AFK§8.");
            scoreboardHandler.updateEverythingForEveryone();
        }

        lastaction.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void on(final PlayerCommandPreprocessEvent event) {
        if(afkList.contains(event.getPlayer())) {
            afkList.remove(event.getPlayer());
            messageUtil.sendMessage(event.getPlayer(), "Du bist jetzt nicht mehr AFK§8.");
            scoreboardHandler.updateEverythingForEveryone();
        }

        lastaction.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void on(final PlayerJoinEvent event) {
        afkList.remove(event.getPlayer());
        lastaction.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        if(afkList.contains(event.getPlayer())) {
            afkList.remove(event.getPlayer());
            messageUtil.sendMessage(event.getPlayer(), "Du bist jetzt nicht mehr AFK§8.");
            scoreboardHandler.updateEverythingForEveryone();
        }

        lastaction.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }
}
