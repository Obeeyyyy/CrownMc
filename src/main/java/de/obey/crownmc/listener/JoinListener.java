package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       13.10.2022 / 17:45

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.commands.FreezeCommand;
import de.obey.crownmc.commands.VanishCommand;
import de.obey.crownmc.handler.CombatHandler;
import de.obey.crownmc.handler.LocationHandler;
import de.obey.crownmc.handler.ScoreboardHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.LabyUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PacketReader;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.ArrayList;

@RequiredArgsConstructor
@NonNull
public final class JoinListener implements Listener {

    private final MessageUtil messageUtil;
    private final LocationHandler locationHandler;
    private final ScoreboardHandler scoreboardHandler;
    private final UserHandler userHandler;
    private final ServerConfig serverConfig;
    private final CombatHandler combatHandler;

    @EventHandler
    public void on(final PlayerSpawnLocationEvent event) {
        userHandler.getUser(event.getPlayer().getUniqueId()).thenAcceptAsync(user -> {
           if(user.is(DataType.SPAWNTELEPORT)) {
               if (locationHandler.getLocation("spawn") != null) {
                   //event.setSpawnLocation(locationHandler.getLocation("spawn"));
                   event.getPlayer().teleport(locationHandler.getLocation("spawn"));
               }
           }
        });
    }

    @EventHandler
    public void on(final PlayerJoinEvent event) {
        event.setJoinMessage("");

        serverConfig.checkDailyCount();

        final Player player = event.getPlayer();

        //Freezed stuff
        if (FreezeCommand.getFreezed().contains(player.getUniqueId()))
            messageUtil.sendMessageToTeamMembers("§3§lFREEZE§8 > §f§o" + player.getName() + "§7 hat den Server wieder betreten§8.");

        for (int i = 0; i < 100; i++)
            player.sendMessage("");

        scoreboardHandler.updateEverythingForEveryone();

        // Check vanished players
        if (VanishCommand.vanished.size() > 0) {
            if (!PermissionUtil.hasPermission(player, "team", false)) {
                for (final Player vanished : VanishCommand.vanished) {
                    player.hidePlayer(vanished);
                }
            }
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));

        if (userHandler.getRegistering().contains(player)) {
            player.getInventory().clear();

            final YamlConfiguration serverCfg = serverConfig.getCfg();
            final ArrayList<ItemStack> contents = serverCfg.contains("firstjoin") ? (ArrayList<ItemStack>) serverCfg.getList("firstjoin") : new ArrayList<>();

            if (contents.size() > 0) {
                contents.forEach(item -> player.getInventory().addItem(item));
            }

            locationHandler.teleportToLocationNameInstant(player, "spawn");
        }

        if (!userHandler.getRegistering().contains(player)) {

            userHandler.getUser(event.getPlayer().getUniqueId()).thenAcceptAsync(user -> {
                user.setPlayer(player);
                user.setOfflinePlayer(player);
                user.setLong(DataType.JOINED, System.currentTimeMillis());
                user.getEnderchest().checkIfUsernameChanged(player);
                user.getPlaytime().onJoin();

                if (user.getLong(DataType.LASTSEEN) != 0) {
                  /*
                    LOGIN STREAK CHECK
                 */
                    if (System.currentTimeMillis() - user.getLong(DataType.LOGINSTREAKUPDATED) >= 86400000) {
                        if (System.currentTimeMillis() - user.getLong(DataType.LASTSEEN) >= 172798000) {
                            // BREAK STREAK
                            user.setInt(DataType.LOGINSTREAK, 1);
                        } else {
                            // CONTINUE STREAK
                            user.addInt(DataType.LOGINSTREAK, 1);
                        }

                        user.setLong(DataType.LOGINSTREAKUPDATED, System.currentTimeMillis());
                    }

                /*
                    Welcome message
                 */

                    player.sendMessage("");
                    player.sendMessage("§8§l§m-----------------------------------");
                    player.sendMessage("");
                    player.sendMessage("§8▰§7▱ §6§lWillkommen zurück §7" + player.getName());
                    player.sendMessage("");
                    //player.sendMessage("  §8- §7Du warst §f§o" + (MathUtil.getHoursAndMinutesAndSecondsFromSeconds((System.currentTimeMillis() - user.getLong(DataType.LASTSEEN)) / 1000)) + "§coffline.");
                    //player.sendMessage("  §8- §7Loginstreak§8: §f§o" + user.getInt(DataType.LOGINSTREAK) + "§8.");
                    //player.sendMessage("  §8- §7Nächster Tag in §a§o" + MathUtil.getHoursAndMinutesAndSecondsFromSeconds((86400000 - (System.currentTimeMillis() - user.getLong(DataType.LOGINSTREAKUPDATED))) / 1000));
                    player.sendMessage("");
                    player.sendMessage(" §8- §7Spieler Online§8: §e§o" + (Bukkit.getOnlinePlayers().size() - VanishCommand.vanished.size()));
                    player.sendMessage(" §8- §7Discord Server§8: §e§o/discord");
                    player.sendMessage(" §8- §7Täglicher Vote§8: §e§o/vote");
                    player.sendMessage(" §8- §7Online Shop§8: §e§oshop.crownmc.de");
                    player.sendMessage("");
                    player.sendMessage("§8§l§m-----------------------------------");
                    player.sendMessage("");
                }

                user.setPacketReader(new PacketReader(player));

                messageUtil.log("#> " + player.getName() + " JOINED ( " + messageUtil.formatLong(user.getLong(DataType.MONEY)) + "$, " + user.getInt(DataType.KILLS) + "/" + user.getInt(DataType.DEATHS) + " )");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Auto Vanish
                        if (user.is(DataType.AUTOVANISHSTATE) && PermissionUtil.hasPermission(player, "autovanish", false)) {
                            VanishCommand.vanished.add(player);

                            for (Player all : Bukkit.getOnlinePlayers()) {
                                if (!PermissionUtil.hasPermission(all, "team", false))
                                    all.hidePlayer(player);
                            }

                            messageUtil.sendMessage(player, "Du bist jetzt für alle Spieler §a§ounsichtbar§7.");

                            // Custom Joinmessage
                        } else if (user.getString(DataType.JOINMESSAGE).length() > 0 && PermissionUtil.hasPermission(player, "joinmessage", false)) {
                            messageUtil.broadcastNoPrefix("§e§lJOIN§8 × §r" + ChatColor.translateAlternateColorCodes('&', user.getString(DataType.JOINMESSAGE)).replace("%name%", player.getName()));
                        }
                    }
                }.runTaskLater(CrownMain.getInstance(), 20);
            });
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                // Remove Blindness
                player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

                //LabyUtil.sendServerBanner(player);
                LabyUtil.sendCurrentPlayingGamemode(player, "§8» §6§lCrownMc.de §7| §f§oJoin Now !");
            }
        }.runTaskLater(CrownMain.getInstance(), 20);

        if (combatHandler.isCombatLogged(player)) {
            messageUtil.sendMessageToTeamMembers("§6§lCOMBATLOG §8> §e§o" + player.getName() + "§7 ist wieder gejoint§8.");
            combatHandler.removeFromLoggers(player.getUniqueId().toString());
            player.damage(200);
        }

    }
}
