package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       25.10.2022 / 23:34

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.Backend;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MathUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public final class RankingHandler {

    @NonNull
    private final Initializer initializer;

    private final HashMap<String, Inventory> inventories = new HashMap<>();

    public Inventory getInventory(final String name) {
        return inventories.get(name);
    }

    public void startUpdater() {

        final Inventory kills = inventories.containsKey("kills") ? inventories.get("kills") : Bukkit.createInventory(null, 9 * 6, "§9§lRanking §c§oKills");
        inventories.put("kills", kills);
        fill(kills);

        final Inventory money = inventories.containsKey("money") ? inventories.get("money") : Bukkit.createInventory(null, 9 * 6, "§9§lRanking §c§oBalance");
        inventories.put("money", money);
        fill(money);

        final Inventory killtreak = inventories.containsKey("killstreak") ? inventories.get("killstreak") : Bukkit.createInventory(null, 9 * 6, "§9§lRanking §c§oKillstreaks");
        inventories.put("killstreak", killtreak);
        fill(killtreak);

        final Inventory elopoints = inventories.containsKey("elopoints") ? inventories.get("elopoints") : Bukkit.createInventory(null, 9 * 6, "§9§lRanking §c§oElopunkte");
        inventories.put("elopoints", elopoints);
        fill(elopoints);

        final Inventory xp = inventories.containsKey("xp") ? inventories.get("xp") : Bukkit.createInventory(null, 9 * 6, "§9§lRanking §c§oXp");
        inventories.put("xp", xp);
        fill(xp);

        final Inventory votes = inventories.containsKey("votes") ? inventories.get("votes") : Bukkit.createInventory(null, 9 * 6, "§9§lRanking §c§oVotes");
        inventories.put("votes", votes);
        fill(votes);

        final Inventory playtime = inventories.containsKey("playtime") ? inventories.get("playtime") : Bukkit.createInventory(null, 9 * 6, "§9§lRanking §c§oSpielzeit");
        inventories.put("playtime", playtime);
        fill(playtime);

        final Inventory crowns = inventories.containsKey("crowns") ? inventories.get("crowns") : Bukkit.createInventory(null, 9 * 6, "§9§lRanking §6§lCrowns");
        inventories.put("crowns", crowns);
        fill(crowns);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (initializer.isRestarting()) {
                    cancel();
                    return;
                }

                updateInventories();
            }
        }.runTaskTimer(CrownMain.getInstance(), 20 * 3, 20 * 60);
    }

    public void updateInventories() {
        updateDataForInventory("kills");
        updateDataForInventory("money");
        updateDataForInventory("killstreak");
        updateDataForInventory("elopoints");
        updateDataForInventory("votes");
        updateDataForInventory("xp");
        updateDataForInventory("playtime");
        updateDataForInventory("crowns");
    }

    private void fill(final Inventory inventory) {
        InventoryUtil.fill(inventory, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setDisplayname("§7-§8/§7-").build());

        final ItemStack bar = new ItemBuilder(Material.IRON_FENCE, 1, (byte) 0).setDisplayname("§7-§8/§7-").build();

        InventoryUtil.fillSideRows(inventory, bar);

        inventory.setItem(4, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("YTkzZGZiM2FlODE3Nzc4NGE2NDU3NzlkY2EyYzEyZGZiYTEyNThjMjAyYWZkYzA0ZDg3ZDgwZjJjZWNlYTFkNyJ9fX0=", UUID.randomUUID())
                .setDisplayname("§7Top 10 §8(§6§lCrowns§8)").build());

        inventory.setItem(10, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZjAwZTdiMzNlZTJhNjAwMjc1OGFjZmUwOGM3ZGY2YmQzN2E0OTdkYzlmODAwMGMzY2E5ODI0YTJjZmFiY2FkZCJ9fX0=", UUID.randomUUID())
                .setDisplayname("§7Top 10 §8(§4§lKills§8)").build());

        inventory.setItem(11, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("YTg4MzJjMTQ2NmM4NDFjYzc5ZDVmMTAyOTVkNDY0Mjc5OTY3OTc1YTI0NTFjN2E1MzNjNzk5Njg5NzQwOGJlYSJ9fX0=", UUID.randomUUID())
                .setDisplayname("§7Top 10 §8(§9§lKillstreak Rekorde§8)").build());

        inventory.setItem(12, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("NGZkOWNiYmU0ZThmNzZmMDk0MGZmNzljYWIwZDg3NWMxYmNiOWRjMzhhM2Y1MjIxMzU4Njc3ZjUyMTJjYmMwIn19fQ==", UUID.randomUUID())
                .setDisplayname("§7Top 10 §8(§d§lElopunkte§8)").build());

        inventory.setItem(13, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("MjA5Mjk5YTExN2JlZTg4ZDMyNjJmNmFiOTgyMTFmYmEzNDRlY2FlMzliNDdlYzg0ODEyOTcwNmRlZGM4MWU0ZiJ9fX0=", UUID.randomUUID())
                .setDisplayname("§7Top 10 §8(§6§lBalance§8)").build());

        inventory.setItem(14, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZTEzODM1MmY0NzQ1ZTAyYzA5MzkxNDZkYmQzNjZlNjUzNWE3ZjRlZjM5NjUzMDA5YjVjMzljMjRiOTRkNGNhNyJ9fX0=", UUID.randomUUID())
                .setDisplayname("§7Top 10 §8(§d§lXP§8)").build());

        inventory.setItem(15, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("OTk2MGQ2ZmZhZjQ0ZThhZmNiZGY4YjI5YTc3ZDg0Y2UyMmM3MWQwMGM2NGJmZDk5YWYzNDBhNjk1MzViZmQ3In19fQ==", UUID.randomUUID())
                .setDisplayname("§7Top 10 §8(§2§lVotes§8)").build());

        inventory.setItem(16, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("M2NhMWE0OGQyZDIzMWZhNzFiYTVmN2M0MGZkYzEwZDNmMmU5OGM1YTYzYzAxNzMyMWU2NzgxMzA4YjhhNTc5MyJ9fX0=", UUID.randomUUID())
                .setDisplayname("§7Top 10 §8(§f§lPlaytime§8)").build());
    }

    private void updateDataForInventory(final String what) {

        final Backend backend = initializer.getBackend();

        initializer.getExecutorService().submit(() -> {
            try {
                if (what.equals("money")) {
                    final Inventory inventory = inventories.get("money");
                    final HashMap<Integer, UUID> ranking = new HashMap<>();
                    final ResultSet resultSet = backend.getResultSet("SELECT uuid FROM users ORDER BY money DESC LIMIT 11");

                    if (resultSet == null)
                        return;


                    int rank = 1;

                    try {
                        while (resultSet.next()) {
                            ranking.put(rank, UUID.fromString(resultSet.getString("uuid")));
                            rank++;
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    for (int i = 1; i < 11; i++)
                        setPlayerHead(ranking.size() >= i ? ranking.get(i) : null, i, what, inventory, getSlotFromRank(i));


                    return;
                }

                if (what.equals("kills")) {
                    final Inventory inventory = inventories.get("kills");
                    final HashMap<Integer, UUID> ranking = new HashMap<>();
                    final ResultSet resultSet = backend.getResultSet("SELECT uuid FROM users ORDER BY kills DESC LIMIT 11");

                    if (resultSet == null)
                        return;


                    int rank = 1;
                    try {
                        while (resultSet.next()) {
                            ranking.put(rank, UUID.fromString(resultSet.getString("uuid")));
                            rank++;
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    for (int i = 1; i < 11; i++)
                        setPlayerHead(ranking.size() >= i ? ranking.get(i) : null, i, what, inventory, getSlotFromRank(i));


                    return;
                }

                if (what.equals("elopoints")) {
                    final Inventory inventory = inventories.get("elopoints");
                    final HashMap<Integer, UUID> ranking = new HashMap<>();
                    final ResultSet resultSet = backend.getResultSet("SELECT uuid FROM users ORDER BY elopoints DESC LIMIT 11");

                    if (resultSet == null)
                        return;


                    int rank = 1;

                    try {
                        while (resultSet.next()) {
                            ranking.put(rank, UUID.fromString(resultSet.getString("uuid")));
                            rank++;
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    for (int i = 1; i < 11; i++)
                        setPlayerHead(ranking.size() >= i ? ranking.get(i) : null, i, what, inventory, getSlotFromRank(i));


                    return;
                }

                if (what.equals("killstreak")) {
                    final Inventory inventory = inventories.get("killstreak");
                    final HashMap<Integer, UUID> ranking = new HashMap<>();
                    final ResultSet resultSet = backend.getResultSet("SELECT uuid FROM users ORDER BY killstreakrecord DESC LIMIT 11");

                    if (resultSet == null)
                        return;


                    int rank = 1;

                    try {
                        while (resultSet.next()) {
                            ranking.put(rank, UUID.fromString(resultSet.getString("uuid")));
                            rank++;
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    for (int i = 1; i < 11; i++)
                        setPlayerHead(ranking.size() >= i ? ranking.get(i) : null, i, what, inventory, getSlotFromRank(i));


                    return;
                }

                if (what.equals("votes")) {
                    final Inventory inventory = inventories.get("votes");
                    final HashMap<Integer, UUID> ranking = new HashMap<>();
                    final ResultSet resultSet = backend.getResultSet("SELECT uuid FROM users ORDER BY votes DESC LIMIT 11");

                    if (resultSet == null)
                        return;


                    int rank = 1;

                    try {
                        while (resultSet.next()) {
                            ranking.put(rank, UUID.fromString(resultSet.getString("uuid")));
                            rank++;
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    for (int i = 1; i < 11; i++)
                        setPlayerHead(ranking.size() >= i ? ranking.get(i) : null, i, what, inventory, getSlotFromRank(i));


                    return;
                }

                if (what.equals("xp")) {
                    final Inventory inventory = inventories.get("xp");
                    final HashMap<Integer, UUID> ranking = new HashMap<>();
                    final ResultSet resultSet = backend.getResultSet("SELECT uuid FROM users ORDER BY xp DESC LIMIT 11");

                    if (resultSet == null)
                        return;


                    int rank = 1;

                    try {
                        while (resultSet.next()) {
                            ranking.put(rank, UUID.fromString(resultSet.getString("uuid")));
                            rank++;
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    for (int i = 1; i < 11; i++)
                        setPlayerHead(ranking.size() >= i ? ranking.get(i) : null, i, what, inventory, getSlotFromRank(i));

                    return;
                }

                if (what.equals("playtime")) {
                    final Inventory inventory = inventories.get("playtime");
                    final HashMap<Integer, UUID> ranking = new HashMap<>();
                    final ResultSet resultSet = backend.getResultSet("SELECT uuid FROM users ORDER BY playtime DESC LIMIT 11");

                    if (resultSet == null)
                        return;

                    int rank = 1;

                    try {
                        while (resultSet.next()) {
                            ranking.put(rank, UUID.fromString(resultSet.getString("uuid")));
                            rank++;
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    for (int i = 1; i < 11; i++)
                        setPlayerHead(ranking.size() >= i ? ranking.get(i) : null, i, what, inventory, getSlotFromRank(i));

                    return;
                }

                if (what.equals("crowns")) {
                    final Inventory inventory = inventories.get("crowns");
                    final HashMap<Integer, UUID> ranking = new HashMap<>();
                    final ResultSet resultSet = backend.getResultSet("SELECT uuid FROM users ORDER BY crowns DESC LIMIT 11");

                    if (resultSet == null)
                        return;

                    int rank = 1;

                    try {
                        while (resultSet.next()) {
                            ranking.put(rank, UUID.fromString(resultSet.getString("uuid")));
                            rank++;
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    for (int i = 1; i < 11; i++)
                        setPlayerHead(ranking.size() >= i ? ranking.get(i) : null, i, what, inventory, getSlotFromRank(i));

                }

            } catch (final NullPointerException exception) {
                exception.printStackTrace();
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        updateDataForInventory(what);
                    }
                }.runTaskLater(CrownMain.getInstance(), 20);
            }
        });
    }

    private void setPlayerHead(final UUID uuid, final int rank, final String what, final Inventory inventory, final int slot) {
        if (uuid == null) {
            inventory.setItem(slot, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setDisplayname("§7 ??? §8(§a#" + rank + "§8)").setTextur("YmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19=", UUID.randomUUID()).build());
            return;
        }

        final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (!initializer.getUserHandler().isRegistered(player)) {
            inventory.setItem(slot, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setDisplayname("§7 ??? §8(§a#" + rank + "§8)").setTextur("YmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19=", UUID.randomUUID()).build());
            return;
        }

        initializer.getUserHandler().getUser(uuid).thenAcceptAsync(user -> {

            user.setUsedForRanking(true);

            if (what.equals("money")) {
                inventory.setItem(slot, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§7" + player.getName() + "§8 (§a#" + rank + "§8)")
                        .setLore("§7", "§8  »§6 Information§8:", "§8    ×§7 Balance§8: §e" + initializer.getMessageUtil().formatLong(user.getLong(DataType.MONEY)) + "§6$")
                        .setSkullOwner(player.getName())
                        .build());

                return;
            }

            if (what.equals("kills")) {
                inventory.setItem(slot, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§7" + player.getName() + "§8 (§a#" + rank + "§8)")
                        .setLore("§7", "§8  »§6 Information§8:", "§8    ×§7 Kills§8: §e" + initializer.getMessageUtil().formatLong(user.getLong(DataType.KILLS)))
                        .setSkullOwner(player.getName())
                        .build());

                return;
            }

            if (what.equals("elopoints")) {
                final long elopunkte = user.getLong(DataType.ELOPOINTS);
                inventory.setItem(slot, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§7" + player.getName() + "§8 (§a#" + rank + "§8)")
                        .setLore("§7", "§8  »§6 Information§8:", "§8    ×§7 Elopunkte§8: §e" + initializer.getMessageUtil().formatLong(elopunkte), "§8    ×§7 Elorang§8: §r" + initializer.getEloHandler().getEloRangFromEloPoints(user.getLong(DataType.ELOPOINTS)))
                        .setSkullOwner(player.getName())
                        .build());

                return;
            }

            if (what.equals("killstreak")) {
                inventory.setItem(slot, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§7" + player.getName() + "§8 (§a#" + rank + "§8)")
                        .setLore("§7", "§8  »§6 Information§8:", "§8    ×§7 Höchste Killstreak§8: §e" + initializer.getMessageUtil().formatLong(user.getLong(DataType.KILLSTREAKRECORD)))
                        .setSkullOwner(player.getName())
                        .build());

                return;
            }

            if (what.equals("votes")) {
                final long votes = user.getLong(DataType.VOTES);
                inventory.setItem(slot, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§7" + player.getName() + "§8 (§a#" + rank + "§8)")
                        .setLore("§7", "§8  »§6 Information§8:", "§8    ×§7 Votes§8: §e" + initializer.getMessageUtil().formatLong(votes))
                        .setSkullOwner(player.getName())
                        .build());

                return;
            }

            if (what.equals("xp")) {
                final long xp = user.getLong(DataType.XP);
                inventory.setItem(slot, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§7" + player.getName() + "§8 (§a#" + rank + "§8)")
                        .setLore("§7", "§8  »§6 Information§8:", "§8    ×§7 XP§8: §e" + initializer.getMessageUtil().formatLong(xp))
                        .setSkullOwner(player.getName())
                        .build());

                return;
            }

            if (what.equals("playtime")) {
                inventory.setItem(slot, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§7" + player.getName() + "§8 (§a#" + rank + "§8)")
                        .setLore("§7", "§8  »§6 Information§8:", "§8    ×§7 Spielzeit§8: §e" + MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds(user.getPlaytime().getCurrentPlaytime()))
                        .setSkullOwner(player.getName())
                        .build());

                return;
            }

            if (what.equals("crowns")) {
                inventory.setItem(slot, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§7" + player.getName() + "§8 (§a#" + rank + "§8)")
                        .setLore("§7", "§8  »§6 Information§8:", "§8    ×§7 Crowns§8: §e" + initializer.getMessageUtil().formatLong(user.getLong(DataType.CROWNS)) + "§8.")
                        .setSkullOwner(player.getName())
                        .build());
            }

        });
    }

    private int getSlotFromRank(int rank) {
        if (rank == 1)
            return 29;

        if (rank == 2)
            return 30;

        if (rank == 3)
            return 31;

        if (rank == 4)
            return 32;

        if (rank == 5)
            return 33;

        if (rank == 6)
            return 38;

        if (rank == 7)
            return 39;

        if (rank == 8)
            return 40;

        if (rank == 9)
            return 41;

        if (rank == 10)
            return 42;

        return 0;
    }

}