package de.obey.crownmc.objects;

import de.obey.crownmc.util.FileUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

@Getter @Setter
public class Clan {

    private final File clanFile;
    private final YamlConfiguration cfg;
    private ArrayList<String> memberList = new ArrayList<>();
    private ArrayList<String> moderatorList = new ArrayList<>();

    private String clanName, clanTag;
    private UUID ownerUUID;
    private Inventory clanChest;

    private int kills, deaths, trophies, chestSlots, memberCap;

    public Clan(final String name) {
        clanFile = FileUtil.getFile("/clanFiles/" + name + ".yml");
        cfg = FileUtil.getCfg(clanFile);

        this.clanName = name;
    }

    public void loadFileData() {
        if(cfg.contains("clan.members"))
            memberList = (ArrayList<String>) cfg.getStringList("clan.members");

        if(cfg.contains("clan.mods"))
            moderatorList = (ArrayList<String>) cfg.getStringList("clan.mods");

        if(cfg.contains("clan.owner"))
            ownerUUID = UUID.fromString(cfg.getString("clan.owner"));

        if(cfg.contains("clan.tag"))
            clanTag = cfg.getString("clan.tag");
    }

    public boolean isNeeded() {
        return false;
    }

}
