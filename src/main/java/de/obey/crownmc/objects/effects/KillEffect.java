package de.obey.crownmc.objects.effects;
/*

    Author - Obey -> SkySlayer-v4
       12.11.2022 / 22:27

*/

import de.obey.crownmc.CrownMain;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class KillEffect {

    private BukkitTask runnable;

    public KillEffect() {
    }

    public void run(final Location location, final int waitBetweenTick, final int runForSeconds) {

        if (runnable == null) {
            runnable = new BukkitRunnable() {

                int overallticked = 0;
                int ticks = 0;
                double angle = 0;
                double yOffset = 0;

                @Override
                public void run() {
                    if (overallticked >= runForSeconds * 20) {
                        stop();
                        return;
                    }

                    overallticked++;

                    if (ticks < waitBetweenTick) {
                        ticks++;
                        return;
                    }

                    location.getWorld().playEffect(location.clone().add(Math.cos(angle) * 0.5, yOffset, Math.sin(angle) * 0.5), Effect.HAPPY_VILLAGER, 10);

                    yOffset += 0.13;
                    angle += 0.3;
                    ticks = 0;
                }
            }.runTaskTimer(CrownMain.getInstance(), 0, 1);
        }
    }

    public void stop() {
        if (runnable == null)
            return;

        runnable.cancel();
    }

}
