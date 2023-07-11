package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       03.07.2023 / 12:51

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.objects.gambling.RouletteTable;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public final class RouletteHandler {

    @NonNull
    private final LocationHandler locationHandler;
    @NonNull
    private final MessageUtil messageUtil;
    @NonNull
    private final UserHandler userHandler;

    @Getter
    private final HashMap<Integer, RouletteTable> tables = new HashMap<>();

    @Getter
    private final String prefix = "§8( §f§lRoulette §8)§7 ";

    public void respawnTables() {
        if(tables.isEmpty())
            return;

        tables.values().forEach(table -> {
            table.setLocation(locationHandler.getLocation("roulette-" + table.getTableID()));
            table.loadLocations();
            table.killStands();
            table.spawnStands();
        });
    }

    public void loadTables() {
        for (int i = 1; i < 5; i++) {
            final Location temp = locationHandler.getLocation("roulette-" + i);

            if(temp == null)
                break;

            tables.put(i, new RouletteTable(i, temp.clone(), this));
        }
    }

    public void createNewTable(final int id, final Location location) {
        locationHandler.setLocation("roulette-" + id, location);
        tables.put(id, new RouletteTable(id, location, this));
    }

    public void deleteTable(final int id) {
        locationHandler.deleteLocation("roulette-" + id);
        tables.get(id).shutdown();
        tables.remove(id);
    }

    public RouletteTable getTable(final int id) {
        return tables.get(id);
    }

    public final HashMap<Player, String> joiningRoulette = new HashMap<>();
    public final HashMap<Player, Integer> joinedTable = new HashMap<>();
    public boolean isJoiningRoulette(final Player player, final String text) {

        final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

        if (joiningRoulette.containsKey(player)) {

            if (text.equalsIgnoreCase("cancel")) {
                joiningRoulette.remove(player);
                messageUtil.sendMessage(player, "Vorgang wurde abgebrochen§8.");
                return true;
            }

            long amount = 0L;

            try {
                amount = Long.parseLong(text);

                if (amount < 100) {
                    messageUtil.sendMessage(player, "Der Wetteinsatz muss mind. 100$ hoch sein§8.");
                    return true;
                }

                final long finalAmount = amount;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        joinRoulette(joinedTable.get(player), player, finalAmount, joiningRoulette.get(player));
                    }
                }.runTask(CrownMain.getInstance());

            } catch (final NumberFormatException exception) {

                amount = MathUtil.getLongFromStringwithSuffix(text);

                if (amount <= 0) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen§8. (§7k, m, mrd, b, brd, t§8)");
                    messageUtil.sendMessage(player, "Um abzubrechen schreibe §c§ocancel§8.");
                    return true;
                }

                if (amount < 100) {
                    messageUtil.sendMessage(player, "Der Wetteinsatz muss mind. 100$ hoch sein§8.");
                    return true;
                }

                final long finalAmount = amount;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        joinRoulette(joinedTable.get(player), player, finalAmount, joiningRoulette.get(player));
                    }
                }.runTask(CrownMain.getInstance());
            }

            return true;
        }

        return false;
    }

    public void joinRoulette(final int id, final Player player, final long amount, final String color) {
        userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

            if(!messageUtil.hasEnougthMoney(user, amount))
                return;

            joiningRoulette.remove(player);
            joinedTable.remove(player);

            final RouletteTable table = getTable(id);

            if(table == null) {
                messageUtil.sendMessage(player, prefix +"Dieser Tisch wurde nich nicht eingestellt§8.");
                return;
            }

            if(table.getState() != 0 && table.getState() != 1) {
                messageUtil.sendMessage(player, prefix + "Bitte warte bis die aktuelle Runde zuende gespielt wurde§8.");
                return;
            }

            if(table.getBetAmounts().containsKey(player)) {
                messageUtil.sendMessage(player, prefix + "Du spielst bereis an diesem Tisch§8. §7Gewettet hast du auf " + getPrefixFromColor(table.getBetColors().get(player)));
                return;
            }

            user.removeLong(DataType.MONEY, amount);
            messageUtil.sendMessage(player, prefix + "Du setzt §e§o" + messageUtil.formatLong(amount) + "§6§l$§7 auf " + getPrefixFromColor(color) + "§8.");

            new BukkitRunnable() {
                @Override
                public void run() {
                    table.join(player, amount, color);
                }
            }.runTask(CrownMain.getInstance());
        });

    }

    public void pay(final UUID uuid, final long amount) {
        userHandler.getUser(uuid).thenAcceptAsync(user -> user.addLong(DataType.MONEY, amount));
    }

    public void openTable(final int id, final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 9*5, "§f§lTisch§7 " + id);

        InventoryUtil.fillSideRows(inventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());

        inventory.setItem(13, new ItemBuilder(Material.ITEM_FRAME)
                        .setDisplayname("§f§lInformation")
                        .setLore("",
                                "§8 » §7Setzte dein Geld auf eine der 3 Farben indem du",
                                "§7  auf ein der Unten angezeigten Items klickst§8.")
                .build());


        //red
        inventory.setItem(29, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§c§lRot")
                        .setLore("",
                                "§c§lInformation",
                                "§8  - §7Multiplikator§8:§f " + getMultiplier("red") + "x",
                                "§8  - §7Chance§8: §f50%")
                        .setTextur("Njk1M2IxMmEwOTQ2YjYyOWI0YzA4ODlkNDFmZDI2ZWQyNmZiNzI5ZDRkNTE0YjU5NzI3MTI0YzM3YmI3MGQ4ZCJ9fX0=", UUID.randomUUID())
                .build());

        // black
        inventory.setItem(31, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setDisplayname("§0§lSchwarz")
                .setLore("",
                        "§0§lInformation",
                        "§8  - §7Multiplikator§8:§f " + getMultiplier("black") + "x",
                        "§8  - §7Chance§8: §f41.6%")
                .setTextur("Y2ZhNGRkYTZkMTlhMWZlMmQ5ODhkNjVkZWM1MzQyOTUwNTMwODE2NmM5MDY3YjY4YTQ3NzBjYTVjNDM2Y2Y5NCJ9fX0=", UUID.randomUUID())
                .build());

        // green
        inventory.setItem(33, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setDisplayname("§a§lGrün")
                .setLore("",
                        "§a§lInformation",
                        "§8  - §7Multiplikator§8:§f " + getMultiplier("green") + "x",
                        "§8  - §7Chance§8: §f8.4%")
                .setTextur("Nzc0NzJkNjA4ODIxZjQ1YTg4MDUzNzZlYzBjNmZmY2I3ODExNzgyOWVhNWY5NjAwNDFjMmEwOWQxMGUwNGNiNCJ9fX0=", UUID.randomUUID())
                .build());

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
    }

    public double getMultiplier(final String color) {
        if (color.equalsIgnoreCase("red"))
            return 1.5;

        if(color.equalsIgnoreCase("black"))
            return 1.8;

        if(color.equalsIgnoreCase("green"))
            return 2.5;

        return 1;
    }

    public String getPrefixFromColor(final String color) {
        if (color.equalsIgnoreCase("green"))
            return "§a§lGrün";

        if(color.equalsIgnoreCase("red"))
            return "§c§lRot";

        return "§0§lSchwarz";
    }

    public void shutdown() {
        if(tables.isEmpty())
            return;

        tables.values().forEach(RouletteTable::shutdown);
    }

}
