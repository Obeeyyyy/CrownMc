package de.obey.crownmc.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       17.01.2023 / 08:40

*/

import de.obey.crownmc.util.MathUtil;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;

public final class UserPunishment {

    private final User user;
    private final YamlConfiguration cfg;

    private final String prefix = "§c§lPUNISH §8▰§7▱ ";

    private boolean muted;

    private long mutedUntil, mutedTimes = 0;

    @Getter
    private ArrayList<Mute> mutes = new ArrayList<>();

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
    }

    public void save() {
        cfg.set("muted", muted);
        cfg.set("muteduntil", mutedUntil);
        cfg.set("mutedtimes", mutedTimes);
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

    public long getRemainingMillis() {
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

        if(offlinePlayer.isOnline()) {
            offlinePlayer.getPlayer().sendMessage(prefix + "Du wurdest von §8'§c§o" + mutedBy.getName() + "§8'§7 für §f" + MathUtil.getHoursAndMinutesAndSecondsFromSeconds(getRemainingMillis() / 1000) + "§7gemutet§8.");
            offlinePlayer.getPlayer().sendMessage(prefix + "Grund§8: §f" + reason);
            offlinePlayer.getPlayer().playSound(offlinePlayer.getPlayer().getLocation(), Sound.BLAZE_DEATH, 0.5f,1);
        }

        mutedBy.sendMessage(prefix + "Du hast §8'§c§o" + offlinePlayer.getName() + "§8'§7 für §f" + MathUtil.getHoursAndMinutesAndSecondsFromSeconds(getRemainingMillis() / 1000) + "§7gemutet§8.");
        mutedBy.sendMessage(prefix + "Grund§8: §f" + reason);
    }

    public void sendMuteInfo(final CommandSender sender) {
        sender.sendMessage(prefix + "Status§8: §f" + isMuted());
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
