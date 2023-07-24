package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       26.06.2023 / 00:21

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.objects.vote.VoteParty;
import de.obey.crownmc.util.FileUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public final class VotePartyHandler {

    @Getter
    private final File file = FileUtil.getFile("voteparty.yml");

    @Getter
    private final YamlConfiguration cfg ;

    @Getter
    private final ArrayList<Location> locations = new ArrayList<>();

    @Getter
    private final ArrayList<VoteParty> parties = new ArrayList<>();

    @Getter
    private ArrayList<ItemStack> items = new ArrayList<>();

    public VotePartyHandler() {

        boolean isNew = false;
        if(!file.exists()) {
            try {
                file.createNewFile();
                isNew = true;
            } catch (IOException e) {}
        }

        cfg = FileUtil.getCfg(file);

        if(isNew) {
            cfg.set("items", new ArrayList<ItemStack>());
            FileUtil.saveToFile(file, cfg);
        } else {
            items = cfg.contains("items") ? (ArrayList<ItemStack>) cfg.getList("items") : new ArrayList<>();
        }
    }

    public ItemStack getRandomItem() {
        if(items.isEmpty())
            return new ItemStack(Material.STICK);

        return items.get(new Random().nextInt(items.size()));
    }

    public void startVoteParty() {
        parties.add(new VoteParty(this));
    }

    public void loadLocations() {
        final LocationHandler locationHandler = CrownMain.getInstance().getInitializer().getLocationHandler();

        if(locationHandler == null)
            return;

        locations.clear();

        for (int i = 1; i < 15; i++) {
            final Location temp = locationHandler.getLocation("voteparty-" + i);

            if(temp == null)
                break;

            locations.add(temp);
        }
    }

    public void saveFile() {
        FileUtil.saveToFile(file, cfg);
    }

    public void setItems(final ArrayList<ItemStack> items) {
        this.items.clear();
        this.items = items;
        cfg.set("items", items);
        saveFile();
    }

    public void shutdown()  {
        if(parties.isEmpty())
            return;

        for (VoteParty party : parties) {
            party.shutdown();
        }
    }

}
