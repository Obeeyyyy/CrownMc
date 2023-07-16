package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       18.10.2022 / 01:17

*/

import de.obey.crownmc.backend.Backend;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@RequiredArgsConstructor
public final class AllMoneyCommand implements CommandExecutor {

    @NonNull
    private Backend backend;
    @NonNull
    private MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "allmoney", true))
            return false;

        final ResultSet resultSet = backend.getResultSet("SELECT money FROM users WHERE money > '0'");

        int overflowed = 0;
        long counted = 0;

        if (resultSet == null)
            return false;

        while (true) {
            try {
                if (!resultSet.next()) break;

                counted += resultSet.getLong("money");

                if (counted < 0) {
                    overflowed++;
                    counted = (counted - Long.MAX_VALUE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        final File folder = FileUtil.getFile("playerFiles");

        if(folder.exists()) {
            if(Objects.requireNonNull(folder.listFiles()).length > 0) {
                for (File file : Objects.requireNonNull(folder.listFiles())) {
                    final YamlConfiguration cfg = FileUtil.getCfg(file);

                    if(cfg.contains("balance"))
                    counted += cfg.getLong("balance");

                    if (counted < 0) {
                        overflowed++;
                        counted = (counted - Long.MAX_VALUE);
                    }
                }
            }
        }

        messageUtil.sendMessage(sender, "Overflow§8: §e§o" + overflowed + "§7 | §f§o1x =§e§o " + messageUtil.formatLong(Long.MAX_VALUE));
        messageUtil.sendMessage(sender, "Geld im umlauf§8: §e§o" + messageUtil.formatLong(counted) + " §8= §e§o" + MathUtil.replaceLongWithSuffix(counted));

        return false;
    }
}
