package de.obey.crownmc.listener;
/*

    Author - Obey -> CrownMc
       05.07.2023 / 01:46

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor @NonNull
public final class StatResetInteract implements Listener {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @EventHandler
    public void on(final PlayerInteractEvent event) {
        if(!InventoryUtil.isItemInHandWithDisplayname(event.getPlayer(), "§c§lStat Reset"))
            return;

        final Player player = event.getPlayer();
        event.setCancelled(true);

        InventoryUtil.removeItemInHand(player, 1);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.9f, 1);

        userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
           user.setLong(DataType.KILLS, 0);
           user.setLong(DataType.DEATHS, 0);
           messageUtil.sendMessage(player, "Deine Stats wurden zurückgesetzt§8.");
        });

    }

}
