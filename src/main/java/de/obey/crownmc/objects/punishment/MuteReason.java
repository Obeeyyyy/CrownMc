package de.obey.crownmc.objects.punishment;
/*

    Author - Obey -> CrownMc
       14.07.2023 / 01:50

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter @Setter
public final class MuteReason {

    private final int id;

    private String name = "change", permission = "crown.change";
    private long duration = 1000;

    private final YamlConfiguration cfg;

    final String path;

    public MuteReason(final int id, final YamlConfiguration cfg) {
        this.id = id;
        this.cfg = cfg;
        path = "mute." + id + ".";

        if(cfg.contains(path + "permission"))
            permission = cfg.getString(path + "permission");

        if(cfg.contains(path + "name"))
            name = cfg.getString(path + "name");

        if(cfg.contains(path + "duration"))
            duration = cfg.getLong(path + "duration");
    }

    public void save() {
        cfg.set(path + "name", name);
        cfg.set(path + "permission", permission);
        cfg.set(path + "duration", duration);
    }

}
