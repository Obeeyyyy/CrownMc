package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       15.01.2023 / 00:02

*/

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import de.obey.slayer.backend.user.User;
import de.obey.slayer.backend.user.UserPlot;
import de.obey.slayer.handler.UserHandler;
import de.obey.slayer.util.*;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor @NonNull
public final class WandCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final PlotAPI plotAPI;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0) {
            userHandler.getUserInstant(player.getUniqueId()).getPlot().openWandInventory(player);
            return false;
        }

        if (args.length == 1) {

            if (!PermissionUtil.hasPermission(player, "plot.wand", true))
                return false;

            if (args[0].equalsIgnoreCase("help")) {
                messageUtil.sendSyntax(sender, "/wand get (block in der hand)",
                        "/wand set <material>",
                        "/wand list <spieler>",
                        "/wand add <spieler> <material>",
                        "/wand remove <spieler> <id>"
                );
                return false;
            }

            if (args[0].equalsIgnoreCase("get")) {

                if (!InventoryUtil.hasItemInHand(player))
                    return false;

                InventoryUtil.addItem(player, new ItemBuilder(player.getItemInHand().getType(), 1, player.getItemInHand().getData().getData())
                        .setDisplayname("§8» §b§lPlotwand")
                        .setLore("",
                                "§8▰§7▱ §bRechtsklick",
                                "§8 - §7Erhalte den Block als Plotwand§8.",
                                "")
                        .build());

                player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);

                return false;
            }

            return false;
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("set")) {
                final Plot standingOn = plotAPI.getPlot(player);

                if (standingOn == null) {
                    messageUtil.sendMessage(sender, "Du stehst auf keinem Plot§8.");
                    return false;
                }

                for (Plot plot : standingOn.getConnectedPlots())
                    plot.setComponent("wall", args[1]);

                messageUtil.sendMessage(sender, "Du hast die Plotwand geändert§8.");
                return false;
            }

            if(args[0].equalsIgnoreCase("list")) {

                if(!messageUtil.hasPlayedBefore(sender, args[1]))
                    return false;

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                    final UserPlot plot = user.getPlot();

                    if(plot.getWand().isEmpty()) {
                        messageUtil.sendMessage(sender, target.getName() + " hat keine Wände freigeschaltet§8.");
                        return;
                    }

                    messageUtil.sendMessage(sender, "Wände von§8: §f" + target.getName());
                    int i = 1;
                    for (String s : plot.getWand()) {
                        player.sendMessage("§8- (§f" + i + "§8) §e" + s);
                        i++;
                    }
                });

                return false;
            }
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("add")) {
                if(!messageUtil.hasPlayedBefore(sender, args[1]))
                    return false;

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                final String value = args[2];

                ItemStack item = new ItemStack(Material.AIR);

                if(value.contains(":")) {
                    final String[] matString = value.split(":");

                    try {
                        item.setType(Material.getMaterial(Integer.parseInt(matString[0])));
                    } catch ( final NumberFormatException exception) {
                        try {
                            item.setType(Material.getMaterial(matString[0].toUpperCase()));
                        } catch (final IllegalArgumentException exception1) {
                            messageUtil.sendMessage(sender, "Bitte gebe ein gültiges Material an§8.");
                            return false;
                        }
                    }

                    try {
                        item.setDurability(((byte) Integer.parseInt(matString[1])));
                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(sender, "Bitte gebe ein gültiges Material an§8.");
                        return false;
                    }
                } else {
                    try {
                        item.setType(Material.getMaterial(Integer.parseInt(args[2])));
                    } catch ( final NumberFormatException exception) {
                        try {
                            item.setType(Material.getMaterial(args[2].toUpperCase()));
                        } catch (final IllegalArgumentException exception1) {
                            messageUtil.sendMessage(sender, "Bitte gebe ein gültiges Material an§8.");
                            return false;
                        }
                    }
                }

                userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                    final UserPlot plot = user.getPlot();

                    if(!plot.addPlotwand(item.clone())) {
                        messageUtil.sendMessage(player, "Der Spieler §e§o" + target.getName() + "§7 hat §e§o" + item.getType().name() + "§7 bereits als Wand§8:");

                        if(target.isOnline())
                            messageUtil.sendMessage(target.getPlayer(), "Du hast §e" + item.getType().name() + "§7 als Plotwand erhalten§8.");
                    } else {
                        messageUtil.sendMessage(player, target.getName() + " hat " + item.getType().name() + " als Wand bekommen§8.");
                    }
                });

                return false;
            }

            if(args[0].equalsIgnoreCase("remove")) {
                if(!messageUtil.hasPlayedBefore(sender, args[1]))
                    return false;

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                try {
                    final int id = Integer.parseInt(args[2]);

                    userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
                        final UserPlot plot = user.getPlot();

                        if (plot.getWand().size() < id) {
                            messageUtil.sendMessage(player, "Diese ID ist ungültig§8.");
                            return;
                        }

                        messageUtil.sendMessage(player, "Die Wand §e" + plot.getWand().get(id - 1) + "§7 wurde von §e" + target.getName() + "§c entfernt§8.");
                        plot.getWand().remove(id - 1);
                    });

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }
        }

        return false;
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        final Player player = event.getPlayer();

        if (!InventoryUtil.hasItemInHand(player, false))
            return;

        final ItemStack item = player.getItemInHand();

        if (InventoryUtil.isItemInHandWithDisplayname(player, "§8» §b§lPlotwand")) {
            event.setCancelled(true);

            if (!userHandler.getUserInstant(player.getUniqueId()).getPlot().addPlotwand(item)) {
                messageUtil.sendMessage(player, "Du hast diese Plotwand schon freigeschaltet§8.");
                player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                return;
            } else {
                InventoryUtil.removeItemInHand(player, 1);
                messageUtil.sendMessage(player, "Du hast eine neue Plotwand freigeschaltet§8.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);
            }
        }
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if (!InventoryUtil.isInventoryTitle(event.getInventory(), "§7Plot Wand"))
            return;

        event.setCancelled(true);

        if (!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§7Plot Wand"))
            return;

        final ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR || item.getType() == Material.IRON_FENCE)
            return;

        final Player player = (Player) event.getWhoClicked();

        final User user = userHandler.getUserInstant(player.getUniqueId());

        if (!user.getCooldowns().isReady("plotwand")) {
            messageUtil.sendMessage(player, "Du musst noch " + MathUtil.getMinutesAndSecondsFromSeconds((user.getCooldowns().getRemainingMillis("plotwand")) / 1000) + "§7 warten§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
            return;
        }

        final Plot standingOn = plotAPI.getPlot(player);

        if (standingOn == null) {
            messageUtil.sendMessage(player, "Du stehst auf keinem Plot§8.");
            return;
        }

        if (!standingOn.isOwner(player.getUniqueId())) {
            messageUtil.sendMessage(player, "§c§oDu bist nicht der Besitzer dieses Plots§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
            return;
        }

        for (Plot plot : standingOn.getConnectedPlots()) {
            if(item.getType() == Material.BARRIER) {
                plot.setComponent("wall", "0:0");
            } else {
                plot.setComponent("wall", item.getType().getId() + ":" + item.getData().getData());
            }
        }

        user.getCooldowns().setCooldown("plotwand", System.currentTimeMillis() + 1000 * 60 * 30);

        player.playSound(player.getLocation(), Sound.BLAZE_DEATH, 0.5f, 1);
        messageUtil.sendMessage(player, "Du hast eine Plotwand gesetzt§8.");
    }
}
