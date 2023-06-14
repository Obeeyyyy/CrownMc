package de.obey.slayer.objects;
/*

    Author - Obey -> SkySlayer-v4
       17.11.2022 / 13:50

*/

import de.obey.slayer.SlayerMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Getter
@Setter
public final class Kit {

    @Setter(AccessLevel.NONE)
    private final File kitFile;

    private long kitCooldown = 0;
    private long buyOutForSecondPrice = 0;

    private String permission = "slayer.*";
    private ArrayList<ItemStack> items = new ArrayList<>();

    private int showSlot = 15;
    private Material showMaterial = Material.BARRIER;
    private String prefix, name;

    public Kit(final File file) {
        kitFile = file;
        name = kitFile.getName().replace(".yml", "");
        prefix = name;

        loadKitData();
    }

    public void loadKitData() {
        if (!kitFile.exists()) {
            try {
                kitFile.createNewFile();
                return;
            } catch (final IOException ignored) {
            }
        }

        final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(kitFile);

        if (cfg.contains("cooldown"))
            kitCooldown = cfg.getLong("cooldown");

        if (cfg.contains("permission"))
            permission = cfg.getString("permission");

        if (cfg.contains("items"))
            items = (ArrayList<ItemStack>) cfg.getList("items");

        if (cfg.contains("price"))
            buyOutForSecondPrice = cfg.getLong("price");

        if (cfg.contains("slot"))
            showSlot = cfg.getInt("slot");

        if (cfg.contains("prefix"))
            prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString("prefix"));

        if (cfg.contains("showmaterial"))
            showMaterial = Material.getMaterial(cfg.getString("showmaterial"));

        SlayerMain.getInstance().getInitializer().getMessageUtil().log("Loaded Kit " + name);
    }

    public void saveKitData() {
        final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(kitFile);

        cfg.set("cooldown", kitCooldown);
        cfg.set("permission", permission);
        cfg.set("items", items);
        cfg.set("price", buyOutForSecondPrice);
        cfg.set("slot", showSlot);
        cfg.set("prefix", prefix);
        cfg.set("showmaterial", showMaterial.name());

        SlayerMain.getInstance().getInitializer().getMessageUtil().log("Saved Kit " + name);

        try {
            cfg.save(kitFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
