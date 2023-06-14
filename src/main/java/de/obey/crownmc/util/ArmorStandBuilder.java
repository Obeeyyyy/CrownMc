package de.obey.crownmc.util;
/*

    Author - Obey -> SkySlayer-v4
       24.10.2022 / 23:51

*/

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public final class ArmorStandBuilder {

    private final ArrayList<ArmorStand> stands = new ArrayList<>();

    private String identifier = "";

    @Getter
    private Location location;

    public ArmorStandBuilder(final Location location) {
        this.location = location;
    }

    public ArmorStandBuilder(final Location location, final String identifier) {
        this.identifier = identifier;
        this.location = location;
    }

    public void teleport(final Location location) {
        this.location = location.clone();
        double yOfflset = 0;

        for (final ArmorStand stand : stands) {
            stand.teleport(location.clone().add(0, yOfflset, 0));
            yOfflset -= 0.25;
        }
    }

    public void delete() {
        stands.forEach(Entity::remove);
        stands.clear();
    }

    public ArmorStandBuilder setLocation(final Location location) {
        this.location = location.clone();
        return this;
    }

    public ArmorStandBuilder addStand() {
        if(location == null)
            return this;

        final ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);

        location.add(0, -0.25, 0);
        stand.setGravity(false);
        stand.setVisible(false);
        stands.add(stand);

        return this;
    }

    public ArmorStandBuilder addStand(final int amount) {

        for (int i = 0; i < amount; i++)
            addStand();

        return this;
    }

    public ArmorStandBuilder setCustomName(final int stand, final String name) {

        if (stands.size() < stand)
            return this;

        stands.get(stand - 1).setCustomName(identifier + name);
        stands.get(stand - 1).setCustomNameVisible(true);

        return this;
    }

    public ArmorStandBuilder setCustomNameVisible(final int stand, final boolean state) {

        if (stands.size() < stand)
            return this;

        stands.get(stand - 1).setCustomNameVisible(state);

        return this;
    }

    public ArmorStandBuilder setItem(final int stand, final Material material) {
        if (stands.size() < stand)
            return this;

        final ArmorStand as = stands.get(stand - 1);

        if(as.getPassenger() != null)
            as.getPassenger().remove();

        final Item drop = as.getWorld().dropItem(as.getLocation(), new ItemStack(material));

        drop.setPickupDelay(Integer.MAX_VALUE);
        as.setPassenger(drop);

        return this;
    }

    public ArmorStandBuilder addLocation(final double x, final double y, final double z) {
        location.add(x, y, z);
        return this;
    }

    public ArrayList<ArmorStand> stands() {
        return stands;
    }
}
