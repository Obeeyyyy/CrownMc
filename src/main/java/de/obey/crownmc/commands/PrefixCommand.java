package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       06.11.2022 / 17:43

*/

import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.backend.user.UserPrefix;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class PrefixCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length > 0 && PermissionUtil.hasPermission(player, "team", false)) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("list")) {
                    if (!PermissionUtil.hasPermission(player, "prefix.list", true))
                        return false;

                    if (!messageUtil.hasPlayedBefore(player, args[1]))
                        return false;

                    final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                    userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                        if (user.getPrefix().getPrefixList().isEmpty()) {
                            messageUtil.sendMessage(player, target.getName() + " hat keine Prefixe§8.");
                            return;
                        }

                        messageUtil.sendMessage(player, target.getName() + " aktiver Prefix§8:§r " + user.getPrefix().getActivePrefix());
                        messageUtil.sendMessage(player, "Alle Prefixe§8:");

                        for (int i = 0; i < user.getPrefix().getPrefixList().size(); i++) {
                            player.sendMessage("§8 - (§f" + (i + 1) + "§8) > §r" + ChatColor.translateAlternateColorCodes('&', user.getPrefix().getPrefixList().get(i)));
                        }
                    });

                    return false;
                }
            }

            if (args.length >= 3) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (!PermissionUtil.hasPermission(player, "prefix.add", true))
                        return false;

                    if (!messageUtil.hasPlayedBefore(player, args[1]))
                        return false;

                    final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                    String pre = args[2];

                    if (args.length > 3) {
                        for (int i = 3; i < args.length; i++)
                            pre = pre + " " + args[i];

                    }

                    final String addPrefix = ChatColor.translateAlternateColorCodes('&', pre);

                    userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                        final UserPrefix userPrefix = user.getPrefix();

                        if (userPrefix.getPrefixList().contains(addPrefix)) {
                            messageUtil.sendMessage(player, "Der Spieler " + target.getName() + " hat diesen Prefix bereits§8.");
                            return;
                        }

                        userPrefix.addPrefix(addPrefix);
                        messageUtil.sendMessage(player, target.getName() + " hat den " + addPrefix + "§7 erhalten§8.");
                    });
                    return false;
                }
            }

            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("remove")) {
                    if (!PermissionUtil.hasPermission(player, "prefix.remove", true))
                        return false;

                    if (!messageUtil.hasPlayedBefore(player, args[1]))
                        return false;

                    try {
                        final int id = Integer.parseInt(args[2]);

                        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                        userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                            final UserPrefix userPrefix = user.getPrefix();

                            if (userPrefix.getPrefixList().size() < id) {
                                messageUtil.sendMessage(player, "Diese ID ist ungültig§8.");
                                return;
                            }

                            final String targetPrefix = userPrefix.getPrefixList().get(id - 1);

                            if (userPrefix.getActivePrefix().equalsIgnoreCase(targetPrefix))
                                userPrefix.setActivePrefix("");

                            userPrefix.getPrefixList().remove(id - 1);

                            messageUtil.sendMessage(player, "Der Prefix " + targetPrefix + "§7 wurde von " + target.getName() + " entfernt§8.");
                        });

                        return false;
                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an§8.");
                        return false;
                    }
                }
            }

            if (args.length > 1) {
                if (args[0].equalsIgnoreCase("get")) {

                    if (!PermissionUtil.hasPermission(player, "createprefix", true))
                        return false;

                    String prefix = args[1];

                    if (args.length > 2) {
                        for (int i = 2; i < args.length; i++) {
                            prefix = prefix + " " + args[i];
                        }
                    }

                    InventoryUtil.addItem(player, new ItemBuilder(Material.PAPER)
                            .setDisplayname("§8» §f§lPrefix Gutschein")
                            .setLore("",
                                    "§8▰§7▱ §f§lInformation",
                                    "  §8- §7Prefix§8: " + ChatColor.translateAlternateColorCodes('&', prefix),
                                    "  §8- §7Rechtsklick um den Prefix freizuschalten§8.",
                                    "")
                            .build());

                    return false;
                }
            }

            messageUtil.sendSyntax(player, "/prefix get <prefix>", "/prefix list <spieler>", "/prefix add <spieler> <prefix>", "/prefix remove <spieler> <id>");

            return false;
        }

        final User user = userHandler.getUserInstant(player.getUniqueId());

        user.getPrefix().openPrefixInventory(player);

        return false;
    }
}
