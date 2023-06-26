package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       14.01.2023 / 20:53

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.util.ArmorStandBuilder;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public final class DailyPotHandler {

    private final LocationHandler locationHandler;
    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Getter
    private ArmorStandBuilder stands;

    private final YamlConfiguration cfg;

    @Setter @Getter
    private long payinAmount = 100, moneyinPot = 0, endMillis = System.currentTimeMillis() + 86400000;

    private ArrayList<String> teilnehmer = new ArrayList<>();

    private UUID lastwinner = UUID.fromString("f4b1497c-622e-4f50-b87a-059a8fa5b024");
    private long lastwinamount = 0;

    public DailyPotHandler(final LocationHandler locationHandler, final MessageUtil messageUtil, final UserHandler userHandler) {
        this.messageUtil = messageUtil;
        this.locationHandler = locationHandler;
        this.userHandler = userHandler;

        cfg = FileUtil.getCfg(FileUtil.getFile("dailypot.yml"));

        if(cfg.contains("payinamount"))
            payinAmount = cfg.getLong("payinamount");

        if(cfg.contains("moneyinpot"))
            moneyinPot = cfg.getLong("moneyinpot");

        if(cfg.contains("endmillis"))
            endMillis = cfg.getLong("endmillis");

        if(cfg.contains("teilnehmer"))
            teilnehmer = (ArrayList<String>) cfg.getStringList("teilnehmer");

        if(cfg.contains("lastwinner"))
            lastwinner = UUID.fromString(cfg.getString("lastwinner"));

        if(cfg.contains("lastwinneramount"))
            lastwinamount = cfg.getLong("lastwinneramount");
    }

    public void save() {
        cfg.set("payinamount", payinAmount);
        cfg.set("moneyinpot", moneyinPot);
        cfg.set("endmillis", endMillis);
        cfg.set("teilnehmer", teilnehmer);
        cfg.set("lastwinner", lastwinner.toString());
        cfg.set("lastwinneramount", lastwinamount);

        FileUtil.saveToFile(FileUtil.getFile("dailypot.yml"), cfg);
    }

    public void setupArmorStands() {
        for (final World world : Bukkit.getWorlds()) {
            for (final Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand && entity.getCustomName() != null) {
                    if (entity.getCustomName().equalsIgnoreCase("§8▰§7▱ §9§lDAILYPOT§7 ▱§8▰"))
                        entity.remove();

                    if (entity.getCustomName().startsWith("§8┃» §7Geld im Pot§8: "))
                        entity.remove();

                    if (entity.getCustomName().startsWith("§8┃» §7Teilnehmer§8: "))
                        entity.remove();

                    if (entity.getCustomName().startsWith("§8┃» §7Chance§8: "))
                        entity.remove();

                    if (entity.getCustomName().startsWith("§8┃» §7Pot öffnet sich in§8: "))
                        entity.remove();

                    if (entity.getCustomName().startsWith("§8┃» §7Teilnahme Gebühr§8: "))
                        entity.remove();

                    if (entity.getCustomName().startsWith("§8┃» §7Letzter Gewinner§8: "))
                        entity.remove();
                }
            }
        }

        if (locationHandler.getLocation("dailypot") == null) {
            messageUtil.sendMessage(Bukkit.getConsoleSender(), "Dailypot locationHandler == null");
            return;
        }

        final Location dailypotLocation = locationHandler.getLocation("dailypot").clone();

        if (dailypotLocation == null) {
            messageUtil.sendMessage(Bukkit.getConsoleSender(), "Dailypot location == null");
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                stands = new ArmorStandBuilder(dailypotLocation).addStand(1)

                        .setCustomName(1, "§8▰§7▱ §9§lDAILYPOT§7 ▱§8▰").addLocation(0, -0.25f, 0)
                        .addStand(1)
                        .setCustomName(2, "§8┃» §7Letzter Gewinner§8: §c§oloading").addLocation(0, -0.25f, 0)
                        .addStand(1)
                        .setCustomName(3, "§8┃» §7Teilnahme Gebühr§8: §c§oloading").addLocation(0, -0.25f, 0)
                        .addStand(3)
                        .setCustomName(5, "§8┃» §7Geld im Pot§8: §c§oloading")
                        .setCustomName(4, "§8┃» §7Chance§8: §c§oloading").addLocation(0, -0.25f, 0)
                        .setCustomName(6, "§8┃» §7Teilnehmer§8: §c§oloading")
                        .addStand(1)
                        .setCustomName(7, "§8┃» §7Pot öffnet sich in§8: §c§oloading");
            }
        }.runTaskLater(CrownMain.getInstance(), 10);
    }

    public void updateStands() {
        if(stands == null) {
            setupArmorStands();
            return;
        }

        final DecimalFormat format = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        stands
                .setCustomName(2, "§8┃» §7Letzter Gewinner§8:  §e§o" + Bukkit.getOfflinePlayer(lastwinner).getName() + " §8(§f" + MathUtil.replaceLongWithSuffix(lastwinamount) + "§6§l$§8)").addLocation(0, -0.25f, 0)
                .setCustomName(3, "§8┃» §7Teilnahme Gebühr§8: §e§o" + messageUtil.formatLong(payinAmount) + "§6§l$").addLocation(0, -0.25f, 0)
                .setCustomName(5, "§8┃» §7Geld im Pot§8: §e§o" + messageUtil.formatLong(moneyinPot) + "§6§l$")
                .setCustomName(4, "§8┃» §7Chance§8: §e§o" + format.format(teilnehmer.size() > 0 ? 100D/teilnehmer.size() + 0.0 : 0) + "§6§l%")
                .setCustomName(6,"§8┃» §7Teilnehmer§8: §e§o" + teilnehmer.size() + "§7 Spieler§8.")
                .setCustomName(7, "§8┃» §7Pot öffnet sich in§8: §e§o" + MathUtil.getHoursAndMinutesAndSecondsFromSeconds((endMillis-System.currentTimeMillis()) / 1000));

        if(System.currentTimeMillis() >= endMillis)
            open();

    }

    public void join(final Player player) {
        if(teilnehmer.contains(player.getUniqueId().toString())) {
            sendMessage(player, "Du nimmst bereits am Pot teil§8.");
            return;
        }

        userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
            if(!messageUtil.hasEnougthMoney(user, payinAmount))
                return;

            moneyinPot += (payinAmount - (payinAmount/10));

            user.removeLong(DataType.MONEY, payinAmount);
            teilnehmer.add(player.getUniqueId().toString());

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);

            sendMessage(player, "Du hast den DailyPot betreten§8.");
            sendToAllPlayers(player.getName() + " hat den Dailypot betreten§8.");
        });
    }

    public void open(final Player player) {
        sendToAllPlayers(player.getName() + " öffnet den §9§lDailyPot§8 ...");
        open();
    }

    public void open() {
        if(teilnehmer.isEmpty()) {
            moneyinPot = 0;
            endMillis = System.currentTimeMillis() + 86400000;
            return;
        }

        final DecimalFormat format = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        final UUID winner = UUID.fromString(teilnehmer.get(new Random().nextInt(teilnehmer.size())));

        userHandler.getUser(winner).thenAcceptAsync(user -> {
            sendToAllPlayers(user.getOfflinePlayer().getName() + " hat §a+§e§o" + messageUtil.formatLong(moneyinPot) + "§6§l$ §7mit einer Chance von §e" + format.format(teilnehmer.size() > 0 ? 100/teilnehmer.size() : 0) + "§6§l% §7gewonnen§8, §7herzlichen Glückwunsch§8.");

            if(user.getOfflinePlayer().isOnline())
                user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.ENDERDRAGON_WINGS, 0.5f, 1);

            lastwinner = winner;
            lastwinamount = moneyinPot;

            user.addLong(DataType.MONEY, moneyinPot);
            moneyinPot = 0;
            teilnehmer.clear();
            endMillis = System.currentTimeMillis() + 86400000;
        });
    }

    private void sendToAllPlayers(final String message) {
        for (Entity entity : stands.getLocation().getWorld().getEntities()) {
            if(entity instanceof Player) {
                sendMessage((Player) entity, message);
            }
        }
    }

    private void sendMessage(final Player player, final String message) {
        player.sendMessage("§9§lDAILYPOT §8×§7 " + message);
    }

    public void addMoney(final long amount) {
        moneyinPot += amount;
    }

}
