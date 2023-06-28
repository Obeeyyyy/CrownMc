// Made by Richard


package de.obey.crownmc.handler;

import de.obey.crownmc.Initializer;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.util.ArmorStandBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@RequiredArgsConstructor
public class BlockEventHandler {

    @NonNull
    private final Initializer initializer;

    private ArmorStandBuilder standBuilder;

    public void setupArmorStands() {
        clearStands();
        setStands();
        updateStands();
    }

    private void clearStands() {
        if (standBuilder != null)
            standBuilder.stands().forEach(Entity::remove);

        for (final World world : Bukkit.getWorlds()) {
            for (final Entity entity : world.getEntities()) {
                if(entity instanceof Item && ((Item) entity).getPickupDelay() >= 20000) {
                    entity.remove();
                    continue;
                }

                if (entity instanceof ArmorStand && entity.getCustomName() != null) {
                    if (entity.getCustomName().equalsIgnoreCase("§8▰§7▱ §a§lBlockEvent§7 ▱§8▰"))
                        entity.remove();

                    if (entity.getCustomName().equalsIgnoreCase("???"))
                        entity.remove();

                    if (entity.getCustomName().startsWith("§8┃» §7Aktueller Block§8:§7 "))
                        entity.remove();

                    if (entity.getCustomName().startsWith("§8┃» §7Platz§8.§a§l"))
                        entity.remove();
                }
            }
        }
    }

    public void resetCounts() {
        initializer.getMySQL().execute("UPDATE users SET destroyedEventBlocks = '0'");
        initializer.getUserHandler().getUserCache().values().forEach(user -> {
            user.setLong(DataType.DESTROYEDEVENTBLOCKS, 0);
        });
    }

    public void setStands() {
        if (initializer.getLocationHandler().getLocation("blockevent") == null)
            return;

        final Location location = initializer.getLocationHandler().getLocation("blockevent").clone();

        if (location == null)
            return;

        standBuilder = new ArmorStandBuilder(location)
                .addStand(1)

                .setCustomName(1, "§8▰§7▱ §a§lBlockEvent§7 ▱§8▰")
                .setItem(1, Material.getMaterial(initializer.getServerConfig().getDestroyEventGoal()))

                .addLocation(0, -0.75, 0)
                .addStand(9)

                .setCustomName(4, "§8┃» §7Aktueller Block§8:§7 " +
                        initializer.getServerConfig().getDestroyEventGoal().replace('_', ' '))

                .setCustomName(5, "???").setCustomNameVisible(5, false)
                .setCustomName(6, "§8┃» §7Platz§8.§a§l1 §8:§7 §c§oniemand")
                .setCustomName(7, "§8┃» §7Platz§8.§a§l2 §8:§7 §c§oniemand")
                .setCustomName(8, "§8┃» §7Platz§8.§a§l3 §8:§7 §c§oniemand")
                .setCustomName(9, "§8┃» §7Platz§8.§a§l4 §8:§7 §c§oniemand")
                .setCustomName(10, "§8┃» §7Platz§8.§a§l5 §8:§7 §c§oniemand");

    }

    public void updateStands() {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (standBuilder == null)
                    setStands();

                determineTopList();
            }
        }.runTaskTimer(initializer.getCrownMain(), 20, 30 * 20);
    }

    private void determineTopList() {
        if (standBuilder == null) return;
        
        initializer.getExecutorService().submit(() -> {
            final ResultSet resultSet = initializer.getMySQL().getResultSet("SELECT uuid, destroyedEventBlocks FROM users ORDER BY destroyedEventBlocks DESC LIMIT 5");
            UUID uuid;

            if (resultSet == null)
                return;

            try {
                int rank = 1;
                while (resultSet.next()) {
                    uuid = UUID.fromString(resultSet.getString("uuid"));

                    final int finalRank = rank;

                    initializer.getUserHandler().getUser(uuid).thenAcceptAsync(user -> {
                        standBuilder.setCustomName(5 + finalRank, "§8┃» §7Platz§8.§a§l" + finalRank + " §8:§7 " + user.getOfflinePlayer().getName() + "§8 (§fx§a" + initializer.getMessageUtil().formatLong(user.getLong(DataType.DESTROYEDEVENTBLOCKS)) + " §7Blöcke§8)");
                    });

                    rank++;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void setEventBlock(final String material) {
        initializer.getServerConfig().setDestroyEventGoal(material);
        standBuilder.setCustomName(4, "§8┃» §7Aktueller Block§8:§7 " + initializer.getServerConfig().getDestroyEventGoal().replace('_', ' '));
    }
}
