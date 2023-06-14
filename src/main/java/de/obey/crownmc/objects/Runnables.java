package de.obey.crownmc.objects;
/*

    Author - Obey -> SkySlayer-v4
       29.10.2022 / 23:36

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.commands.AfkCommand;
import de.obey.crownmc.handler.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
@NonNull
public final class Runnables {

    private final LoginRewardHandler loginRewardHandler;
    private final KitHandler kitHandler;
    private final UserHandler userHandler;
    private final ScoreboardHandler scoreboardHandler;
    private final AutoBroadcastHandler autoBroadcastHandler;
    private final DailyPotHandler dailyPotHandler;

    public void start2TickTimerAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                scoreboardHandler.runUpdateTick();
                autoBroadcastHandler.checkBroadcast();

                AfkCommand.checkAllIfAfk();
            }
        }.runTaskTimerAsynchronously(CrownMain.getInstance(), 2, 2);
    }

    public void start10TickTimerAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                kitHandler.runUpdateTick();
                loginRewardHandler.runUpdateTick();
                userHandler.runInterval();
                dailyPotHandler.updateStands();
            }
        }.runTaskTimerAsynchronously(CrownMain.getInstance(), 10, 10);
    }

}
