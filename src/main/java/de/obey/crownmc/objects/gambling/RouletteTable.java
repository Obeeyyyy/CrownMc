package de.obey.crownmc.objects.gambling;
/*

    Author - Obey -> CrownMc
       03.07.2023 / 12:53

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.RouletteHandler;
import de.obey.crownmc.util.ArmorStandBuilder;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
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
    private final HashMap<UUID, Long> betAmounts = new HashMap<>();

    @Getter
    private final HashMap<UUID, String> betColors = new HashMap<>();

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

        spawnHolo();
    }

    private void spawnRod(final int id) {
        final Location location = standLocations.get(id).clone().add(0.15, 0.5, 0);

        location.setYaw(yaws.get(1));

        final ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);

        stand.setVisible(false);
        stand.setCustomName(identifier + "rod " + tableID);
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
        stand.setCustomName(identifier + "green " + tableID);
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
        stand.setCustomName(identifier + "red " + tableID);
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
        stand.setCustomName(identifier + "black " + tableID);
        stand.setCustomNameVisible(false);
        stand.setGravity(false);

        stand.setHelmet(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("Y2ZhNGRkYTZkMTlhMWZlMmQ5ODhkNjVkZWM1MzQyOTUwNTMwODE2NmM5MDY3YjY4YTQ3NzBjYTVjNDM2Y2Y5NCJ9fX0=", UUID.randomUUID())
                .build());

        stands.put(id, stand);
    }

    private void spawnHolo() {
        holo = new ArmorStandBuilder(location.clone().add(0, 4, 0), identifier)
                .addStandUnder(5)
                .setCustomName(1, "§f§lRoulette")
                .setCustomName(2,  "i").setCustomNameVisible(2, false)
                .setCustomName(3,  "§c§lROT §f" + rouletteHandler.getMultiplier("red") + "x §8│ §0§lSCHWARZ §f" + rouletteHandler.getMultiplier("black") + "x§8 │ §a§lGRÜN §f" + rouletteHandler.getMultiplier("green") + "x").setCustomNameVisible(2, false)
                .setCustomName(4,  "i").setCustomNameVisible(4, false)
                .setCustomName(5, "§7Nutze das §f§lSchild§7 um eine Runde zu starten§8.");
    }

    public void killStands() {
        if (location == null)
            return;

        if (location.getWorld().getEntities().isEmpty())
            return;

        for (final Entity entity : location.getWorld().getEntities()) {
            if (entity instanceof ArmorStand) {
                if (entity.getCustomName() == null)
                    continue;

                if (entity.getCustomName().startsWith(identifier))
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

        if(holo != null)
            holo.delete();

        for (final UUID uuid : betAmounts.keySet())
            rouletteHandler.pay(uuid, betAmounts.get(uuid));
    }

    private int getStandSlotFromYaw(final float yaw) {
        for (Integer slot : yaws.keySet()) {
            if(yaws.get(slot) == yaw)
                return slot;
        }

        return -1;
    }

    private ArmorStand getStandFromLocation(final Location location) {
        for (ArmorStand stand : stands.values()) {
            if(stand.getLocation().equals(location))
                return stand;
        }

        return null;
    }

    public void join(final Player player, final long amount, final String color) {
        if(state == 0) {
            holo.setCustomName(5, "§7Die Runde startet in §f§o20 §7Sekunden§8.")
                    .addStandUnder(2)
                    .setCustomName(6, "i").setCustomNameVisible(6, false)
                    .setCustomName(7, "§f§lSpieler§8:");

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
                    holo.setCustomName(5, "§7Runde startet in §f§o" + cooldown +" §7Sekunden§8.");

                }
            }.runTaskTimer(CrownMain.getInstance(), 20, 20);
        }

        holo.addStandUnder().setCustomName(holo.stands().size(), "§7" + player.getName() + "§8 │ §e§o" + messageUtil.formatLong(amount) + "§6§l$ §8│ " + rouletteHandler.getPrefixFromColor(color));
        holo.teleport(location.clone().add(0, 4 + (0.5 * betAmounts.size()), 0));

        state = 1;

        betAmounts.put(player.getUniqueId(), amount);
        betColors.put(player.getUniqueId(), color);
        player.playSound(player.getLocation(), Sound.VILLAGER_HAGGLE, 0.5f, 1);
    }

    private void startAnimation() {
        state = 2;
        runnable.cancel();

        holo.setCustomName(5, "§7Viel Glück!");

        standOffset = 1;
        final Random random = new Random();

        runnable = new BukkitRunnable() {

            int delay = 1, delayTick = 0, tick = 0;

            int maxSpins = 60 + random.nextInt(12);
            int spins = 0, animState = 0;

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

                if(animState == 0) {
                    if (spins >= maxSpins) {

                        endSpin();
                        cancel();

                        return;
                    }

                    spinBlocks();

                    if(random.nextInt(10) <= 5)
                        spinRod();

                    betAmounts.keySet().forEach(uuid ->  {
                        final Player player = Bukkit.getPlayer(uuid);

                        if(player != null)
                            player.playSound(player.getLocation(), Sound.CLICK, 1, 1f);
                    });
                }

                spins++;
            }
        }.runTaskTimer(CrownMain.getInstance(), 1, 1);

    }

    private void endSpin() {
        state = 3;

        final int winSlot = getStandSlotFromYaw(stands.get(0).getLocation().getYaw());
        final ArmorStand winStand = getStandFromLocation(standLocations.get(winSlot));

        if(winSlot < 1 || winStand == null)
            return;

        final String winColor = ChatColor.stripColor(winStand.getCustomName().split(" ")[0]);
        final double multiplier = rouletteHandler.getMultiplier(winColor);

        for (final UUID uuid : betAmounts.keySet()) {
            final Player player = Bukkit.getPlayer(uuid);

            if(betColors.get(uuid).equalsIgnoreCase(winColor)) {
                final long reward = (long)(betAmounts.get(uuid) * multiplier);
                rouletteHandler.pay(uuid, reward);

                if(player == null)
                    continue;

                messageUtil.sendMessage(player, "Du hast §a+§f§o" + messageUtil.formatLong(reward) + "§6§l$§7 erhalten§8. ( §a+" + messageUtil.formatLong(reward - betAmounts.get(uuid)) + " §8)");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);
                continue;
            }

            if(player == null)
                continue;

            player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
            messageUtil.sendMessage(player, "Du hast leider verloren§8,§7 viel Glück beim nächsten mal§8. ( §c§o-" + messageUtil.formatLong(betAmounts.get(uuid)) + "§6§l$§8 )");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                betAmounts.clear();
                betColors.clear();
                holo.delete();

                spawnHolo();

                state = 0;
            }
        }.runTaskLater(CrownMain.getInstance(), 40);
    }

    private int standOffset = 1;
    private void spinBlocks() {
        if(standOffset > 12)
            standOffset = 1;

        for (int i = 1; i <= 12 ; i++) {
            int nextLocation = i + standOffset;

            if(nextLocation > 12)
                nextLocation -= 12;

            stands.get(i).teleport(standLocations.get(nextLocation));
        }

        standOffset++;
    }

    private int yawOffset = 1;
    private void spinRod() {

        if(yawOffset < 1)
            yawOffset = 12;

        // Moving rod
        final Location temp = stands.get(0).getLocation();
        temp.setYaw(yaws.get(yawOffset));
        stands.get(0).teleport(temp);
        ///

        yawOffset--;
    }


    private void sendMessageToAll(final String message) {
        if(betAmounts.isEmpty())
            return;

        for (final UUID uuid : betAmounts.keySet()) {
            final Player player = Bukkit.getPlayer(uuid);

            if(player == null)
                continue;

            messageUtil.sendMessage(player, rouletteHandler.getPrefix() + message);
        }

    }
}
