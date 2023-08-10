package de.obey.crownmc.objects.vote;
/*

    Author - Obey -> CrownMc
       25.06.2023 / 18:25

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import com.google.common.collect.Maps;
import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.handler.VotePartyHandler;
import de.obey.crownmc.util.MessageUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class VoteParty {

    private final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
    private final UserHandler userHandler = CrownMain.getInstance().getInitializer().getUserHandler();
    private final VotePartyHandler votePartyHandler;

    private BukkitTask runnable;

    private final Map<UUID, Double> bossDamage = Maps.newConcurrentMap();

    private final String prefix = "&a&lV&6&lo&d&lt&3&le&2&lP&5&la&b&lr&c&lt&e&ly";

    public VoteParty(final VotePartyHandler votePartyHandler, final boolean instaBoss) {
        this.votePartyHandler = votePartyHandler;

        if(instaBoss) {
            spawnBossMob();
            return;
        }

        messageUtil.broadcast("In 60 Sekunden startet eine " + prefix + " §8!");

        runnable = new BukkitRunnable() {
            int ticks = 60, state = 0;
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

    public VoteParty(final VotePartyHandler votePartyHandler) {
        this.votePartyHandler = votePartyHandler;

        messageUtil.broadcast("In 60 Sekunden startet eine " + prefix + " §8!");

        runnable = new BukkitRunnable() {
            int ticks = 60, state = 0;
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
                        votePartyHandler.playeSoundForEveryOne(location, Sound.FIREWORK_LARGE_BLAST2);
                        votePartyHandler.playEffectForEveryone(location, Effect.ENDER_SIGNAL);

                        final ItemStack stack = votePartyHandler.getRandomItem();
                        final Item item = location.getWorld().dropItem(location, stack);
                        item.setCustomNameVisible(true);
                        item.setPickupDelay(3);
                        item.setCustomName(stack.hasItemMeta() ? stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : stack.getType().name() : stack.getType().name());
                        item.setVelocity(votePartyHandler.getRandomVelocity(random));
                    }
                }
            }
        }.runTaskTimer(CrownMain.getInstance(), 5, 5);
    }

    public void spawnBossMob() {
        messageUtil.broadcast("§8(" + prefix + "§8) §7Gleich erscheint ein Boss Mob§9.");

        runnable = new BukkitRunnable() {
            @Override
            public void run() {

                final Location location = votePartyHandler.getLocations().get(0);
                final Giant giant = location.getWorld().spawn(location, Giant.class);

                giant.setMaxHealth(2000);
                giant.setHealth(2000);

                giant.setCustomNameVisible(true);
                giant.setCustomName(giant.getHealth() + "§c§l❤");
            }
        }.runTaskLater(CrownMain.getInstance(), 60);
    }

    public void damageBoss(final Player player, final double damage) {
        if(bossDamage.containsKey(player.getUniqueId())) {
            bossDamage.put(player.getUniqueId(), damage + bossDamage.get(player.getUniqueId()));
            return;
        }

        bossDamage.put(player.getUniqueId(), damage);
    }

    public void end() {
        messageUtil.broadcast("§8(" + prefix + "§8) §7Der Boss ist gestorben§8.");

        for (final UUID uuid : bossDamage.keySet()) {
            userHandler.getUser(uuid).thenAcceptAsync(user -> {
                final long money = (long) (bossDamage.get(uuid) * 20L);
                final long xp = (long) (bossDamage.get(uuid) * 12L);

                user.addLong(DataType.MONEY, money);
                user.addXP(xp);

                if(user.getOfflinePlayer().isOnline()) {
                    messageUtil.sendMessage(user.getOfflinePlayer().getPlayer(), "Du hast " + bossDamage.get(uuid) + " Schaden am Boss verursacht§8.");
                    messageUtil.sendMessage(user.getOfflinePlayer().getPlayer(), "§a§o+" + messageUtil.formatLong(money) + "§2§l$");
                    messageUtil.sendMessage(user.getOfflinePlayer().getPlayer(), "§a§o+" + messageUtil.formatLong(xp) + " §2§lXP");
                    user.getOfflinePlayer().getPlayer().playSound(user.getOfflinePlayer().getPlayer().getLocation(), Sound.ENDERDRAGON_DEATH, 0.5f, 1);
                }
            });
        }
    }

    public void shutdown() {
        if(runnable == null)
            return;

        runnable.cancel();
    }

}
