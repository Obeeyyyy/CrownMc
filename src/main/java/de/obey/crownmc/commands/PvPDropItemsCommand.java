package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       04.08.2023 / 18:58

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.PvPDropHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

@RequiredArgsConstructor @NonNull
public final class PvPDropItemsCommand implements CommandExecutor, Listener {

    private final PvPDropHandler pvPDropHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player =(Player) sender;

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        final Inventory inv = Bukkit.createInventory(null, 9*6, "PvPDrops");

        int slot = 0;
        for (ItemStack pvpDropItem : pvPDropHandler.getItems()) {
            inv.setItem(slot, pvpDropItem);
            slot++;
        }

        player.openInventory(inv);

        return false;
    }

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if(!InventoryUtil.isInventoryTitle(event.getInventory(), "PvPDrops"))
            return;

        pvPDropHandler.setItems(event.getInventory());
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if (InventoryUtil.isInventoryTitle(event.getInventory(), "PvPDrops")) {
            event.setCancelled(true);

            if (!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "PvPDrops"))
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
}
