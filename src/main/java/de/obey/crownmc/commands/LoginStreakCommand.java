package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       28.10.2022 / 20:49

*/

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.InventoryUtil;
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@RequiredArgsConstructor
@NonNull
public final class LoginStreakCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final ServerConfig serverConfig;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0) {
            userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                user.getLoginStreak().updateInventory();
                player.openInventory(user.getLoginStreak().getInventory());
            });
            return false;
        }

        if (!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setreward")) {

                try {
                    final int tag = Integer.parseInt(args[1]);

                    userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

                        if(!user.getLoginStreak().isRewardSlot(tag - 1)) {
                            messageUtil.sendMessage(player, tag + " ist kein Reward Tag§8.");
                            return;
                        }

                        final Inventory inventory = Bukkit.createInventory(null, 9 * 5, "LoginReward " + tag);
                        final YamlConfiguration cfg = serverConfig.getCfg();

                        final ArrayList<ItemStack> contents = cfg.contains("loginreward." + tag) ? (ArrayList<ItemStack>) cfg.getList("loginreward." + tag) : new ArrayList<>();

                        if (contents.size() > 0)
                            contents.forEach(inventory::addItem);

                        player.openInventory(inventory);
                        player.updateInventory();

                    });

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl ein§8.");
                }
                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/loginstreak setreward <tag>");

        return false;
    }

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if (!InventoryUtil.startsWithInventoryTitle(event.getInventory(), "LoginReward "))
            return;

        final YamlConfiguration cfg = serverConfig.getCfg();
        final int tag = Integer.parseInt(event.getInventory().getName().split(" ")[1]);
        final ArrayList<ItemStack> items = new ArrayList<>();

        for (ItemStack content : event.getInventory().getContents()) {
            if (content != null && content.getType() != Material.AIR)
                items.add(content);
        }

        cfg.set("loginreward." + tag, items);

        FileUtil.saveToFile(serverConfig.getConfigFile(), cfg);
        messageUtil.sendMessage(event.getPlayer(), "LoginReward für Tag " + tag + " gespeichert.");
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {

        if (InventoryUtil.isInventoryTitle(event.getInventory(), "§d§lLoginStreak")) {


            event.setCancelled(true);

            if (!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§d§lLoginStreak"))
                return;

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
                return;

            final Player player = (Player) event.getWhoClicked();

            if (event.getCurrentItem().getType() == Material.POWERED_MINECART) {

                if (event.isLeftClick()) {
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.1f, 1);
                    messageUtil.sendMessage(player, "Du kannst diese Belohnung noch nicht einlösen§8.");
                    return;
                }

                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                    final int tag = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[2]);
                    user.getLoginStreak().openPreview(player, tag);
                });
                return;
            }

            if (event.getCurrentItem().getType() == Material.HOPPER_MINECART) {

                if (event.isLeftClick()) {
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.1f, 1);
                    messageUtil.sendMessage(player, "Du hast diese Belohnung bereits eingelöst§8.");
                    return;
                }

                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                    final int tag = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[2]);
                    user.getLoginStreak().openPreview(player, tag);
                });

                return;
            }

            if (event.getCurrentItem().getType() == Material.STORAGE_MINECART) {
                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                    final int tag = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[2]);

                    if (event.isLeftClick()) {
                        user.getLoginStreak().claimReward(tag);

                        player.playSound(player.getLocation(), Sound.FIREWORK_TWINKLE, 0.6f, 1);
                        messageUtil.sendMessage(player, "Du hast die Belohnung für Tag §e§o" + tag + "§7 erhalten§8.");

                        return;
                    }

                    user.getLoginStreak().openPreview(player, tag);
                });
            }

            return;
        }

        if (InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§7Preview Tag ")) {
            event.setCancelled(true);

            final Player player = (Player) event.getWhoClicked();

            if (event.getSlot() == 53)
                userHandler.getUser(event.getWhoClicked().getUniqueId()).thenAccept(user -> {

                    player.openInventory(user.getLoginStreak().getInventory());
                    player.updateInventory();

                });
        }

    }
}
