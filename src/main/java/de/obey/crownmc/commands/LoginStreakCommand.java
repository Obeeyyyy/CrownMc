package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       28.10.2022 / 20:49

*/

import de.obey.crownmc.handler.LoginRewardHandler;
import de.obey.crownmc.util.InventoryUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@RequiredArgsConstructor
@NonNull
public final class LoginStreakCommand implements CommandExecutor, Listener {

    private final LoginRewardHandler loginRewardHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0) {
            loginRewardHandler.openInventory(player);
            return false;
        }

        return false;
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {

        if (!InventoryUtil.isInventoryTitle(event.getInventory(), "§d§lLoginstreak"))
            return;

        event.setCancelled(true);
    }
}
