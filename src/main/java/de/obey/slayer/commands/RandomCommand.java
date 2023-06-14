package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       08.12.2022 / 14:36

*/

import de.obey.slayer.SlayerMain;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

@RequiredArgsConstructor
@NonNull
public final class RandomCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "random", true))
            return false;

        if (args.length == 1) {

            try {
                final int amount = Integer.parseInt(args[0]);

                if (amount < 1) {
                    messageUtil.sendMessage(sender, "Die Zahl ist zu klein§8.");
                    return false;
                }

                messageUtil.broadcast("Es " + (amount > 1 ? "werden" : "wird") + " §8x§e§o" + amount + "§7 Spieler ausgewählt§8. ( §e§o" + player.getName() + " §8)");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        final Random random = new Random();
                        final ArrayList<Player> allPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

                        for (int i = 1; i <= amount; i++) {
                            final Player randomPlayer = allPlayers.get(random.nextInt(allPlayers.size()));
                            messageUtil.broadcast("Nr§8.§e" + i + "§8 - §7" + randomPlayer.getName());

                            randomPlayer.playSound(randomPlayer.getLocation(), Sound.LEVEL_UP, 1, 1);
                        }

                        allPlayers.clear();

                    }
                }.runTaskLater(SlayerMain.getInstance(), 50);

            } catch (final NumberFormatException exception) {
                messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an§8.");
            }

            return false;
        }

        messageUtil.sendSyntax(sender, "/random <zahl>");

        return false;
    }
}
