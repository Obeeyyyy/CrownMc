package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       20.11.2022 / 10:46

*/

import de.obey.slayer.util.InventoryUtil;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@NonNull
public final class LoreCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "editlore", true))
            return false;

        if (args.length > 0) {

            if (!InventoryUtil.hasItemInHand(player))
                return false;

            if (args[0].equalsIgnoreCase("add")) {
                if (args.length >= 2) {

                    final ItemStack item = player.getItemInHand();
                    final ItemMeta meta = item.getItemMeta();
                    final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

                    String text = args[1];

                    if (args.length > 2) {
                        for (int i = 2; i < args.length; i++) {
                            text = text + " " + args[i];
                        }
                    }

                    lore.add(ChatColor.translateAlternateColorCodes('&', text));
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    player.setItemInHand(item);
                    player.updateInventory();

                    messageUtil.sendMessage(player, "Du hast der Lore eine Line hinzugefügt§8.");

                    return false;
                }
            }

            if (args[0].equalsIgnoreCase("set")) {
                if (args.length >= 3) {
                    try {
                        final ItemStack item = player.getItemInHand();

                        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
                            messageUtil.sendMessage(player, "Das Item hat keine Lore§8.");
                            return false;
                        }

                        final int line = Integer.parseInt(args[1]) - 1;
                        final ItemMeta meta = item.getItemMeta();
                        final List<String> lore = meta.getLore();

                        if (lore.size() < line + 1) {
                            messageUtil.sendMessage(player, "Das Item hat nur " + lore.size() + " lines§8.");
                            return false;
                        }

                        String text = args[2];

                        if (args.length > 3) {
                            for (int i = 3; i < args.length; i++) {
                                text = text + " " + args[i];
                            }
                        }

                        lore.set(line, ChatColor.translateAlternateColorCodes('&', text));
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        player.setItemInHand(item);
                        player.updateInventory();

                        messageUtil.sendMessage(player, "Du hast die Line " + (line + 1) + " bearbeitet§8.");

                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(player, "Bitte gebe eine Zahl als line an§8.");
                    }
                    return false;
                }
            }

            if (args[0].equalsIgnoreCase("remove")) {
                if (args.length == 2) {
                    try {
                        final ItemStack item = player.getItemInHand();

                        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
                            messageUtil.sendMessage(player, "Das Item hat keine Lore§8.");
                            return false;
                        }

                        final int line = Integer.parseInt(args[1]) - 1;

                        final List<String> lore = item.getItemMeta().getLore();

                        if (lore.size() < line + 1) {
                            messageUtil.sendMessage(player, "Das Item hat nur " + lore.size() + " lines§8.");
                            return false;
                        }

                        final ItemMeta meta = item.getItemMeta();

                        lore.remove(line);
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        player.setItemInHand(item);
                        player.updateInventory();

                        messageUtil.sendMessage(player, "Du hast die Line " + (line + 1) + " aus der Lore entfernt§8.");

                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(player, "Bitte gebe eine Zahl als line an§8.");
                    }
                    return false;
                }
            }
        }

        messageUtil.sendSyntax(player, "/lore add <text>",
                "/lore remove <1, 2 = line >",
                "/lore set <1, 2 = line> <text>");

        return false;
    }
}
