package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       17.11.2022 / 13:49

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.objects.pvp.Kit;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class KitHandler {

    @NonNull
    private final MessageUtil messageUtil;

    @NonNull
    private final UserHandler userHandler;

    @Getter
    private final Map<String, Kit> kitCache = new HashMap<>();
    private final Map<Player, Inventory> openInventories = new HashMap<>();
    private final Map<Player, Kit> editing = new HashMap<>();

    public void loadKits() {
        final File kitFolder = new File(CrownMain.getInstance().getDataFolder().getPath() + "/kits");

        if (!kitFolder.exists()) {
            kitFolder.mkdir();
            return;
        }

        if (kitFolder.listFiles().length == 0)
            return;

        for (final File file : kitFolder.listFiles())
            kitCache.put(file.getName().replace(".yml", ""), new Kit(file));
    }

    public void save() {
        if (kitCache.isEmpty())
            return;

        kitCache.values().forEach(Kit::saveKitData);
    }

    public void createKit(final Player player, final String name) {
        if (kitCache.containsKey(name)) {
            messageUtil.sendMessage(player, "Das Kit " + name + " existiert bereits§8.");
            return;
        }

        kitCache.put(name, new Kit(new File(CrownMain.getInstance().getDataFolder().getPath() + "/kits/" + name + ".yml")));
        messageUtil.sendMessage(player, "Du hast das " + name + " Kit erstellt§8.");
    }

    public void deleteKit(final Player player, final String name) {
        if (!kitCache.containsKey(name)) {
            messageUtil.sendMessage(player, "Das Kit " + name + " existiert nicht§8.");
            return;
        }

        final Kit kit = kitCache.get(name);

        kit.getKitFile().delete();

        kitCache.remove(name);
        messageUtil.sendMessage(player, "Du hast das " + name + " Kit gelöscht§8.");
    }

    public void openEditInventory(final Player player, final String kitName) {
        if (!kitCache.containsKey(kitName)) {
            messageUtil.sendMessage(player, "Das Kit " + kitName + " existiert nicht§8.");
            return;
        }

        final Kit kit = getKitByName(kitName);

        editing.put(player, kit);

        final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "Edit " + kitName);

        if (!kit.getItems().isEmpty())
            kit.getItems().forEach(inventory::addItem);

        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
        player.openInventory(inventory);
    }

    public void closeEditInventory(final Player player, final Inventory inventory) {
        if (!editing.containsKey(player))
            return;

        final Kit kit = editing.get(player);

        editing.remove(player);

        kit.getItems().clear();

        for (final ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR)
                continue;

            kit.getItems().add(item.clone());
        }

        kit.saveKitData();
        messageUtil.sendMessage(player, "§a§oItems für " + kit.getName() + " gesetzt§8.");
    }

    public void equipKit(final User user, final Kit kit) {
        final Player player = user.getPlayer();

        user.getCooldowns().setCooldown(kit.getName(), System.currentTimeMillis() + kit.getKitCooldown());

        if (!kit.getItems().isEmpty())
            kit.getItems().forEach(item -> InventoryUtil.addItem(player, item.clone()));

        player.playSound(player.getLocation(), Sound.HORSE_ARMOR, 1, 1);
        messageUtil.sendMessage(player, "Du hast §8'§e§l" + kit.getPrefix() + "§8'§7 ausgerüstet§8.");
    }

    public void equipKit(final Player player, final Kit kit) {

        if (!kit.getItems().isEmpty())
            kit.getItems().forEach(item -> InventoryUtil.addItem(player, item.clone()));

        player.playSound(player.getLocation(), Sound.HORSE_ARMOR, 1, 1);
        messageUtil.sendMessage(player, "Du hast §8'§e§l" + kit.getPrefix() + "§8'§7 ausgerüstet§8.");
    }

    public Kit getKitBySlot(final int slot) {
        if (kitCache.isEmpty())
            return null;

        for (final Kit value : kitCache.values()) {
            if (value.getShowSlot() == slot)
                return value;
        }

        return null;
    }

    public Kit getKitByName(final String name) {
        if (kitCache.isEmpty())
            return null;

        return kitCache.get(name);
    }

    public void openKitPreviewForPlayer(final Player player, final Kit kit) {
        final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§6§lPreview§7 " + kit.getPrefix());

        if (!kit.getItems().isEmpty())
            kit.getItems().forEach(inventory::addItem);

        inventory.setItem(49, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setDisplayname("§c§lZurück")
                .setLore("",
                        "§8▰§7▱ §c§lLinksklick",
                        "§8 - §7Öffne die Kitauswahl§8.",
                        "")
                .setTextur("YmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==", UUID.randomUUID())
                .build());

        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
        player.openInventory(inventory);
    }

    public void openInventory(final Player player) {
        final User user = userHandler.getUserInstant(player.getUniqueId());

        if (user == null) {
            messageUtil.sendMessage(player, "§c§oBitte warte einen Moment ...");
            return;
        }

        final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§e§oWähle dein Kit");

        InventoryUtil.fillSideRows(inventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());

        updateInventory(user, inventory);

        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
        player.openInventory(inventory);
        openInventories.put(player, inventory);
    }

    public void updateInventory(final User user, final Inventory inventory) {
        final Player player = user.getPlayer();

        kitCache.values().forEach(kit -> {
            if (player.hasPermission(kit.getPermission())) {
                if (user.getCooldowns().isReady(kit.getName())) {
                    inventory.setItem(kit.getShowSlot(), new ItemBuilder(kit.getShowMaterial())
                            .setLore("",
                                    "§8▰§7▱ §6§lLinksklick",
                                    "§8 - §7Rüste das Kit aus.",
                                    "",
                                    "§8▰§7▱ §6§lRechtsklick",
                                    "§8 - §7Öffne die Kit Preview§8.",
                                    "")
                            .setDisplayname(kit.getPrefix() + " §8(§a§obereit§8)")
                            .build());
                } else {
                    final long remainingSeconds = user.getCooldowns().getRemainingMillis(kit.getName()) / 1000;

                    if(kit.getBuyOutForSecondPrice() <= 0) {
                        inventory.setItem(kit.getShowSlot(), new ItemBuilder(kit.getShowMaterial())
                                .setLore("", "§8▰§7▱ §6§lStatus",
                                        "§8 - §7Abholbar in§8: §e§o" + MathUtil.getHoursAndMinutesAndSecondsFromSeconds(remainingSeconds),
                                        "",
                                        "§8▰§7▱ §6§lRechtsklick",
                                        "§8 - §7Öffne die Kit Preview§8.",
                                        "")
                                .setDisplayname(kit.getPrefix() + "§8 (§c§onicht bereit§8)")
                                .build());
                    } else {
                        inventory.setItem(kit.getShowSlot(), new ItemBuilder(kit.getShowMaterial())
                                .setLore("", "§8▰§7▱ §6§lStatus",
                                        "§8 - §7Abholbar in§8: §e§o" + MathUtil.getHoursAndMinutesAndSecondsFromSeconds(remainingSeconds),
                                        "",
                                        "§8▰§7▱ §6§lSofort abholen ? §8(§7Linksklick§8)",
                                        "§8 - §7Preis§8: §e§o" + messageUtil.formatLong((long) (kit.getBuyOutForSecondPrice() * remainingSeconds)) + "§6§l$",
                                        "",
                                        "§8▰§7▱ §6§lRechtsklick",
                                        "§8 - §7Öffne die Kit Preview§8.",
                                        "")
                                .setDisplayname(kit.getPrefix() + "§8 (§c§onicht bereit§8)")
                                .build());
                    }
                }
            } else {
                inventory.setItem(kit.getShowSlot(), new ItemBuilder(Material.BARRIER)
                        .setLore("",
                                "§8▰§7▱ §6§lStatus",
                                "§8 - §c§onicht freigeschaltet",
                                "",
                                "§8▰§7▱ §6§lRechtsklick",
                                "§8 - §7Öffne die Kit Preview§8.",
                                "")
                        .setDisplayname(kit.getPrefix())
                        .addEnchantment(Enchantment.ARROW_DAMAGE)
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .build());
            }
        });

        player.updateInventory();
    }

    public void runUpdateTick() {
        if (kitCache.isEmpty())
            return;

        if (openInventories.isEmpty())
            return;

        for (final Player player : openInventories.keySet()) {

            if (player == null || !player.isOnline()) {
                openInventories.remove(player);
                return;
            }

            final Inventory inventory = openInventories.get(player);

            if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CHEST) {
                openInventories.remove(player);
                return;
            }

            updateInventory(userHandler.getUserInstant(player.getUniqueId()), inventory);
        }
    }
}
