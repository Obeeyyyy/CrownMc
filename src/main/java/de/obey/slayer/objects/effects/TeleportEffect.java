package de.obey.slayer.objects.effects;
/*

    Author - Obey -> TraxFight
       16.07.2021 / 18:18

*/

import de.obey.slayer.SlayerMain;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TeleportEffect {
    private BukkitTask runnable;

    public TeleportEffect() {
    }

    public void run(final Player player, final int waitBetweenTick) {

        if (runnable == null) {
            runnable = new BukkitRunnable() {

                int ticks = 0;
                int state = 0;
                double angle = 0;
                double yOffset1 = 0;
                double yOffset2 = 2.3;

                @Override
                public void run() {
                    if (player == null || !player.isOnline())
                        return;

                    if (ticks < waitBetweenTick) {
                        ticks++;
                        return;
                    }

                    player.getWorld().playEffect(player.getLocation().clone().add(Math.cos(angle) * 1.2, yOffset1, Math.sin(angle) * 1.2), Effect.COLOURED_DUST, 1);
                    player.getWorld().playEffect(player.getLocation().clone().add(Math.cos(-angle) * 1.2, yOffset1, Math.sin(-angle) * 1.2), Effect.COLOURED_DUST, 1);

                    player.getWorld().playEffect(player.getLocation().clone().add(Math.cos(-angle) * 1.2, yOffset2, Math.sin(-angle) * 1.2), Effect.COLOURED_DUST, 1);
                    player.getWorld().playEffect(player.getLocation().clone().add(Math.cos(angle) * 1.2, yOffset2, Math.sin(angle) * 1.2), Effect.COLOURED_DUST, 1);

                    if (yOffset1 > 2.3)
                        state = 1;

                    if (yOffset1 <= 0)
                        state = 0;

                    if (state == 0) {
                        yOffset1 += 0.2;
                        yOffset2 -= 0.2;
                    } else {
                        yOffset1 -= 0.2;
                        yOffset2 += 0.2;
                    }

                    angle += 0.25;
                    ticks = 0;
                }
            }.runTaskTimer(SlayerMain.getInstance(), 0, 1);
        }
    }

    public void stop() {
        if (runnable == null)
            return;

        runnable.cancel();
    }
}
