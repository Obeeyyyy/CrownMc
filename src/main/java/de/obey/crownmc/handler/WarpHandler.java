package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       07.11.2022 / 17:49

*/

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.objects.Warp;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public final class WarpHandler {

    @Getter
    private final Map<String, Warp> warps = new HashMap<>();

    @NonNull
    private final MessageUtil messageUtil;
    @NonNull
    private final LocationHandler locationHandler;
    @NonNull
    private final ServerConfig serverConfig;

    public void loadWarps() {
        final YamlConfiguration cfg = serverConfig.getCfg();

        if (cfg.contains("warps")) {
            final Set<String> warpSection = cfg.getConfigurationSection("warps").getKeys(false);

            warpSection.forEach(warpName -> {
                warps.put(warpName, new Warp(warpName, cfg));
            });
        }
    }

    public void createWarp(final Player player, String warpName) {
        warpName = warpName.toLowerCase();
        if (warps.containsKey(warpName)) {
            messageUtil.sendMessage(player, "Der Warp " + warpName + " existiert bereits§8.");
            return;
        }

        final YamlConfiguration cfg = serverConfig.getCfg();

        cfg.set("warps." + warpName + ".slot", 15);
        cfg.set("warps." + warpName + ".prefix", "§f§l" + warpName);

        FileUtil.saveToFile(serverConfig.getConfigFile(), cfg);
        warps.put(warpName, new Warp(warpName, cfg));
        locationHandler.setLocation(warpName, player.getLocation());
        messageUtil.sendMessage(player, "Du hast den Warp " + warpName + " erstellt§8.");
    }

    public void deleteWarp(final Player player, String warpName) {
        warpName = warpName.toLowerCase();
        if (!warps.containsKey(warpName)) {
            messageUtil.sendMessage(player, "Der Warp " + warpName + " existiert nicht§8.");
            return;
        }

        final YamlConfiguration cfg = serverConfig.getCfg();

        cfg.set("warps." + warpName, null);
        warps.remove(warpName);
        locationHandler.deleteLocation(warpName);

        messageUtil.sendMessage(player, "Du hast den Warp " + warpName + " gelöscht§8.");
        FileUtil.saveToFile(serverConfig.getConfigFile(), cfg);
    }

    public void setSlot(final Player player, String warpName, final int slot) {
        warpName = warpName.toLowerCase();
        if (!warps.containsKey(warpName)) {
            messageUtil.sendMessage(player, "Der Warp " + warpName + " existiert nicht§8.");
            return;
        }

        final YamlConfiguration cfg = serverConfig.getCfg();

        cfg.set("warps." + warpName + ".slot", slot);
        warps.get(warpName).setSlot(slot);

        messageUtil.sendMessage(player, "Slot für " + warpName + " auf " + slot + " gesetzt§8.");
        FileUtil.saveToFile(serverConfig.getConfigFile(), cfg);
    }

    public void setItem(final Player player, String warpName, final ItemStack item) {
        warpName = warpName.toLowerCase();

        if (!warps.containsKey(warpName)) {
            messageUtil.sendMessage(player, "Der Warp " + warpName + " existiert nicht§8.");
            return;
        }

        final YamlConfiguration cfg = serverConfig.getCfg();

        cfg.set("warps." + warpName + ".material", item.getType().toString());
        warps.get(warpName).setShowMaterial(item.getType());

        messageUtil.sendMessage(player, "Item für " + warpName + " aktuallisiert§8.");
        FileUtil.saveToFile(serverConfig.getConfigFile(), cfg);
    }

    public void setPrefix(final Player player, String warpName, final String prefix) {
        warpName = warpName.toLowerCase();

        if (!warps.containsKey(warpName)) {
            messageUtil.sendMessage(player, "Der Warp " + warpName + " existiert nicht§8.");
            return;
        }

        final YamlConfiguration cfg = serverConfig.getCfg();

        cfg.set("warps." + warpName + ".prefix", prefix);
        warps.get(warpName).setPrefix(prefix);

        messageUtil.sendMessage(player, "Prefix für " + warpName + " auf " + prefix + " §7gesetzt§8.");
        FileUtil.saveToFile(serverConfig.getConfigFile(), cfg);
    }

    public void teleportToWarp(final Player player, String warpName) {
        warpName = warpName.toLowerCase();

        if (!warps.containsKey(warpName)) {
            messageUtil.sendMessage(player, "Der Warp " + warpName + " existiert nicht§8.");
            return;
        }

        locationHandler.teleportToLocationName(player, warpName);
    }

    public void openWarpInventory(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§e§lWARPS");

        final ItemStack bar = new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build();

        InventoryUtil.fillSideRows(inventory, bar);

        if (!warps.isEmpty()) {
            warps.values().forEach(warp -> {
                final ItemStack item = new ItemBuilder(warp.getShowMaterial()).setDisplayname(ChatColor.translateAlternateColorCodes('&', warp.getPrefix()))
                        .setLore("", "§8▰§7▱ §e§lKlick",
                                "§8  - §7Starte die Teleportation§8.",
                                ""
                        ).build();
                inventory.setItem(warp.getSlot(), item);
            });
        }

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);
    }

}
