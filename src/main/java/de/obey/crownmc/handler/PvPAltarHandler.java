package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       11.07.2023 / 03:45

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.objects.pvp.PvPAltar;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public final class PvPAltarHandler {

    private final MessageUtil messageUtil;

    @Getter
    private final HashMap<Integer, PvPAltar> pvpAltarMap = new HashMap<>();

    @Getter
    private final HashMap<UUID, PvPAltar> capturing = new HashMap<>();

    @Getter
    private final HashMap<UUID, Long> blocked = new HashMap<>();

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

    public void deletePvPAltar(final int id) {
        final PvPAltar altar = pvpAltarMap.get(id);

        altar.shutdown();
        altar.delete();

        pvpAltarMap.remove(id);
    }

    public void startPvPAltar(final int id, final Player player) {
        final PvPAltar altar = pvpAltarMap.get(id);

        if(altar == null) {
            messageUtil.sendMessage(player, "Der PvPAltar ist defekt, bitte kontaktiere ein Teammitglied§8.");
            return;
        }

        if(blocked.containsKey(player.getUniqueId())) {
            messageUtil.sendMessage(player, "Du bist noch für " + MathUtil.getMinutesAndSecondsFromSeconds((blocked.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000) + " blockiert§8.");
            return;
        }

        if(altar.getState() != 0) {

            if(altar.getState() == 1) {
                messageUtil.sendMessage(player, "Der Altar wird bereits von §f§o" + Bukkit.getOfflinePlayer(altar.getPlayerUUID()).getName() + " §7eingenommen§8.");
                return;
            }

            if(altar.getState() == 2) {
                messageUtil.sendMessage(player, "Der Altar kann erst in §f§o" + MathUtil.getHoursAndMinutesAndSecondsFromSeconds(altar.getCooldownUntilMillis()/1000) + "§7wieder eingenommen werden§8.");
                return;
            }
            return;
        }

        altar.startCapturing(player);
        capturing.put(player.getUniqueId(), altar);
    }

    public void block(final UUID uuid, final long millis) {
        blocked.put(uuid, System.currentTimeMillis() + millis);
    }
}
