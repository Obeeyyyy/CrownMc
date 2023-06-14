package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       22.10.2022 / 02:10

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class SaveCommand implements CommandExecutor {

    private final Initializer initializer;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "*", true))
            return false;

        initializer.getChatFilterHandler().save();
        initializer.getRangHandler().save();
        initializer.getLoginRewardHandler().save();
        initializer.getKitHandler().save();
        initializer.getBadgeHandler().save();
        initializer.getShopHandler().save();
        initializer.getWorldProtectionHandler().save();
        initializer.getUserHandler().getUserCache().values().forEach(initializer.getUserHandler()::saveData);
        initializer.getServerConfig().save();
        initializer.getDailyPotHandler().save();

        initializer.getMessageUtil().sendMessage(sender, "Saved data.");

        return false;
    }
}
