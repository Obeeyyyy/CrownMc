package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       11.07.2023 / 03:45

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.objects.pvp.PvPAltar;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

public final class PvPAltarHandler {

    private final MessageUtil messageUtil;

    @Getter
    private final HashMap<Integer, PvPAltar> pvpAltarMap = new HashMap<>();

    private final File file;
    private final YamlConfiguration cfg;

    public PvPAltarHandler(final MessageUtil messageUtil) {
        this.messageUtil = messageUtil;

        file = FileUtil.getFile("pvpaltar.yml");
        cfg = FileUtil.getCfg(file);
    }

    public void loadAllPvPAltars() {
        if(!cfg.contains("altars"))
            return;

        final Set<String> allAltarIds = cfg.getConfigurationSection("altars").getKeys(false);

        if(allAltarIds.isEmpty())
            return;

        for (String allAltarId : allAltarIds) {
            final int id = Integer.parseInt(allAltarId);

            pvpAltarMap.put(id, new PvPAltar(id, cfg));
        }
    }

    public void shutdown() {
        if(pvpAltarMap.isEmpty())
            return;

        for (final PvPAltar altar : pvpAltarMap.values())
            altar.shutdown();
    }

    public void save() {
        if(pvpAltarMap.isEmpty())
            return;

        messageUtil.log("Saving (" + pvpAltarMap.size() + ") PvPAltars.");

        pvpAltarMap.values().forEach(PvPAltar::save);

        FileUtil.saveToFile(file, cfg);
    }

    public void createPvPAltar(final int id, final Location location) {
        final PvPAltar altar = new PvPAltar(id, cfg);

        altar.setLocation(location);
        altar.save();
        altar.shutdown();
        altar.spawnAltar();

        pvpAltarMap.put(id, altar);
    }

}
