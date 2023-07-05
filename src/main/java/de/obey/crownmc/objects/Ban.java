package de.obey.crownmc.objects;
/*

    Author - Obey -> CrownMc
       02.07.2023 / 23:59

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
public class Ban {

    final String id, author, reason;
    final long duration;

    public Ban(final String id, final YamlConfiguration cfg) {
        this.id = id;
        author = cfg.getString("bans." + id + ".author");
        reason = cfg.getString("bans." + id + ".reason");
        duration = cfg.getLong("bans." + id + ".duration");
    }

}
