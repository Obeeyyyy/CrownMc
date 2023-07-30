package de.obey.crownmc.objects.pvp;
/*

    Author - Obey -> CrownMc
       11.07.2023 / 03:45

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.PvPAltarHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.UUID;

@Getter @Setter
public final class PvPAltar {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final PvPAltarHandler pvPAltarHandler;
    private final YamlConfiguration cfg;

    private final int id;
    private int state = 0; // 0 = idle , 1 = wird eingenommen, 2 = cooldown

    private long eloReward, moneyReward, xpReward, cooldownUntilMillis, eloPunish, moneyPunish,
        timeToCapture, cooldownMillis;

    private ArrayList<ItemStack> itemRewards = new ArrayList<>();
    private Location location;

    private String prefix;

    private final String identifier = "§9§9";

    private UUID playerUUID;
    private ArmorStandBuilder base, holo;

    private BukkitTask runnable;

    public PvPAltar(final int id, final YamlConfiguration cfg) {
        this.messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
        this.userHandler = CrownMain.getInstance().getInitializer().getUserHandler();
        this.pvPAltarHandler = CrownMain.getInstance().getInitializer().getPvPAltarHandler();

        this.id = id;
        this.cfg = cfg;

        final String path = "altars." + id + ".";

        moneyReward = cfg.getLong(path + "moneyReward", 0);
        eloReward = cfg.getLong(path + "eloReward", 0);
        xpReward = cfg.getLong(path + "xpReward", 0);
        eloPunish = cfg.getLong(path + "eloPunish", 0);
        moneyPunish = cfg.getLong(path + "moneyPunish", 0);
        timeToCapture = cfg.getLong(path + "timeToCapture", 1000*60*10);
        cooldownMillis = cfg.getLong(path + "cooldownMillis", 1000*60*10);

        if (cfg.contains(path + "itemRewards"))
            itemRewards = (ArrayList<ItemStack>) cfg.getList(path + "itemRewards", new ArrayList<>());

        if (cfg.contains(path + "location"))
            location = LocationUtil.decode(cfg.getString(path + "location"));

        prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString(path + "prefix", "CHANGEME"));

        spawnAltar();
    }

    public void spawnAltar() {
        if(location == null) {
            messageUtil.log("Cant spawn Altar " + id);
            return;
        }

        shutdown();

        base = new ArmorStandBuilder(location.clone(), identifier)
                .addStandAbove(5, 0.625)
                .setHelmet(1, Material.ENDER_STONE)
                .setHelmet(2, Material.ENDER_STONE)
                .setHelmet(3, Material.ENDER_STONE)
                .setHelmet(4, Material.ENDER_STONE)
                .setHelmet(5, Material.ENDER_STONE);

        holo = new ArmorStandBuilder(location.clone().add(0, 4.5, 0), identifier)
                .addStandUnder(4, 0.5)
                .setCustomName(1, "§8( §r" + ChatColor.translateAlternateColorCodes('&', prefix) + " §8)")
                .setCustomName(2, "§7Status§8: " + getStringFromState())
                .setCustomName(3, "§7Nutze das Schild um den Vorgang zu starten§8.")
                .setCustomName(4, "§7Zeit um den Altar einzunehmen§8: §f§o" + MathUtil.getMinutesAndSecondsFromSeconds(timeToCapture/1000));

        messageUtil.log("Loaded PvPAltar " + id);

        while (base == null)
            spawnAltar();
    }

    public void startCapturing(final Player player) {
        setState(1);
        setPlayerUUID(player.getUniqueId());

        player.playSound(player.getLocation(), Sound.CAT_MEOW, 0.5f, 1);
        messageUtil.broadcast(prefix + "§7 wird von §f§o" + player.getName() + "§7 eingenommen§8,§7 töte ihn um den Vorgang abzubrechen§8.");
        messageUtil.sendMessage(player, "§7Entferne dich nicht vom Altar§8!§7 Überlebe um die Belohnung zu erhalten§8.");

        holo.setCustomName(2, "§7Status§8: " + getStringFromState() + "§8 (§f§o" + player.getName() + "§8)");
        holo.setCustomNameVisible(4, true);

        runnable = new BukkitRunnable() {
            final long neededMillis = System.currentTimeMillis() + timeToCapture;
            int ticks = 0;
            @Override
            public void run() {

                if(player.getLocation().distance(base.getLocation()) > 20) {
                    wentTooFar();
                    cancel();
                    return;
                }

                holo.setCustomName(3, getProgressbar(neededMillis - System.currentTimeMillis()));
                holo.setCustomName(4, "§7" + MathUtil.getMinutesAndSecondsFromSeconds((neededMillis - System.currentTimeMillis()) / 1000));

                ticks++;

                if(neededMillis <= System.currentTimeMillis()) {
                    end();
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(CrownMain.getInstance(), 0, 20);
    }

    public void end() {

        final Player player = Bukkit.getPlayer(playerUUID);

        pvPAltarHandler.getCapturing().remove(playerUUID);

        player.sendMessage("");
        messageUtil.sendMessage(player, "Du hast es Geschafft§8,§7 deine Belohnung§8:");
        messageUtil.sendMessage(player, "§8  - §7Money§8: §e§o" + messageUtil.formatLong(moneyReward) + "§6§l$");
        messageUtil.sendMessage(player, "§8  - §7Elo§8: §f§o" + messageUtil.formatLong(eloReward));
        messageUtil.sendMessage(player, "§8  - §7XP§8: §f§o" + messageUtil.formatLong(xpReward));
        messageUtil.sendMessage(player, "§8  - §7Items§8: §f§o" + messageUtil.formatLong(itemRewards.size()));
        player.sendMessage("");

        player.playSound(player.getLocation(), Sound.FIREWORK_TWINKLE2, 0.5f, 1);

        new BukkitRunnable() {
            @Override
            public void run() {

                userHandler.getUser(playerUUID).thenAcceptAsync(user -> {
                    user.addLong(DataType.MONEY, moneyReward);
                    user.addLong(DataType.ELOPOINTS,eloReward);
                    user.addXP(xpReward);
                });

                if(!getItemRewards().isEmpty()) {
                    for (final ItemStack item : getItemRewards()) {
                        if(item == null || item.getType() == Material.AIR)
                            continue;

                        InventoryUtil.addItem(player, item);
                    }
                }

                startCooldown();
            }
        }.runTask(CrownMain.getInstance());
    }

    public void died(final Player player, final Player killer) {
        if(!player.getName().equalsIgnoreCase(killer.getName())) {
            messageUtil.broadcast("§8(" + prefix + "§8) §c§o" + player.getName() + "§7 wurde von §c§o" + killer.getName() + "§7 getötet§8.");
            messageUtil.broadcast("§8(" + prefix + "§8) §c§o" + player.getName() + "§7 hat es nicht geschafft den Altar einzunehmen§8.");
        }

        final long a = moneyReward / 4;
        final long b = xpReward / 4;
        final long c = eloReward / 4;

        killer.sendMessage("");
        messageUtil.sendMessage(killer, "Du hast " + player.getName() + " aufgehalten§8,§7 deine Belohnung§8:");
        messageUtil.sendMessage(killer, "§8  - §7Money§8: §e§o" + messageUtil.formatLong(a) + "§6§l$");
        messageUtil.sendMessage(killer, "§8  - §7Elo§8: §f§o" + messageUtil.formatLong(c));
        messageUtil.sendMessage(killer, "§8  - §7XP§8: §f§o" + messageUtil.formatLong(b));
        killer.sendMessage("");

        userHandler.getUser(killer.getUniqueId()).thenAcceptAsync(user -> {
            user.addLong(DataType.MONEY, a);
            user.addLong(DataType.ELOPOINTS, c);
            user.addLong(DataType.XP, b);
        });

        pvPAltarHandler.getCapturing().remove(playerUUID);
        startCooldown();
    }

    public void wentTooFar() {
        final Player player = Bukkit.getPlayer(playerUUID);

        if(player != null) {
            player.sendMessage("");
            messageUtil.sendMessage(player, "Du hast dich zu weit vom Altar entfernt§8.");
            messageUtil.sendMessage(player, "§8  - §7Money§8: §c§o-" + messageUtil.formatLong(moneyPunish) + "§4§l$");
            messageUtil.sendMessage(player, "§8  - §7Elo§8: §c§o-" + messageUtil.formatLong(eloPunish));
            player.sendMessage("");

            player.playSound(player.getLocation(), Sound.EXPLODE, 0.2f, 1);
        }

        leave();
    }

    public void leave() {
        if(playerUUID == null)
            return;

        pvPAltarHandler.block(playerUUID, 1000*60*60);
        pvPAltarHandler.getCapturing().remove(playerUUID);

        userHandler.getUser(playerUUID).thenAcceptAsync(user -> {
            user.removeLong(DataType.MONEY, moneyPunish);
            user.removeLong(DataType.ELOPOINTS, eloPunish);
        });

        startCooldown();
    }

    private void startCooldown() {
        runnable.cancel();
        playerUUID = null;
        state = 2;

        cooldownUntilMillis = System.currentTimeMillis() + cooldownMillis;

        runnable = new BukkitRunnable() {
            @Override
            public void run() {

                if(cooldownUntilMillis <= System.currentTimeMillis()) {
                    shutdown();
                    spawnAltar();
                    cancel();
                    return;
                }

                holo.setCustomName(2, "§7Status§8: " + getStringFromState());
                holo.setCustomName(3, "§7Zeit bis der Altar wieder eingenommen werden kann§8.");
                holo.setCustomName(4, "§f§o" + MathUtil.getMinutesAndSecondsFromSeconds((cooldownUntilMillis - System.currentTimeMillis()) / 1000));
            }
        }.runTaskTimer(CrownMain.getInstance(), 0, 20);
    }

    private String getProgressbar(final long neededMillis) {
        final long percent = ((timeToCapture - neededMillis) * 100 / timeToCapture) / 5;
        String bar = "";

        for (int i = 0; i < percent; i++) {
            bar = bar + "§a▌";
        }

        if(20 - percent > 0) {
            for (int i = 0; i < 20-percent; i++) {
                bar = bar + "§7▌";
            }
        }

        return bar;
    }

    private String getStringFromState() {
        if(state == 0)
            return "§f§lOffen";

        if(state == 1)
            return "§a§lWird eingenommen";

        return "§c§lCooldown";
    }

    public void shutdown() {
        save();

        state = 0;
        playerUUID = null;

        if(runnable != null)
            runnable.cancel();

        if(base != null)
            base.delete();

        if(holo != null)
            holo.delete();

        for (final Entity entity : location.getChunk().getEntities()) {
            if(!(entity instanceof ArmorStand))
                continue;

            if(entity.getCustomName() == null)
                continue;

            if(entity.getCustomName().startsWith(identifier))
                entity.remove();
        }
    }

    public void delete() {
        final String path = "altars." + id;

        cfg.set(path, null);
    }

    public void save() {
        final String path = "altars." + id + ".";

        cfg.set(path + "moneyReward", moneyReward);
        cfg.set(path + "eloReward", eloReward);
        cfg.set(path + "xpReward", xpReward);
        cfg.set(path + "eloPunish", eloPunish);
        cfg.set(path + "moneyPunish", moneyPunish);
        cfg.set(path + "itemRewards", itemRewards);
        cfg.set(path + "cooldownMillis", cooldownMillis);
        cfg.set(path + "timeToCapture", timeToCapture);
        cfg.set(path + "prefix", prefix);
        cfg.set(path + "location", LocationUtil.encode(location));
    }

}
