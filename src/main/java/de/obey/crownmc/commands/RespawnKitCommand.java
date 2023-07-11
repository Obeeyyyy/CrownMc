package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       11.11.2022 / 17:27

*/

import de.obey.crownmc.backend.RespawnKitItem;
import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.backend.user.UserRespawnKit;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@RequiredArgsConstructor
@NonNull
public final class RespawnKitCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final ServerConfig serverConfig;

    /*

        ServerConfig RespawnKit structure

        respawnkit:
            1:
                1:
                    item: Item
                    price: 1000
                2:
                    item: Item
                    price: 2000

     */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0) {
            userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                user.getRespawnKit().openInventory();
            });
            return false;
        }

        if (!PermissionUtil.hasPermission(((Player) sender).getPlayer(), "edit.respawnkit", true))
            return false;

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("reset")) {

                if (!messageUtil.hasPlayedBefore(sender, args[1]))
                    return false;

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(targetUser -> {
                    targetUser.getRespawnKit().resetLevels();
                    messageUtil.sendMessage(sender, "Du hast das RespawnKit von " + targetUser.getOfflinePlayer().getName() + " zurückgesetzt§8.");
                });

                return false;
            }

            if (args[0].equalsIgnoreCase("info")) {
                try {
                    final int type = Integer.parseInt(args[1]);

                    if (type > 8) {
                        messageUtil.sendMessage(sender, "Ungültiger Type§8.");
                        messageUtil.sendMessage(sender, "1 = helm, 2 = chest, 3 = leggings, 4 = boots, 5 = weapon, 6 = bow, 7 = apple, 8 = enderpearls");
                        return false;
                    }

                    if (!serverConfig.getRespawnKitItems().containsKey(type)) {
                        messageUtil.sendMessage(sender, "Dieser typ wurde noch nicht eingestellt§8.");
                        return false;
                    }

                    final RespawnKitItem kitItem = serverConfig.getRespawnKitItems().get(type);

                    messageUtil.sendMessage(sender, "Info für " + type);

                    kitItem.getLevelItems().keySet().forEach(level -> {
                        player.sendMessage("§8 - §7Level§8: §f§l" + level);
                        player.sendMessage("§8    - §7Material§8: §e§o" + kitItem.getItemForLevel(level).getType().name());
                        player.sendMessage("§8    - §7Peis§8: §e§o" + messageUtil.formatLong(kitItem.getPriceForLevel(level)));
                    });

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Ungültiger Type§8.");
                    messageUtil.sendMessage(sender, "1 = helm, 2 = chest, 3 = leggings, 4 = boots, 5 = weapon, 6 = bow, 7 = apple, 8 = enderpearls");
                }

                return false;
            }

            if (args[0].equalsIgnoreCase("get")) {

                final int level = Integer.parseInt(args[1]);

                for (int i = 1; i <= 8; i++) {
                    final RespawnKitItem kitItem = serverConfig.getRespawnKitItems().get(i);
                    InventoryUtil.addItem(player, kitItem.getItemForLevel(level));
                }

                messageUtil.sendMessage(player, "Du hast dir das RespawnKit Level " + level + " gegeben§8.");

                return false;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setitem")) {
                if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                    messageUtil.sendMessage(sender, "Du musst ein Item in der Hand halten.");
                    return false;
                }

                try {
                    final int type = Integer.parseInt(args[1]);

                    if (type > 8) {
                        messageUtil.sendMessage(sender, "Ungültiger Type§8.");
                        messageUtil.sendMessage(sender, "1 = helm, 2 = chest, 3 = leggings, 4 = boots, 5 = weapon, 6 = bow, 7 = apple, 8 = enderpearls");
                        return false;
                    }

                    try {
                        final int level = Integer.parseInt(args[2]);
                        final RespawnKitItem kitItem = serverConfig.getRespawnKitItems().get(type);
                        kitItem.getLevelItems().put(level, player.getItemInHand());
                        messageUtil.sendMessage(sender, "Item für  " + type + "." + level + " angepasst§8.");
                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(sender, "Level muss eine Zahl sein§8.");
                        return false;
                    }

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Ungültiger Type§8.");
                    messageUtil.sendMessage(sender, "1 = helm, 2 = chest, 3 = leggings, 4 = boots, 5 = weapon, 6 = bow, 7 = apple, 8 = enderpearls");
                }

                return false;
            }

            if (args[0].equalsIgnoreCase("clearitem")) {
                try {
                    final int type = Integer.parseInt(args[1]);

                    if (type > 8) {
                        messageUtil.sendMessage(sender, "Ungültiger Type§8.");
                        messageUtil.sendMessage(sender, "1 = helm, 2 = chest, 3 = leggings, 4 = boots, 5 = weapon, 6 = bow, 7 = apple, 8 = enderpearls");
                        return false;
                    }

                    try {
                        final int level = Integer.parseInt(args[2]);
                        final RespawnKitItem kitItem = serverConfig.getRespawnKitItems().get(type);
                        kitItem.getLevelItems().remove(level);
                        messageUtil.sendMessage(sender, "Item für  " + type + "." + level + " resettet§8.");
                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(sender, "Level muss eine Zahl sein§8.");
                        return false;
                    }

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Ungültiger Type§8.");
                    messageUtil.sendMessage(sender, "1 = helm, 2 = chest, 3 = leggings, 4 = boots, 5 = weapon, 6 = bow, 7 = apple, 8 = enderpearls");
                }

                return false;
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("setprice")) {

                try {
                    final int type = Integer.parseInt(args[1]);

                    if (type > 8) {
                        messageUtil.sendMessage(sender, "Ungültiger Type§8.");
                        messageUtil.sendMessage(sender, "1 = helm, 2 = chest, 3 = leggings, 4 = boots, 5 = weapon, 6 = bow, 7 = apple, 8 = enderpearls");
                        return false;
                    }

                    try {
                        final int level = Integer.parseInt(args[2]);
                        final long price = Long.parseLong(args[3]);
                        final RespawnKitItem kitItem = serverConfig.getRespawnKitItems().get(type);

                        kitItem.getLevelPrices().put(level, price);
                        messageUtil.sendMessage(sender, "Preis für " + type + "." + level + " auf " + messageUtil.formatLong(price) + " gesetzt§8.");
                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(sender, "Level muss eine Zahl sein§8.");
                        return false;
                    }

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Ungültiger Type§8.");
                    messageUtil.sendMessage(sender, "1 = helm, 2 = chest, 3 = leggings, 4 = boots, 5 = weapon, 6 = bow, 7 = apple, 8 = enderpearls");
                }


                return false;
            }

            if(args[0].equalsIgnoreCase("setlevel")) {

                final int type = Integer.parseInt(args[2]);
                final int level = Integer.parseInt(args[3]);

                if (type > 8) {
                    messageUtil.sendMessage(sender, "Ungültiger Type§8.");
                    messageUtil.sendMessage(sender, "1 = helm, 2 = chest, 3 = leggings, 4 = boots, 5 = weapon, 6 = bow, 7 = apple, 8 = enderpearls");
                    return false;
                }

                if(level > serverConfig.getRespawnKitItems().get(type).getMaxLevel()) {
                    messageUtil.sendMessage(sender, "Level zu hoch§8.");
                    return false;
                }

                if (!messageUtil.hasPlayedBefore(sender, args[0]))
                    return false;

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> user.getRespawnKit().getItemLevels().put(type, level));
                messageUtil.sendMessage(sender, target.getName() + " type auf " + level + "§8.");

                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/respawnkit setlevel <spieler> <type> <value>",
                "/respawnkit setitem <type> <level> muss item in der hand halten",
                "/respawnkit setprice <type> <level> <price>",
                "/respawnkit clearitem <type> <level>",
                "/respawnkit info <type>",
                "/respawnkit get <level>",
                "TYPES: 1 = helm, 2 = chest, 3 = leggings, 4 = boots, 5 = weapon, 6 = bow, 7 = apple, 8 = enderpearls",
                "/respawnkit reset <spieler>");

        return false;
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if (!InventoryUtil.isInventoryTitle(event.getInventory(), "§f§lRESPAWNKIT"))
            return;

        event.setCancelled(true);

        if (!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§f§lRESPAWNKIT"))
            return;

        if (!event.isLeftClick())
            return;

        final Player player = (Player) event.getWhoClicked();
        final User user = userHandler.getUserInstant(player.getUniqueId());
        final UserRespawnKit userRespawnKit = user.getRespawnKit();

        serverConfig.getRespawnKitItems().values().forEach(respawnKitItem -> {
            if (respawnKitItem.getSlot() == event.getSlot()) {

                final long price = respawnKitItem.getPriceForLevel(userRespawnKit.getLevelForType(respawnKitItem.getType()) + 1);

                if (respawnKitItem.getMaxLevel() <= userRespawnKit.getLevelForType(respawnKitItem.getType())) {
                    messageUtil.sendMessage(player, "Du hast dieses Item bereits auf dem §a§oMaximallevel§8.");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return;
                }

                if (!messageUtil.hasEnougthMoney(user, price))
                    return;

                user.removeLong(DataType.MONEY, price);
                userRespawnKit.addLevel(respawnKitItem.getType(), event.getClickedInventory());
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                messageUtil.sendMessage(player, "Du hast das §a§oUpgrade§7 für §e§o" + messageUtil.formatLong(price) + "§6§l$§7 gekauft§8.");
            }
        });
        // SOON
    }
}
