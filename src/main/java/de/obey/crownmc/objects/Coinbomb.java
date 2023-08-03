package de.obey.crownmc.objects;

import de.obey.crownmc.util.Config;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class Coinbomb {

    List<ItemStack> items;
    final Config config;

    public Coinbomb() {
        this.config = new Config("plugins/CrownMc/", "coinbomb.yml");

        this.items = (List<ItemStack>) config.getConfig().getList("items", new ArrayList<ItemStack>());
    }

    public void saveItems(List<ItemStack> newItems) {
        this.items = newItems;
        this.config.getConfig().set("items", this.items);
        this.config.saveConfig();
    }

}
