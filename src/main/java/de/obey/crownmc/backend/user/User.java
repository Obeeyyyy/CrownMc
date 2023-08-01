package de.obey.crownmc.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 20:43

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.objects.Clan;
import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.LevelUtil;
import de.obey.crownmc.util.PacketReader;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public final class User {

    private final Initializer initializer = CrownMain.getInstance().getInitializer();
    private OfflinePlayer offlinePlayer;
    private Player player;
    private File playerFile;
    private YamlConfiguration cfg;
    private boolean usedForRanking = false;
    private UserEnderchest enderchest;
    private UserPrefix prefix;
    private UserRespawnKit respawnKit;
    private UserCooldowns cooldowns;
    private UserBadges badges;
    private UserPlaytime playtime;
    private UserPlot plot;
    private UserBank bank;
    private UserPunishment punishment;
    private UserLoginStreak loginStreak;
    private UserPeace userPeace;
    private Clan clan;

    private PacketReader packetReader;

    private final Map<DataType, Object> data = new HashMap<>();

    public User(final Player player) {
        this.player = player;
        this.offlinePlayer = player;

        this.playerFile = new File(CrownMain.getInstance().getDataFolder() + "/playerFiles/" + offlinePlayer.getUniqueId().toString() + ".yml");
        cfg = FileUtil.getCfg(playerFile);
    }

    public User(final OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;

        if (offlinePlayer.isOnline())
            player = offlinePlayer.getPlayer();

        this.playerFile = new File(CrownMain.getInstance().getDataFolder() + "/playerFiles/" + offlinePlayer.getUniqueId().toString() + ".yml");
        cfg = FileUtil.getCfg(playerFile);
    }

    public void loadObjects() {
        this.enderchest = new UserEnderchest(this);
        this.respawnKit = new UserRespawnKit(this);
        this.cooldowns = new UserCooldowns(this);
        this.prefix = new UserPrefix(this);
        this.badges = new UserBadges(this);
        this.playtime = new UserPlaytime(this);
        this.plot = new UserPlot(this);
        this.bank = new UserBank(this);
        this.punishment = new UserPunishment(this);
        this.loginStreak = new UserLoginStreak(this);
        this.userPeace = new UserPeace(this);
    }

    public void loadClan() {
        if(!getString(DataType.CLANNAME).equalsIgnoreCase("-")) {
            initializer.getClanHandler().loadData(getString(DataType.CLANNAME)).thenAcceptAsync(data -> {
                if (data == null) {
                    setString(DataType.CLANNAME, "-");
                    return;
                }

                clan = data;
            });
        }
    }

    public void saveObjects() {
        enderchest.save();
        respawnKit.save();
        cooldowns.save();
        prefix.save();
        badges.save();
        plot.save();
        bank.save();
        punishment.save();
        userPeace.save();
    }

    public void addXP(long amount) {
        if (Bools.doubleXP)
            amount *= 2;

        if(clan != null)
            clan.addXP(amount);

        addLong(DataType.XP, amount);
        LevelUtil.checkForLevelUp(this);
    }

    private void updateData() {
        if (!offlinePlayer.isOnline())
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                initializer.getScoreboardHandler().updateScoreboard(offlinePlayer.getPlayer());
            }
        }.runTask(initializer.getCrownMain());
    }

    public long getLong(final DataType type) {
        return data.containsKey(type) ? (long) data.get(type) : (long) type.getDefaultValue();
    }

    public String getString(final DataType type) {
        return data.containsKey(type) ? (String) data.get(type) : (String) type.getDefaultValue();
    }

    public List<?> getList(final DataType type) {
        return data.containsKey(type) ? (List<?>) data.get(type) : (List<?>) type.getDefaultValue();
    }

    public boolean is(final DataType type) {
        return data.containsKey(type) ? (boolean) data.get(type) : (boolean) type.getDefaultValue();
    }

    public void addLong(final DataType type, final long amount) {
        data.put(type, (data.containsKey(type) ? ((long) data.get(type) + amount < 0 ? 0 : (long) data.get(type) + amount) : amount));
        updateData();
    }

    public void removeLong(final DataType type, final long amount) {
        data.put(type, (data.containsKey(type) ? ((long) data.get(type) - amount < 0 ? 0 : (long) data.get(type) - amount) : 0));
        updateData();
    }

    public void clearList(final DataType type) {
        data.put(type, Collections.emptyList());
        updateData();
    }

    public void setLong(final DataType type, final long amount) {
        data.put(type, amount);
        updateData();
    }

    public void setString(final DataType type, final String text) {
        data.put(type, text);
        updateData();
    }

    public void setBoolean(final DataType type, final boolean state) {
        data.put(type, state);
        updateData();
    }

    public void setList(final DataType type, List<?> list) {
        data.put(type, list);
        updateData();
    }

}
