package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       30.12.2022 / 20:29

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.ShopHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.objects.shop.Category;
import de.obey.crownmc.objects.shop.CategoryType;
import de.obey.crownmc.objects.shop.ShopItem;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
@NonNull
public final class ShopCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final ShopHandler shopHandler;
    private final UserHandler userHandler;
    private Player player;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0) {
            shopHandler.openShop(player);
            return false;
        }

        if (!PermissionUtil.hasPermission(player, "admin", true))
            return false;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("listcat")) {

                if (shopHandler.getCategories().isEmpty()) {
                    messageUtil.sendMessage(player, "Es existieren keine Kategorien§8.");
                    return false;
                }

                for (Category value : shopHandler.getCategories().values()) {
                    player.sendMessage("§7Kategorie§8: §f" + value.getName());
                    player.sendMessage("§8 -> §7Prefix§8: §f" + value.getPrefix());
                    player.sendMessage("§8 -> §7Slot§8: §f" + value.getShowSlot());
                    player.sendMessage("§8 -> §7Item§8: §f" + value.getMaterial().name());
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("resetcount")) {

                if (shopHandler.getCategories().isEmpty()) {
                    messageUtil.sendMessage(player, "Es existieren keine Kategorien§8.");
                    return false;
                }

                shopHandler.getCategories().values().forEach(category -> category.getItems().forEach(shopItem -> shopItem.setCount(0)));
                messageUtil.sendMessage(player, "Count resettet§8.");

                return false;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("createcat")) {

                if (shopHandler.getCategories().containsKey(args[1])) {
                    messageUtil.sendMessage(player, "Die Kategorie " + args[1] + " existiert schon§8.");
                    return false;
                }

                shopHandler.createCategory(args[1]);
                messageUtil.sendMessage(player, "Du hast die Kategorie " + args[1] + " erstellt§8.");

                return false;
            }

            if (args[0].equalsIgnoreCase("removecat")) {

                if (!shopHandler.getCategories().containsKey(args[1])) {
                    messageUtil.sendMessage(player, "Die Kategorie " + args[1] + " existiert nicht§8.");
                    return false;
                }

                shopHandler.deleteCategory(args[1]);
                messageUtil.sendMessage(player, "Du hast die Kategorie " + args[1] + " gelöscht§8.");

                return false;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("cat")) {
                if (args[1].equalsIgnoreCase("setitem")) {

                    if (!InventoryUtil.hasItemInHand(player))
                        return false;

                    if (!shopHandler.getCategories().containsKey(args[2])) {
                        messageUtil.sendMessage(player, "Die Kategorie " + args[2] + " existiert nicht§8.");
                        return false;
                    }

                    shopHandler.getCategories().get(args[2]).setMaterial(player.getItemInHand());
                    messageUtil.sendMessage(player, "Du hast das ShowItem für " + args[2] + " auf " + player.getItemInHand().getType().name() + " gesetzt§8.");
                    shopHandler.updateShopInventory();

                    return false;
                }

                if(args[2].equalsIgnoreCase("items")) {

                    if (!shopHandler.getCategories().containsKey(args[1])) {
                        messageUtil.sendMessage(player, "Die Kategorie " + args[1] + " existiert nicht§8.");
                        return false;
                    }

                    final Category category = shopHandler.getCategories().get(args[1]);

                    if(category.getItems().isEmpty()) {
                        messageUtil.sendMessage(player, "Die Kategorie hat noch keine Items§8.");
                        return false;
                    }

                    messageUtil.sendMessage(player, "Items in der Kategorie (" + category.getName() + ") :");

                    category.getItems().forEach(shopItem -> {
                        player.sendMessage("§8 - §3ID§8:§b" + shopItem.getID());
                        player.sendMessage("§8   ->§7 item§8:§f " + (shopItem.getItemStack().hasItemMeta() ? (shopItem.getItemStack().getItemMeta().hasDisplayName() ? shopItem.getItemStack().getItemMeta().getDisplayName() : shopItem.getItemStack().getType().name()) : shopItem.getItemStack().getType().name()));
                        player.sendMessage("§8   ->§7 amount§8:§f " + shopItem.getItemStack().getAmount());
                        player.sendMessage("§8   ->§7 slot§8:§f " + shopItem.getSlot());
                        player.sendMessage("§8   ->§7 price§8:§f " + shopItem.getPrice());
                    });

                    return false;
                }
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("cat")) {
                if (args[1].equalsIgnoreCase("setslot")) {

                    if (!shopHandler.getCategories().containsKey(args[2])) {
                        messageUtil.sendMessage(player, "Die Kategorie " + args[2] + " existiert nicht§8.");
                        return false;
                    }

                    try {
                        final int slot = Integer.parseInt(args[3]);

                        shopHandler.getCategories().get(args[2]).setShowSlot(slot);
                        messageUtil.sendMessage(player, "Du hast den ShowSlot für " + args[2] + " auf " + slot + " gesetzt§8.");
                        shopHandler.updateShopInventory();

                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                    }


                    return false;
                }

                if (args[2].equalsIgnoreCase("setitem")) {

                    final Category category = shopHandler.getCategories().get(args[1]);

                    if (category == null) {
                        messageUtil.sendMessage(player, "Die Kategorie " + args[1] + " existiert nicht §8.");
                        return false;
                    }

                    if (!InventoryUtil.hasItemInHand(player))
                        return false;

                    try {
                        final int ID = Integer.parseInt(args[3]);

                        category.addShopItem(ID, player.getItemInHand());
                        messageUtil.sendMessage(player, "Du hast das item " + ID + " erstellt§8.");
                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(player, "Bitte gebe eine ID an§8.");
                    }

                    return false;
                }

                if (args[2].equalsIgnoreCase("removeitem")) {

                    final Category category = shopHandler.getCategories().get(args[1]);

                    if (category == null) {
                        messageUtil.sendMessage(player, "Die Kategorie " + args[1] + " existiert nicht §8.");
                        return false;
                    }

                    try {
                        final int ID = Integer.parseInt(args[3]);
                        final ShopItem shopItem = category.getShopItemFromID(ID);

                        if (shopItem == null) {
                            messageUtil.sendMessage(player, "Es existiert kein Item mit der ID " + ID + "§8.");
                            return false;
                        }

                        category.getItems().remove(shopItem.removeItem());
                        shopHandler.save();
                        messageUtil.sendMessage(player, "Du hast das item " + ID + " entfernt§8.");
                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(player, "Bitte gebe eine ID an§8.");
                    }

                    return false;
                }
            }
        }

        if (args.length >= 4) {
            if (args[0].equalsIgnoreCase("cat")) {
                if (args[1].equalsIgnoreCase("setprefix")) {

                    if (!shopHandler.getCategories().containsKey(args[2])) {
                        messageUtil.sendMessage(player, "Die Kategorie " + args[2] + " existiert nicht§8.");
                        return false;
                    }

                    String prefix = args[3];

                    if (args.length > 4) {
                        for (int i = 4; i < args.length; i++) {
                            prefix = prefix + " " + args[i];
                        }
                    }

                    shopHandler.getCategories().get(args[2]).setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
                    messageUtil.sendMessage(player, "Du hast den Prefix für " + args[2] + " auf " + prefix + " gesetzt§8.");
                    shopHandler.updateShopInventory();

                    return false;
                }
            }
        }

        if (args.length == 5) {
            if (args[0].equalsIgnoreCase("cat")) {

                if (args[2].equalsIgnoreCase("setprice")) {

                    final Category category = shopHandler.getCategories().get(args[1]);

                    if (category == null) {
                        messageUtil.sendMessage(player, "Die Kategorie " + args[1] + " existiert nicht §8.");
                        return false;
                    }

                    try {
                        final int ID = Integer.parseInt(args[3]);
                        final ShopItem shopItem = category.getShopItemFromID(ID);

                        if (shopItem == null) {
                            messageUtil.sendMessage(player, "Es existiert kein Item mit der ID " + ID + "§8.");
                            return false;
                        }
                        try {
                            final long price = Long.parseLong(args[4]);

                            shopItem.setPrice(price);
                            messageUtil.sendMessage(player, "Du hast den Preis für das Item " + shopItem.getID() + " auf " + messageUtil.formatLong(price) + "§7 gesetzt§8.");

                        } catch (final NumberFormatException exception) {
                            messageUtil.sendMessage(player, "Bitte gebe einen Preis an§8.");
                        }
                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(player, "Bitte gebe eine ID an§8.");
                    }

                    return false;
                }

                if (args[2].equalsIgnoreCase("setslot")) {

                    final Category category = shopHandler.getCategories().get(args[1]);

                    if (category == null) {
                        messageUtil.sendMessage(player, "Die Kategorie " + args[1] + " existiert nicht §8.");
                        return false;
                    }

                    try {
                        final int ID = Integer.parseInt(args[3]);
                        final ShopItem shopItem = category.getShopItemFromID(ID);

                        if (shopItem == null) {
                            messageUtil.sendMessage(player, "Es existiert kein Item mit der ID " + ID + "§8.");
                            return false;
                        }
                        try {
                            final int slot = Integer.parseInt(args[4]);

                            shopItem.setSlot(slot);
                            messageUtil.sendMessage(player, "Du hast den Slot für das Item " + shopItem.getID() + " auf " + slot + "§7 gesetzt§8.");

                        } catch (final NumberFormatException exception) {
                            messageUtil.sendMessage(player, "Bitte gebe einen Preis an§8.");
                        }
                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(player, "Bitte gebe eine ID an§8.");
                    }

                    return false;
                }
            }
            return false;
        }

        messageUtil.sendSyntax(sender, "/shop createcat <name>",
                "/shop removecat <name>",
                "/shop listcat",
                "/shop resetcount",
                "/shop cat setprefix <name> <prefix>",
                "/shop cat setslot <name> <slot>",
                "/shop cat setitem <name>",
                "/shop cat <category> items",
                "/shop cat <category> setitem <id>",
                "/shop cat <category> removeitem <id>",
                "/shop cat <category> setslot <id> <slot>",
                "/shop cat <category> setprice <id> <price>"
        );

        return false;
    }


    // Shop Inventory
    @EventHandler
    public void onShop(final InventoryClickEvent event) {
        if (!InventoryUtil.isInventoryTitle(event.getInventory(), "§6§lShop"))
            return;

        event.setCancelled(true);

        if (!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§6§lShop"))
            return;

        final Category kategory = shopHandler.getcategoryFromClickedSlot(event.getSlot());

        if (kategory == null)
            return;

        kategory.openInventory((Player) event.getWhoClicked());
    }


    // Kategory Inventory
    @EventHandler
    public void onKategory(final InventoryClickEvent event) {
        if (!InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§7Kategorie "))
            return;

        event.setCancelled(true);

        if (!InventoryUtil.startsWithInventoryTitle(event.getClickedInventory(), "§7Kategorie "))
            return;

        final Category kategory = shopHandler.getCategoryFromPrefix(event.getClickedInventory().getTitle().split(" ")[1]);

        if (kategory == null)
            return;

        final Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER) {
            shopHandler.openShop(player);
            return;
        }

        final ShopItem shopItem = kategory.getShopItemFromClickedSlot(event.getSlot());

        if (shopItem == null)
            return;

        if (shopItem.getPrice() == 0) {
            messageUtil.sendMessage(player, "Dieser Gegenstand wurde noch nicht eingesetllt§8.");
            return;
        }

        if (kategory.getCategoryType() == CategoryType.BUY) {

            if (!event.isLeftClick())
                return;

            if (player.getInventory().firstEmpty() == -1) {
                messageUtil.sendMessage(player, "§c§oDein Inventar ist voll§8.");
                return;
            }

            int amount = event.isShiftClick() ? 64 : 1;

            if (shopItem.getItemStack().getMaxStackSize() == 1 && amount > 1)
                amount = 1;

            final int finalAmount = amount;

            userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

                if(kategory.getName().equalsIgnoreCase("crowns")) {

                    if (!messageUtil.hasEnougthCrowns(user, Math.toIntExact(finalAmount * shopItem.getPrice())))
                        return;

                    user.removeLong(DataType.CROWNS, Math.toIntExact(shopItem.getPrice() * finalAmount));

                    shopItem.setCount(shopItem.getCount() + finalAmount);
                    kategory.updateInventory(event.getInventory());

                    messageUtil.sendMessage(player, "Du hast §8x§f" + finalAmount + "§e§o " +
                            (shopItem.getItemStack().hasItemMeta() ?
                                    (shopItem.getItemStack().getItemMeta().hasDisplayName() ?
                                            shopItem.getItemStack().getItemMeta().getDisplayName() :
                                            shopItem.getItemStack().getType().name()) :
                                    shopItem.getItemStack().getType().name()) +
                            "§7 für §c§o-" + messageUtil.formatLong(shopItem.getPrice() * finalAmount) + (kategory.getName().equalsIgnoreCase("crowns") ? "§7 Crowns" : "§6§l$") + "§7 gekauft§8.");

                    for (int i = 0; i < finalAmount; i++)
                        InventoryUtil.addItem(player, shopItem.getItemStack().clone());

                    messageUtil.log("BUY " + player.getName() + " -> x" + + finalAmount + " " + shopItem.getItemStack().getType().name() + " / " + (shopItem.getPrice() * finalAmount) + "$");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 10);

                    return;
                }

                if (!messageUtil.hasEnougthMoney(user, finalAmount * shopItem.getPrice()))
                    return;

                user.removeLong(DataType.MONEY, shopItem.getPrice() * finalAmount);

                shopItem.setCount(shopItem.getCount() + finalAmount);
                kategory.updateInventory(event.getInventory());

                messageUtil.sendMessage(player, "Du hast §8x§f" + finalAmount + "§e§o " +
                        (shopItem.getItemStack().hasItemMeta() ?
                                (shopItem.getItemStack().getItemMeta().hasDisplayName() ?
                                        shopItem.getItemStack().getItemMeta().getDisplayName() :
                                        shopItem.getItemStack().getType().name()) :
                                shopItem.getItemStack().getType().name()) +
                        "§7 für §c§o-" + messageUtil.formatLong(shopItem.getPrice() * finalAmount) + (kategory.getName().equalsIgnoreCase("crowns") ? "§7 Crowns" : "§6§l$") + "§7 gekauft§8.");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < finalAmount; i++)
                            InventoryUtil.addItem(player, shopItem.getItemStack().clone());
                    }
                }.runTask(CrownMain.getInstance());

                messageUtil.log("CROWN BUY " + player.getName() + " -> " + shopItem.getItemStack().getType().name() + " / " + (shopItem.getPrice() * finalAmount));
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 10);
            });

            return;

        } else {

            final int amount = event.isLeftClick() ? 1 : -1;
            int soldItems = amount > 0 ? InventoryUtil.removeItem(player, shopItem.getItemStack(), amount) : InventoryUtil.removeItem(player, shopItem.getItemStack());

            if (soldItems == 0) {
                messageUtil.sendMessage(player, "Du hast nicht genug §e§o" +
                        (shopItem.getItemStack().hasItemMeta() ?
                                (shopItem.getItemStack().getItemMeta().hasDisplayName() ?
                                        shopItem.getItemStack().getItemMeta().getDisplayName() :
                                        shopItem.getItemStack().getType().name()) :
                                shopItem.getItemStack().getType().name()) +
                        "§7 in deinem Inventar§8.");

                player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                return;
            }

            userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

                shopItem.setCount(shopItem.getCount() + soldItems);
                kategory.updateInventory(event.getInventory());
                user.addLong(DataType.MONEY, shopItem.getPrice() * soldItems);

                messageUtil.sendMessage(player, "Du hast §8x§f" + (soldItems * shopItem.getItemStack().getAmount()) + "§e§o " +
                        (shopItem.getItemStack().hasItemMeta() ?
                                (shopItem.getItemStack().getItemMeta().hasDisplayName() ?
                                        shopItem.getItemStack().getItemMeta().getDisplayName() :
                                        shopItem.getItemStack().getType().name()) :
                                shopItem.getItemStack().getType().name()) +
                        "§7 für §a§o+" + messageUtil.formatLong(shopItem.getPrice() * soldItems) + "§6§l$§7 verkauft§8.");

                messageUtil.log("SOLD " + player.getName() + " -> x" + (soldItems * shopItem.getItemStack().getAmount()) + " " + shopItem.getItemStack().getType().name() + " / " + (shopItem.getPrice() * soldItems) + "$");

                player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 10);
            });
        }
    }
}
