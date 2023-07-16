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
import org.bukkit.entity.Giant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Random;

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

        final Random random = new Random();

        runnable.cancel();
        runnable = new BukkitRunnable() {

            int itemDrops = 0, ticks = 0;

            @Override
            public void run() {

                if(itemDrops >= 20) {
                    spawnBossMob();
                    cancel();
                    return;
                }

                ticks++;

                if(ticks == 3) {
                    ticks = 0;
                    itemDrops++;

                    for (final Location location : votePartyHandler.getLocations()) {
                        playeSoundForEveryOne(location, Sound.FIREWORK_LARGE_BLAST2);
                        playEffectForEveryone(location, Effect.ENDER_SIGNAL);

                        final Item item = location.getWorld().dropItem(location, votePartyHandler.getRandomItem());
                        item.setVelocity(getRandomVelocity(random));
                    }
                }
            }
        }.runTaskTimer(CrownMain.getInstance(), 5, 5);
    }

    private void spawnBossMob() {
        messageUtil.broadcast("§8(" + prefix + "§8) §7Gleich erscheint ein Boss Mob§9.");

        runnable = new BukkitRunnable() {
            @Override
            public void run() {

                final Location location = votePartyHandler.getLocations().get(new Random().nextInt(votePartyHandler.getLocations().size()));
                final Giant giant = location.getWorld().spawn(location, Giant.class);

                giant.setMaxHealth(2000);
                giant.setHealth(2000);

                giant.setCustomNameVisible(true);
                giant.setCustomName(giant.getHealth() + "§c§l❤");
            }
        }.runTaskLater(CrownMain.getInstance(), 60);
    }

    private void playEffectForEveryone(final Location location, final Effect effect) {
        for (Entity entity : location.getWorld().getEntities()) {

            if(!(entity instanceof Player))
                continue;

            ((Player) entity).playEffect(location, effect, 1);
        }
    }

    private void playeSoundForEveryOne(final Location location, final Sound sound) {
        for (Entity entity : location.getWorld().getEntities()) {

            if(!(entity instanceof Player))
                continue;

            ((Player) entity).playSound(location, sound, 0.5f, 1);
        }
    }

    private Vector getRandomVelocity(Random random) {
        double x = random.nextDouble() - 0.5;
        double y = random.nextDouble() - 0.5;
        double z = random.nextDouble() - 0.5;

        return new Vector(x, y, z).normalize().multiply(0.6);
    }

    public void shutdown() {
        if(runnable == null)
            return;

        runnable.cancel();
    }

}
