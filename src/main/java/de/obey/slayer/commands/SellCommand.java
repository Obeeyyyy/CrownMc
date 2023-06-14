package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       07.01.2023 / 20:42

*/

import de.obey.slayer.handler.ShopHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @NonNull
public final class SellCommand implements CommandExecutor {

    private final ShopHandler shopHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        shopHandler.getCategories().get("sell").openInventory((Player) sender);

        return false;
    }
}
