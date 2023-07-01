package de.obey.crownmc.objects;
/*

    Author - Obey -> CrownMc
       25.06.2023 / 18:25

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.VotePartyHandler;
import de.obey.crownmc.util.MessageUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class VoteParty {

    private final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
    private final VotePartyHandler votePartyHandler;

    private BukkitTask runnable;

    private final String prefix = "&a&lV&6&lo&d&lt&3&le&2&lP&5&la&b&lr&c&lt&e&ly";

    public VoteParty(final VotePartyHandler votePartyHandler) {
        this.votePartyHandler = votePartyHandler;

        messageUtil.broadcast("In 20 Sekunden startet eine " + prefix + " §8!");

        runnable = new BukkitRunnable() {
            int ticks = 20, state = 0;
            @Override
            public void run() {
                if(state != 2) {
                    ticks--;

                    if (ticks == 5) {
                        state = 1;
                    }

                    if (state == 1) {
                        messageUtil.broadcast("§8(" + prefix + "§8) §7Noch §f" + ticks + " §7Sekunde" + (ticks > 1 ? "n" : "") + "§8.");
                    }

                    if(ticks == 1) {
                        state = 2;
                        ticks = 0;
                    }

                    return;
                }

                startDropRunnable();
            }
        }.runTaskTimer(CrownMain.getInstance(), 20, 20);
    }

    private void startDropRunnable() {
        runnable.cancel();
        runnable = new BukkitRunnable() {

            int itemDrops = 0, ticks = 0;

            @Override
            public void run() {

                if(itemDrops >= 5) {
                    messageUtil.broadcast("§8(  " + prefix + "§8)§7 Die VoteParty ist beendet§8.");
                    cancel();
                    return;
                }

                ticks++;

                if(ticks == 3) {
                    ticks = 0;
                    itemDrops++;

                    for (final Location location : votePartyHandler.getLocations()) {
                        for (final Entity entity : location.getWorld().getEntities()) {
                            if(!(entity instanceof Player))
                                continue;

                            ((Player) entity).playSound(location, Sound.FIREWORK_BLAST, 1, 1);
                            ((Player) entity).playEffect(location, Effect.ENDER_SIGNAL, 1);
                        }
                        location.getWorld().dropItem(location, votePartyHandler.getRandomItem());
                    }
                }
            }
        }.runTaskTimer(CrownMain.getInstance(), 5, 5);
    }

    public void shutdown() {
        if(runnable == null)
            return;

        runnable.cancel();
    }

}
