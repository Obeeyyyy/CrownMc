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
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


@Getter
public final class VotePartyHandler {

    private final File file = FileUtil.getFile("voteparty.yml");
    private final YamlConfiguration cfg ;

    private final ArrayList<Location> locations = new ArrayList<>();
    private final ArrayList<VoteParty> parties = new ArrayList<>();
    private ArrayList<ItemStack> items = new ArrayList<>();
    private ArrayList<ItemStack> chanceList = new ArrayList<>();

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

        setChanceItems();
    }

    public void setChanceItems() {
        if(items.isEmpty())
            return;

        chanceList = new ArrayList<>();

        items.forEach(item -> {
            final double chance = getChanceFromItem(item);

            if (chance > 0) {
                for (int i = 0; i < chance * 100; i++) {
                    chanceList.add(item);
                }
            }
        });
    }

    public double getChanceFromItem(final ItemStack item) {
        if(!item.hasItemMeta())
            return 0;

        if(!item.getItemMeta().hasLore())
            return 0;

        return Double.parseDouble(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1).split(" ")[1].replace("%", ""));
    }

    public ItemStack getRandomItem() {
        final ItemStack item =  chanceList.get(new Random().nextInt(chanceList.size())).clone();
        final ItemMeta meta = item.getItemMeta();

        final ArrayList<String> lore = (ArrayList<String>) meta.getLore();
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void startVoteParty() {
        parties.add(new VoteParty(this));
    }

    public void spawnBoss() {
        parties.add(new VoteParty(this, true));
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

    public void setItems(final Inventory inventory) {
        items.clear();

        final ArrayList<ItemStack> temp = new ArrayList<>();

        for (final ItemStack item : inventory.getContents()) {
            if(item != null && item.getType() != Material.AIR) {
                setLoreWithChanceForItem(item);
                temp.add(item);
            }
        }

        items = temp;

        setChanceItems();
        save();
    }

    private void setLoreWithChanceForItem(final ItemStack item){
        final ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();

        if(meta.hasLore()) {
            lore = (ArrayList<String>) meta.getLore();

            if(!lore.get(lore.size() - 1).startsWith("Chance ")) {
                lore.add("");
                lore.add("Chance 0.00%");
            }

        } else {
            lore.add("");
            lore.add("Chance 0.00%");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public void playEffectForEveryone(final Location location, final Effect effect) {
        for (Entity entity : location.getWorld().getEntities()) {

            if(!(entity instanceof Player))
                continue;

            ((Player) entity).playEffect(location, effect, 1);
        }
    }

    public void playeSoundForEveryOne(final Location location, final Sound sound) {
        for (Entity entity : location.getWorld().getEntities()) {

            if(!(entity instanceof Player))
                continue;

            ((Player) entity).playSound(location, sound, 1, 1);
        }
    }

    public Vector getRandomVelocity(Random random) {
        double x = random.nextDouble() - random.nextDouble();
        double y = random.nextDouble() + 0.2;
        double z = random.nextDouble() - random.nextDouble();

        return new Vector(x, y, z).normalize().multiply(1.1);
    }


    public void shutdown()  {
        if(parties.isEmpty())
            return;

        for (final VoteParty party : parties) {
            if(party != null)
                party.shutdown();
        }
    }

    private void save() {
        cfg.set("items", items);
        FileUtil.saveToFile(file, cfg);
    }

}
