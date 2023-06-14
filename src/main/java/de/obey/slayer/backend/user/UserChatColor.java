package de.obey.slayer.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       18.01.2023 / 08:52

*/

import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;

public final class UserChatColor {

    @Setter
    private String activeColor = "";

    private final ArrayList<String> colorList;

    private final YamlConfiguration cfg;

    public UserChatColor(final User user) {
        cfg = user.getCfg();

        final String readActiveColor = cfg.getString("activecolor");
        final ArrayList<String> readColoList= (ArrayList<String>) cfg.getList("colorlist");

        activeColor = readActiveColor == null ? "&7" : readActiveColor;
        colorList = readColoList == null ? new ArrayList<>() : readColoList;
    }

}
