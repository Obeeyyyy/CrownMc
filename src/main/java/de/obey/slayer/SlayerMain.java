package de.obey.slayer;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 20:41

*/

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class SlayerMain extends JavaPlugin implements Listener {

    private Initializer initializer;

    @Override
    public void onEnable() {
        initializer = new Initializer(this);

        initializer.initializeSystem();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        if (!initializer.isRestarting())
            initializer.disableSystem();
    }

    @EventHandler
    public void on(final PlayerLoginEvent event) {
        if (initializer.isRestarting())
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§c§oDer Server startet jeden Moment ...");
    }

    public static SlayerMain getInstance() {
        return getPlugin(SlayerMain.class);
    }
}
