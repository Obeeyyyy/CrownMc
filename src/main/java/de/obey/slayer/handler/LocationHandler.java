package de.obey.slayer.handler;
/*

    Author - Obey -> SkySlayer-v4
       13.10.2022 / 00:23

*/

import com.google.common.collect.Maps;
import de.obey.slayer.SlayerMain;
import de.obey.slayer.objects.effects.TeleportEffect;
import de.obey.slayer.util.FileUtil;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class LocationHandler {

    @NonNull
    private final MessageUtil messageUtil;

    private final File file = new File(SlayerMain.getInstance().getDataFolder().getPath() + "/locations.yml");

    @Getter
    private final Map<String, Location> locations = Maps.newConcurrentMap();

    public void loadLocations() {
        final YamlConfiguration cfg = FileUtil.getCfg(file);

        if (!cfg.contains("locations"))
            return;

        for (String key : cfg.getConfigurationSection("locations").getKeys(false)) {
            locations.put(key, decode(cfg.getString("locations." + key)));
            messageUtil.log("Loaded location < " + key + " >");
        }
    }

    private String encode(final Location location) {
        return "#" + location.getWorld().getName() + "#" + location.getX() + "#" + location.getY() + "#" + location.getZ() + "#" + location.getYaw() + "#" + location.getPitch();
    }

    private Location decode(final String value) {

        //#world#x#y#z#yaw#pitch

        final String[] splitted = value.split("#");
        final Location location = new Location(Bukkit.getWorld(splitted[1]), Float.parseFloat(splitted[2]), Float.parseFloat(splitted[3]), Float.parseFloat(splitted[4]));

        location.setYaw(Float.parseFloat(splitted[5]));
        location.setPitch(Float.parseFloat(splitted[6]));

        return location;
    }

    public void setLocation(final String name, final Location location) {
        locations.put(name, location);

        final YamlConfiguration cfg = FileUtil.getCfg(file);

        cfg.set("locations." + name, encode(location));

        FileUtil.saveToFile(file, cfg);
    }

    public void deleteLocation(final String name) {
        locations.remove(name);

        final YamlConfiguration cfg = FileUtil.getCfg(file);

        cfg.set("locations." + name, null);

        FileUtil.saveToFile(file, cfg);
    }

    public Location getLocation(final String name) {
        return locations.get(name);
    }

    private final ArrayList<UUID> isTeleporting = new ArrayList<UUID>();

    public void teleportToLocationNameInstant(final Player player, final String locationName) {
        if (isTeleporting.contains(player.getUniqueId())) {
            messageUtil.sendMessage(player, "Du wirst bereits teleportiert§8.");
            return;
        }

        final Location location = getLocation(locationName);

        if (location == null) {
            messageUtil.sendMessage(player, "Die Location " + locationName + " existiert nicht§8.");
            return;
        }

        player.teleport(location);
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
        player.setAllowFlight(false);
        player.setFlying(false);
    }

    public void teleportToLocationName(final Player player, final String locationName) {
        if (isTeleporting.contains(player.getUniqueId())) {
            messageUtil.sendMessage(player, "Du wirst bereits teleportiert§8.");
            return;
        }

        final Location location = getLocation(locationName);

        if (location == null) {
            messageUtil.sendMessage(player, "Die Location " + locationName + " existiert nicht§8.");
            return;
        }

        teleportToLocation(player, location);
    }

    public void teleportToLocation(final Player player, final Location location) {

        // Teleport with animation
        if(!PermissionUtil.hasPermission(player, "*", false)) {
            if (player.getWorld().getName().equalsIgnoreCase("pvp")) {

                final TeleportEffect teleportEffect = new TeleportEffect();
                final int cooldown = 5;

                isTeleporting.add(player.getUniqueId());
                teleportEffect.run(player, 1);

                new BukkitRunnable() {

                    final Location saved = player.getLocation();
                    int remain = cooldown;
                    int ticks = 0;

                    @Override
                    public void run() {
                        if (ticks >= cooldown) {
                            teleportEffect.stop();
                            isTeleporting.remove(player.getUniqueId());
                            player.teleport(location);
                            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);

                            if (player.getGameMode() == GameMode.SURVIVAL) {
                                player.setAllowFlight(false);
                                player.setFlying(false);
                            }

                            cancel();
                            return;
                        }

                        if ((player.getLocation().getX() - saved.getX() > 0.5 || player.getLocation().getX() - saved.getX() < -0.5) ||
                                (player.getLocation().getZ() - saved.getZ() > 0.5 || player.getLocation().getZ() - saved.getZ() < -0.5)) {
                            teleportEffect.stop();
                            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 0.4f, 0.4f);
                            isTeleporting.remove(player.getUniqueId());
                            messageUtil.sendMessage(player, "§c§oTeleportation abgebrochen§8.");
                            cancel();
                            return;
                        }

                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);

                        messageUtil.sendMessage(player, "Teleportiert in §a§o" + remain + "§8.");

                        remain--;
                        ticks++;
                    }
                }.runTaskTimer(SlayerMain.getInstance(), 0, 20);

                return;
            }
        }

        //Teleport instant
        player.teleport(location);
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
    }
}
