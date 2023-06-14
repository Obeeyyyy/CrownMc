package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       23.10.2022 / 03:00

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.CombatHandler;
import de.obey.crownmc.handler.KillFarmHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.objects.Combat;
import de.obey.crownmc.objects.effects.KillEffect;
import de.obey.crownmc.util.ArmorStandPacketBuilder;
import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@RequiredArgsConstructor
@NonNull
public final class DeathListener implements Listener {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final KillFarmHandler killFarmHandler;
    private final CombatHandler combatHandler;
    private final ServerConfig serverConfig;

    private final DecimalFormat format = new DecimalFormat("0.0",  new DecimalFormatSymbols(Locale.ENGLISH));

    @EventHandler
    public void on(final PlayerDeathEvent event) {

        final Player player = event.getEntity();
        final User user = userHandler.getUserInstant(player.getUniqueId());

        messageUtil.log("#> " + player.getName() + " DIED / ITEMS:" + player.getInventory().getContents().length);

        Player tempKiller = player.getKiller();

        if (tempKiller == null) {
            final Combat combat = combatHandler.isInCombat(player);

            if (combat == null)
                return;

            tempKiller = combat.getOpponent();
        }

        final Player killer = tempKiller;
        tempKiller = null;

        if(killer == player)
            return;

        final User killerUser = userHandler.getUserInstant(killer.getUniqueId());

        // Punishment start
        user.addInt(DataType.DEATHS, 1);
        user.removeLong(DataType.MONEY, serverConfig.getDeathMoneyLose());
        user.removeInt(DataType.ELOPOINTS, serverConfig.getDeathEloLose());
        // Punishment end


        // Reward stuff start
        int killerUserKillstreak = killerUser.getInt(DataType.KILLSTREAK);

        killFarmHandler.check(player, killer);
        if (!killFarmHandler.isBlocked(player)) {

            killerUser.addInt(DataType.KILLS, 1);
            killerUser.addInt(DataType.ELOPOINTS, serverConfig.getKillEloReward());
            killerUser.addInt(DataType.KILLSTREAK, 1);
            killerUser.addLong(DataType.MONEY, serverConfig.getKillMoneyReward());
            killerUser.addXP(serverConfig.getKillXPReward());

            killerUserKillstreak = killerUser.getInt(DataType.KILLSTREAK);

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

            if (killerUser.getInt(DataType.KILLSTREAKRECORD) < killerUserKillstreak) {
                killerUser.setInt(DataType.KILLSTREAKRECORD, killerUserKillstreak);
                messageUtil.sendMessage(killer, "Du hast einen neuen persönlichen §6§lKillstreakrekord§7 aufgestellt§8. §e§o" + killerUserKillstreak + "§7 " + (killerUserKillstreak > 1 ? "Kills" : "Kill") + " in Folge §8!");
            }
        }
        // Reward stuff end

        // Bounty stuff start
        final long bounty = user.getLong(DataType.BOUNTY);
        if (bounty > 0) {
            user.setLong(DataType.BOUNTY, 0);
            messageUtil.sendMessage(player, "Du hast dein Kopfgeld verloren. §8(§c§o" + messageUtil.formatLong(bounty) + "$§8)");

            InventoryUtil.addItem(killer, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setDisplayname("§8»┃ §7Kopf von§e§o " + player.getName())
                    .setLore("",
                            "§8▰§7▱  §6§lKopfgeld",
                            "  §8-§f§o " + messageUtil.formatLong(bounty) + "§e§o$",
                            "",
                            "§8▰§7▱  §6§lWie erhalte ich mein Kopfgeld §8?",
                            "  §8- §7Das §eKopfgeld §7kann am Spawn",
                            "  §8- §7bei §6§oMert §7abgeholt werden§8.")
                    .setSkullOwner(player.getName())
                    .build());
        }
        // Bounty stuff end

        // HOLO start
        if (killerUser.is(DataType.KILLHOLOSTATE)) {

            final ArmorStandPacketBuilder stand = new ArmorStandPacketBuilder(player.getLocation().add(0, 1, 0));

            stand.addStand(8).setGravity(false).setVisible(false).
                    setCustomName(1, "§6§lSky§e§lSlayer").
                    setCustomName(3, "§7Du hast §e§o" + player.getName() + "§7 getötet§8.").
                    setCustomName(4, "§7Deine Belohnung§8:").
                    setCustomName(6, "§a§o+ §7" + messageUtil.formatLong(serverConfig.getKillMoneyReward()) + "§e$").
                    setCustomName(7, "§a§o+ §7" + (Bools.doubleXP ? "§d§l" + messageUtil.formatLong(serverConfig.getKillXPReward() * 2L) : messageUtil.formatLong(serverConfig.getKillXPReward())) + "§e Xp").
                    setCustomName(8, "§a§o+ §7" + messageUtil.formatLong(serverConfig.getKillEloReward()) + "§e Elo").
                    spawn(killer);

            Bukkit.getScheduler().runTaskLater(CrownMain.getInstance(), () -> stand.delete(killer), 20 * 10);
        }
        // HOLO end

        //Kill Effect
        final KillEffect killEffect = new KillEffect();

        killEffect.run(player.getLocation().clone().add(0, -0.5, 0), 1, 4);

        // CombatTag Start
        new BukkitRunnable() {
            @Override
            public void run() {
                Combat combat = combatHandler.isInCombat(player);
                if (combat != null) {
                    final int playerUserKillstreak = user.getInt(DataType.KILLSTREAK);
                    messageUtil.sendMessage(player, "Du wurdest von " + killer.getName() + " §8(§f§o" +
                            "" + format.format((killer.getHealth() / 2)) + "§4§l❤§8)§7 getötet§8.§7 Der Kampf hat " + combat.getDurationString() + " gedauert§8." + (playerUserKillstreak > 0 ? " §7Du hattest eine §e§o" + playerUserKillstreak + "§7er Killstreak§8." : ""));
                    combat.end();
                }

                user.setInt(DataType.KILLSTREAK, 0);

                combat = combatHandler.isInCombat(killer);
                if (combat != null) {
                    messageUtil.sendMessage(killer, "Du hast " + player.getName() + " getötet§8.§7 Der Kampf hat " + combat.getDurationString() + " gedauert§8." + (killerUser.getInt(DataType.KILLSTREAK) > 0 ? " §7Deine Killstreak§8: §6§o" + killerUser.getInt(DataType.KILLSTREAK) + "§7 Kills§8." : ""));
                    killer.playSound(player.getLocation(), Sound.SILVERFISH_KILL, 0.5f, 100);
                    combat.end();
                }
            }
        }.runTaskLater(CrownMain.getInstance(), 5);
        // CombatTag End
    }
}
