package de.obey.crownmc.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       08.12.2022 / 17:27

*/

import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@Getter
public final class UserPlot {

    private ArrayList<String> rand = new ArrayList<>();
    private ArrayList<String> wand = new ArrayList<>();

    final YamlConfiguration cfg;

    public UserPlot(final User user) {
        cfg = user.getCfg();

        if (cfg.contains("plot.rand"))
            rand = (ArrayList<String>) cfg.getStringList("plot.rand");

        if (cfg.contains("plot.wand"))
            wand = (ArrayList<String>) cfg.getStringList("plot.wand");
    }

    public void save() {
        if (!rand.isEmpty())
            cfg.set("plot.rand", rand);

        if (!wand.isEmpty())
            cfg.set("plot.wand", wand);
    }

    public boolean addPlotrand(final ItemStack item) {
        if (rand.contains(item.getTypeId() + ":" + item.getData().getData()))
            return false;

        rand.add(item.getTypeId() + ":" + item.getData().getData());
        return true;
    }

    public boolean addPlotwand(final ItemStack item) {
        if (wand.contains(item.getTypeId() + ":" + item.getData().getData()))
            return false;

        wand.add(item.getTypeId() + ":" + item.getData().getData());
        return true;
    }

    public void openRandInventory(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§7Plot Rand");

        InventoryUtil.fillSideRows(inventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());

        inventory.addItem(new ItemBuilder(Material.WOOL, 1, (byte) 13)
                .setDisplayname("§8» §f§l" + Material.WOOL.name())
                .setLore("",
                        "§8▰§7▱ §b§lRechtsklick",
                        "§8 - §7Setzte diesen Block als deinen Plotrand§8.",
                        "")
                .build());

        if (!rand.isEmpty()) {
            for (String text : rand) {
                final String[] data = text.split(":");

                inventory.addItem(new ItemBuilder(Material.getMaterial(Integer.parseInt(data[0])), 1, Byte.parseByte(data[1]))
                        .setDisplayname("§8» §f§l" + Material.getMaterial(Integer.parseInt(data[0])).name())
                        .setLore("",
                                "§8▰§7▱ §b§lRechtsklick",
                                "§8 - §7Setzte diesen Block als deinen Plotrand§8.",
                                "")
                        .build());
            }
        }

        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
        player.openInventory(inventory);
    }

    public void openWandInventory(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§7Plot Wand");

        InventoryUtil.fillSideRows(inventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());

        inventory.addItem(new ItemBuilder(Material.STONE, 1)
                .setDisplayname("§8» §f§l" + Material.STONE.name())
                .setLore("",
                        "§8▰§7▱ §b§lRechtsklick",
                        "§8 - §7Setzte diesen Block als deinen Plotrand§8.",
                        "")
                .build());

        if (!wand.isEmpty()) {
            for (String text : wand) {
                final String[] data = text.split(":");

                inventory.addItem(new ItemBuilder(Material.getMaterial(data[0]), 1, Byte.parseByte(data[1]))
                        .setDisplayname("§8» §f§l" + Material.getMaterial(data[0]).name())
                        .setLore("",
                                "§8▰§7▱ §b§lRechtsklick",
                                "§8 - §7Setzte diesen Block als deine Plotwand§8.",
                                "")
                        .build());
            }
        }

        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
        player.openInventory(inventory);
    }

}
