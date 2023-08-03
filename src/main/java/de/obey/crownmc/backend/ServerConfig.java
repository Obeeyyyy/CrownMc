package de.obey.crownmc.backend;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 21:09

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.InventoryUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public final class ServerConfig {

    @NonNull
    @Getter(AccessLevel.NONE)
    private final Initializer initializer;
    private final Map<String, Integer> domainJoins = new HashMap<>();
    private final Map<Integer, RespawnKitItem> respawnKitItems = new HashMap<>();
    private File configFile;
    private YamlConfiguration cfg;
    @Setter
    private int playerCount, killMoneyReward = 0,
            deathMoneyLose = 0, killXPReward = 0,
            killEloReward = 0, deathEloLose = 0,
            votes = 0, voteparty = 0,
            dailycount = 0,
            baseXPkillstreak = 0, baseMoneyKillstreak = 0, baseEloKillstreak = 0, levelUpMoney = 0,
            netherPrice = 1000000, endPrice = 1000000, soulReward = 0, epCooldown = 3,
            netherLevel = 1, endLevel = 1,
            clanPrice = 20000;
    @Setter
    private long dailyMillisCount = 0;
    @Setter
    private boolean whitelist, betawhitelist, playtest;
    @Setter
    private String prefix;
    private String host, username, database, password;
    @Setter
    private String motd1 = "", motd2 = "", event = "change";
    private List<String> blockedCommands = new ArrayList<>(), blockedCombatCommands = new ArrayList<>();
    private List<String> autoBroadcastMessages = new ArrayList<>();
    @Setter
    private long breakCounter, placeCounter, autoBroadcastDelay = 8;
    @Setter
    private String destroyEventGoal = "VOID";

    public void load() {
        configFile = new File(initializer.getCrownMain().getDataFolder().getPath() + "/server.yml");
        cfg = FileUtil.getCfg(configFile);

        if (!initializer.getCrownMain().getDataFolder().exists())
            initializer.getCrownMain().getDataFolder().mkdir();

        final File playerFiles = new File(initializer.getCrownMain().getDataFolder() + "/playerFiles");

        if (!playerFiles.exists())
            playerFiles.mkdir();

        final File clanFiles = new File(initializer.getCrownMain().getDataFolder() + "/clanFiles");

        if(!clanFiles.exists())
            clanFiles.mkdir();

        if (!configFile.exists()) {

            cfg.set("whitelist", true);
            cfg.set("betawhitelist", false);
            cfg.set("playtest", false);
            cfg.set("playercount", 0);
            cfg.set("prefix", "&f&lObeySystem &8>&7 ");
            cfg.set("mysql.host", "change");
            cfg.set("mysql.password", "change");
            cfg.set("mysql.username", "change");
            cfg.set("mysql.database", "change");
            cfg.set("firstjoin", new ArrayList<ItemStack>());
            cfg.set("voteitems", new ArrayList<ItemStack>());
            cfg.set("domains.45,142,114,29", 0);
            cfg.set("motd.1", "&f&lObeysystem");
            cfg.set("motd.2", "&7change me :)");
            cfg.set("event", "CHANGE THIS");

            FileUtil.saveToFile(configFile, cfg);
        }

        whitelist = cfg.getBoolean("whitelist");
        betawhitelist = cfg.getBoolean("betawhitelist");
        prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString("prefix"));
        host = cfg.getString("mysql.host");
        username = cfg.getString("mysql.username");
        database = cfg.getString("mysql.database");
        password = cfg.getString("mysql.password");

        if (cfg.contains("playercount"))
            playerCount = cfg.getInt("playercount");

        if (cfg.getConfigurationSection("domains") != null) {
            cfg.getConfigurationSection("domains").getKeys(false).forEach(domain -> {
                domainJoins.put(domain, cfg.getInt("domains." + domain));
            });
        }

        if (cfg.contains("killMoneyReward"))
            killMoneyReward = cfg.getInt("killMoneyReward");

        if (cfg.contains("killXPReward"))
            killXPReward = cfg.getInt("killXPReward");

        if (cfg.contains("killEloReward"))
            killEloReward = cfg.getInt("killEloReward");

        if (cfg.contains("deathMoneyLose"))
            deathMoneyLose = cfg.getInt("deathMoneyLose");

        if (cfg.contains("deathEloLose"))
            deathEloLose = cfg.getInt("deathEloLose");

        if (cfg.contains("votes"))
            votes = cfg.getInt("votes");

        if (cfg.contains("voteparty"))
            voteparty = cfg.getInt("voteparty");

        if (cfg.contains("motd.1"))
            motd1 = cfg.getString("motd.1");

        if (cfg.contains("motd.2"))
            motd2 = cfg.getString("motd.2");

        if (cfg.contains("blockedCommands"))
            blockedCommands = cfg.getStringList("blockedCommands");

        if (cfg.contains("blockedCombatCommands"))
            blockedCombatCommands = cfg.getStringList("blockedCombatCommands");

        if (cfg.contains("event"))
            event = ChatColor.translateAlternateColorCodes('&', cfg.getString("event"));

        if (cfg.contains("dailycount"))
            dailycount = cfg.getInt("dailycount");

        if (cfg.contains("dailyMillisCount"))
            dailyMillisCount = cfg.getLong("dailyMillisCount");

        if (cfg.contains("autoBroadcast.messages"))
            autoBroadcastMessages = cfg.getStringList("autoBroadcast.messages");

        if (cfg.contains("autoBroadcast.delay"))
            autoBroadcastDelay = cfg.getLong("autoBroadcast.delay");

        if (cfg.contains("breakCounter"))
            breakCounter = cfg.getLong("breakCounter");

        if (cfg.contains("placeCounter"))
            placeCounter = cfg.getLong("placeCounter");

        if (cfg.contains("destroyEventGoal"))
            destroyEventGoal = cfg.getString("destroyEventGoal");

        if (cfg.contains("baseXPkillstreak"))
            baseXPkillstreak = cfg.getInt("baseXPkillstreak");

        if (cfg.contains("baseMoneyKillstreak"))
            baseMoneyKillstreak = cfg.getInt("baseMoneyKillstreak");

        if (cfg.contains("baseEloKillstreak"))
            baseEloKillstreak = cfg.getInt("baseEloKillstreak");

        if (cfg.contains("levelUpMoney"))
            levelUpMoney = cfg.getInt("levelUpMoney");

        if (cfg.contains("netherprice"))
            netherPrice = cfg.getInt("netherprice");

        if (cfg.contains("endprice"))
            endPrice = cfg.getInt("endprice");

        if (cfg.contains("soulReward"))
            soulReward = cfg.getInt("soulReward");

        if (cfg.contains("epCooldown"))
            epCooldown = cfg.getInt("epCooldown");

        if (cfg.contains("endLevel"))
            endLevel = cfg.getInt("endLevel");

        if (cfg.contains("netherLevel"))
            netherLevel = cfg.getInt("netherLevel");

        clanPrice = cfg.getInt("clanPrice", 20000);

        playtest = cfg.getBoolean("playtest", false);

        loadRespawnKitItems();
    }

    public void save() {
        cfg.set("whitelist", whitelist);
        cfg.set("betawhitelist", betawhitelist);
        cfg.set("playtest", playtest);
        cfg.set("playercount", playerCount);
        cfg.set("motd.1", motd1);
        cfg.set("motd.2", motd2);
        cfg.set("killMoneyReward", killMoneyReward);
        cfg.set("killXPReward", killXPReward);
        cfg.set("killEloReward", killEloReward);
        cfg.set("deathMoneyLose", deathMoneyLose);
        cfg.set("deathEloLose", deathEloLose);
        cfg.set("voteparty", voteparty);
        cfg.set("votes", votes);
        cfg.set("blockedCommands", blockedCommands);
        cfg.set("blockedCombatCommands", blockedCombatCommands);
        cfg.set("event", event);
        cfg.set("dailycount", dailycount);
        cfg.set("dailyMillisCount", dailyMillisCount);
        cfg.set("autoBroadcast.messages", autoBroadcastMessages);
        cfg.set("autoBroadcast.delay", autoBroadcastDelay);
        cfg.set("autoBroadcast.delay", autoBroadcastDelay);
        cfg.set("breakCounter", breakCounter);
        cfg.set("placeCounter", placeCounter);
        cfg.set("destroyEventGoal", destroyEventGoal);
        cfg.set("baseMoneyKillstreak", baseMoneyKillstreak);
        cfg.set("baseXPkillstreak", baseXPkillstreak);
        cfg.set("baseEloKillstreak", baseEloKillstreak);
        cfg.set("levelUpMoney", levelUpMoney);
        cfg.set("netherprice", netherPrice);
        cfg.set("endprice", endPrice);
        cfg.set("soulReward", soulReward);
        cfg.set("epCooldown", epCooldown);
        cfg.set("netherLevel", netherLevel);
        cfg.set("endLevel", endLevel);
        cfg.set("clanPrice", clanPrice);

        if (domainJoins.size() > 0) {
            domainJoins.keySet().forEach(domain -> cfg.set("domains." + domain, domainJoins.get(domain)));
        } else {
            cfg.set("domains.45,142,114,29", 0);
        }

        if (!respawnKitItems.isEmpty()) {
            respawnKitItems.values().forEach(respawnKitItem -> respawnKitItem.getLevelItems().keySet().forEach(level -> {
                cfg.set("respawnkit." + respawnKitItem.getType() + "." + level + ".item", respawnKitItem.getItemForLevel(level));
                cfg.set("respawnkit." + respawnKitItem.getType() + "." + level + ".price", respawnKitItem.getPriceForLevel(level));
            }));
        }

        FileUtil.saveToFile(configFile, cfg);
    }

    private void loadRespawnKitItems() {
        for (int i = 1; i <= 8; i++) {
            respawnKitItems.put(i, new RespawnKitItem(i));
            Bukkit.getConsoleSender().sendMessage("§a§oLoaded respawnkit type " + i);
        }
    }

    public int addAndGetPlayerCount() {
        playerCount++;
        return playerCount;
    }

    public void checkDailyCount() {
        if (Bukkit.getOnlinePlayers().size() > dailycount)
            dailycount = Bukkit.getOnlinePlayers().size();

        if (System.currentTimeMillis() - dailyMillisCount >= 86400000L) {
            dailycount = Bukkit.getOnlinePlayers().size();
            dailyMillisCount = System.currentTimeMillis() + 86400000L;
        }
    }

    public int getDailyCount() {
        return dailycount;
    }

    public void checkLoginDomain(final String domain) {
        if (domainJoins.containsKey(domain)) {
            domainJoins.put(domain, domainJoins.get(domain) + 1);
            return;
        }

        domainJoins.put(domain, 1);
    }

    public void vote(final Player player) {
        votes++;

        final ArrayList<ItemStack> items = cfg.contains("voteitems") ? (ArrayList<ItemStack>) cfg.getList("voteitems") : new ArrayList<>();

        if(items.isEmpty())
            return;

        for (final ItemStack item : items)
            InventoryUtil.addItem(player, item);
    }
}
