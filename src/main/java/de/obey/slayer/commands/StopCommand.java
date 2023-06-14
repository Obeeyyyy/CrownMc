package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 23:41

*/

import de.obey.slayer.Initializer;
import de.obey.slayer.SlayerMain;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public final class StopCommand implements CommandExecutor {

    @NonNull
    private final Initializer initializer;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            if (!PermissionUtil.hasPermission(sender, "*", true))
                return false;
        }

        // Remove all combats
        initializer.getCombatHandler().getPlayerCombat().clear();

        // Disable System
        initializer.disableSystem();

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().shutdown();
            }
        }.runTaskLater(SlayerMain.getInstance(), 20 * 4);

        return false;
    }

}
