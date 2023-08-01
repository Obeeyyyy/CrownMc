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
public class Mute {

    private final int id;
    private final MuteReason muteReason;

    private final YamlConfiguration cfg;

    @Setter
    private String author;
    @Setter
    private long mutedUntil;

    public Mute(final int id, final YamlConfiguration cfg) {
        this.cfg = cfg;
        this.id = id;
        this.muteReason = CrownMain.getInstance().getInitializer().getMuteHandler().getReadsonFromID(cfg.getInt("mutes." + id + ".reason"));

        mutedUntil = cfg.getLong("mutes." + id + ".mutedUntil");
    }

    public Mute(final int id, final MuteReason muteReason, final YamlConfiguration cfg) {
        this.cfg = cfg;
        this.id = id;
        this.muteReason = muteReason;

        if(muteReason.getDuration() <= 0) {
            mutedUntil = -1;
            return;
        }

        mutedUntil = System.currentTimeMillis() + muteReason.getDuration();
    }

    public void save() {
        cfg.set("mutes." + id + ".reason", muteReason.getId());
        cfg.set("mutes." + id + ".mutedUntil", mutedUntil);
        cfg.set("mutes." + id + ".author", author);
    }

}
