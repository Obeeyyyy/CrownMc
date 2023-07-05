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
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

@RequiredArgsConstructor
public final class RouletteHandler {

    @NonNull
    private final LocationHandler locationHandler;
    @NonNull
    private final MessageUtil messageUtil;
    @NonNull
    private final UserHandler userHandler;

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

    public void joinRoulette(final int id, final Player player, final long amount, final String color) {
        userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

            if(!messageUtil.hasEnougthMoney(user, amount))
                return;

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
