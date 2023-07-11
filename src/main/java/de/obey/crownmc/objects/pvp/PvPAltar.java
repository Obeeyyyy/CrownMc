package de.obey.crownmc.objects.pvp;
/*

    Author - Obey -> CrownMc
       11.07.2023 / 03:45

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.util.ArmorStandBuilder;
import de.obey.crownmc.util.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@Getter @Setter
public final class PvPAltar {

    private final YamlConfiguration cfg;

    private final int id;
    private int state = 0; // 0 = idle , 1 = wird eingenommen, 2 = cooldown

    private long eloReward, moneyReward, xpReward;

    private ArrayList<ItemStack> itemRewards;
    private Location location;

    private String prefix;

    private final String identifier = "§8§8";

    private ArmorStandBuilder base, holo;

    public PvPAltar(final int id, final YamlConfiguration cfg) {
        this.id = id;
        this.cfg = cfg;

        final String path = "altars." + id + ".";

        moneyReward = cfg.getLong(path + "moneyReward", 0);
        eloReward = cfg.getLong(path + "eloReward", 0);
        xpReward = cfg.getLong(path + "xpReward", 0);
        itemRewards = (ArrayList<ItemStack>) cfg.getList(path + "itemReward", new ArrayList<>());
        location = LocationUtil.decode(cfg.getString(path + "location"));
        prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString(path + "prefix", "CHANGEME"));

        spawnAltar();
    }

    public void spawnAltar() {
        if(location == null)
            return;

        base = new ArmorStandBuilder(location, identifier)
                .addStandAbove(5, 0.6)
                .setHelmet(1, Material.ENDER_STONE)
                .setHelmet(2, Material.ENDER_STONE)
                .setHelmet(3, Material.ENDER_STONE)
                .setHelmet(4, Material.ENDER_STONE)
                .setHelmet(5, Material.ENDER_STONE);
    }

    public void shutdown() {
        save();

        if(base != null)
            base.delete();

        if(holo != null)
            holo.delete();

        for (final Entity entity : location.getWorld().getEntities()) {
            if(!(entity instanceof ArmorStand))
                continue;

            if(entity.getCustomName() == null)
                continue;

            if(entity.getCustomName().startsWith(identifier))
                entity.remove();
        }
    }

    public void save() {
        final String path = "altars." + id + ".";

        cfg.set(path + "moneyReward", moneyReward);
        cfg.set(path + "eloReward", eloReward);
        cfg.set(path + "xpReward", xpReward);
        cfg.set(path + "itemReward", itemRewards);
        cfg.set(path + "prefix", prefix);
        cfg.set(path + "location", LocationUtil.encode(location));
    }

}
