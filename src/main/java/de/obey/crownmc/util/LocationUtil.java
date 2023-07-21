package de.obey.crownmc.util;
/*

    Author - Obey -> CrownMc
       11.07.2023 / 04:01

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;

import java.util.logging.Level;

@UtilityClass
public final class LocationUtil {

    public String encodeEuler(final EulerAngle angle) {
        return "#" + angle.getX() + "#" + angle.getY() + "#" + angle.getZ();
    }

    public EulerAngle decodeEuler(final String value) {
        final String[] splitted = value.split("#");

        if(splitted.length < 3) {
            return null;
        }

        return new EulerAngle(Double.parseDouble(splitted[1]),
                Double.parseDouble(splitted[2]),
                Double.parseDouble(splitted[3]));
    }

    public String encode(final Location location) {
        return "#" + location.getWorld().getName() + "#" + location.getX() + "#" + location.getY() + "#" + location.getZ() + "#" + location.getYaw() + "#" + location.getPitch();
    }

    public Location decode(final String value) {

        //#world#x#y#z#yaw#pitch

        final String[] splitted = value.split("#");

        if(splitted.length <= 5) {
            return null;
        }

        final World world = Bukkit.getWorld(splitted[1]);

        if(world == null) {
            Bukkit.getLogger().log(Level.WARNING, "World '" + splitted[1] + "' does not exist.");

            return null;
        }

        final Location location = new Location(world, Float.parseFloat(splitted[2]), Float.parseFloat(splitted[3]), Float.parseFloat(splitted[4]));

        location.setYaw(Float.parseFloat(splitted[5]));
        location.setPitch(Float.parseFloat(splitted[6]));

        return location;
    }

}
