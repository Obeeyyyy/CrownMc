package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       15.06.2023 / 02:32

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.LuckySpinHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor @NonNull
public final class LuckySpinCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final LuckySpinHandler luckySpinHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        final Player player = (Player) sender;

        if(args.length == 1) {
            if (args[0].equalsIgnoreCase("cc")) {

                final double[] currentSum = {0};

                luckySpinHandler.getItems().forEach(item -> currentSum[0] += luckySpinHandler.getChanceFromItem(item));

                final DecimalFormat format = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));

                sender.sendMessage("Alle Chancen ergeben §8:");
                sender.sendMessage(format.format(currentSum[0]) + "%");

                return false;
            }

            if (args[0].equalsIgnoreCase("items")) {

                final Inventory inventory = Bukkit.createInventory(null, 9*7, "setluckyitems");

                if(luckySpinHandler.getItems().size() > 0) {
                    final AtomicInteger slot = new AtomicInteger();
                    luckySpinHandler.getItems().forEach(item -> inventory.setItem(slot.getAndIncrement(), item));
                }

                player.openInventory(inventory);

                return false;
            }

            if (args[0].equalsIgnoreCase("chance")) {

                final Inventory inventory = Bukkit.createInventory(null, 9*7, "luckychance");

                if(luckySpinHandler.getItems().size() > 0) {
                    final AtomicInteger slot = new AtomicInteger();
                    luckySpinHandler.getItems().forEach(item -> inventory.setItem(slot.getAndIncrement(), item));
                }

                player.openInventory(inventory);

                return false;
            }
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("reset")) {

                if(!messageUtil.hasPlayedBefore(sender, args[1]))
                    return false;

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(user -> {
                   user.setLong(DataType.LASTLUCKYSPIN, System.currentTimeMillis() - 1000*60*60*25);
                   messageUtil.sendMessage(player, user.getOfflinePlayer().getName() + " kann das Rad jetzt wieder drehen§8.");
                });

                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/luckyspin cc",
                        "/luckyspin items",
                "/luckyspin chance",
                "/luckyspin reset <spieler>");

        return false;
    }


    @EventHandler
    public void on(final InventoryClickEvent event) {
        if (InventoryUtil.isInventoryTitle(event.getInventory(), "luckychance")) {
            event.setCancelled(true);

            if (!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "luckychance"))
                return;

            final ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR)
                return;

            ArrayList<String> lore = new ArrayList<>();

            if (item.hasItemMeta() && item.getItemMeta().hasLore())
                lore = (ArrayList<String>) item.getItemMeta().getLore();

            final Player player = (Player) event.getWhoClicked();
            final DecimalFormat format = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
            final double chance = Double.parseDouble(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1).split(" ")[1].replace("%", ""));

            // Chance hoch
            if (event.getClick().isLeftClick()) {
                if (event.getClick().isShiftClick()) {
                    lore.remove(lore.size() - 1);
                    lore.add("Chance " + format.format((chance + 0.01)) + "%");
                } else {
                    lore.remove(lore.size() - 1);
                    lore.add("Chance " + format.format((chance + 1)) + "%");
                }

                final ItemMeta meta = item.getItemMeta();

                meta.setLore(lore);
                item.setItemMeta(meta);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);

                return;
            }

            // Chance runter
            if (event.getClick().isRightClick()) {
                if (event.getClick().isShiftClick()) {
                    lore.remove(lore.size() - 1);
                    lore.add("Chance " + format.format((chance - 0.01)) + "%");
                } else {
                    lore.remove(lore.size() - 1);
                    lore.add("Chance " + format.format((chance - 1)) + "%");
                }

                final ItemMeta meta = item.getItemMeta();

                meta.setLore(lore);
                item.setItemMeta(meta);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            }
        }
    }

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if(InventoryUtil.isInventoryTitle(event.getInventory(), "setluckyitems")
        || InventoryUtil.isInventoryTitle(event.getInventory(), "luckychance")) {

            luckySpinHandler.setItems(event.getInventory());
            messageUtil.sendMessage(event.getPlayer(), "Items gesetzt§8.");
        }
    }

    @EventHandler
    public void on(final SignChangeEvent event) {
        if(!PermissionUtil.hasPermission(event.getPlayer(), "admin", false))
            return;

        if(event.getLine(0).startsWith("lw")) {
            event.setLine(0, "§8▰§7▱ §dLuckyWheel §7▱§8▰");
            event.setLine(2, "Klicke hier um");
            event.setLine(3,"zu spielen§8.");
        }
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(!event.getClickedBlock().getType().name().contains("SIGN"))
            return;

        final Sign sign = (Sign) event.getClickedBlock().getState();


        if(sign.getLine(0).equalsIgnoreCase("§8▰§7▱ §dLuckyWheel §7▱§8▰"))
            luckySpinHandler.spin(event.getPlayer());
    }
}
