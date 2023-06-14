package de.obey.slayer.util;
/*

    Author - Obey -> SkySlayer-v4
       24.10.2022 / 23:51

*/

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public final class ArmorStandPacketBuilder {

    private final ArrayList<EntityArmorStand> stands = new ArrayList<>();
    private Location location;

    public ArmorStandPacketBuilder(final Location location) {
        this.location = location;
    }

    public ArmorStandPacketBuilder setLocation(final Location location) {
        this.location = location;
        return this;
    }

    public ArmorStandPacketBuilder addStand() {
        final EntityArmorStand stand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());

        stand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        location.add(0, -0.25, 0);
        stands.add(stand);

        return this;
    }

    public ArmorStandPacketBuilder addStand(final int amount) {

        for (int i = 0; i < amount; i++)
            addStand();

        return this;
    }

    public ArmorStandPacketBuilder setGravity(final boolean state) {
        stands.forEach(stand -> stand.setGravity(state));
        return this;
    }

    public ArmorStandPacketBuilder setVisible(final boolean state) {
        stands.forEach(stand -> stand.setInvisible(!state));
        return this;
    }

    public ArmorStandPacketBuilder setCustomName(final int stand, final String name) {

        if (stands.size() < stand)
            return this;

        stands.get(stand - 1).setCustomName(name);
        stands.get(stand - 1).setCustomNameVisible(true);

        return this;
    }

    public ArmorStandPacketBuilder setCustomNameVisible(final int stand, final boolean state) {

        if (stands.size() < stand)
            return this;

        stands.get(stand - 1).setCustomNameVisible(state);

        return this;
    }

    public void delete(final Player player) {

        if (stands.size() == 0)
            return;

        stands.forEach(stand -> {
            final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(stand.getId());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        });
    }

    public void spawn(final Player player) {

        if (stands.size() == 0)
            return;

        stands.forEach(stand -> {
            final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(stand);
            final PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(stand.getId(), stand.getDataWatcher(), true);

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metadata);
        });
    }

    public void deleteForEveryone() {
        if (stands.size() == 0)
            return;

        Bukkit.getOnlinePlayers().forEach(this::delete);
    }

    public void spawnForEveryone() {
        if (stands.size() == 0)
            return;

        Bukkit.getOnlinePlayers().forEach(this::spawn);
    }

}
