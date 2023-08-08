package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       17.11.2022 / 23:41

*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.KitHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.objects.pvp.Kit;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
@NonNull
public final class KitCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final KitHandler kitHandler;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0) {

            kitHandler.openInventory(player);

            return false;
        }

        if (!PermissionUtil.hasPermission(player, "kits.edit", true))
            return false;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {

                if (kitHandler.getKitCache().isEmpty()) {
                    messageUtil.sendMessage(player, "Es existieren noch keine Kits§8.");
                    return false;
                }

                messageUtil.sendMessage(player, "Alle Kits§8:");
                kitHandler.getKitCache().values().forEach(kit -> {
                    player.sendMessage("§8 -> §7" + kit.getName() + " §8( §r" + kit.getPrefix() + " §8)");
                    player.sendMessage("§a     - §7Cooldown§8: §r" + kit.getKitCooldown() + " = " + MathUtil.getHoursAndMinutesAndSecondsFromSeconds(kit.getKitCooldown()));
                    player.sendMessage("§a     - §7Permission§8: §r" + kit.getPermission());
                    player.sendMessage("§a     - §7Price§8: §r" + kit.getBuyOutForSecondPrice() + " pro sek cd");
                    player.sendMessage("§a     - §7ShowMaterial§8: §r" + kit.getShowMaterial().name());
                    player.sendMessage("§a     - §7ShowSlot§8: §r" + kit.getShowSlot());
                });

                return false;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                kitHandler.createKit(player, args[1]);
                return false;
            }

            if (args[0].equalsIgnoreCase("delete")) {
                kitHandler.deleteKit(player, args[1]);
                return false;
            }

            if (args[0].equalsIgnoreCase("edit")) {
                kitHandler.openEditInventory(player, args[1]);
                return false;
            }

            if (args[0].equalsIgnoreCase("setshowitem")) {

                final Kit kit = kitHandler.getKitByName(args[1]);

                if (kit == null) {
                    messageUtil.sendMessage(player, "Das Kit " + args[1] + " existiert nicht§8.");
                    return false;
                }

                if (!InventoryUtil.hasItemInHand(player))
                    return false;

                kit.setShowMaterial(player.getItemInHand().getType());
                messageUtil.sendMessage(player, "Du hast das ShowItem für das " + kit.getPrefix() + "§7 Kit gesetzt§8.");

                return false;
            }
        }

        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("setprefix")) {
                final Kit kit = kitHandler.getKitByName(args[1]);

                if (kit == null) {
                    messageUtil.sendMessage(player, "Das Kit " + args[1] + " existiert nicht§8.");
                    return false;
                }

                String prefix = args[2];

                for (int i = 3; i < args.length; i++) {
                    prefix = prefix + " " + args[i];
                }

                kit.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
                kit.saveKitData();
                messageUtil.sendMessage(player, "Du hast den Prefix für das " + kit.getPrefix() + "§7 Kit gesetzt§8.");

                return false;
            }
        }

        if (args.length == 3) {

            final Kit kit = kitHandler.getKitByName(args[1]);

            if (kit == null) {
                messageUtil.sendMessage(player, "Das Kit " + args[1] + " existiert nicht§8.");
                return false;
            }

            if (args[0].equalsIgnoreCase("setslot")) {
                try {
                    final int slot = Integer.parseInt(args[2]);

                    kit.setShowSlot(slot);
                    kit.saveKitData();
                    messageUtil.sendMessage(player, "Du hast den ShowSlot für das " + kit.getPrefix() + "§7 Kit gesetzt§8.");
                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Du musst eine Zahl angeben§8.");
                }
                return false;
            }

            if (args[0].equalsIgnoreCase("setprice")) {
                try {
                    final double amount = Double.parseDouble(args[2]);

                    kit.setBuyOutForSecondPrice(amount);
                    kit.saveKitData();
                    messageUtil.sendMessage(player, "Du hast den Preis für das " + kit.getPrefix() + "§7 Kit gesetzt§8.");

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Du musst eine Zahl angeben§8.");
                }
                return false;
            }

            if (args[0].equalsIgnoreCase("setcooldown")) {
                try {
                    final long amount = Long.parseLong(args[2]);

                    kit.setKitCooldown(amount);
                    kit.saveKitData();
                    messageUtil.sendMessage(player, "Du hast den Cooldowsn für das " + kit.getPrefix() + "§7 Kit gesetzt§8.");

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Du musst eine Zahl angeben§8.");
                }
                return false;
            }

            if (args[0].equalsIgnoreCase("setpermission")) {

                final String permission = args[2];

                kit.setPermission(permission);
                kit.saveKitData();
                messageUtil.sendMessage(player, "Du hast die Permission für das " + kit.getPrefix() + "§7 Kit gesetzt§8. ");

                return false;
            }
        }

        /*

            /kit create <name>
            /kit setprefix <name> <prefix>
            /kit setslot <name> <slot>
            /kit setprice <name> <price>
            /kit setcooldown <name> <millis>
            /kit setpermission <name> <permission>
            /kit setshowitem <name>
            /kit edit <name>

         */

        messageUtil.sendSyntax(sender,
                "/kit list",
                "/kit create <name>",
                "/kit delete <name>",
                "/kit edit <name>",
                "/kit setshowitem <name>",
                "/kit setprefix <name> <prefix>",
                "/kit setslot <name> <slot>",
                "/kit setprice <name> <price>",
                "/kit setcooldown <name> <millis>",
                "/kit setpermission <name> <permission>");

        return false;
    }

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if (!InventoryUtil.startsWithInventoryTitle(event.getInventory(), "Edit "))
            return;

        kitHandler.closeEditInventory((Player) event.getPlayer(), event.getInventory());
    }

    private final HashMap<UUID, Long> buyCooldown = new HashMap<>();

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if (InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§6§lPreview§7 ")) {
            event.setCancelled(true);

            if (InventoryUtil.startsWithInventoryTitle(event.getClickedInventory(), "§6§lPreview§7 ")) {
                if (event.getSlot() == 49)
                    kitHandler.openInventory((Player) event.getWhoClicked());

                return;
            }

            return;
        }

        if (!InventoryUtil.isInventoryTitle(event.getInventory(), "§e§oWähle dein Kit"))
            return;

        event.setCancelled(true);

        if (!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§e§oWähle dein Kit"))
            return;

        final Kit kit = kitHandler.getKitBySlot(event.getSlot());

        if (kit == null)
            return;

        final Player player = (Player) event.getWhoClicked();

        if (event.isRightClick()) {
            kitHandler.openKitPreviewForPlayer(player, kit);
            return;
        }

        if (event.isLeftClick()) {
            final User user = userHandler.getUserInstant(player.getUniqueId());

            if (!player.hasPermission(kit.getPermission())) {
                messageUtil.sendMessage(player, "§c§oDu hast dieses Kit nicht freigeschaltet§8.");
                player.playSound(player.getLocation(), Sound.EXPLODE, 1, 1);
                return;
            }

            // IF THE KIT IS READY
            if (user.getCooldowns().isReady(kit.getName())) {
                kitHandler.equipKit(user, kit);
                return;
            }

            if(kit.getBuyOutForSecondPrice() <= 0) {
                messageUtil.sendMessage(player, "Dieses Kit kann nicht gekauft werden§8.");
                return;
            }

            // IF THE KIT IS NOT READY

            if (buyCooldown.containsKey(player.getUniqueId()) && buyCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                messageUtil.sendMessage(player, "Bitte warte einen Moment§8.");
                return;
            }

            final long price = (long)(user.getCooldowns().getRemainingMillis(kit.getName()) / 1000 * kit.getBuyOutForSecondPrice());

            if (!messageUtil.hasEnougthMoney(user, price))
                return;

            player.openInventory(InventoryUtil.getConfirmation("Kit " + kit.getName()));
            confirming.put(player, kit);
            prices.put(player, price);
        }
    }

    private final HashMap<Player, Kit> confirming = new HashMap<>();
    private final HashMap<Player, Long> prices = new HashMap<>();
    @EventHandler
    public void onConfirm(final InventoryClickEvent event) {
        if(!InventoryUtil.startsWithInventoryTitle(event.getInventory(), "Kit "))
            return;

        event.setCancelled(true);

        if(!InventoryUtil.startsWithInventoryTitle(event.getClickedInventory(), "Kit "))
            return;

        final Player player = (Player) event.getWhoClicked();
        final User user = userHandler.getUserInstant(player.getUniqueId());

        // grün
        if(event.getCurrentItem().getData().getData() == 5) {

            user.removeLong(DataType.MONEY, prices.get(player));
            messageUtil.sendMessage(player, "Du hast §e§o" + messageUtil.formatLong(prices.get(player)) + "§6§l$ §7für das §8'§e§l" + confirming.get(player).getName() + "§8' §7Kit bezahlt§8.");
            kitHandler.equipKit(user, confirming.get(player));
            buyCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (1000 * 10));

            confirming.remove(player);
            prices.remove(player);

            player.closeInventory();
            return;
        }

        // rot
        if(event.getCurrentItem().getData().getData() == 14) {
            confirming.remove(player);
            prices.remove(player);

            player.playSound(player.getLocation(), Sound.EXPLODE, 0.3f,  1f);
            player.closeInventory();
            messageUtil.sendMessage(player, "§c§oVorgang abgebrochen§8.");
            return;
        }
    }
}
