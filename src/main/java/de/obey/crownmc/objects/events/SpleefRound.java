package de.obey.crownmc.objects.events;
/*

    Author - Obey -> CrownMc
       12.08.2023 / 11:18

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.LocationHandler;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class SpleefRound {

    private final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
    private final LocationHandler locationHandler = CrownMain.getInstance().getInitializer().getLocationHandler();

    @Getter
    private int state = 0; // 0 = waiting, 1 = startetd, 2 = ended

    @Getter
    private final ArrayList<Player> teilnehmer = new ArrayList<>();

    private final HashMap<UUID, Location> savedLocations = new HashMap<>();
    private final HashMap<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private final HashMap<UUID, ItemStack[]> savedArmor = new HashMap<>();
    @Getter
    private final HashMap<Player, Long> lastMoved = new HashMap<>();
    private final HashMap<UUID, Long> trackingTime = new HashMap<>();

    private final Location startLocation;

    private final String prefix = "§8(§f§lSpleef§8) §7";

    private BukkitTask runnable;

    @Getter
    private final HashMap<Block, Long> blocks = new HashMap<>();

    public SpleefRound(final Location location) {
        startLocation = location;

        messageUtil.broadcast(prefix + "Ein Spleef Event wurde gestartet§8,§7 es startet in 30 Sekunden§8.");
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            messageUtil.sendHoverTextCommand(onlinePlayer, prefix + " §8» §7Klicke hier um §a§obeizutreten§8.", "/spleef join");
            onlinePlayer.sendMessage("");
        }


        runnable = new BukkitRunnable() {
            int ticks = 30;
            @Override
            public void run() {
                ticks--;

                if(ticks == 20 || ticks == 10) {
                    sendMessageToAll("Die Runde startet in " + ticks + " Sekunden§8.");
                }

                if(ticks <= 5 && ticks > 1)
                    sendMessageToAll("Die Runde startet in " + ticks + " Sekuden§8.");

                if(ticks == 0)
                    start();
            }
        }.runTaskTimer(CrownMain.getInstance(), 20, 20);
    }

    private void start() {
        if(teilnehmer.size() <= 1) {
            sendMessageToAll("Zu Wenig mitspieler§8.");
            end();
            return;
        }

        state = 1;
        sendMessageToAll("Die Runde hat begonnen§8,§7 viel Glück§8!");

        runnable.cancel();

        for (Player player : teilnehmer)
            lastMoved.put(player, System.currentTimeMillis());

        runnable = new BukkitRunnable() {

            @Override
            public void run() {
                if(state != 1) {
                    cancel();
                    return;
                }

                for (final Player player : teilnehmer) {
                    if(System.currentTimeMillis() - lastMoved.get(player) >= 7000) {
                        player.teleport(player.getLocation().clone().add(0, -1.5, 0));
                        lastMoved.put(player, System.currentTimeMillis());
                    }
                }

                for (final Block block : blocks.keySet()) {
                    if(System.currentTimeMillis() >= blocks.get(block)) {
                        if(block.getType() == Material.SNOW_BLOCK) {
                            block.setType(Material.EMERALD_BLOCK);
                            blocks.put(block, System.currentTimeMillis() + 800);
                            continue;
                        }

                        if(block.getType() == Material.EMERALD_BLOCK) {
                            block.setType(Material.REDSTONE_BLOCK);
                            blocks.put(block, System.currentTimeMillis() + 500);
                            continue;
                        }

                        if(block.getType() == Material.REDSTONE_BLOCK)
                            block.setType(Material.AIR);
                    }
                }
            }
        }.runTaskTimer(CrownMain.getInstance(), 1, 1);
    }

    public void playerJoin(final Player player) {
        if(state != 0) {
            messageUtil.sendMessage(player, "Die Spleef Runde hat bereits begonnen§8.");
            return;
        }

        if(teilnehmer.contains(player)) {
            messageUtil.sendMessage(player, "Du nimmst bereits an der Spleef Runde teil§8.");
            return;
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
        teilnehmer.add(player);

        trackingTime.put(player.getUniqueId(), System.currentTimeMillis());
        lastMoved.put(player, System.currentTimeMillis());

        savedLocations.put(player.getUniqueId(), player.getLocation());
        savedInventories.put(player.getUniqueId(), player.getInventory().getContents());
        savedArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());

        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        player.setVelocity(new Vector(0, 3, 0));

        new BukkitRunnable() {
            @Override
            public void run() {
                locationHandler.teleportToLocation(player, startLocation);

                for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                    player.removePotionEffect(activePotionEffect.getType());
                }

                sendMessageToAll(player.getName() + " hat die Runde §a§obetreten§8. (§f§o" + teilnehmer.size() + " Spieler§8)");

            }
        }.runTaskLater(CrownMain.getInstance(), 10);
    }

    public void quit(final Player player) {
        teilnehmer.remove(player);

        player.playSound(player.getLocation(), Sound.VILLAGER_HIT, 1, 0.5f);

        if(savedLocations.containsKey(player.getUniqueId()))
            player.teleport(savedLocations.get(player.getUniqueId()));

        if(savedInventories.containsKey(player.getUniqueId()))
            player.getInventory().setContents(savedInventories.get(player.getUniqueId()));

        if(savedArmor.containsKey(player.getUniqueId()))
            player.getInventory().setArmorContents(savedArmor.get(player.getUniqueId()));

        lastMoved.remove(player);

        savedLocations.remove(player.getUniqueId());
        savedInventories.remove(player.getUniqueId());
        savedArmor.remove(player.getUniqueId());

        final long survived = System.currentTimeMillis() - trackingTime.get(player.getUniqueId());

        sendMessageToAll(player.getName() + " ist ausgeschieden§8. (§f§o" + teilnehmer.size() + " Spieler §8)");
        messageUtil.sendMessage(player, prefix + "Du bist ausgeschieden§c§o :C§8!");
        messageUtil.sendMessage(player, "Du hast §f" + MathUtil.getHoursAndMinutesAndSecondsFromSeconds(survived/1000) + " überlebt§8.");

        if(teilnehmer.size() == 1) {
            state = 3;
            runnable.cancel();
            end();
        }

    }

    private void end() {
        state = 2;
        final Player player = teilnehmer.get(0);
        messageUtil.broadcast("Die Spleef Runde ist beendet§8,§7 gewonnen hat §f§o" + player.getName() + "§8!");

        messageUtil.sendMessage(player, "Du hast §f§o" + MathUtil.getMinutesAndSecondsFromSeconds((System.currentTimeMillis() - trackingTime.get(player.getUniqueId()))/1000));

        if(savedLocations.containsKey(player.getUniqueId()))
            player.teleport(savedLocations.get(player.getUniqueId()));

        if(savedInventories.containsKey(player.getUniqueId()))
            player.getInventory().setContents(savedInventories.get(player.getUniqueId()));

        if(savedArmor.containsKey(player.getUniqueId()))
            player.getInventory().setArmorContents(savedArmor.get(player.getUniqueId()));

        savedLocations.remove(player.getUniqueId());
        savedInventories.remove(player.getUniqueId());
        savedArmor.remove(player.getUniqueId());

        lastMoved.clear();
        teilnehmer.clear();

        for (final Block block : blocks.keySet()) {
            block.setType(Material.SNOW_BLOCK);
        }

        CrownMain.getInstance().getInitializer().getSpleefHandler().setSpleefRound(null);
    }

    private void sendMessageToAll(final String message) {
        if(teilnehmer.isEmpty())
            return;

        for (final Player player : teilnehmer) {
            messageUtil.sendMessage(player,"§8(§f§lSpleef§8) §7" +  message);
        }
    }



}
