package de.obey.slayer.objects.shop;
/*

    Author - Obey -> SkySlayer-v4
       30.12.2022 / 20:15

*/

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.Item;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public final class ShopItem {

    private int ID, slot = 0;
    private long price = 0, count = 0;
    private ItemStack itemStack = new ItemStack(Material.BARRIER);
    private final Category category;

    /*

        kategory:
            pvp:
                '1':
                    price: 1000
                    item: THE ITEM
                    slot: 10

     */


    public ShopItem(final int ID, final Category category) {
        this.category = category;
        this.ID = ID;

        String path = "category." + category.getName() + ".items." + ID + ".";

        final YamlConfiguration cfg = category.getCfg();

        if (cfg.contains(path + "slot"))
            slot = cfg.getInt(path + "slot");

        if (cfg.contains(path + "price"))
            price = cfg.getLong(path + "price");

        if (cfg.contains(path + "item"))
            itemStack = cfg.getItemStack(path + "item");

        if (cfg.contains(path + "count"))
            count = cfg.getLong(path + "count");
    }

    public ShopItem setItemStack(final ItemStack itemStack) {

        this.itemStack = itemStack;

        return this;
    }

    public ShopItem removeItem() {
        final YamlConfiguration cfg = category.getCfg();

        cfg.set("category." + category.getName() + ".items." + ID, null);

        category.save(cfg);

        return this;
    }

}
