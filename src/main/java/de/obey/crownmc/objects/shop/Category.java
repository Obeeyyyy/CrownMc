package de.obey.crownmc.objects.shop;
/*

    Author - Obey -> SkySlayer-v4
       30.12.2022 / 19:29

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public final class Category {

    private String name, prefix, currency;
    private Material material;
    private ItemStack skull;
    private Integer showSlot;
    private CategoryType categoryType;
    private YamlConfiguration cfg;

    private final ArrayList<ShopItem> items = new ArrayList<>();

    public Category(final String name) {
        this.name = name;
        prefix = "§f§l" + name.toUpperCase();
        material = Material.POTATO_ITEM;

        currency = name.equalsIgnoreCase("crowns") ? "§7 Crowns " : "§6§l$ ";

        categoryType = name.equalsIgnoreCase("sell") ? CategoryType.SELL : CategoryType.BUY;
    }

    /*
        kategory:
            pvp:
                '1':
                    price: 1000
                    item: THE ITEM

     */

    public Category loadData(final YamlConfiguration cfg) {

        final String path = "category." + name + ".";

        this.cfg = cfg;

        if (cfg.contains(path + "prefix"))
            prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString(path + "prefix"));

        if (cfg.contains(path + "material"))
            material = Material.getMaterial(cfg.getString(path + "material"));

        if(material == Material.SKULL_ITEM  && cfg.contains(path + "skull"))
            skull = cfg.getItemStack(path + "skull");

        if (cfg.contains(path + "slot"))
            showSlot = cfg.getInt(path + "slot");

        if (cfg.contains(path + "items")) {
            final Set<String> itemIds = cfg.getConfigurationSection(path + "items").getKeys(false);

            if (itemIds.isEmpty())
                return this;

            for (String ID : itemIds) {
                items.add(new ShopItem(Integer.parseInt(ID), this));
            }
        }

        return this;
    }

    public void setMaterial(final ItemStack item) {
        this.material = item.getType();

        if(material == Material.SKULL_ITEM) {
            skull = item.clone();
        } else {
            skull = null;
        }

        save(cfg);
    }

    public void updateInventory(final Inventory inventory) {
        inventory.clear();
        if (!items.isEmpty()) {
            CrownMain.getInstance().getInitializer().getExecutorService().submit(() -> {
                for (ShopItem shopItem : items) {
                    final ItemStack item = shopItem.getItemStack().clone();
                    final ItemMeta meta = item.getItemMeta();
                    final List<String> lore = (item.hasItemMeta() ? (item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>());

                    lore.add("");
                    lore.add("§8▰§7▱ §6§lInformationen");
                    lore.add("§8 - §7ID§8: §7" + shopItem.getID());
                    lore.add("§8 - §7Preis§8: §fx" + shopItem.getItemStack().getAmount() + " §8-> §e§o" + NumberFormat.getInstance().format(shopItem.getPrice()) + currency + (item.getMaxStackSize() > 1 ? "§fx" + (shopItem.getItemStack().getAmount() * 64) + " §8-> §e§o" + NumberFormat.getInstance().format(shopItem.getPrice() * 64) + currency : ""));
                    lore.add("§8 - §7" + (categoryType == CategoryType.BUY ? "Gekauft§8: §fx§e§o" : "§7Verkauft§8: §fx§e§o") + shopItem.getCount());
                    lore.add("");

                    if (categoryType == CategoryType.BUY) {
                        lore.add("§8▰§7▱ §6§lLinksklick");
                        lore.add("§8 - §7Kaufen§8: §f§ox" + shopItem.getItemStack().getAmount());
                        if (item.getMaxStackSize() > 1) {
                            lore.add("");
                            lore.add("§8▰§7▱ §6§lShift + Linksklick");
                            lore.add("§8 - §7Kaufen§8: §f§ox" + (shopItem.getItemStack().getAmount() * 64));
                            lore.add("");
                        }
                    } else {
                        lore.add("§8▰§7▱ §6§lLinksklick");
                        lore.add("§8 - §7Verkaufen§8: §f§ox1");
                        lore.add("");
                        lore.add("§8▰§7▱ §6§lShift + Linksklick");
                        lore.add("§8 - §7Verkaufen§8: §f§ox64");
                        lore.add("");
                        lore.add("§8▰§7▱ §6§lRechtsklick");
                        lore.add("§8 - §7Verkaufen§8: §f§oalles");
                        lore.add("");
                    }

                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    inventory.setItem(shopItem.getSlot(), item.clone());
                }
            });
        }

        inventory.setItem(53, new ItemBuilder(Material.BARRIER).setDisplayname("§c§oZurück§8.").build());
    }

    public void openInventory(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§7Kategorie " + prefix);

        updateInventory(inventory);

        player.openInventory(inventory);
    }

    public void addShopItem(final int ID, final ItemStack itemStack) {
        final ShopItem shopItem = getShopItemFromID(ID);
        if(shopItem != null) {
            shopItem.setItemStack(itemStack);
            return;
        }

        items.add(new ShopItem(ID, this).setItemStack(itemStack.clone()));
    }

    public ShopItem getShopItemFromClickedSlot(final int clickedSlot) {

        for (final ShopItem item : items) {
            if (item.getSlot() == clickedSlot)
                return item;
        }

        return null;
    }

    public ShopItem getShopItemFromID(final int ID) {

        for (final ShopItem item : items) {
            if (item.getID() == ID)
                return item;
        }

        return null;
    }

    public void save(final YamlConfiguration cfg) {
        final String path = "category." + name + ".";

        cfg.set(path + "prefix", prefix);
        cfg.set(path + "material", material.name());
        cfg.set(path + "slot", showSlot);

        if(skull != null)
            cfg.set(path + "skull", skull);

        if (!items.isEmpty()) {
            for (ShopItem item : items) {
                cfg.set(path + "items." + item.getID() + ".item", item.getItemStack());
                cfg.set(path + "items." + item.getID() + ".price", item.getPrice());
                cfg.set(path + "items." + item.getID() + ".slot", item.getSlot());
            }
        }
    }

}
