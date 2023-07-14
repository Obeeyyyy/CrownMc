package de.obey.crownmc.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       17.01.2023 / 08:40

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.BanHandler;
import de.obey.crownmc.objects.punishment.Ban;
import de.obey.crownmc.objects.punishment.BanReason;
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

    private final BanHandler banHandler = CrownMain.getInstance().getInitializer().getBanHandler();

    private final User user;
    private final YamlConfiguration cfg;

    private final String prefix = "§c§lPUNISH §8▰§7▱ ";

    private boolean muted, banned;

    private long mutedUntil, mutedTimes = 0, bannedUntil;
    private int banTimes = 0;

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
            banTimes = cfg.getInt("bantimes");

        if(cfg.contains("bans")) {
            cfg.getConfigurationSection("bans").getKeys(false).forEach(id -> bans.add(new Ban(Integer.parseInt(id), cfg)));
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
                return -1;

            return bannedUntil - System.currentTimeMillis();
        }

        return 0;
    }

    public boolean registerUnban() {
        final OfflinePlayer offlinePlayer = user.getOfflinePlayer();

        if(!isBanned())
            return false;

        banned = false;
        bannedUntil = System.currentTimeMillis();

        return true;
    }

    public void registerNewBan(final String authorName, final BanReason banReason) {
        banTimes++;
        banned = true;

        if(banReason.getDuration() <= 0) {
            bannedUntil = -1;
        } else {
            bannedUntil = System.currentTimeMillis() + banReason.getDuration();
        }

        cfg.set("bans." + banTimes + ".reason", banReason.getId());
        cfg.set("bans." + banTimes + ".author", authorName);

        bans.add(new Ban(banTimes, cfg));
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
            sender.sendMessage("§f     -> §cGrund§8: §7" + ban.getBanReason().getName());
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

