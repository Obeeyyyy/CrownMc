package de.obey.crownmc.objects;
/*

    Author - Obey -> SkySlayer-v4
       16.12.2022 / 18:44

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.CrashHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.ArmorStandBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

@Getter
@Setter
public final class Crash {

    private double finalMultiplier = 1.00D;
    private double multiplier = 0.00D;

    private int state = 0; // 0 = waiting - 1 = countdown to start - 2 = running - 3 = crashed

    private long moneyInRound = 0;

    private final HashMap<UUID, Long> bets = new HashMap<>();

    private BukkitTask runnable;

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    private ArmorStandBuilder mainHolo, playersHolo;

    private final ArrayList<Double> multiplierLog = new ArrayList<>();
    private final HashMap<Player, Double> playerMultiplierLog = new HashMap<>();
    private final HashMap<Player, Long> playerBetLog = new HashMap<>();

    private final DecimalFormat format = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));

    private final CrashHandler crashHandler;

    private Location graphLocation;

    public Crash(final CrashHandler crashHandler) {
        messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
        userHandler = CrownMain.getInstance().getInitializer().getUserHandler();

        this.crashHandler = crashHandler;
        graphLocation = crashHandler.getCrashLocationGraph().clone();

        mainHolo = new ArmorStandBuilder(crashHandler.getCrashLocationOne(), "§0")
                .addStand(5)
                .setCustomName(1, "§f§oWarte auf §aSpieler§8.")
                .setCustomName(2, "§fNutze §a/crash §fum zu spielen§8.")
                .setCustomName(3, "!!!!").setCustomNameVisible(3, false)
                .setCustomName(4, "§fLetzte §fRunden")
                .setCustomName(5, "§f[§ax1.1§f]");

        multiplierLog.add(1.1d);

        playersHolo = new ArmorStandBuilder(crashHandler.getCrashLocationTwo(), "§0");
    }

    private final ArrayList<ArmorStand> graphStands = new ArrayList<>();

    private void spawnGraphStand(final double increasing, final boolean crashed) {
        graphLocation.add(0.25, increasing, 0);

        final ArmorStand stand = graphLocation.getWorld().spawn(graphLocation, ArmorStand.class);

        stand.setGravity(false);
        stand.setVisible(false);
        stand.setCustomName("§0--");
        stand.setCustomNameVisible(false);
        stand.setSmall(true);
        stand.setArms(true);

        graphStands.add(stand);

        if(crashed) {
            stand.setItemInHand(new ItemStack(Material.REDSTONE_BLOCK));
            return;
        }

        stand.setItemInHand(new ItemStack(Material.GOLD_BLOCK));
    }

    private void setMainHolo() {
        mainHolo.delete();
        mainHolo.setLocation(crashHandler.getCrashLocationOne());

        mainHolo.addStand(4)
                .setCustomName(1, "§f§oWarte auf §aSpieler§8.")
                .setCustomName(2, "§fNutze §a/crash §fum zu spielen§8.")
                .setCustomName(3, "!!!!").setCustomNameVisible(3, false)
                .setCustomName(4, "§fLetzte §fRunden");

        int standNumber = 5;
        for (Double aDouble : multiplierLog) {
            mainHolo.addStand().setCustomName(standNumber, "§f[x" + getColor(aDouble) + Double.parseDouble(format.format(aDouble).replace(",", ".")) + "§f]");
            standNumber++;
        }
    }

    private void setPlayersHolo() {
        playersHolo.delete();
        playersHolo.setLocation(crashHandler.getCrashLocationTwo());
        playersHolo.addStand(1 + bets.size())
                .setCustomName(1, "§aSpieler:");

        int standNumber = 2;
        for (UUID uuid : bets.keySet()) {
            playersHolo.addStand().setCustomName(standNumber, "§f" + Bukkit.getOfflinePlayer(uuid).getName() + "§8 - §e§o" +  NumberFormat.getInstance().format(bets.get(uuid)) + "§6§l$");
            standNumber++;
        }

        if(playerBetLog.size() > 0) {
            for (final Player player : playerMultiplierLog.keySet()) {
                playersHolo.addStand().setCustomName(standNumber, "§f" + player.getName() + "§8 - §2+§a§l" +  NumberFormat.getInstance().format(playerBetLog.get(player)) + "§2§l$ §f[x" + getColor(playerMultiplierLog.get(player)) + playerMultiplierLog.get(player) + "§f]");
                standNumber++;
            }
        }
    }

    public void joinCrash(final Player player, final long bet) {
        if (state >= 2) {
            sendMessage(player, "Bitte warte bis die aktuelle Runde vorbei ist§8.");
            return;
        }

        if (bets.containsKey(player.getUniqueId())) {
            sendMessage(player, "Du nimmst bereits an der Runde teil§8.");
            return;
        }

        final User user = userHandler.getUserInstant(player.getUniqueId());

        if (user == null) {
            sendMessage(player, "Bitte versuche es später nochmal§.");
            return;
        }

        if (!messageUtil.hasEnougthMoney(user, bet))
            return;

        user.removeLong(DataType.MONEY, bet);
        bets.put(player.getUniqueId(), bet);
        playerBetLog.put(player, bet);

        moneyInRound += bet;

        sendMessage(player, "Du hast eine Crashlobby betreten§8. §7Einsatz§8: §a§o" + messageUtil.formatLong(bet) + "§6§l$");
        sendMessage(player, "Nutze §8/§7Crash leave zum auszahlen§8.");

        if (state == 0) { // Wartezeit startet jz
            state = 1;

            mainHolo.teleport(crashHandler.getCrashLocationThree());
            mainHolo.setCustomName(1, "§fNächste Runde in§8: §a§o20s");

            setPlayersHolo();

            runnable = new BukkitRunnable() {

                int cd = 10;

                @Override
                public void run() {

                    if (cd == 0) {
                        state = 2;
                        startCrash();
                        cancel();
                        return;
                    }

                    mainHolo.setCustomName(1, "§fNächste Runde in§8: §a§o" + cd + "s");
                    cd--;

                }
            }.runTaskTimer(CrownMain.getInstance(), 0, 20);

        } else { // wartezeit lief schon
            playersHolo.addStand()
                    .setCustomName(1 + bets.size(), "§f" + player.getName() + "§8 : §e§o" + NumberFormat.getInstance().format(bet) + "§6§l$");
        }
    }

    private String getColor(final double multiplier) {
        if(multiplier > 1.0)
            return "§a§o";

        return "§c§o";
    }

    private void startCrash() {
        multiplier = 0.0;
        mainHolo.delete();
        mainHolo.addStand().setCustomName(1, "§fx" + getColor(0.0) + "0.0");

        graphLocation = crashHandler.getCrashLocationGraph().clone();

        final Random random = new Random();

        /*
        int initialChance = random.nextInt(4);

        if (initialChance == 0) {
            finalMultiplier = 1 + (random.nextInt(3) / 10D) + (random.nextDouble() / 10D);
        } else {
            initialChance = random.nextInt(5);
            if (initialChance == 0) {
                finalMultiplier = 2 + (random.nextDouble() / 10D) + (random.nextDouble() / 10D);
            } else {
                finalMultiplier = 1.3 + (random.nextInt(8) / 10D) + (random.nextDouble() / 10D);
            }
        }
         */

        finalMultiplier = random.nextInt(10) + random.nextDouble();
        finalMultiplier = Double.parseDouble(format.format(finalMultiplier).replace(",", "."));

        bets.keySet().forEach(uuid -> {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {

                if (player.hasPermission("crownmc.admin") && player.getGameMode() == GameMode.CREATIVE) {
                    sendMessage(player, "Crash Multilpier ist§8: §a§o" + format.format(finalMultiplier));
                }
            }
        });

        spawnGraphStand(0.0, false);

        runnable = new BukkitRunnable() {
            float pitch = 0.6f;
            int delay = 20;
            int finalTicks = 0;
            int tempTicks = 0;

            @Override
            public void run() {

                if (tempTicks >= delay) {

                    double increasing = random.nextInt(5)/10d + random.nextDouble()/10d;

                    if(multiplier + increasing > finalMultiplier)
                        increasing = finalMultiplier - multiplier;

                    tempTicks = 0;
                    multiplier += increasing;

                    finalTicks++;
                    if(delay > 2)
                        delay--;

                    mainHolo.setCustomName(1, "§fx" + getColor(multiplier) + "" + format.format(multiplier));

                    if (multiplier >= finalMultiplier) {
                        endCrash();
                        cancel();
                        spawnGraphStand(increasing, true);
                        return;
                    }

                    spawnGraphStand(increasing, false);

                    if (pitch > 1.5)
                        pitch = 0.6f;

                    bets.keySet().forEach(uuid -> {
                        final Player player = Bukkit.getPlayer(uuid);
                        if (player != null && player.isOnline()) {
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.8f, pitch);
                        }
                    });

                    pitch += 0.1;
                    return;
                }

                tempTicks++;
            }
        }.runTaskTimer(CrownMain.getInstance(), 1, 1);
    }

    private void endCrash() {
        state = 3;
        moneyInRound = 0;

        bets.keySet().forEach(uuid -> {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                CrownMain.getInstance().getInitializer().getDailyPotHandler().addMoney(bets.get(uuid)/20);
                sendToAllPlayers("Die Lobby ist bei x§c§l" + format.format(finalMultiplier) + "§7 gecrasht§8.§7 " + player.getName() + " hat §c§o-" + messageUtil.formatLong(bets.get(uuid)) + "§6§l$§7 verloren§8.");
                player.playSound(player.getLocation(), Sound.EXPLODE, 1, 1);

                playerMultiplierLog.put(player, 0.0);
                playerBetLog.put(player, 0L);
            }
        });

        bets.clear();
        setPlayersHolo();

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                state = 0;


                multiplierLog.add(finalMultiplier);

                if(multiplierLog.size() > 5)
                    multiplierLog.remove(0);

                setMainHolo();
                playersHolo.delete();

                playerMultiplierLog.clear();
                playerBetLog.clear();

                graphStands.forEach(Entity::remove);
                graphStands.clear();

                cancel();
            }
        }.runTaskLater(CrownMain.getInstance(), 20 * 5);
    }

    public void leaveCrash(final Player player) {
        if (!bets.containsKey(player.getUniqueId())) {
            sendMessage(player, "Du bist in keiner Crashlobby§8.");
            return;
        }

        final User user = userHandler.getUserInstant(player.getUniqueId());

        if (user == null)
            return;

        if (state < 2) {
            sendMessage(player, "Du hast die Lobby verlassen§8.");
            user.addLong(DataType.MONEY, bets.get(player.getUniqueId()));
            moneyInRound -= bets.get(player.getUniqueId());
            bets.remove(player.getUniqueId());
            setPlayersHolo();
            return;
        }

        final long reward = Double.valueOf(bets.get(player.getUniqueId()) * multiplier).longValue();

        moneyInRound -= bets.get(player.getUniqueId());

        bets.remove(player.getUniqueId());
        user.addLong(DataType.MONEY, reward);

        playerBetLog.put(player, reward);
        playerMultiplierLog.put(player, Double.parseDouble(format.format(multiplier).replace(",", ".")));

        sendToAllPlayers(player.getName() + " hat bei x§a§l" + format.format(multiplier) + "§7 ausgezahlt und §a§o+" + messageUtil.formatLong(reward) + "§6§l$§7 gewonnen§8.");
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);
        setPlayersHolo();
    }

    private void sendToAllPlayers(final String message) {
        for (Entity entity : mainHolo.getLocation().getWorld().getEntities()) {
            if(entity instanceof Player) {
                sendMessage((Player) entity, message);
            }
        }
    }

    private void sendMessage(final Player player, final String message) {
        player.sendMessage("§a§lCRASH §8×§7 " + message);
    }

}
