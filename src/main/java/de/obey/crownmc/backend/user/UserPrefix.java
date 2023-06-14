package de.obey.crownmc.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       06.11.2022 / 17:34

*/

import de.obey.crownmc.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@Getter
public final class UserPrefix {

    @Setter
    private String activePrefix = "", nameColor = "§7";
    private final ArrayList<String> prefixList;

    private final YamlConfiguration cfg;

    public UserPrefix(final User user) {
        cfg = user.getCfg();

        final String readActivePrefix = cfg.getString("activeprefix");
        final ArrayList<String> readPrefixList = (ArrayList<String>) cfg.getList("prefixlist");

        activePrefix = readActivePrefix == null ? "" : readActivePrefix;
        prefixList = readPrefixList == null ? new ArrayList<>() : readPrefixList;

        if(cfg.contains("namecolor"))
            nameColor = cfg.getString("namecolor");
    }

    public void openPrefixInventory(final Player player) {

        final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§f§lPREFIX");

        updatePrefixInventory(inventory);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
    }

    public void updatePrefixInventory(final Inventory inventory) {

        final ItemStack bar = new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build();
        final ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 8).setDisplayname("§7-§8/§7-").build();

        inventory.setItem(0, bar);
        inventory.setItem(1, pane);

        inventory.setItem(9, new ItemBuilder(Material.ITEM_FRAME)
                .setDisplayname("§8┃» §f§lINFORMATION")
                .setLore("",
                        "§8▰§7▱ §f§lAktiver Prefix",
                        "  §8- §7" + (activePrefix.equalsIgnoreCase("") ? "§c§okein Prefix" : activePrefix))
                .build());
        inventory.setItem(10, pane);

        inventory.setItem(18, bar);
        inventory.setItem(19, pane);

        inventory.setItem(27, bar);
        inventory.setItem(28, pane);

        inventory.setItem(36, new ItemBuilder(Material.BARRIER)
                .setDisplayname("§8┃» §4§lPrefix zurücksetzen")
                .setLore("",
                        "§8▰§7▱ §c§lLinksklick",
                        "  §8- §7Deaktiviere deinen aktuellen Prefix§8.")
                .build());
        inventory.setItem(37, pane);

        inventory.setItem(45, bar);
        inventory.setItem(46, pane);


        if (prefixList.isEmpty())
            return;

        inventory.remove(Material.PAPER);

        if (!prefixList.isEmpty()) {
            prefixList.forEach(prefix -> {
                if (activePrefix.equalsIgnoreCase(prefix)) {
                    inventory.addItem(new ItemBuilder(Material.PAPER)
                            .setDisplayname(ChatColor.translateAlternateColorCodes('&', prefix))
                            .setLore("",
                                    "§8▰§7▱ §c§lLinksklick",
                                    "  §8- §7Um diesen Prefix zu deaktivieren§8.")
                            .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            .addEnchantment(Enchantment.ARROW_DAMAGE)
                            .build());
                } else {
                    inventory.addItem(new ItemBuilder(Material.PAPER)
                            .setDisplayname(ChatColor.translateAlternateColorCodes('&', prefix))
                            .setLore("",
                                    "§8▰§7▱ §c§lLinksklick",
                                    "  §8- §7Um diesen Prefix zu aktivieren§8.")
                            .build());
                }
            });
        }
    }

    public void save() {
        cfg.set("activeprefix", activePrefix);
        cfg.set("prefixlist", prefixList);
        cfg.set("namecolor", nameColor);
    }

    public boolean addPrefix(final String prefix) {
        if (prefixList.contains(prefix))
            return false;

        prefixList.add(prefix);

        return true;
    }

}
