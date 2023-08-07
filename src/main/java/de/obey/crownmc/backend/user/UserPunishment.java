package de.obey.crownmc.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       17.01.2023 / 08:40

*/

import de.obey.crownmc.objects.punishment.Ban;
import de.obey.crownmc.objects.punishment.BanReason;
import de.obey.crownmc.objects.punishment.Mute;
import de.obey.crownmc.objects.punishment.MuteReason;
import de.obey.crownmc.util.MathUtil;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;

public final class UserPunishment {

    private final User user;
    private final YamlConfiguration cfg;

    private final String prefix = "§c§lPUNISH §8▰§7▱ ";

    private boolean muted, banned;
    private int banTimes = 0, mutedTimes = 0;

    @Getter
    private final ArrayList<Mute> mutes = new ArrayList<>();

    @Getter
    private final ArrayList<Ban> bans = new ArrayList<>();

    public UserPunishment(final User user) {
        this.user = user;
        cfg = user.getCfg();

        if(cfg.contains("muted"))
            muted = cfg.getBoolean("muted");

        if(cfg.contains("mutedtimes"))
            mutedTimes = cfg.getInt("mutedtimes");

        if(cfg.contains("mutes"))
            cfg.getConfigurationSection("mutes").getKeys(false).forEach(id -> mutes.add(new Mute(Integer.parseInt(id), cfg)));

        if(cfg.contains("banned"))
            banned = cfg.getBoolean("banned");

        if(cfg.contains("bantimes"))
            banTimes = cfg.getInt("bantimes");

        if(cfg.contains("bans"))
            cfg.getConfigurationSection("bans").getKeys(false).forEach(id -> bans.add(new Ban(Integer.parseInt(id), cfg)));
    }

    public void save() {
        cfg.set("muted", muted);
        cfg.set("mutedtimes", mutedTimes);

        cfg.set("banned", banned);
        cfg.set("bantimes", banTimes);

        for (final Mute mute : mutes)
            mute.save();

        for (final Ban ban : bans)
            ban.save();
    }

    public void registerNewMute(final String authorName, final MuteReason muteReason) {
        mutedTimes++;

        final Mute mute = new Mute(mutedTimes, muteReason, cfg);

        mute.setAuthor(authorName);
        mutes.add(mute);

        muted = true;
    }

    public void registerNewBan(final String authorName, final BanReason banReason) {
        banTimes++;

        final Ban ban = new Ban(banTimes, banReason, cfg);

        ban.setAuthor(authorName);
        bans.add(ban);

        banned = true;
    }

    public boolean registerUnmute() {
        if(!isMuted())
            return false;

        muted = false;
        mutes.get(mutedTimes-1).setMutedUntil(System.currentTimeMillis() - 1000);

        return true;
    }

    public boolean registerUnban() {
        if(!isBanned())
            return false;

        banned = false;
        bans.get(banTimes-1).setBannedUntil(System.currentTimeMillis() - 1000);

        return true;
    }

    public boolean isMuted() {
        if(muted) {
            if(mutes.get(mutedTimes-1).getMutedUntil() <= 0)
                return true;

            if(System.currentTimeMillis() >= mutes.get(mutedTimes-1).getMutedUntil()) {
                muted = false;
                return false;
            }

            return true;
        }

        return false;
    }

    public boolean isBanned() {
        if(banned) {
            if(bans.get(banTimes-1).getBannedUntil() <= 0)
                return true;

            if(System.currentTimeMillis() >= bans.get(banTimes-1).getBannedUntil()) {
                banned = false;
                return false;
            }

            return true;
        }

        return false;
    }



    public long getRemainingMuteMillis() {
        if(isMuted()) {
            if(mutes.get(mutedTimes-1).getMutedUntil() <= 0)
                return -1;

            return mutes.get(mutedTimes-1).getMutedUntil() - System.currentTimeMillis();
        }

        return 0;
    }

    public long getRemainingBanMillis() {
        if(isBanned()) {
            if(bans.get(banTimes-1).getBannedUntil() <= 0)
                return -1;

            return bans.get(banTimes-1).getBannedUntil() - System.currentTimeMillis();
        }

        return 0;
    }

    public void sendMuteInfo(final CommandSender sender) {
        if(mutes.isEmpty()) {
            sender.sendMessage("§8  - §a§oKeine Mutes");
            return;
        }

        sender.sendMessage("");
        sender.sendMessage(prefix + "Gemutet§8: §f" + (isMuted() ? "§a§l✔" : "§c§l✘"));
        sender.sendMessage("");
        sender.sendMessage(prefix + "Mutes von §8: §c§o" + user.getOfflinePlayer().getName());
        mutes.forEach(mute -> {
            sender.sendMessage("");
            sender.sendMessage("§8 - §7Mute§8.§7" + mute.getId());
            sender.sendMessage("§f     -> §cAuthor§8: §7" + mute.getAuthor());
            sender.sendMessage("§f     -> §cGrund§8: §7" + mute.getMuteReason().getName());
            if(mute.getMutedUntil() <= 0) {
                sender.sendMessage("§f     -> §cDauer§8: §4§lPERMANENT");
            } else {
                sender.sendMessage("§f     -> §cVerbleibend§8: §7" + (mute.getMutedUntil() <= System.currentTimeMillis() ? "§a§oabgelaufen" : MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds((mute.getMutedUntil() - System.currentTimeMillis()) / 1000 )));
            }
            sender.sendMessage("");
        });
    }

    public void sendBanInfo(final CommandSender sender) {
        if(bans.isEmpty()) {
            sender.sendMessage("§8  - §a§oKeine Bans");
            return;
        }

        sender.sendMessage("");
        sender.sendMessage(prefix + "Gebannt§8: §f" + (isBanned() ? "§a§l✔" : "§c§l✘"));
        sender.sendMessage("");
        sender.sendMessage(prefix + "Bans von §8: §c§o" + user.getOfflinePlayer().getName());
        bans.forEach(ban -> {
            sender.sendMessage("");
            sender.sendMessage("§8 - §7Ban§8.§7" + ban.getId());
            sender.sendMessage("§f     -> §cAuthor§8: §7" + ban.getAuthor());
            sender.sendMessage("§f     -> §cGrund§8: §7" + ban.getBanReason().getName());
            if(ban.getBannedUntil() <= 0) {
                sender.sendMessage("§f     -> §cDauer§8: §4§lPERMANENT");
            } else {
                sender.sendMessage("§f     -> §cVerbleibend§8: §7" + (ban.getBannedUntil() <= System.currentTimeMillis() ? "§a§oabgelaufen" : MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds((ban.getBannedUntil() - System.currentTimeMillis()) / 1000 )));
            }
            sender.sendMessage("");
        });
    }
}
