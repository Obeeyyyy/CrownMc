package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       24.10.2022 / 16:11

*/

import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
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

@RequiredArgsConstructor
public final class BodySeeCommand implements CommandExecutor, Listener {

    @NonNull
    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "bodysee", true))
            return false;

        if (args.length == 1) {

            if (!messageUtil.isOnline(sender, args[0]))
                return false;

            final Player target = Bukkit.getPlayer(args[0]);

            if (player == target) {
                messageUtil.sendMessage(sender, "Das klappt leider nicht.");
                return false;
            }

            final Inventory inventory = Bukkit.createInventory(null, 27, "§6§lBodySee§7 " + target.getName());

            InventoryUtil.fill(inventory, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 8).setDisplayname("§7-§8/§7-").build());


            if (target.getInventory().getHelmet() == null || target.getInventory().getHelmet().getType() == Material.AIR) {
                inventory.setItem(11, new ItemStack(Material.AIR));
            } else {
                inventory.setItem(11, target.getInventory().getHelmet());
            }

            if (target.getInventory().getChestplate() == null || target.getInventory().getChestplate().getType() == Material.AIR) {
                inventory.setItem(12, new ItemStack(Material.AIR));
            } else {
                inventory.setItem(12, target.getInventory().getChestplate());
            }

            if (target.getInventory().getLeggings() == null || target.getInventory().getLeggings().getType() == Material.AIR) {
                inventory.setItem(14, new ItemStack(Material.AIR));
            } else {
                inventory.setItem(14, target.getInventory().getLeggings());
            }

            if (target.getInventory().getBoots() == null || target.getInventory().getBoots().getType() == Material.AIR) {
                inventory.setItem(15, new ItemStack(Material.AIR));
            } else {
                inventory.setItem(15, target.getInventory().getBoots());
            }


            player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
            player.openInventory(inventory);

            return false;
        }

        messageUtil.sendSyntax(sender, "/bodysee <spieler>");

        return false;
    }

    @EventHandler
    public void onBodyseeClick(final InventoryClickEvent event) {

        if (!InventoryUtil.startsWithInventoryTitle(event.getClickedInventory(), "§6§lBodySee§7 "))
            return;

        final Player player = (Player) event.getWhoClicked();

        if (!PermissionUtil.hasPermission(player, "bodysee.edit", false)) {
            event.setCancelled(true);
            return;
        }

        if (event.getSlot() != 11 && event.getSlot() != 12 && event.getSlot() != 14 && event.getSlot() != 15) {
            event.setCancelled(true);
            return;
        }

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.BARRIER) {
            event.setCancelled(true);
            return;
        }
    }


    @EventHandler
    public void onCloseInv(final InventoryCloseEvent event) {
        if (!InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§6§lBodySee§7 "))
            return;

        final Player player = (Player) event.getPlayer();

        if (!PermissionUtil.hasPermission(player, "bodysee.edit", false))
            return;

        if (!messageUtil.isOnline(player, event.getInventory().getTitle().split(" ")[1]))
            return;

        final Player target = Bukkit.getPlayer(event.getInventory().getTitle().split(" ")[1]);

        target.getInventory().setHelmet(event.getInventory().getItem(11));
        target.getInventory().setChestplate(event.getInventory().getItem(12));
        target.getInventory().setLeggings(event.getInventory().getItem(14));
        target.getInventory().setBoots(event.getInventory().getItem(15));

        messageUtil.sendMessage(player, "Die Rüstung von " + target.getName() + " wurde geupdatet.");
    }
}
