package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       16.12.2022 / 18:44

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.objects.gambling.Crash;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@RequiredArgsConstructor
public final class CrashHandler {

    @NonNull
    private final LocationHandler locationHandler;

    @Getter
    private Location crashLocationOne,  crashLocationTwo , crashLocationThree, crashLocationGraph;

    @Getter
    private Crash crash;

    public void shutdown() {
        if (crash == null)
            return;

        if (crash.getBets().isEmpty())
            return;

        if (crash.getRunnable() != null)
            crash.getRunnable().cancel();

        if (crash.getBets().isEmpty())
            return;

        for (UUID uuid : crash.getBets().keySet()) {
            crash.getUserHandler().getUserInstant(uuid).addLong(DataType.MONEY, crash.getBets().get(uuid));
        }

        crash.getBets().clear();

       killStands();
    }

    private void killStands()  {
        for (final World world : Bukkit.getWorlds()) {
            for (final Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand && entity.getCustomName() != null) {
                    String standIdentifier = "§0";
                    if (entity.getCustomName().startsWith(standIdentifier))
                        entity.remove();
                }
            }
        }
    }

    public void setupArmorStands() {

        killStands();

        if (locationHandler.getLocation("crash1") == null ||
                locationHandler.getLocation("crash2") == null ||
                locationHandler.getLocation("crash3") == null ||
                locationHandler.getLocation("crashgraph") == null) {
            return;
        }

        crashLocationOne = locationHandler.getLocation("crash1").clone();
        crashLocationTwo = locationHandler.getLocation("crash2").clone();
        crashLocationThree = locationHandler.getLocation("crash3").clone();
        crashLocationGraph = locationHandler.getLocation("crashgraph").clone();

        new BukkitRunnable() {
            @Override
            public void run() {
                crash = new Crash(CrashHandler.this);
            }
        }.runTaskLater(CrownMain.getInstance(), 10);
    }
}