package de.obey.crownmc.handler;

import com.google.common.collect.Maps;
import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.Backend;
import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.objects.Clan;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/*

    Author - Obey -> CrownMc
       18.06.2023 / 15:38

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

@RequiredArgsConstructor @NonNull
public final class ClanHandler {

    private final MessageUtil messageUtil;
    private final Backend backend;
    private final ExecutorService executorService;
    private final UserHandler userHandler;
    private final ServerConfig serverConfig;

    private final String regex = "[a-zA-Z0-9]+";

    private Inventory noClanInventory;

    @Getter
    private final Map<String, Clan> clanCache = Maps.newConcurrentMap();
    @Getter
    private final Map<String, String> clanTagsAndNames = Maps.newConcurrentMap();

    @Getter
    private final ArrayList<UUID> creatingClan = new ArrayList<>();

    private int intervalTicked = 0;
    public void runInterval() {
        intervalTicked++;

        if (intervalTicked == 180) { // runs every 90 seconds
            intervalTicked = 0;

            clanCache.values().forEach(this::saveData);
        }
    }

    public boolean isCreatingClan(final Player player, final String message) {

        if(!creatingClan.contains(player.getUniqueId()))
            return false;

        if(message.equalsIgnoreCase("cancel")) {
            creatingClan.remove(player.getUniqueId());
            return true;
        }

        final String[] data = message.split(" ");

        if (data.length != 2) {
            messageUtil.sendMessage(player, "Schreibe den Clannamen und den Clantag in den Chat§8.");
            messageUtil.sendMessage(player, "Nutze folgendes Format§8: §fclanname clantag");
            messageUtil.sendMessage(player, "Schreibe §c§ocancel§7 um den Vorgang abzubrechen§8.");
            return true;
        }

        if(createClan(player, data[0], data[1])) {
            creatingClan.remove(player.getUniqueId());
            return true;
        }

        messageUtil.sendMessage(player, "Schreibe §c§ocancel§7 um den Vorgang abzubrechen§8.");

        return true;
    }

    public void openClanCommandInventory(final Player player) {
        if (!isInClan(player)) {
            // is not in clan
            if (noClanInventory == null) {
                noClanInventory = Bukkit.createInventory(null, 9 * 5, "§c§oKein Clan");
                noClanInventory.setItem(13, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§a§oClan erstellen")
                        .setLore("",
                                "§f§lInformation",
                                "§8  - §7Preis§8: §e§o" + messageUtil.formatLong(serverConfig.getClanPrice()) + "§6§l$",
                                "")
                        .build());
            }

            player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.1f, 1);
            player.openInventory(noClanInventory);

            return;
        }
        // is in clan

        userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
            player.openInventory(user.getClan().getClanInfo());
            player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.1f, 1);
        });

    }

    public boolean existsTag(final String tag) {
        if(clanCache.isEmpty())
            return false;

        for (final Clan value : clanCache.values()) {
            return value.getClanTag().equalsIgnoreCase(tag);
        }

        return false;
    }

    public boolean exists(final String clanName) {
        return new File(CrownMain.getInstance().getDataFolder() + "/clanFiles/" + clanName + ".yml").exists();
    }

    public boolean isInClan(final Player player) {
        return userHandler.getUserInstant(player.getUniqueId()).getClan() != null;
    }

    public boolean createClan(final Player player, final String clanName, final String clanTag) {
        if(isInClan(player)) {
            messageUtil.sendMessage(player, "Du bist bereits in einem Clan§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.1f, 1);
            return false;
        }

        if(!clanName.matches(regex) || clanName.length() < 3 || clanName.length() > 8) {
            messageUtil.sendMessage(player, "ClanName " + clanName + " ist ungültig§8.");
            messageUtil.sendMessage(player, "Der Clanname darf nur aus Buchstaben und Zahlen bestehen und muss länger als 2 und kürzer als 9 sein§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.1f, 1);
            return false;
        }

        if(!clanTag.matches(regex) || clanTag.length() < 2 || clanName.length() > 3) {
            messageUtil.sendMessage(player, "Clantag " + clanTag + " ist ungültig§8.");
            messageUtil.sendMessage(player, "Der Clantag darf nur aus Buchstaben und Zahlen bestehen und muss länger als 1 und kürzer als 4 sein§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.1f, 1);
            return false;
        }

        if(exists(clanName)) {
            messageUtil.sendMessage(player, "Es existiert bereits ein Clan mit dem Namen " + clanName + "§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.1f, 1);
            return false;
        }

        if(existsTag(clanTag)) {
            messageUtil.sendMessage(player, "Es existiert bereits ein Clan mit dem Tag " + clanTag + "§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.1f, 1);
            return false;
        }

        final User user = userHandler.getUserInstant(player.getUniqueId());

        if(!messageUtil.hasEnougthMoney(user, (long) serverConfig.getClanPrice()))
            return false;

        FileUtil.getFile("/clanFiles/" + clanName + ".yml");

        messageUtil.sendMessage(player, "Dein Clan wird erstellt §8...");
        user.removeLong(DataType.MONEY, serverConfig.getClanPrice());

        executorService.submit(() -> {
           backend.execute("INSERT INTO clans(name, leader, trophies, kills, deaths) " +
                   "VALUES ('" + clanName + "', " +
                   "'" + player.getUniqueId().toString() + "', " +
                   "'0', " +
                   "'0', " +
                   "'0')");
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                final Clan clan = new Clan(clanName);

                clan.setTrophies(0);
                clan.setKills(0);
                clan.setDeaths(0);
                clan.setOwnerUUID(player.getUniqueId());
                clan.setClanTag(clanTag);
                clan.getMemberList().add(player.getUniqueId().toString());
                clan.saveFileData();
                clan.loadFileData();
                user.setClan(clan);

                clanCache.put(clanName, clan);
                messageUtil.sendMessage(player, "Dein Clan wurde erfolgreich erstellt§8.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);
            }
        }.runTaskLater(CrownMain.getInstance(), 20*2);

        return true;
    }

    public CompletableFuture<Clan> loadData(final String clanName) {
        return CompletableFuture.supplyAsync(() -> {
            if(clanCache.containsKey(clanName.toLowerCase()))
                return clanCache.get(clanName.toLowerCase());

            if(!exists(clanName))
                return null;

            final Clan clan = new Clan(clanName);
            final ResultSet results = backend.getResultSet("SELECT * FROM clans WHERE name='" + clanName + "'");

            clan.loadFileData();

            clanTagsAndNames.put(clan.getClanTag(), clanName);

            if (results == null) {
                messageUtil.warn("§c§o failed to load mysql clan " + clanName);
                return clan;
            }

            try {
                if(results.next()) {
                    clan.setKills(results.getInt("kills"));
                    clan.setDeaths(results.getInt("deaths"));
                    clan.setTrophies(results.getInt("trophies"));
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }

            clanCache.put(clanName.toLowerCase(), clan);

            return clan;
        });
    }

    public void saveData(final Clan clan) {
        executorService.submit(() -> {
            clan.saveFileData();

            backend.execute("UPDATE clans SET " +
                    "kills='" + clan.getKills() + "', " +
                    "deaths='" + clan.getDeaths() + "', " +
                    "trophies='" + clan.getTrophies() + "' " +
                    "WHERE name='" + clan.getClanName() + "';");
        });
    }

}
