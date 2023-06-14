package de.obey.slayer.backend;
/*

    Author - Obey -> SkySlayer-v4
       14.10.2022 / 18:35

*/

import de.obey.slayer.util.FileUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class Rang {

    @Getter
    @Setter
    private String name, chatPrefix = "", chatSuffix = "", tabPrefix = "", tabSuffix = "", ntPrefix = "", ntSuffix = "", showprefix = "", sort = "000", chatcolor = "§7";

    @Getter
    @Setter
    private int id = 0;

    private final File file;

    public Rang(final File file) {
        this.file = file;

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
            }
        }

        name = file.getName().replace(".yml", "");
        showprefix = "§f§l" + name.toUpperCase();

        final YamlConfiguration cfg = FileUtil.getCfg(file);

        if (cfg.contains("chatprefix"))
            chatPrefix = cfg.getString("chatprefix");

        if (cfg.contains("chatsuffix"))
            chatSuffix = cfg.getString("chatsuffix");

        if (cfg.contains("tabprefix"))
            tabPrefix = cfg.getString("tabprefix");

        if (cfg.contains("tabsuffix"))
            tabSuffix = cfg.getString("tabsuffix");

        if (cfg.contains("ntprefix"))
            ntPrefix = cfg.getString("ntprefix");

        if (cfg.contains("ntsuffix"))
            ntSuffix = cfg.getString("ntsuffix");

        if (cfg.contains("showprefix"))
            showprefix = ChatColor.translateAlternateColorCodes('&', cfg.getString("showprefix"));

        if (cfg.contains("sort"))
            sort = cfg.getString("sort");

        if (cfg.contains("id"))
            id = cfg.getInt("id");

        if (cfg.contains("chatcolor"))
            chatcolor = cfg.getString("chatcolor");
    }

    public void save() {
        final YamlConfiguration cfg = FileUtil.getCfg(file);

        cfg.set("chatprefix", chatPrefix);
        cfg.set("chatsuffix", chatSuffix);
        cfg.set("tabprefix", tabPrefix);
        cfg.set("tabsuffix", tabSuffix);
        cfg.set("ntprefix", ntPrefix);
        cfg.set("ntsuffix", ntSuffix);
        cfg.set("showprefix", showprefix);
        cfg.set("sort", sort);
        cfg.set("id", id);
        cfg.set("chatcolor", chatcolor);

        FileUtil.saveToFile(file, cfg);
    }

}
