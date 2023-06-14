package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       26.10.2022 / 17:57

*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@NonNull
public final class GiveAllCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "giveall", true))
            return false;

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("hand")) {

                final ItemStack item = player.getItemInHand();

                if (item == null || item.getType() == Material.AIR) {
                    messageUtil.sendMessage(sender, "Du musst ein Item in der Hand halten §8.");
                    return false;
                }

                Bukkit.getOnlinePlayers().forEach(online -> {
                    if (!online.getName().equalsIgnoreCase(player.getName())) {
                        InventoryUtil.addItem(online, item.clone());
                    }
                });

                String displayname = player.getItemInHand().getType().toString();

                if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName())
                    displayname = player.getItemInHand().getItemMeta().getDisplayName();

                messageUtil.broadcast("Alle Spieler haben §f§ox" + player.getItemInHand().getAmount() + " §8'§e§o" + displayname + "§8'§7 bekommen§8.");

                return false;
            }

            if (args[0].equalsIgnoreCase("inv")) {

                final ItemStack[] contents = player.getInventory().getContents();

                Bukkit.getOnlinePlayers().forEach(online -> {
                    if (!online.getName().equalsIgnoreCase(player.getName())) {
                        for (ItemStack content : contents) {
                            if (content != null && content.getType() != Material.AIR) {
                                InventoryUtil.addItem(online, content);
                            }
                        }
                    }
                });

                messageUtil.broadcast("Alle Spieler haben das §e§oInventar§7 von " + player.getName() + " bekommen§8.");

                return false;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("money")) {

                try {
                    final long amount = Long.parseLong(args[1]);

                    Bukkit.getOnlinePlayers().forEach(online -> {
                        userHandler.getUserInstant(online.getUniqueId()).addLong(DataType.MONEY, amount);
                    });

                    messageUtil.broadcast("Alle Spieler haben §e§o" + messageUtil.formatLong(amount) + "§6§l$§7 bekommen§8.");

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/giveall <inv, hand>", "/giveall <money> <amount>");

        return false;
    }
}
