package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       26.06.2023 / 19:38

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.*;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public final class CrownCommand implements CommandExecutor, Listener {

    @NonNull
    private final MessageUtil messageUtil;

    @NonNull
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 0) {
            userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

                if(user.getLong(DataType.CROWNS) == 0) {
                    messageUtil.sendMessage(sender, "Kauf dir §6Crowns§7 in unserem §8/§6§lStore§8.!");
                    return;
                }

                messageUtil.sendMessage(sender, "Du hast §e§o" + messageUtil.formatLong(user.getLong(DataType.CROWNS)) + "§7 Crowns§8.");
            });

            return false;
        }

        if(args.length == 1) {
            if (!messageUtil.hasPlayedBefore(sender, args[0]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> messageUtil.sendMessage(sender, target.getName() + " hat §e§o" + messageUtil.formatLong(user.getLong(DataType.CROWNS)) + "§7 Crowns§8."));

            return false;
        }

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("get")) {
                int amount = 0;
                try {
                    amount = Integer.parseInt(args[1]);

                    player.getInventory().addItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                                    .setDisplayname("§8» §7Gutschein §8(§6§lCrowns§8)")
                                    .setLore("",
                                            "§8▰§7▱  §e§lBetrag",
                                            "§8  -§f§o " + messageUtil.formatLong(amount) + "§6§l¢",
                                            "",
                                            "§8▰§7▱  §e§lRechtsklick",
                                            "§8  -§7 Löst die §6§lCrowns§7 ein§8.",
                                            "")
                                    .setTextur("OTVmZDY3ZDU2ZmZjNTNmYjM2MGExNzg3OWQ5YjUzMzhkNzMzMmQ4ZjEyOTQ5MWE1ZTE3ZThkNmU4YWVhNmMzYSJ9fX0=", UUID.fromString("e692a373-3de2-4087-bbbb-2e0778ab12b2"))
                            .build());
                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an.");
                    return false;

                }
                return false;
            }
        }

        if (args.length == 3) {

            if (!messageUtil.hasPlayedBefore(sender, args[1]))
                return false;

            int amount = 0;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (final NumberFormatException exception) {
                messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an.");
                return false;

            }
            if (amount < 0) {
                messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an.");
                return false;
            }

            int finalAmount = amount;

            if (args[0].equalsIgnoreCase("add")) {

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(user -> {
                    user.addLong(DataType.CROWNS, finalAmount);
                    messageUtil.sendMessage(sender, args[1] + " hat §e§o" + messageUtil.formatLong(finalAmount) + "§7 Crowns bekommen§8.");
                });
                return false;
            }

            if (args[0].equalsIgnoreCase("remove")) {

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(user -> {
                    user.removeLong(DataType.CROWNS, finalAmount);
                    messageUtil.sendMessage(sender, args[1] + " hat §e§o" + messageUtil.formatLong(finalAmount) + "§7 Crowns verloren§8.");
                });

                return false;
            }

            if (args[0].equalsIgnoreCase("set")) {

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(user -> {
                    user.setLong(DataType.CROWNS, finalAmount);
                    messageUtil.sendMessage(sender, args[1] + " hat jetzt §e§o" + messageUtil.formatLong(finalAmount) + "§7 Crowns§8.");
                });

                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/crowns add <spieler> <amount>",
                "/crowns remove <spieler> <amount>",
                "/crowns set <spieler> <amount>",
                "/crowns get <amount>"
        );

        return false;
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        final Player player = event.getPlayer();

        if (!InventoryUtil.isItemInHandWithDisplayname(player, "§8» §7Gutschein §8(§6§lCrowns§8)"))
            return;

        event.setCancelled(true);

        final int multiplier = player.getItemInHand().getAmount();
        final List<String> lore = player.getItemInHand().getItemMeta().getLore();
        final int amount = Integer.parseInt(lore.get(2).split(" ")[3].replace("§6§l¢", "").replace(",", "").replace(".", ""));

        InventoryUtil.removeItemInHand(player, multiplier);

        userHandler.getUserInstant(player.getUniqueId()).addLong(DataType.CROWNS, amount * multiplier);
        messageUtil.sendMessage(player, "Du hast §f§o" + messageUtil.formatLong((long) amount * multiplier) + "§6¢§7 eingelöst§8.");
        player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 0.8f, 0.5f);
    }
}
