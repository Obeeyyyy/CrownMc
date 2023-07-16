package de.obey.crownmc.listener;
/*

    Author - Obey -> CrownMc
       14.07.2023 / 21:20

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.PvPAltarHandler;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor @NonNull
public final class PvPAltarListener implements Listener {

    private final MessageUtil messageUtil;
    private final PvPAltarHandler pvPAltarHandler;

    @EventHandler
    public void on(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if(!pvPAltarHandler.getCapturing().containsKey(player.getUniqueId()))
            return;

        pvPAltarHandler.getCapturing().get(player.getUniqueId()).leave();
    }

}
