package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 20:43

*/

import com.google.common.collect.Maps;
import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.MySQL;
import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.enums.StoreType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
public final class UserHandler {

    @NonNull
    private final ServerConfig serverConfig;
    @NonNull
    private final MessageUtil messageUtil;
    @NonNull
    private final ScoreboardHandler scoreboardHandler;
    @NonNull
    private final LocationHandler locationHandler;
    @NonNull
    private final MySQL mySQL;
    @NonNull
    private final ExecutorService executorService;

    @Getter
    private final Map<UUID, User> userCache = Maps.newConcurrentMap();
    private final Map<UUID, Long> loadingTimes = Maps.newConcurrentMap();

    @Getter
    private final ArrayList<Player> registering = new ArrayList<>();

    private int intervalTicked = 0;

    public void runInterval() {
        intervalTicked++;

        if (intervalTicked == 180) { // runs every 90 seconds
            intervalTicked = 0;

            userCache.values().forEach(user -> {
                saveData(user);

                if (user.getOfflinePlayer().isOnline())
                    return;

                // if the user was not online for 30 minutes the user will be removed from the cache
                if (user.isUsedForRanking() || System.currentTimeMillis() - user.getLong(DataType.LASTSEEN) <= 1000 * 60 * 30)
                    return;

                userCache.remove(user.getOfflinePlayer().getUniqueId());
            });
        }
    }

    public boolean isRegistered(final OfflinePlayer target) {
        if (target == null)
            return false;

        final File file = new File(CrownMain.getInstance().getDataFolder().getPath() + "/playerFiles/" + target.getUniqueId() + ".yml");

        if (!file.exists())
            return false;

        final YamlConfiguration cfg = FileUtil.getCfg(file);

        return cfg.getBoolean("registered");
    }

