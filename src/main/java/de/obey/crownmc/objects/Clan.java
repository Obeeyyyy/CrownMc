package de.obey.crownmc.objects;

import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Getter @Setter
public class Clan {

    private final File clanFile;
    private final YamlConfiguration cfg;
    private ArrayList<String> memberList = new ArrayList<>();
    private ArrayList<String> moderatorList = new ArrayList<>();

    private String clanName, clanTag;
    private UUID ownerUUID;
    private Inventory clanChest, clanInfo;

    private int kills, deaths, trophies, chestSlots, memberCap;

    public Clan(final String name) {
        clanFile = FileUtil.getFile("/clanFiles/" + name + ".yml");
        cfg = FileUtil.getCfg(clanFile);

        this.clanName = name;
    }

    public void saveFileData() {
        cfg.set("clan.members", memberList);
        cfg.set("clan.mods", moderatorList);
        cfg.set("clan.tag", clanTag);
        cfg.set("clan.memberCap", memberCap);
        cfg.set("clan.chest.slots", chestSlots);

        final ArrayList<ItemStack> chestContents = new ArrayList<>();

        for (int i = 0; i < clanChest.getSize(); i++) {
            if (i+1 <= chestSlots)
                chestContents.add(clanChest.getItem(i));
        }

        cfg.set("clan.chest.items", chestContents);

        FileUtil.saveToFile(clanFile, cfg);
    }

    public void loadFileData() {
        if (cfg.contains("clan.members"))
            memberList = (ArrayList<String>) cfg.getStringList("clan.members");

        if (cfg.contains("clan.mods"))
            moderatorList = (ArrayList<String>) cfg.getStringList("clan.mods");

        if (cfg.contains("clan.owner"))
            ownerUUID = UUID.fromString(cfg.getString("clan.owner"));

        clanTag = cfg.getString("clan.tag", clanName);
        chestSlots = cfg.getInt("clan.chest.slots", 9);
        memberCap = cfg.getInt("clan.memberCap", 2);

        clanChest = Bukkit.createInventory(null, 9*6, "§7Chest §8(§f " + clanTag + "§8)");
        clanInfo = Bukkit.createInventory(null, 9*5, "§7Clan §8(§f" + clanTag + "§8)");

        loadChestContents();
        updateClanChest();
        updateClanInfo();
    }

    public void loadChestContents() {
        if (cfg.contains("clan.chest.items")) {
            final ArrayList<ItemStack> contents = (ArrayList<ItemStack>) cfg.getList("clan.chest.items");
            final AtomicInteger slot = new AtomicInteger();

            if (contents.size() > 0) {
                contents.forEach(item -> {
                    if(slot.get() + 1 > chestSlots)
                        return;

                    if (item == null) {
                        clanChest.setItem(slot.get(), new ItemStack(Material.AIR));
                    } else {
                        clanChest.setItem(slot.get(), item);
                    }

                    slot.getAndAdd(1);
                });
            }
        }
    }

    public void updateClanChest() {
        for (int i = 0; i < clanChest.getSize(); i++) {
            if(i + 1 > chestSlots) {
                clanChest.setItem(i, new ItemBuilder(Material.BARRIER)
                                .setDisplayname("§c§onicht freigeschaltet")
                        .build());
            }

        }
    }

    public void updateClanInfo() {
        clanInfo.setItem(13, new ItemBuilder(Material.ITEM_FRAME).build());
    }

    public boolean isNeeded() {
        return false;
    }

}
