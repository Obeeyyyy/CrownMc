package de.obey.crownmc.objects.gambling;
/*

    Author - Obey -> CrownMc
       03.07.2023 / 12:53

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.RouletteHandler;
import de.obey.crownmc.util.ArmorStandBuilder;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public final class RouletteTable {

    private final MessageUtil messageUtil;
    private final RouletteHandler rouletteHandler;

    @Getter
    private final int tableID;

    @Setter
    private Location location;

    private ArmorStandBuilder holo;

    private final String identifier = "§8§8";

    @Getter
    private final HashMap<Player, Long> betAmounts = new HashMap<>();

    @Getter
    private final HashMap<Player, String> betColors = new HashMap<>();

    @Getter
    private int state = 0; // 0 = waiting, 1 = stating soon, 2 = spinning, 3 = done

    private BukkitTask runnable;

    public RouletteTable(final int tableID, final Location location, final RouletteHandler rouletteHandler) {
        this.tableID = tableID;
        this.location = location;
        this.rouletteHandler = rouletteHandler;
        messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

        if(location == null) {
            messageUtil.warn("Cant find Location  roulette" + tableID);
            return;
        }

        loadLocations();
        loadYaws();
        killStands();
        spawnStands();

        messageUtil.log("Created Roulettetable " + tableID);
    }

    private final HashMap<Integer, Location> standLocations = new HashMap<>();
    public void loadLocations() {
         /*
            1 2 3
           12    4
           11    5
           10    6
            9 8 7
         */
        standLocations.clear();

        location.setYaw(0);
        location.setPitch(0);

        Location temp = location.clone();
        standLocations.put(0, temp.clone());

        temp = location.clone();
        standLocations.put(1, temp.add(1.35, 0, -0.70).clone());
        standLocations.put(2, temp.add(0, 0, 0.70).clone());
        standLocations.put(3, temp.add(0, 0, 0.70).clone());

        temp = location.clone();
        standLocations.put(4, temp.add(0.70, 0, 1.35).clone());
        standLocations.put(5, temp.add(-0.70, 0, 0).clone());
        standLocations.put(6, temp.add(-0.70, 0, 0).clone());

        temp = location.clone();
        standLocations.put(7, temp.add(-1.35, 0, 0.70).clone());
        standLocations.put(8, temp.add(0, 0, -0.70).clone());
        standLocations.put(9, temp.add(0, 0, -0.70).clone());

        temp = location.clone();
        standLocations.put(10, temp.add(-0.70, 0, -1.35).clone());
        standLocations.put(11, temp.add(0.70, 0, 0).clone());
        standLocations.put(12, temp.add(0.70, 0, 0).clone());

        messageUtil.log("Loaded Roulette " + tableID + " stand locations: " + standLocations.size());
    }

    private final HashMap<Integer, Float> yaws = new HashMap<>();
    public void loadYaws() {
        yaws.put(1, -220f);
        yaws.put(2, -185f);
        yaws.put(3, -150f);
        yaws.put(4, -120f);
        yaws.put(5, -90f);
        yaws.put(6, -60f);
        yaws.put(7, -30f);
        yaws.put(8, -5f);
        yaws.put(9, 25f);
        yaws.put(10, 50f);
        yaws.put(11, 80f);
        yaws.put(12, 110f);
    }

    public void setYaw(final float yaw ) {
        final Location temp = stands.get(0).getLocation();
        temp.setYaw(yaw);
        stands.get(0).teleport(temp);
    }

    private final HashMap<Integer, ArmorStand> stands = new HashMap<>();
    public void spawnStands() {

        spawnRod(0);

        spawnRed(1);
        spawnGreen(2);
        spawnRed(3);

        spawnBlack(4);
        spawnRed(5);
        spawnBlack(6);

        spawnRed(7);
        spawnBlack(8);
        spawnRed(9);

        spawnBlack(10);
        spawnRed(11);
        spawnBlack(12);

    }

    private void spawnRod(final int id) {
        final Location location = standLocations.get(id).clone().add(0.15, 0.5, 0);
        final ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);

        stand.setVisible(false);
        stand.setCustomName(identifier);
        stand.setCustomNameVisible(false);
        stand.setGravity(false);

        stand.setRightArmPose(new EulerAngle(Math.toRadians(330), Math.toRadians(90), 0));
        stand.setItemInHand(new ItemBuilder(Material.BLAZE_ROD).addItemFlags(ItemFlag.HIDE_ENCHANTS).addEnchantment(Enchantment.DURABILITY).build());
        stands.put(id, stand);
    }

    private void spawnGreen(final int id) {
        final Location location = standLocations.get(id);
        final ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);

        stand.setVisible(false);
        stand.setCustomName(identifier + "green");
        stand.setCustomNameVisible(false);
        stand.setGravity(false);

        stand.setHelmet(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setTextur("Nzc0NzJkNjA4ODIxZjQ1YTg4MDUzNzZlYzBjNmZmY2I3ODExNzgyOWVhNWY5NjAwNDFjMmEwOWQxMGUwNGNiNCJ9fX0=", UUID.randomUUID())
                .build());

        stands.put(id, stand);
    }

    private void spawnRed(final int id) {
        final Location location = standLocations.get(id);
        final ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);

        stand.setVisible(false);
        stand.setCustomName(identifier + "red");
        stand.setCustomNameVisible(false);
        stand.setGravity(false);

        stand.setHelmet(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("Njk1M2IxMmEwOTQ2YjYyOWI0YzA4ODlkNDFmZDI2ZWQyNmZiNzI5ZDRkNTE0YjU5NzI3MTI0YzM3YmI3MGQ4ZCJ9fX0=", UUID.randomUUID())
                .build());

        stands.put(id, stand);
    }

    private void spawnBlack(final int id) {
        final Location location = standLocations.get(id);
        final ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);

        stand.setVisible(false);
        stand.setCustomName(identifier + "black");
        stand.setCustomNameVisible(false);
        stand.setGravity(false);

        stand.setHelmet(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("Y2ZhNGRkYTZkMTlhMWZlMmQ5ODhkNjVkZWM1MzQyOTUwNTMwODE2NmM5MDY3YjY4YTQ3NzBjYTVjNDM2Y2Y5NCJ9fX0=", UUID.randomUUID())
                .build());

        stands.put(id, stand);
    }

    public void killStands() {
        if(location == null)
            return;

        for (Entity entity : location.getWorld().getEntities()) {
            if(entity instanceof ArmorStand && entity.getCustomName().startsWith(identifier)) {
                entity.remove();
            }
        }
    }

    public void shutdown() {
        killStands();

        state = 3;

        if(runnable != null)
            runnable.cancel();

        if(betAmounts.isEmpty())
            return;

        for (Player player : betAmounts.keySet()) {
            CrownMain.getInstance().getInitializer().getUserHandler().getUser(player.getUniqueId()).thenAcceptAsync(user -> {
               user.addLong(DataType.MONEY, betAmounts.get(player));
            });
        }
    }

    public void join(final Player player, final long amount, final String color) {
        if(state == 0) {
            holo = new ArmorStandBuilder(location.clone().add(0, 3, 0), identifier)
                    .addStand(5)
                    .setCustomName(1, "§f§lRoulette")
                    .setCustomName(2,  "i").setCustomNameVisible(2, false)
                    .setCustomName(3, "§7Runde startet in §f§o20 §7Sekunden§8.")
                    .setCustomName(4, "i").setCustomNameVisible(4, false)
                    .setCustomName(5, "§f§lSpieler§8:");

            runnable = new BukkitRunnable() {
                int cooldown = 20;
                @Override
                public void run() {

                    if(cooldown == 0) {
                        sendMessageToAll("Die Runde startet jetzt§8,§7 viel Glück§8!");
                        startAnimation();
                        return;
                    }

                    cooldown--;
                    holo.setCustomName(3, "§7Runde startet in §f§o" + cooldown +" §7Sekunden§8.");

                }
            }.runTaskTimer(CrownMain.getInstance(), 20, 20);
        }

        holo.addStand().setCustomName(holo.stands().size(), "§7" + player.getName() + "§8 │ §e§o" + messageUtil.formatLong(amount) + "§6§l$ §8│ " + rouletteHandler.getPrefixFromColor(color));

        state = 1;

        betAmounts.put(player, amount);
        betColors.put(player, color);
        player.playSound(player.getLocation(), Sound.VILLAGER_HAGGLE, 0.5f, 1);
    }

    private void startAnimation() {
        state = 2;
        runnable.cancel();

        holo.setCustomName(3, "§7Die Runde läuft§8.");

        standOffset = 1;
        state = 1;
        runnable = new BukkitRunnable() {

            int delay = 1;
            int delayTick = 0;
            int tick = 0;

            final int maxSpins = 60 + new Random().nextInt(12);
            int spins = 0;

            @Override
            public void run() {

                if (delayTick < delay) {
                    delayTick++;
                    return;
                }

                delayTick = 0;
                tick++;

                if (tick >= 45) {
                    delay++;
                }

                if (spins >= maxSpins) {
                    tick = 0;
                    delay = 1;

                    endSpin();
                    cancel();

                    return;
                }

                spin();

                spins++;
            }
        }.runTaskTimer(CrownMain.getInstance(), 1, 1);

    }

    private void endSpin() {
        state = 3;

        betAmounts.clear();
        betColors.clear();

        new BukkitRunnable() {
            @Override
            public void run() {
                state = 0;
                holo.delete();
                holo = null;
            }
        }.runTaskLater(CrownMain.getInstance(), 20);
    }

    private int standOffset = 1;
    private int yawOffset = 1;
    private void spin() {
        if(standOffset > 12)
            standOffset = 1;

        if(yawOffset < 1)
            yawOffset = 12;

        for (int i = 1; i <= 12 ; i++) {
            int nextLocation = i + standOffset;

            if(nextLocation > 12)
                nextLocation -= 12;

            stands.get(i).teleport(standLocations.get(nextLocation));
        }

        // Moving rod
        final Location temp = stands.get(0).getLocation();
        temp.setYaw(yaws.get(yawOffset));
        stands.get(0).teleport(temp);
        ///

        standOffset++;
        yawOffset--;

        betAmounts.keySet().forEach(player -> player.playSound(player.getLocation(), Sound.CLICK, 1, 1f));
    }
    private void sendMessageToAll(final String message) {
        if(betAmounts.isEmpty())
            return;

        for (Player player : betAmounts.keySet())
            messageUtil.sendMessage(player, rouletteHandler.getPrefix() + message);

    }
}
