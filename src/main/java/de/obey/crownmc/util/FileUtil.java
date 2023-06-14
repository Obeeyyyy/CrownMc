package de.obey.crownmc.util;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 21:38

*/

import de.obey.crownmc.CrownMain;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@UtilityClass
public final class FileUtil {

    public File getFile(final String path) {
        final File file = new File(CrownMain.getInstance().getDataFolder().getPath() + "/" + path);

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }

        return file;
    }

    public YamlConfiguration getCfg(final File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveToFile(final File file, final YamlConfiguration cfg) {
        try {
            if (!file.exists())
                file.createNewFile();

            cfg.save(file);
        } catch (IOException ignored) {
        }
    }
}
