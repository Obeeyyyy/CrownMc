package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       05.08.2023 / 01:36

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public final class PvPDropHandler {

    private final MessageUtil messageUtil;

    private final YamlConfiguration cfg;
    private final File file;

    @Getter
    private ArrayList<ItemStack> items = new ArrayList<>();

    @Getter
    private ArrayList<ItemStack> chanceList = new ArrayList<>();

    public PvPDropHandler(final MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
        file = FileUtil.getFile("pvpdrops.yml");
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

    private final Random random = new Random();
    public void drop(final Player player) {
        if(random.nextInt(2) == 0)
            return;

        final int amount = random.nextInt(5) + 1;
        final ItemStack item = getRandomItem();
        item.setAmount(item.getAmount() * amount);

        InventoryUtil.addItem(player, item);

        final String display = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name();
        messageUtil.sendMessage(player, "Du hast §fx" + item.getAmount() + " " + display + "§7 erhalten§8.");
    }

    private ItemStack getRandomItem() {
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
