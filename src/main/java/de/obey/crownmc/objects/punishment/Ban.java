package de.obey.crownmc.objects.punishment;
/*

    Author - Obey -> CrownMc
       02.07.2023 / 23:59

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
public class Ban {

    private final int id;
    private final BanReason banReason;

    private final YamlConfiguration cfg;

    @Setter
    private String author;
    @Setter
    private long bannedUntil;

    public Ban(final int id, final YamlConfiguration cfg) {
        this.cfg = cfg;
        this.id = id;
        this.banReason = CrownMain.getInstance().getInitializer().getBanHandler().getReadsonFromID(cfg.getInt("bans." + id + ".reason"));

        if(banReason.getDuration() <= 0) {
            bannedUntil = -1;
        } else {
            bannedUntil = cfg.getLong("bans." + id + ".bannedUntil");
        }

        author = cfg.getString("bans." + id + ".author");
    }

    public Ban(final int id, final BanReason banReason, final YamlConfiguration cfg) {
        this.cfg = cfg;
        this.id = id;
        this.banReason = banReason;

        if(banReason.getDuration() <= 0) {
            bannedUntil = -1;
        } else {
            bannedUntil = System.currentTimeMillis() + banReason.getDuration();
        }

        save();
    }

    public void save() {
        cfg.set("bans." + id + ".reason", banReason.getId());
        cfg.set("bans." + id + ".bannedUntil", bannedUntil);
        cfg.set("bans." + id + ".author", author);
    }

}
