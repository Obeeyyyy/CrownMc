package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       23.10.2022 / 03:00

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.*;
import de.obey.crownmc.objects.pvp.Combat;
import de.obey.crownmc.objects.effects.KillEffect;
import de.obey.crownmc.objects.pvp.PvPAltar;
import de.obey.crownmc.util.ArmorStandPacketBuilder;
import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@NonNull
public final class DeathListener implements Listener {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final KillFarmHandler killFarmHandler;
    private final CombatHandler combatHandler;
    private final ServerConfig serverConfig;
    private final LocationHandler locationHandler;
    private final PvPAltarHandler pvPAltarHandler;
    private final StatTrackHandler statTrackHandler;
    private final PvPDropHandler pvPDropHandler;

    private final DecimalFormat format = new DecimalFormat("0.0",  new DecimalFormatSymbols(Locale.ENGLISH));

    @EventHandler
    public void on(final PlayerDeathEvent event) {

        final Player died = event.getEntity();
        final User user = userHandler.getUserInstant(died.getUniqueId());

        messageUtil.log("#> " + died.getName() + " DIED IN " + died.getLocation().getWorld().getName());

        if(died.getLocation().getWorld().getName().equalsIgnoreCase("hardcore")) {
            user.getCooldowns().setCooldown("hardcore", System.currentTimeMillis() + 9000000);
        }

        Player tempKiller = died.getKiller();

        if (tempKiller == null) {
            final Combat combat = combatHandler.isInCombat(died);

            if (combat == null)
                return;

            tempKiller = combat.getOpponent();
        }

        final Player killer = tempKiller;
        tempKiller = null;

        // PvP Altar stuff start

        if(pvPAltarHandler.getCapturing().containsKey(died.getUniqueId())) {
            final PvPAltar altar = pvPAltarHandler.getCapturing().get(died.getUniqueId());
            altar.died(died, killer);
        }

        // PvP Altar stuff end

        if(killer == died)
            return;

        final User killerUser = userHandler.getUserInstant(killer.getUniqueId());

        // Punishment start
        user.addLong(DataType.DEATHS, 1);
        user.removeLong(DataType.MONEY, serverConfig.getDeathMoneyLose());
        user.removeLong(DataType.ELOPOINTS, serverConfig.getDeathEloLose());

        if(user.getClan() != null) {
            user.getClan().addDeath(1);
        }
        // Punishment end

        statTrackHandler.addCount(killer);

        // Reward stuff start
        long killerUserKillstreak;

        long moneyReward = serverConfig.getKillMoneyReward(),
                eloReward =  serverConfig.getKillEloReward(),
                xpReward = serverConfig.getKillXPReward();

        killFarmHandler.check(died, killer);
        if (!killFarmHandler.isBlocked(died)) {

            killerUser.addLong(DataType.KILLS, 1);
            killerUser.addLong(DataType.KILLSTREAK, 1);

            killerUserKillstreak = killerUser.getLong(DataType.KILLSTREAK);

            if((killerUserKillstreak / 5) % 2 == 0) { // wenn kill streak 5, 10, 15 , 20 usw
                moneyReward += (serverConfig.getBaseMoneyKillstreak() * (killerUserKillstreak/5L));
                eloReward += serverConfig.getBaseMoneyKillstreak() * (killerUserKillstreak/5L);
                xpReward += serverConfig.getBaseXPkillstreak() * (killerUserKillstreak/5);
            }

            if(killerUserKillstreak >= 10)
                killerUser.getBadges().addBadge("ks10");

            if(killerUserKillstreak >= 25)
                killerUser.getBadges().addBadge("ks25");

            if(killerUserKillstreak >= 50)
                killerUser.getBadges().addBadge("ks50");

            if(killerUserKillstreak >= 100)
                killerUser.getBadges().addBadge("ks100");

            if(killerUserKillstreak >= 250)
                killerUser.getBadges().addBadge("ks250");

            if (killerUser.getLong(DataType.KILLSTREAKRECORD) < killerUserKillstreak) {
                killerUser.setLong(DataType.KILLSTREAKRECORD, killerUserKillstreak);
                messageUtil.sendActionBar(killer, "§a§lKillstreak §7Rekord §8- §f§o" + killerUserKillstreak);
            }

            killerUser.addLong(DataType.ELOPOINTS, eloReward);
            killerUser.addLong(DataType.MONEY, moneyReward);
            killerUser.addXP(xpReward);

            if(killerUser.getClan() != null) {
                killerUser.getClan().addKill(1);
            }

            // End Drop
            final Location location = locationHandler.getLocation("end");

            if(location != null && killer.getLocation().getWorld() == location.getWorld()) {
                event.getDrops().add(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                                .setTextur("MTA4OTFlNzY2NmE0MWQxM2FlMTM5YTE5Njk2OGFjY2U4YTA1NGQ4NGRkODMwNGNlYWRkMjhhODc4OTg4M2IyNiJ9fX0=", UUID.fromString("f4b1497c-1122-3344-5566-059a8fa5b024"))
                                .setDisplayname("§8( §2§l☯ §8) §f§o" + died.getName() + "'s §2§lSeele")
                                .setLore("",
                                        "§8▰§7▱ §2§lSeelen Tausch",
                                        "§8  -§7 Die Seele kann bei §5§lReaper",
                                        "§8  -§7 am Spawn getauscht werden§8.",
                                        "")
                        .build());
            }
        }

        // Item Drops
        if(killerUser.is(DataType.GETKILLITEMS)) {
            final List<ItemStack> temp = event.getDrops();

            if (!temp.isEmpty()) {
                for (final ItemStack drop : temp)
                    InventoryUtil.addItem(killer, drop);
            }

            event.getDrops().clear();
        }

        pvPDropHandler.drop(killer);
        // Reward stuff end

        // Bounty stuff start
        final long bounty = user.getLong(DataType.BOUNTY);
        if (bounty > 0) {
            user.setLong(DataType.BOUNTY, 0);
            messageUtil.sendMessage(died, "Du hast dein Kopfgeld verloren. §8(§c§o" + messageUtil.formatLong(bounty) + "$§8)");

            InventoryUtil.addItem(killer, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setDisplayname("§8»┃ §7Kopf von§e§o " + died.getName())
                    .setLore("",
                            "§8▰§7▱  §6§lKopfgeld",
                            "  §8-§f§o " + messageUtil.formatLong(bounty) + "§e§o$",
                            "",
                            "§8▰§7▱  §6§lWie erhalte ich mein Kopfgeld §8?",
                            "  §8- §7Das §eKopfgeld §7kann am Spawn",
                            "  §8- §7bei §5§lReaper §7abgeholt werden§8.",
                            "")
                    .setSkullOwner(died.getName())
                    .build());
        }
        // Bounty stuff end


        // HOLO start
        if (killerUser.is(DataType.KILLHOLOSTATE)) {

            final ArmorStandPacketBuilder stand = new ArmorStandPacketBuilder(died.getLocation().add(0, 1, 0));

            stand.addStand(8).setGravity(false).setVisible(false).
                    setCustomName(1, "§6§lCrownMc").
                    setCustomName(3, "§7Du hast §e§o" + died.getName() + "§7 getötet§8.").
                    setCustomName(4, "§7Deine Belohnung§8:").
                    setCustomName(6, "§a§o+ §7" + moneyReward + "§e$").
                    setCustomName(7, "§a§o+ §7" + (Bools.doubleXP ? "§d§l" + messageUtil.formatLong(xpReward * 2L) : messageUtil.formatLong(xpReward)) + "§e Xp").
                    setCustomName(8, "§a§o+ §7" + messageUtil.formatLong(eloReward) + "§e Elo").
                    spawn(killer);

            Bukkit.getScheduler().runTaskLater(CrownMain.getInstance(), () -> stand.delete(killer), 20 * 10);
        }
        // HOLO end

        //Kill Effect
        final KillEffect killEffect = new KillEffect();

        killEffect.run(died.getLocation().clone().add(0, -0.5, 0), 1, 4);

        // CombatTag Start
        new BukkitRunnable() {
            @Override
            public void run() {
                Combat combat = combatHandler.isInCombat(died);
                if (combat != null) {
                    final long playerUserKillstreak = user.getLong(DataType.KILLSTREAK);
                    if(playerUserKillstreak > 1)
                        messageUtil.sendActionBar(died, "§a§lKillstreak §f§o§m" + playerUserKillstreak + "er Streak");

                    messageUtil.sendMessage(died, "§8(§4§l✘§8) §c§o" + killer.getName() + "§8 (§f§o" + format.format((killer.getHealth() / 2)) + "§4§l❤§8) §8- §f§o" + combat.getDurationString() + "§8- §c§o-" + messageUtil.formatLong(serverConfig.getDeathMoneyLose()) + "§4§l$ §8- §c§o-" + messageUtil.formatLong(serverConfig.getDeathEloLose()) + "§4 Elo");
                    combat.end();
                }

                user.setLong(DataType.KILLSTREAK, 0);

                combat = combatHandler.isInCombat(killer);
                if (combat != null) {
                    messageUtil.sendMessage(killer, "§8(§4§l⚔§8) §a§o" + died.getName() + " §8- §f§o" + combat.getDurationString() + "§8 - §7Streak§8: §f§o" + killerUser.getLong(DataType.KILLSTREAK));
                    killer.playSound(died.getLocation(), Sound.SILVERFISH_KILL, 0.5f, 100);
                    combat.end();
                }
            }
        }.runTaskLater(CrownMain.getInstance(), 5);
        // CombatTag End
    }
}
