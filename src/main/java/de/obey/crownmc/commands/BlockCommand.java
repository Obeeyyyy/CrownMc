package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       06.01.2023 / 21:21

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@RequiredArgsConstructor
@NonNull
public final class BlockCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        if(!PermissionUtil.hasPermission(sender, "block", true))
            return false;

        if (args.length == 1) {
            try {
                final int site = Integer.parseInt(args[0]);
                openBlockSite((Player) sender, site);
                return false;
            } catch (final NumberFormatException exception) {

            }
        }

        openBlockSite((Player) sender, 1);

        return false;
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {

        if (!InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§7Blöcke Seite§f"))
            return;

        event.setCancelled(true);

        if (!InventoryUtil.startsWithInventoryTitle(event.getClickedInventory(), "§7Blöcke Seite§f"))
            return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;

        final Player player = (Player) event.getWhoClicked();

        if (event.getSlot() > 44) {
            if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {

                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8» §7Nächste Seite")) {
                    try {
                        openBlockSite(player, Integer.parseInt(event.getClickedInventory().getTitle().split(" ")[2]) + 1);
                    } catch (final NumberFormatException ignored) {
                    }
                    return;
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8« §7Vorhärige Seite")) {
                    try {
                        openBlockSite(player, Integer.parseInt(event.getClickedInventory().getTitle().split(" ")[2]) - 1);
                    } catch (final NumberFormatException ignored) {
                    }
                    return;
                }
            }
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            messageUtil.sendMessage(player, "§c§oDein Inventar ist voll§8.");
            return;
        }

        InventoryUtil.addItem(player, event.getCurrentItem().clone(), 64);
        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.2f, 1);
    }

    private void openBlockSite(final Player player, final int site) {
        final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§7Blöcke Seite§f " + site);

        if (site == 3) {
            for (int i = 0; i <= 3; i++)
                inventory.addItem(new ItemStack(Material.LEAVES, 1, (byte) i));

            for (int i = 0; i <= 1; i++)
                inventory.addItem(new ItemStack(Material.LEAVES_2, 1, (byte) i));

            inventory.addItem(new ItemStack(Material.SNOW_BLOCK));
            inventory.addItem(new ItemStack(Material.GLOWSTONE));
            inventory.addItem(new ItemStack(Material.CLAY));
            inventory.addItem(new ItemStack(Material.ENDER_STONE));
            inventory.addItem(new ItemStack(Material.BRICK_STAIRS));
            inventory.addItem(new ItemStack(Material.BRICK));

            for (int i = 0; i <= 2; i++)
                inventory.addItem(new ItemStack(Material.PRISMARINE, 1, (byte) i));

            inventory.addItem(new ItemStack(Material.SEA_LANTERN));

            inventory.addItem(new ItemStack(Material.STEP, 1, (byte) 0));
            inventory.addItem(new ItemStack(Material.STEP, 1, (byte) 1));

            for (int i = 3; i <= 7; i++)
                inventory.addItem(new ItemStack(Material.STEP, 1, (byte) i));

            for (int i = 0; i <= 5; i++)
                inventory.addItem(new ItemStack(Material.WOOD_STEP, 1, (byte) i));

            inventory.addItem(new ItemStack(Material.SAND));

            for (int i = 0; i <= 2; i++)
                inventory.addItem(new ItemStack(Material.SANDSTONE, 1, (byte) i));

            inventory.addItem(new ItemStack(Material.SANDSTONE_STAIRS, 1, (byte) 1));

            inventory.addItem(new ItemStack(Material.SAND, 1, (byte) 1));

            for (int i = 0; i <= 2; i++)
                inventory.addItem(new ItemStack(Material.RED_SANDSTONE, 1, (byte) i));

            inventory.addItem(new ItemStack(Material.RED_SANDSTONE_STAIRS, 1, (byte) 1));
        }

        if (site == 2) {
            for (int i = 0; i <= 15; i++)
                inventory.addItem(new ItemStack(Material.STAINED_CLAY, 1, (byte) i));

            for (int i = 0; i <= 15; i++)
                inventory.addItem(new ItemStack(Material.WOOL, 1, (byte) i));

            inventory.addItem(new ItemStack(Material.NETHERRACK));
            inventory.addItem(new ItemStack(Material.NETHER_BRICK));
            inventory.addItem(new ItemStack(Material.NETHER_BRICK_STAIRS));

            for (int i = 0; i <= 2; i++)
                inventory.addItem(new ItemStack(Material.QUARTZ_BLOCK, 1, (byte) i));

            inventory.addItem(new ItemStack(Material.QUARTZ_STAIRS));

            for (int i = 0; i <= 3; i++)
                inventory.addItem(new ItemStack(Material.SMOOTH_BRICK, 1, (byte) i));

            inventory.addItem(new ItemStack(Material.SMOOTH_STAIRS));
            inventory.addItem(new ItemStack(Material.COBBLESTONE_STAIRS));
        }

        if (site == 1) {
            inventory.addItem(new ItemStack(Material.GRASS));
            inventory.addItem(new ItemStack(Material.DIRT, 1));
            inventory.addItem(new ItemStack(Material.DIRT, 1, (byte) 1));
            inventory.addItem(new ItemStack(Material.DIRT, 1, (byte) 2));

            for (int i = 0; i <= 6; i++)
                inventory.addItem(new ItemStack(Material.STONE, 1, (byte) i));

            inventory.addItem(new ItemStack(Material.COBBLESTONE));

            for (int i = 0; i <= 5; i++)
                inventory.addItem(new ItemStack(Material.WOOD, 1, (byte) i));

            for (int i = 0; i <= 3; i++)
                inventory.addItem(new ItemStack(Material.LOG, 1, (byte) i));

            for (int i = 0; i <= 1; i++)
                inventory.addItem(new ItemStack(Material.LOG_2, 1, (byte) i));

            inventory.addItem(new ItemStack(Material.ICE));
            inventory.addItem(new ItemStack(Material.PACKED_ICE));

            inventory.addItem(new ItemStack(Material.GLASS, 1));
            for (int i = 0; i <= 15; i++)
                inventory.addItem(new ItemStack(Material.STAINED_GLASS, 1, (byte) i));
        }

        InventoryUtil.fillFromTo(inventory, new ItemBuilder(Material.IRON_FENCE, 1).setDisplayname("§7-§8/§7-").build(), 45, 53);

        if (site > 1)
            inventory.setItem(47, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setDisplayname("§8« §7Vorhärige Seite")
                    .setTextur("YmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ", UUID.randomUUID())
                    .build());

        if (site < 3)
            inventory.setItem(52, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setDisplayname("§8» §7Nächste Seite")
                    .setTextur("MTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19", UUID.randomUUID())
                    .build());

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.2f, 1);

    }
}
