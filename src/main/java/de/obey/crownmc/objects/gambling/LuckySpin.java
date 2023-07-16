package de.obey.crownmc.objects.gambling;
/*

    Author - Obey -> CrownMc
       15.06.2023 / 02:20

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.LuckySpinHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public final class LuckySpin {

    private final LuckySpinHandler luckySpinHandler;
    private final String identifier = "§6";

    @Getter
    private int state = 0; // 0 = free - 1 = spinning

    @Setter
    private Location location;

    private BukkitTask runnable;
    private Player player;

    private final HashMap<Integer, ArmorStand> defaultStands = new HashMap<>();
    private final HashMap<Integer, ArmorStand> animationStands = new HashMap<>();
    private final HashMap<Integer, Location> locations = new HashMap<>();

    public LuckySpin(final LuckySpinHandler luckySpinHandler, final Location location) {
        this.luckySpinHandler = luckySpinHandler;
        this.location = location;

        removeAllCreatedEntities();
        setup();
    }

    public void setup() {
        state = 0;

        loadLocations();
        spawnDefaultStands();
        spawnMarkers();
        spawnItemWall();
    }

    private void loadLocations() {
        /*
            1 2 3
           12    4
          11      5
           10    6
            9 8 7
         */

        locations.put(1, location.clone().add(-1, 2.5, 0));
        locations.put(2, location.clone().add(0, 2.75, 0));
        locations.put(3, location.clone().add(1, 2.5, 0));

        locations.put(4, location.clone().add(1.5, 1.5, 0));
        locations.put(5, location.clone().add(1.75, 0.5, 0));
        locations.put(6, location.clone().add(1.5, -0.5, 0));

        locations.put(7, location.clone().add(1, -1.5, 0));
        locations.put(8, location.clone().add(0, -1.75, 0));
        locations.put(9, location.clone().add(-1, -1.5, 0));

        locations.put(10, location.clone().add(-1.5, -0.5, 0));
        locations.put(11, location.clone().add(-1.75, 0.5, 0));
        locations.put(12, location.clone().add(-1.5, 1.5, 0));
    }

    private void spawnDefaultStands() {
        defaultStands.clear();

        for (int i = 1; i <= 12; i++) {
            final ArmorStand armorStand = location.getWorld().spawn(locations.get(i), ArmorStand.class);

            defaultStands.put(i, armorStand);

            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setCustomNameVisible(false);
            armorStand.setCustomName(identifier);

            if(i == 2) {
                armorStand.setHelmet(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                                .setTextur("M2VkNmU0YmQ4MTNmZGViNGNiNTQzZjgxOTk0Y2NiYzI2YjhlNDYwMjIxMjM5MTFmZDdlZWYzMjJmMGQ3ZDNlNyJ9fX0=", UUID.randomUUID())
                        .build());
            } else {
                armorStand.setHelmet(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setTextur("ZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==", UUID.randomUUID())
                        .build());
            }
        }
    }

    private void spawnMarkers() {
        // spawning marker
        final ArmorStand marker1 = location.getWorld().spawn(location.clone().add(0, 3, 0), ArmorStand.class);
        marker1.setVisible(false);
        marker1.setGravity(false);
        marker1.setCustomName(identifier + "§a§l♦♦♦");
        marker1.setCustomNameVisible(true);

        final ArmorStand marker2 = location.getWorld().spawn(location.clone().add(0, 1.25, 0), ArmorStand.class);
        marker2.setVisible(false);
        marker2.setGravity(false);
        marker2.setCustomName(identifier + "§6§a§l♦♦♦");
        marker2.setCustomNameVisible(true);
        // done
    }

    private void spawnStand(final ItemStack item, final Location location) {
        final ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);

        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomName((item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null) ? identifier + item.getItemMeta().getDisplayName() : identifier + "§a§o" + item.getType().name());
        stand.setCustomName(stand.getCustomName() + "§8 (§f§o" + luckySpinHandler.getChanceFromItem(item) + "%§8)");
        stand.setCustomNameVisible(true);
    }

    private void spawnAnimationStand(final int id, final ItemStack item) {
        final ArmorStand stand = location.getWorld().spawn(locations.get(id), ArmorStand.class);

        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomName(identifier);
        stand.setCustomNameVisible(false);

        spawnDrop(item, stand);

        animationStands.put(id, stand);
    }

    private void spawnDrop(final ItemStack item, final ArmorStand stand) {
        final Item drop = stand.getWorld().dropItem(stand.getLocation(), item);

        drop.setPickupDelay(Integer.MAX_VALUE);
        drop.setCustomName((item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null) ? item.getItemMeta().getDisplayName() : identifier + "§a§o" + item.getType().name());
        drop.setCustomNameVisible(true);

        stand.setPassenger(drop);
    }

    private void spawnItemWall() {

        final Location location = luckySpinHandler.getLocationHandler().getLocation("luckyspinwall");

        if(location == null) {
            Bukkit.getConsoleSender().sendMessage("§c§oLuckyspinwall location existiert nicht§8.");
            return;
        }

        final ArrayList<ItemStack> items = luckySpinHandler.getItems();

        if(items.size() == 0)
            return;

        int slot = 0;
        int row = 0;
        for (final ItemStack item : items) {

            if(slot > 3) {
                slot = 0;
                row++;
            }

            spawnStand(item, location.clone().add(slot*2.75, -row*0.5, 0));
            slot++;
        }

    }

    public void startSpinning(final Player player, final User user) {
        this.player = player;

        offset = 1;
        state = 1;
        runnable = new BukkitRunnable() {

            int delay = 5;
            int delayTick = 0;
            int tick = 0;

            int animationState = 0;

            final int maxSpins = 55 + new Random().nextInt(12);
            int spins = 0;

            @Override
            public void run() {

                if(delayTick < delay) {
                    delayTick++;
                    return;
                }

                delayTick = 0;
                tick++;

                if(animationState == 0) {
                    if(tick == 13) {
                        animationState = 1;
                        tick = 0;
                        delay = 1;
                        return;
                    }

                    defaultStands.get(tick).remove();
                    spawnAnimationStand(tick, luckySpinHandler.getRandomItem());

                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 5, 1);
                    return;
                }

                if(animationState == 1) {
                    if(tick == 5) {
                        animationState = 2;
                        tick = 0;
                        delay = 1;
                    }
                    return;
                }

                if(animationState == 2) {
                    if(tick >= 45) {
                        delay++;
                    }

                    if(spins >= maxSpins ) {
                        animationState = 3;
                        tick = 0;
                        delay = 1;

                        player.playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST2, 1, 1);
                        player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 1, 1);

                        player.playEffect(locations.get(2), Effect.MOBSPAWNER_FLAMES, 1);

                        return;
                    }

                    spinStands();

                    spins++;
                    return;
                }

                if(animationState == 3) {
                    if(tick >= 10) {
                        endSpin();

                        user.setLong(DataType.LASTLUCKYSPIN, System.currentTimeMillis());

                        cancel();
                    }
                }
            }
        }.runTaskTimer(CrownMain.getInstance(), 1, 1);
    }

    private void endSpin() {
        InventoryUtil.addItem(player, ((Item)winningStand.getPassenger()).getItemStack());

        removeAllCreatedEntities();
        spawnDefaultStands();
        spawnItemWall();
        spawnMarkers();

        state = 0;
    }

    private int offset = 1;
    private ArmorStand winningStand;
    private void spinStands() {
        if(offset > 12)
            offset = 1;

        for (int standId : animationStands.keySet()) {
            int nextLocation = standId + offset;

            if(nextLocation > 12)
                nextLocation -= 12;

            if(nextLocation == 2)
                winningStand = animationStands.get(standId);

            teleport(animationStands.get(standId), locations.get(nextLocation));
        }

        offset++;

        player.playSound(player.getLocation(), Sound.NOTE_BASS_DRUM, 5, 1f);
    }

    private void teleport(final ArmorStand stand, final Location location) {
        final Item drop = (Item) stand.getPassenger();

        stand.eject();
        stand.teleport(location);
        drop.teleport(location);
        stand.setPassenger(drop);

        player.playEffect(location.clone().add(0, 1.5, 0), Effect.HAPPY_VILLAGER, 0);

    }

    public void shutdown() {
        state = 1;

        if(runnable != null)
            runnable.cancel();

        removeAllCreatedEntities();
    }

    public void removeAllCreatedEntities() {
        if(animationStands.size() > 0) {
            for (ArmorStand temp : animationStands.values()) {
                if(temp == null)
                    continue;

                temp.setCustomNameVisible(false);
                temp.remove();
            }

            animationStands.clear();
        }

        if(defaultStands.size() > 0) {
            for (ArmorStand temp : defaultStands.values()) {
                if(temp == null)
                    continue;

                temp.setCustomNameVisible(false);
                temp.remove();
            }

            defaultStands.clear();
        }

        for (final World world : Bukkit.getWorlds()) {
            for (final Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand && entity.getCustomName() != null) {
                    if (entity.getCustomName().startsWith(identifier))
                        entity.remove();

                    continue;
                }

                if(entity instanceof Item && ((Item) entity).getPickupDelay() >= 10000)
                    entity.remove();
            }
        }

        winningStand = null;
    }

}
