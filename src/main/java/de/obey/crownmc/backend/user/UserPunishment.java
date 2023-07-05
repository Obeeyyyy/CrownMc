package de.obey.crownmc.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       17.01.2023 / 08:40

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.objects.Ban;
import de.obey.crownmc.util.MathUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public final class UserPunishment {

    private final User user;
    private final YamlConfiguration cfg;

    private final String prefix = "§c§lPUNISH §8▰§7▱ ";

    private boolean muted, banned;

    private long mutedUntil, mutedTimes = 0, bannedUntil, banTimes = 0;

    @Getter
    private ArrayList<Mute> mutes = new ArrayList<>();

    @Getter
    private ArrayList<Ban> bans = new ArrayList<>();

    public UserPunishment(final User user) {
        this.user = user;
        cfg = user.getCfg();

        if(cfg.contains("muted"))
            muted = cfg.getBoolean("muted");

        if(cfg.contains("muteduntil"))
            mutedUntil = cfg.getLong("muteduntil");

        if(cfg.contains("mutedtimes"))
            mutedTimes = cfg.getLong("mutedtimes");

        if(cfg.contains("mutes")) {
            cfg.getConfigurationSection("mutes").getKeys(false).forEach(id -> {
                mutes.add(new Mute(id, cfg));
            });
        }

        if(cfg.contains("banned"))
            banned = cfg.getBoolean("banned");

        if(cfg.contains("banneduntil"))
            bannedUntil = cfg.getLong("banneduntil");

        if(cfg.contains("bantimes"))
            banTimes = cfg.getLong("bantimes");

        if(cfg.contains("bans")) {
            cfg.getConfigurationSection("bans").getKeys(false).forEach(id -> {
                bans.add(new Ban(id, cfg));
            });
        }
    }

    public void save() {
        cfg.set("muted", muted);
        cfg.set("muteduntil", mutedUntil);
        cfg.set("mutedtimes", mutedTimes);

        cfg.set("banned", banned);
        cfg.set("bannedduntil", bannedUntil);
        cfg.set("bantimes", banTimes);
    }


    public boolean isBanned() {
        if(banned) {

            if(bannedUntil <= 0)
                return true;

            if(System.currentTimeMillis() >= bannedUntil) {
                banned = false;
                return false;
            }

            return true;
        }

        return false;
    }

    public long getRemainingBanMillis() {
        if(isBanned()) {
            if(bannedUntil <= 0)
                return bannedUntil;

            return bannedUntil - System.currentTimeMillis();
        }

        return 0;
    }

    public void unBan(final CommandSender unbanBy) {
        final OfflinePlayer offlinePlayer = user.getOfflinePlayer();

        if(!isMuted()) {
            unbanBy.sendMessage(prefix + "Der Spieler " + offlinePlayer.getName() + " ist nicht gebannt§8.");
            return;
        }

        banned = false;
        bannedUntil = System.currentTimeMillis();

        unbanBy.sendMessage(prefix + "Du hast §8'§a§o" + offlinePlayer.getName() + "§8'§7 entbannt§8.");
    }

    public void ban(final CommandSender banBy, final String reason, final long bannedMillis) {
        final OfflinePlayer offlinePlayer = user.getOfflinePlayer();

        if (isBanned()) {
            banBy.sendMessage(prefix + "Der Spieler " + offlinePlayer.getName() + " ist schon gebannt§8.");
            return;
        }

        banTimes++;
        banned = true;
        if (bannedMillis <= 0) {
            bannedUntil = -1;
        } else {
            bannedUntil = System.currentTimeMillis() + bannedMillis;
        }

        cfg.set("bans." + banTimes + ".reason", reason);
        cfg.set("bans." + banTimes + ".author", banBy.getName());
        cfg.set("bans." + banTimes + ".duration", bannedMillis);

        bans.add(new Ban(banTimes + "", cfg));

        Bukkit.broadcastMessage(prefix + "§8'§c§o" + offlinePlayer.getName() + "§8'§7 wurde von " + banBy.getName() + " gebannt§8.");

        if (offlinePlayer.isOnline()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    offlinePlayer.getPlayer().kickPlayer("\n§6§lCrownMc§8.§6§lde\n\n" +
                            "§c§oDu wurdest für '" + reason + "' gebannt.\n\n" +
                            "§7Author§8: §f" + banBy.getName() + "\n" +
                            "§7Dauer§8: §f" + (bannedMillis <= 0 ? "§4§lPermanent" : MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds(getRemainingBanMillis() / 1000)));
                }
            }.runTask(CrownMain.getInstance());
        }
    }

    public void sendBanInfo(final CommandSender sender) {
        sender.sendMessage(prefix + "Banned§8: §f" + isBanned());

        if(bans.isEmpty()) {
            sender.sendMessage("Keine Bans");
            return;
        }

        sender.sendMessage(prefix + "Bans von §8: §c§o" + user.getOfflinePlayer().getName());
        bans.forEach(ban -> {
            sender.sendMessage("");
            sender.sendMessage("§8 - §7Ban§8.§7" + ban.getId());
            sender.sendMessage("§f     -> §cAuthor§8: §7" + ban.getAuthor());
            sender.sendMessage("§f     -> §cGrund§8: §7" + ban.getReason());
            sender.sendMessage("§f     -> §cDuration§8: §7" + (bannedUntil <= 0 ? "§4§lPermanent" : MathUtil.getHoursAndMinutesAndSecondsFromSeconds(getRemainingBanMillis()/ 1000)));
        });
    }

    public boolean isMuted() {
        if(muted) {
            if(System.currentTimeMillis() >= mutedUntil) {
                muted = false;
                return false;
            }

            return true;
        }

        return false;
    }

    public long getRemainingMuteMillis() {
        if(isMuted())
            return mutedUntil - System.currentTimeMillis();

        return 0;
    }

    public void unMute(final CommandSender unmutedBy) {
        final OfflinePlayer offlinePlayer = user.getOfflinePlayer();

        if(!isMuted()) {
            unmutedBy.sendMessage(prefix + "Der Spieler " + offlinePlayer.getName() + " ist nicht gemutet§8.");
            return;
        }

        muted = false;
        mutedUntil = System.currentTimeMillis();

        if(offlinePlayer.isOnline())
            offlinePlayer.getPlayer().sendMessage(prefix + "Du wurdest von §a§o" + unmutedBy.getName() + "§7 entmutet§8.");

        unmutedBy.sendMessage(prefix + "Du hast §8'§a§o" + offlinePlayer.getName() + "§8'§7 entmutet§8.");
    }

    public void mute(final CommandSender mutedBy, final String reason, final long mutedmillis) {
        final OfflinePlayer offlinePlayer = user.getOfflinePlayer();

        if(isMuted()) {
            mutedBy.sendMessage(prefix + "Der Spieler " + offlinePlayer.getName() + " ist schon gemutet§8.");
            return;
        }

        mutedTimes++;
        muted = true;
        mutedUntil = System.currentTimeMillis() + mutedmillis;

        cfg.set("mutes." + mutedTimes + ".reason", reason);
        cfg.set("mutes." + mutedTimes + ".author", mutedBy.getName());
        cfg.set("mutes." + mutedTimes + ".duration", mutedmillis);

        mutes.add(new Mute(mutedTimes + "", cfg));

        Bukkit.broadcastMessage(prefix + offlinePlayer.getName() + " wurde von §8'§c§o" + mutedBy.getName() + "§8'§7 gemutet§8.");

        if(offlinePlayer.isOnline())
            offlinePlayer.getPlayer().playSound(offlinePlayer.getPlayer().getLocation(), Sound.BLAZE_DEATH, 0.5f,1);
    }

    public void sendMuteInfo(final CommandSender sender) {
        sender.sendMessage(prefix + "Muted§8: §f" + isMuted());

        if(mutes.isEmpty()) {
            sender.sendMessage("Keine Mutes");
            return;
        }

        sender.sendMessage(prefix + "Mutes von §8: §c§o" + user.getOfflinePlayer().getName());
        mutes.forEach(mute -> {
            sender.sendMessage("");
            sender.sendMessage("§8 - §7Mute§8.§7" + mute.id);
            sender.sendMessage("§f     -> §cAuthor§8: §7" + mute.author);
            sender.sendMessage("§f     -> §cGrund§8: §7" + mute.reason);
            sender.sendMessage("§f     -> §cDuration§8: §7" + MathUtil.getHoursAndMinutesAndSecondsFromSeconds(mute.duration / 1000));
        });
    }
}

@Getter
final class Mute {

    final String id, author, reason;
    final long duration;

    Mute(final String id, final YamlConfiguration cfg) {
        this.id = id;
        author = cfg.getString("mutes." + id + ".author");
        reason = cfg.getString("mutes." + id + ".reason");
        duration = cfg.getLong("mutes." + id + ".duration");
    }

}

