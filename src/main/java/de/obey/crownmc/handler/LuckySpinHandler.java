package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       15.06.2023 / 02:10

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.objects.gambling.LuckySpin;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public final class LuckySpinHandler {

    @Getter
    private final LocationHandler locationHandler;
    @Getter
    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    private final YamlConfiguration cfg;
    private final File file;

    @Getter
    private ArrayList<ItemStack> items = new ArrayList<>();

    @Getter
    private ArrayList<ItemStack> chanceList = new ArrayList<>();

    @Getter
    private LuckySpin luckySpin;

    public LuckySpinHandler(final LocationHandler locationHandler, final MessageUtil messageUtil, final UserHandler userHandler) {
        this.locationHandler = locationHandler;
        this.messageUtil = messageUtil;
        this.userHandler = userHandler;

        file = FileUtil.getFile("luckyspin.yml");
        cfg = FileUtil.getCfg(file);

        if(cfg.contains("items")) {
            items = (ArrayList<ItemStack>) cfg.get("items");
        } else {
            items.add(new ItemStack(Material.STICK));
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

    public void spin(final Player player) {
        if(luckySpin == null)
            return;

        if(luckySpin.getState() == 1) {
            messageUtil.sendMessage(player, "Bitte warte bis das Rad steht§8.");
            return;
        }

        userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
            final long remaining = System.currentTimeMillis() - user.getLong(DataType.LASTLUCKYSPIN);

            if(remaining <= 86400000) {
                messageUtil.sendMessage(player, "Du musst noch " + MathUtil.getHoursAndMinutesAndSecondsFromSeconds((86400000-remaining)/1000) + "warten§8.");
                player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f,1);
                return;
            }

            if(items.isEmpty()) {
                messageUtil.sendMessage(player, "Fehler ! Bitte kontaktiere ein Teammitglied§8.");
                return;
            }

            messageUtil.sendMessage(player, "Du drehst kräftig am Glücksrad ...");
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
            luckySpin.startSpinning(player, user);
        });
    }

    public void shutdown() {
        if(luckySpin != null)
            luckySpin.shutdown();
    }

    public void setup() {
        shutdown();

        if (locationHandler.getLocation("luckyspin") == null) {
            messageUtil.warn("§c§oLuckyspin location existiert nicht.");
            return;
        }

        if(luckySpin == null) {
            luckySpin = new LuckySpin(this, locationHandler.getLocation("luckyspin"));
            return;
        }

        luckySpin.setLocation(locationHandler.getLocation("luckyspin"));
        luckySpin.setup();
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

        luckySpin.shutdown();
        luckySpin.setup();
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

    public double getChanceFromItem(final ItemStack item) {
        if(!item.hasItemMeta())
            return 0;

        if(!item.getItemMeta().hasLore())
            return 0;

        return Double.parseDouble(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1).split(" ")[1].replace("%", ""));
    }

    private void save() {
        cfg.set("items", items);
        FileUtil.saveToFile(file, cfg);
    }

}
