package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       12.11.2022 / 23:01

*/

import de.obey.crownmc.Initializer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@RequiredArgsConstructor
@NonNull
public final class AsyncPlayerPreLoginListener implements Listener {

    private final Initializer initializer;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(final AsyncPlayerPreLoginEvent event) {

        if (initializer.isRestarting())
            return;

        // If Player is not registered return
        if (!initializer.getUserHandler().isRegistered(Bukkit.getOfflinePlayer(event.getUniqueId())))
            return;

        // Load User Data if not Loaded
        initializer.getUserHandler().getUser(event.getUniqueId()).thenAcceptAsync(user -> {
            event.allow();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.ALLOWED);
        });
    }
}
