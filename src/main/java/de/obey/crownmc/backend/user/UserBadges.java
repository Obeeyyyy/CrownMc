package de.obey.crownmc.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       20.11.2022 / 14:52

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.BadgeHandler;
import de.obey.crownmc.objects.Badge;
import de.obey.crownmc.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public final class UserBadges {

    @Getter
    private final HashMap<String, UserBadge> badges = new HashMap<>();

    private final User user;

    private final YamlConfiguration cfg;

    final BadgeHandler badgeHandler;

    public UserBadges(final User user) {
        badgeHandler = CrownMain.getInstance().getInitializer().getBadgeHandler();

        this.user = user;
        cfg = user.getCfg();

        if (!cfg.contains("badges"))
            return;

        cfg.getConfigurationSection("badges").getKeys(false).forEach(name -> {
            if (badgeHandler.getBadgeFromName(name) != null) {
                badges.put(name, new UserBadge(badgeHandler.getBadgeFromName(name), cfg));
            } else {
                cfg.set("badges." + name, null);
            }
        });
    }

    public void removeBadge(final String badgeName) {
        final Badge badge = badgeHandler.getBadgeFromName(badgeName);

        if (badge == null)
            return;

        badges.remove(badgeName);
        badge.remove();

        if (user.getOfflinePlayer().isOnline())
            user.getPlayer().sendMessage("§f§lBADGES§8 × §7Dir wurde eine Badge entzogen§8. ( §r" + badge.getPrefix() + " §8)");
    }

    public void addBadge(final String badgeName) {
        final Badge badge = badgeHandler.getBadgeFromName(badgeName);

        if (badge == null)
            return;

        if(badges.containsKey(badgeName))
            return;

        final UserBadge userBadge = new UserBadge(badge, YamlConfiguration.loadConfiguration(user.getPlayerFile()));

        for (UserBadge temp : badges.values()) {
            if (temp.getBadge() == badge)
                return;
        }

        badges.put(badgeName, userBadge);
        badge.add();

        if (user.getOfflinePlayer().isOnline())
            user.getPlayer().sendMessage("§f§lBADGES§8 × §7Dir wurde eine Badge gegeben§8. ( §r" + badge.getPrefix() + " §8)");
    }

    public void save() {
        if (badges.isEmpty()) {
            cfg.set("badges", null);
            return;
        }

        badges.values().forEach(userBadge -> cfg.set("badges." + userBadge.getBadge().getName() + ".receiveddate", userBadge.getReceivedDate()));
    }

    public void openBadgeInventory(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§f§lBADGES§7 " + user.getOfflinePlayer().getName());

        final ItemStack bar = new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build();
        final ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 8).setDisplayname("§7-§8/§7-").build();

        inventory.setItem(0, bar);
        inventory.setItem(1, pane);

        inventory.setItem(9, new ItemBuilder(Material.ITEM_FRAME)
                .setDisplayname("§8┃» §f§lINFORMATION")
                .setLore("",
                        "§8▰§7▱ §f§lFreigeschaltete Badges",
                        "  §8- §f§o" + badges.size() + "§8/§f§l" + badgeHandler.getBadgeMap().size() + "§7 " + (badges.size() == 1 ? "Badge" : "Badges") + "§8.",
                        "")
                .build());
        inventory.setItem(10, pane);

        inventory.setItem(18, bar);
        inventory.setItem(19, pane);

        inventory.setItem(27, bar);
        inventory.setItem(28, pane);

        inventory.setItem(36, bar);
        inventory.setItem(37, pane);

        inventory.setItem(45, bar);
        inventory.setItem(46, pane);

        if (!badgeHandler.getBadgeMap().isEmpty()) {
            badgeHandler.getBadgeMap().values().forEach(badge -> {

                final ItemStack showItem = badge.getShowItem().clone();
                final ItemMeta meta = showItem.getItemMeta();
                final ArrayList<String> lore = new ArrayList<>();

                if (badges.containsKey(badge.getName())) {
                    final UserBadge userBadge = badges.get(badge.getName());

                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', badge.getPrefix()));

                    lore.add("");
                    lore.add("§8▰§7▱ §f§lErstellt am§8:");
                    lore.add("§8 -§7 " + badge.getCreationDate());
                    lore.add("");
                    lore.add("§8▰§7▱ §f§lErhalten am§8:");
                    lore.add("§8 -§7 " + userBadge.getReceivedDate());
                    lore.add("");
                    lore.add("§8▰§7▱ §f§lInformation§8:");
                    lore.add("§8 -§7 Beschreibung§8: §7" + ChatColor.translateAlternateColorCodes('&', badge.getDescription()));
                    lore.add("§8 -§7 Diese Badge wurde x§f" + badge.getOwned() + "§7 mal vergeben§8.");
                    lore.add("");

                } else {
                    showItem.setType(Material.BARRIER);

                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', badge.getPrefix()) + "§c§o nicht freigeschaltet");

                    lore.add("");
                    lore.add("§8▰§7▱ §f§lErstellt am§8:");
                    lore.add("§8 -§7 " + badge.getCreationDate());
                    lore.add("");
                    lore.add("§8▰§7▱ §f§lInformation§8:");
                    lore.add("§8 -§7 Beschreibung§8: §7" + ChatColor.translateAlternateColorCodes('&', badge.getDescription()));
                    lore.add("§8 -§7 Diese Badge wurde x§f" + badge.getOwned() + "§7 mal vergeben§8.");
                    lore.add("");
                    lore.add("§c§oDu hast diese Badge nicht freigeschaltet§8.");
                    lore.add("");
                }

                meta.setLore(lore);

                showItem.setItemMeta(meta);
                inventory.addItem(showItem);
            });
        }

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
    }

}
