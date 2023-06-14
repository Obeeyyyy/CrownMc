package de.obey.slayer.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       11.11.2022 / 16:50

*/

import de.obey.slayer.SlayerMain;
import de.obey.slayer.backend.RespawnKitItem;
import de.obey.slayer.backend.ServerConfig;
import de.obey.slayer.util.FileUtil;
import de.obey.slayer.util.InventoryUtil;
import de.obey.slayer.util.ItemBuilder;
import de.obey.slayer.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Setter
public final class UserRespawnKit {

    @Setter(AccessLevel.NONE)
    private final MessageUtil messageUtil = SlayerMain.getInstance().getInitializer().getMessageUtil();

    @Setter(AccessLevel.NONE)
    private final ServerConfig serverConfig = SlayerMain.getInstance().getInitializer().getServerConfig();

    @Setter(AccessLevel.NONE)
    private final User user;

    private final YamlConfiguration cfg;

    private final Map<Integer, Integer> itemLevels = new HashMap<>();

    public UserRespawnKit(final User user) {
        this.user = user;
        cfg = user.getCfg();
        loadLevels();
    }

    public int getLevelForType(final int type) {
        return itemLevels.get(type);
    }

    private void loadLevels() {
        if (!cfg.contains("respawnkit")) {
            cfg.set("respawnkit.1.level", 0);
            cfg.set("respawnkit.2.level", 0);
            cfg.set("respawnkit.3.level", 0);
            cfg.set("respawnkit.4.level", 0);
            cfg.set("respawnkit.5.level", 0);
            cfg.set("respawnkit.6.level", 0);
            cfg.set("respawnkit.7.level", 0);
            cfg.set("respawnkit.8.level", 0);

            for (int i = 1; i <= 8; i++)
                itemLevels.put(i, cfg.getInt("respawnkit." + i + ".level"));

            FileUtil.saveToFile(user.getPlayerFile(), cfg);
        } else {
            for (int i = 1; i <= 8; i++)
                itemLevels.put(i, cfg.getInt("respawnkit." + i + ".level"));

        }
    }

    public void save() {
        for (int i = 1; i <= 8; i++)
            cfg.set("respawnkit." + i + ".level", itemLevels.get(i));
    }

    public void resetLevels() {
        for (int i = 1; i <= 8; i++)
            itemLevels.put(i, 0);
    }

    public void addLevel(final int type, final Inventory inventory) {
        itemLevels.put(type, itemLevels.get(type) + 1);
        updateInventory(inventory);
    }

    public void openInventory() {
        final Player player = user.getPlayer();
        final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§f§lRESPAWNKIT");

        updateInventory(inventory);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);
    }

    public void updateInventory(final Inventory inventory) {
        InventoryUtil.fill(inventory, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 8).setDisplayname("§7-§8/§7-").build());
        InventoryUtil.fillSideRows(inventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());

        serverConfig.getRespawnKitItems().keySet().forEach(kittype -> {
            final RespawnKitItem kitItem = serverConfig.getRespawnKitItems().get(kittype);
            final int itemLevel = itemLevels.get(kittype);
            final ItemStack item = kitItem.getItemForLevel(itemLevel);

            // IF MAX LEVEL IS REAHCED
            if (kitItem.getMaxLevel() <= itemLevels.get(kittype)) {
                final ItemStack theItem = new ItemBuilder(item.getType(), item.getAmount())
                        .setDisplayname("§a§lMaximal §f§lLevel§8.§7" + itemLevel)
                        .setLore("",
                                "§8▰§7▱ §f§lUpgrades",
                                "§8  - §a§oDieses Item ist auf dem Maximallevel§8.",
                                "")
                        .setEnchantments(item.getEnchantments())
                        .build();

                inventory.setItem(kitItem.getSlot(), theItem);

            } else {  // IF MAX LEVEL IS NOT REAHCED
                final ItemStack theItem = new ItemBuilder(item.getType(), (item.getAmount() == 0 ? 1 : item.getAmount()))
                        .setDisplayname("§f§lLevel§8.§f§o" + itemLevel + "§8/§f§l" + kitItem.getMaxLevel())
                        .setLore("",
                                "§8▰§7▱ §f§lUpgrades",
                                "§8  -§7 Dieses Item ist auf Stufe §f§o" + itemLevel + "§8/§f§l" + kitItem.getMaxLevel(),
                                "§8  -§7 Upgradepreis§8: §f§o" + messageUtil.formatLong(kitItem.getPriceForLevel(itemLevel + 1)) + "§f§l$",
                                "",
                                "§8▰§7▱ §f§lLinksklick",
                                "§8  -§7 Um das Upgrade freizuschalten§8.",
                                "")
                        .setEnchantments(item.getEnchantments())
                        .build();
                inventory.setItem(kitItem.getSlot(), theItem);
            }
        });
    }

    public void equipRespawnKit() {

        final Player player = user.getPlayer();

        if (player == null)
            return;

        final RespawnKitItem helm = serverConfig.getRespawnKitItems().get(1);
        if (helm != null && helm.getItemForLevel(itemLevels.get(1)).getType() != Material.BARRIER)
            player.getInventory().setHelmet(helm.getItemForLevel(itemLevels.get(1)));

        final RespawnKitItem chestplate = serverConfig.getRespawnKitItems().get(2);
        if (chestplate != null && chestplate.getItemForLevel(itemLevels.get(2)).getType() != Material.BARRIER)
            player.getInventory().setChestplate(chestplate.getItemForLevel(itemLevels.get(2)));

        final RespawnKitItem leggings = serverConfig.getRespawnKitItems().get(3);
        if (leggings != null && leggings.getItemForLevel(itemLevels.get(3)).getType() != Material.BARRIER)
            player.getInventory().setLeggings(leggings.getItemForLevel(itemLevels.get(3)));

        final RespawnKitItem boots = serverConfig.getRespawnKitItems().get(4);
        if (boots != null && boots.getItemForLevel(itemLevels.get(4)).getType() != Material.BARRIER)
            player.getInventory().setBoots(boots.getItemForLevel(itemLevels.get(4)));

        final RespawnKitItem weapon = serverConfig.getRespawnKitItems().get(5);
        if (weapon != null && weapon.getItemForLevel(itemLevels.get(5)).getType() != Material.BARRIER)
            player.getInventory().addItem(weapon.getItemForLevel(itemLevels.get(5)));

        final RespawnKitItem bow = serverConfig.getRespawnKitItems().get(6);
        if (bow != null && bow.getItemForLevel(itemLevels.get(6)).getType() != Material.BARRIER)
            player.getInventory().addItem(bow.getItemForLevel(itemLevels.get(6)));

        final RespawnKitItem apple = serverConfig.getRespawnKitItems().get(7);
        if (apple != null && apple.getItemForLevel(itemLevels.get(7)).getType() != Material.BARRIER)
            player.getInventory().addItem(apple.getItemForLevel(itemLevels.get(7)));

        final RespawnKitItem enderpearl = serverConfig.getRespawnKitItems().get(8);
        if (enderpearl != null && enderpearl.getItemForLevel(itemLevels.get(8)).getType() != Material.BARRIER)
            player.getInventory().addItem(enderpearl.getItemForLevel(itemLevels.get(8)));
    }
}
