package de.obey.crownmc.commands;

import de.obey.crownmc.handler.ClanHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@RequiredArgsConstructor @NonNull
public class ClanCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final ClanHandler clanHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 0) {
            clanHandler.openClanCommandInventory(player);
            return false;
        }

        messageUtil.sendSyntax(sender,
                "/clan");

        return false;
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if(InventoryUtil.isInventoryTitle(event.getInventory(), "§c§oKein Clan")) {
            event.setCancelled(true);

            if(event.getSlot() == 13) {
                // create clan
            }

            return;
        }


    }
}