    public boolean register(final Player player) {
        // NEW PLAYER

        registering.add(player);

        final User user = new User(player);
        final int ID = serverConfig.addAndGetPlayerCount();

        userCache.put(player.getUniqueId(), user);

        try {
            if (!user.getPlayerFile().exists())
                user.getPlayerFile().createNewFile();

            final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm");
            format.setTimeZone(TimeZone.getTimeZone("CET"));

            // Load user Objects after File was created
            user.loadObjects();

            executorService.submit(() -> {

                for (DataType value : DataType.values()) {
                    if (value.getDefaultValue() != null)
                        user.getData().put(value, value.getDefaultValue());
                }

                // Setting user data
                user.setInt(DataType.ID, ID);
                user.setLong(DataType.JOINED, System.currentTimeMillis());
                user.setLong(DataType.LASTSEEN, System.currentTimeMillis());
                user.setLong(DataType.LOGINSTREAKUPDATED, System.currentTimeMillis());
                user.setString(DataType.FIRSTJOINDATE, format.format(new Date()));
                user.setBoolean(DataType.REGISTERED, true);

                // Creating mysql table row
                final ResultSet check = mySQL.getResultSet("SELECT id FROM users WHERE uuid='" + player.getUniqueId().toString() + "'");
                try {
                    if (check.next()) {
                        mySQL.execute("UPDATE users SET id='" + user.getInt(DataType.ID) + "' WHERE uuid='" + player.getUniqueId().toString() + "'");
                    } else {
                        mySQL.execute("INSERT INTO users(id, uuid, money, kills, deaths, bounty, level, xp, killstreak, killstreakrecord, elopoints, votes, playtime, destroyedBlocks, destroyedEventBlocks) " +
                                "VALUES ('" + user.getInt(DataType.ID) + "'," +  /* id */
                                " '" + user.getOfflinePlayer().getUniqueId().toString() + "'," + /* uuid */
                                " '10000', " + /* money */
                                " '0', " + /* kills */
                                " '0', " + /* deaths */
                                " '0', " + /* bounty */
                                "'0', " + /* level */
                                "'0'," + /* xp */
                                "'0'," + /* killstreak */
                                "'0'," + /* bestkillstreak */
                                "'0'," + /* elopoints */
                                "'0'," + /* votes */
                                "'0'," + /* playtime */
                                "'0'," + /* destroyedBlocks */
                                "'0')" /* destroyedEventBlocks */
                        );
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            // Saving data
            saveData(user);

            new BukkitRunnable() {
                @Override
                public void run() {
                    messageUtil.broadcast("");
                    messageUtil.broadcast("§8§l§m-----------------------------------");
                    messageUtil.broadcast("");
                    messageUtil.broadcast("   §8┃> §e§o" + user.getOfflinePlayer().getName() + "§8 (§6#§f§o" + ID + "§8)§7 ist neu auf §e§oSkySlayer.de §7!");
                    messageUtil.broadcast("");
                    messageUtil.broadcast("§8§l§m-----------------------------------");
                    messageUtil.broadcast("");

                    locationHandler.teleportToLocationNameInstant(player, "spawn");

                    registering.remove(player);
                }
            }.runTaskLater(CrownMain.getInstance(), 15);

        } catch (final IOException exception) {
            if (user.getPlayer() != null)
                user.getPlayer().kickPlayer("§c§oEin Fehler ist aufgetreten ! ( Code:13 )");

            exception.printStackTrace();
        }

        return true;
    }

    public User getUserInstant(final UUID uuid) {
        return userCache.get(uuid);
    }

    public CompletableFuture<User> getUser(final UUID uuid) {
        return loadData(uuid, false);
    }

    public CompletableFuture<User> loadData(final UUID uuid, final boolean reload) {

        loadingTimes.put(uuid, System.currentTimeMillis());

        return CompletableFuture.supplyAsync(() -> {
            if (userCache.containsKey(uuid) && !reload) {
                return userCache.get(uuid);
            }

            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            if (offlinePlayer == null)
                return null;

            final User user = new User(offlinePlayer);
            final YamlConfiguration cfg = FileUtil.getCfg(user.getPlayerFile());

            userCache.put(uuid, user);

            // Load User objects (enderchest, prefix, respawnkit usw)
            user.loadObjects();

            // Load user cooldowns
            user.getCooldowns().load();

            // Loading data from file
            for (DataType value : DataType.values()) {
                if (value.getStoreType() == StoreType.CONFIG) {
                    user.getData().put(value, cfg.contains(value.getSavedAs()) ? cfg.get(value.getSavedAs()) : value.getDefaultValue());
                }
            }

            // Loading data from mysql
            final ResultSet results = mySQL.getResultSet("SELECT * FROM users WHERE uuid='" + user.getOfflinePlayer().getUniqueId() + "'");

            if (results == null) {
                messageUtil.log("WARNING : failed to load mysql for " + uuid.toString() + " (" + offlinePlayer.getName() + ")");
                return user;
            }

            try {
                if (results.next()) {
                    user.setInt(DataType.ID, results.getInt("id"));
                    user.setLong(DataType.MONEY, results.getLong("money"));
                    user.setLong(DataType.BOUNTY, results.getLong("bounty"));
                    user.setInt(DataType.KILLS, results.getInt("kills"));
                    user.setInt(DataType.DEATHS, results.getInt("deaths"));
                    user.setInt(DataType.ELOPOINTS, results.getInt("elopoints"));
                    user.setInt(DataType.XP, results.getInt("xp"));
                    user.setInt(DataType.LEVEL, results.getInt("level"));
                    user.setInt(DataType.KILLSTREAK, results.getInt("killstreak"));
                    user.setInt(DataType.KILLSTREAKRECORD, results.getInt("killstreakrecord"));
                    user.setInt(DataType.VOTES, results.getInt("votes"));
                    user.setLong(DataType.PLAYTIME, results.getLong("playtime"));
                    user.setLong(DataType.DESTROYEDBLOCKS, results.getLong(DataType.DESTROYEDBLOCKS.getSavedAs()));
                    user.setLong(DataType.DESTROYEDEVENTBLOCKS, results.getLong(DataType.DESTROYEDEVENTBLOCKS.getSavedAs()));
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            try {
                results.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            messageUtil.log("Loaded user in " + (System.currentTimeMillis() - loadingTimes.get(uuid)) + "ms (" + uuid + ") - " + user.getOfflinePlayer().getName());

            if (user.getOfflinePlayer().isOnline())
                scoreboardHandler.updateScoreboard(user.getOfflinePlayer().getPlayer());

            return user;
        });
    }

    public void saveData(final User user) {

        if (user.getOfflinePlayer() == null)
            return;

        if (user.getPlayer() == null || !registering.contains(user.getPlayer())) {
            if (!isRegistered(user.getOfflinePlayer()))
                return;
        }

        executorService.submit(() -> {

            mySQL.execute("UPDATE users SET " +
                    "money='" + user.getLong(DataType.MONEY) + "', " +
                    "kills='" + user.getInt(DataType.KILLS) + "', " +
                    "deaths='" + user.getInt(DataType.DEATHS) + "', " +
                    "bounty='" + user.getLong(DataType.BOUNTY) + "', " +
                    "level='" + user.getInt(DataType.LEVEL) + "', " +
                    "xp='" + user.getInt(DataType.XP) + "', " +
                    "killstreak='" + user.getInt(DataType.KILLSTREAK) + "', " +
                    "killstreakrecord='" + user.getInt(DataType.KILLSTREAKRECORD) + "', " +
                    "elopoints='" + user.getInt(DataType.ELOPOINTS) + "', " +
                    "votes='" + user.getInt(DataType.VOTES) + "', " +
                    "playtime='" + user.getPlaytime().getCurrentPlaytime() + "', " +
                    "destroyedBlocks='" + user.getLong(DataType.DESTROYEDBLOCKS) + "', " +
                    "destroyedEventBlocks='" + user.getLong(DataType.DESTROYEDEVENTBLOCKS) + "' " +
                    "WHERE uuid='" + user.getOfflinePlayer().getUniqueId().toString() + "';");

            final YamlConfiguration cfg = user.getCfg();

            for (DataType value : DataType.values()) {
                if (value.getStoreType() == StoreType.CONFIG) {
                    cfg.set(value.getSavedAs(), user.getData().get(value));
                }
            }

            user.saveObjects();

            if (user.getPlayerFile().exists())
                FileUtil.saveToFile(user.getPlayerFile(), cfg);

        });
    }
}
