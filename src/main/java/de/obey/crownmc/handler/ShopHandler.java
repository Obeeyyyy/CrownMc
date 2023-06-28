package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       30.12.2022 / 19:29

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.objects.shop.Category;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.ItemBuilder;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public final class ShopHandler {

    private final File shopFile;

    @Getter
    private YamlConfiguration cfg;

    @Getter
    private final HashMap<String, Category> categories = new HashMap<>();

    private final Inventory shopInventory = Bukkit.createInventory(null, 9 * 6, "§6§lShop");

    public ShopHandler() {
        shopFile = new File(CrownMain.getInstance().getDataFolder().getPath() + "/shop.yml");

        if (!shopFile.exists()) {
            try {
                shopFile.createNewFile();
                cfg = FileUtil.getCfg(shopFile);
            } catch (IOException ignored) {
            }
        } else {
            cfg = FileUtil.getCfg(shopFile);

            if (cfg.contains("category")) {
                final Set<String> catNames = cfg.getConfigurationSection("category").getKeys(false);

                if (catNames.isEmpty())
                    return;

                for (String name : catNames) {
                    categories.put(name, new Category(name).loadData(cfg));
                }
            }
        }

        updateShopInventory();
    }

    public Category getcategoryFromClickedSlot(final int slot) {
        if (categories.isEmpty())
            return null;

        for (Category value : categories.values()) {
            if (value.getShowSlot() == slot)
                return value;
        }

        return null;
    }

    public Category getCategoryFromPrefix(final String prefix) {
        for (Category value : categories.values()) {
            if (value.getPrefix().equalsIgnoreCase(prefix))
                return value;
        }

        return null;
    }

    public void createCategory(final String name) {
        categories.put(name, new Category(name).loadData(cfg));
        updateShopInventory();
        save();
    }

    public void deleteCategory(final String name) {
        categories.remove(name);
        cfg.set("category." + name, null);
        updateShopInventory();
        save();
    }

    public void updateShopInventory() {
        CrownMain.getInstance().getInitializer().getExecutorService().submit(() -> {
            shopInventory.clear();
            final ItemStack bar = new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build();
            final ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 8).setDisplayname("§7-§8/§7-").build();

            shopInventory.setItem(0, bar);
            shopInventory.setItem(1, pane);

            shopInventory.setItem(9, bar);
            shopInventory.setItem(10, pane);

            shopInventory.setItem(18, bar);
            shopInventory.setItem(19, pane);

            shopInventory.setItem(27, bar);
            shopInventory.setItem(28, pane);

            shopInventory.setItem(36, bar);
            shopInventory.setItem(37, pane);

            shopInventory.setItem(45, bar);
            shopInventory.setItem(46, pane);

            if (!categories.isEmpty()) {
                for (Category value : categories.values()) {

                    if(value.getSkull() != null)  {

                        final ItemStack skull = value.getSkull().clone();
                        final ItemMeta meta = skull.getItemMeta();

                        meta.setDisplayName(value.getPrefix());

                        final List<String> lore = new ArrayList<>();

                        lore.add("§7");
                        lore.add("§8▰§7▱ §6§lRechtsklick");
                        lore.add("§8 -§7 Öffne diese Kategorie§8.");
                        lore.add("§7");

                        meta.setLore(lore);
                        skull.setItemMeta(meta);

                        shopInventory.setItem(value.getShowSlot(), skull);
                        continue;
                    }

                    shopInventory.setItem(value.getShowSlot(), new ItemBuilder(value.getMaterial())
                            .setDisplayname(value.getPrefix())
                            .setLore("",
                                    "§8▰§7▱ §6§lRechtsklick",
                                    "§8 -§7 Öffne diese Kategorie§8.",
                                    "")
                            .build());
                }
            }
        });
    }

    public void openShop(final Player player) {
        player.openInventory(shopInventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
    }

    public void save() {
        if (!categories.isEmpty()) {
            for (Category value : categories.values()) {
                value.save(cfg);
            }
        }

        FileUtil.saveToFile(shopFile, cfg);
    }

}
