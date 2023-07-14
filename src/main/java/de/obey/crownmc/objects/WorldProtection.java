package de.obey.crownmc.objects;
/*

    Author - Obey -> SkySlayer-v4
       26.11.2022 / 17:54

*/

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;

public final class WorldProtection {

    @Getter
    @Setter
    private boolean pvp, build, mobspawn, blockexplosion, enderpearl, interact, fly, pve, homes, projectiles, itemDrops;

    private final String worldName;

    public WorldProtection(final String worldName, final YamlConfiguration cfg) {
        this.worldName = worldName;

        pvp = cfg.getBoolean("worlds." + worldName + ".pvp");
        build = cfg.getBoolean("worlds." + worldName + ".build");
        mobspawn = cfg.getBoolean("worlds." + worldName + ".mobspawn");
        blockexplosion = cfg.getBoolean("worlds." + worldName + ".blockexplosion");
        enderpearl = cfg.getBoolean("worlds." + worldName + ".enderpearl");
        interact = cfg.getBoolean("worlds." + worldName + ".interact");
        fly = cfg.getBoolean("worlds." + worldName + ".fly");
        pve = cfg.getBoolean("worlds." + worldName + ".pve");
        homes = cfg.getBoolean("worlds." + worldName + ".homes");
        projectiles = cfg.getBoolean("worlds." + worldName + ".projectiles");
        itemDrops = cfg.getBoolean("worlds." + worldName + ".itemdrops");
    }

    public void saveWorldProtection(final YamlConfiguration cfg) {
        cfg.set("worlds." + worldName + ".pvp", pvp);
        cfg.set("worlds." + worldName + ".build", build);
        cfg.set("worlds." + worldName + ".mobspawn", mobspawn);
        cfg.set("worlds." + worldName + ".blockexplosion", blockexplosion);
        cfg.set("worlds." + worldName + ".enderpearl", enderpearl);
        cfg.set("worlds." + worldName + ".interact", interact);
        cfg.set("worlds." + worldName + ".fly", fly);
        cfg.set("worlds." + worldName + ".pve", pve);
        cfg.set("worlds." + worldName + ".homes", homes);
        cfg.set("worlds." + worldName + ".projectiles", projectiles);
        cfg.set("worlds." + worldName + ".itemdrops", itemDrops);
    }

}
