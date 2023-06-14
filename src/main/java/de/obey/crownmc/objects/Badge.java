package de.obey.crownmc.objects;
/*

    Author - Obey -> SkySlayer-v4
       20.11.2022 / 14:52

*/

import de.obey.crownmc.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public final class Badge {

    private String prefix, creationDate, name;
    private String description = "Change dis";
    private ItemStack showItem;
    private int owned = 0;

    public Badge(final String name, final YamlConfiguration cfg) {
        this.name = name;
        this.prefix = "§f§l" + name;

        if (cfg.contains("badges." + name + ".prefix"))
            prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString("badges." + name + ".prefix"));

        if (cfg.contains("badges." + name + ".date"))
            creationDate = ChatColor.translateAlternateColorCodes('&', cfg.getString("badges." + name + ".date"));

        if (cfg.contains("badges." + name + ".description"))
            description = ChatColor.translateAlternateColorCodes('&', cfg.getString("badges." + name + ".description"));

        if (cfg.contains("badges." + name + ".showitem")) {
            showItem = cfg.getItemStack("badges." + name + ".showitem");
        } else {
            showItem = new ItemBuilder(Material.PAPER).build();
        }

        if (cfg.contains("badges." + name + ".owned"))
            owned = cfg.getInt("badges." + name + ".owned");
    }

    public void add() {
        owned++;
    }


    public void remove() {
        if(owned > 0)
            owned--;
    }

    public void save(final YamlConfiguration cfg) {
        cfg.set("badges." + name + ".prefix", prefix);
        cfg.set("badges." + name + ".date", creationDate);
        cfg.set("badges." + name + ".description", description);
        cfg.set("badges." + name + ".showitem", showItem);
        cfg.set("badges." + name + ".owned", owned);
    }
}
