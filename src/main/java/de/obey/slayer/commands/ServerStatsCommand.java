package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       17.10.2022 / 22:47

*/

import de.obey.slayer.backend.MySQL;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

@RequiredArgsConstructor
public final class ServerStatsCommand implements CommandExecutor {

    @NonNull
    private final MySQL mySQL;

    private final Runtime runtime = Runtime.getRuntime();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "*", true))
            return false;

        final NumberFormat numberFormat = NumberFormat.getInstance();

        final long maxMem = runtime.maxMemory() / 1024 / 1024;
        final long allocatedMem = runtime.totalMemory() / 1024 / 1024;
        final long freeMemory = runtime.freeMemory() / 1024 / 1024;

        sender.sendMessage("§8§l§m----------------------");
        sender.sendMessage("§8");
        sender.sendMessage("§8  * §7 RAM Information");
        sender.sendMessage("§8    §8 > §f Max§8: §e" + numberFormat.format(maxMem) + " MB");
        sender.sendMessage("§8    §8 > §f Allocated§8: §e" + numberFormat.format(allocatedMem) + " MB");
        sender.sendMessage("§8    §8 > §f Free§8: §e" + numberFormat.format(maxMem - allocatedMem) + " MB");
        sender.sendMessage("§8    §8 > §f Used§8: §e" + numberFormat.format(freeMemory) + " MB");
        sender.sendMessage("§8    §8 > §f MySQL Connections§8: §e" + mySQL.getHikariDataSource().getHikariPoolMXBean().getActiveConnections() + " (" + mySQL.getHikariDataSource().getHikariPoolMXBean().getIdleConnections() + ") /" + mySQL.getHikariDataSource().getHikariPoolMXBean().getTotalConnections());
        sender.sendMessage("§8");
        sender.sendMessage("§8§l§m----------------------");

        return false;
    }
}
