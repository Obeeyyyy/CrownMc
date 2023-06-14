package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       14.10.2022 / 15:13

*/

import de.obey.crownmc.handler.ScoreboardHandler;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@RequiredArgsConstructor
public final class VanishCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;
    @NonNull
    private final ScoreboardHandler scoreboardHandler;

    public static final ArrayList<Player> vanished = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "team", true))
            return false;

        if (vanished.contains(player)) {
            vanished.remove(player);

            for (Player all : Bukkit.getOnlinePlayers()) {
                all.showPlayer(player);
            }

            messageUtil.sendMessage(sender, "Du bist jetzt für alle Spieler §c§osichtbar§7.");

        } else {
            vanished.add(player);

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (!PermissionUtil.hasPermission(all, "team", false))
                    all.hidePlayer(player);
            }

            messageUtil.sendMessage(sender, "Du bist jetzt für alle Spieler §a§ounsichtbar§7.");
        }

        scoreboardHandler.updateEverythingForEveryone();
        scoreboardHandler.setTablistName(player);

        // UPDATE PLAYER LIST NAME

        return false;
    }
}
