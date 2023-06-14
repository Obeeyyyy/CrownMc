package de.obey.crownmc.objects;
/*

    Author - Obey -> SkySlayer-v4
       07.11.2022 / 18:12

*/

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
@Setter
public final class Warp {

    private final String warpName;
    private String prefix;
    private Material showMaterial = Material.BARRIER;
    private int slot = 15;

    public Warp(final String warpName, final YamlConfiguration cfg) {
        this.warpName = warpName;

        final String path = "warps." + warpName + ".";

        if (cfg.contains(path + "slot"))
            slot = cfg.getInt(path + "slot");

        if (cfg.contains(path + "prefix"))
            prefix = cfg.getString(path + "prefix");

        if (cfg.contains(path + "material"))
            showMaterial = Material.getMaterial(cfg.getString(path + "material"));
    }

}
