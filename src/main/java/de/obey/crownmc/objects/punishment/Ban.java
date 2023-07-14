package de.obey.crownmc.objects.punishment;
/*

    Author - Obey -> CrownMc
       02.07.2023 / 23:59

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
public class Ban {

    final int id;
    final String author;
    final BanReason banReason;

    public Ban(final int id, final YamlConfiguration cfg) {
        this.id = id;
        author = cfg.getString("bans." + id + ".author");
        banReason = CrownMain.getInstance().getInitializer().getBanHandler().getReadsonFromID(cfg.getInt("bans." + id + ".reason"));
    }

}
