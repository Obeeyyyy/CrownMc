package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       20.11.2022 / 15:42

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.BadgeHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.objects.Badge;
import de.obey.crownmc.util.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@NonNull
public final class BadgeCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final BadgeHandler badgeHandler;
    private final ExecutorService executorService;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0) {

            final User user = userHandler.getUserInstant(player.getUniqueId());

            if (user == null) {
                messageUtil.sendMessage(player, "§c§oBitte warte einen Moment ...");
                return false;
            }

            user.getBadges().openBadgeInventory(player);
            return false;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("list")) {
                if (!PermissionUtil.hasPermission(player, "edit.badges", true))
                    return false;

                if (badgeHandler.getBadgeMap().isEmpty()) {
                    messageUtil.sendMessage(player, "Es wurden noch keine Badges erstellt§8.");
                    return false;
                }

                messageUtil.sendMessage(player, "Liste aller Badges§8:");
                badgeHandler.getBadgeMap().values().forEach(badge -> {
                    player.sendMessage("§8 > §7Name§8: §f§o" + badge.getName());
                    player.sendMessage("§8  -> §7Prefix§8: §r" + badge.getPrefix());
                    player.sendMessage("§8  -> §7CreationDate§8: §r" + badge.getCreationDate());
                    player.sendMessage("§8  -> §7Description§8: §r" + badge.getDescription());
                    player.sendMessage("§8  -> §7ShowItem§8: §r" + badge.getShowItem().getType().name());
                });

                return false;
            }

            if (args[0].equalsIgnoreCase("help")) {
                if (!PermissionUtil.hasPermission(player, "edit.badges", true))
                    return false;

                messageUtil.sendSyntax(sender,
                        "/badge list",
                        "/badge help",
                        "/badge resetbadgecount",
                        "/badge create <name>",
                        "/badge delete <name>",
                        "/badge get <name>",
                        "/badge setitem <name>",
                        "/badge setdate <name> <text>",
                        "/badge setprefix <name> <text>",
                        "/badge setdesc <name> <text>",
                        "/badge giveall <name>",
                        "/badge removeall <name>",
                        "/badge givebade <player> <name>",
                        "/badge removebadge <player> <name>"
                );

                return false;
            }

            if (args[0].equalsIgnoreCase("resetbadgecount")) {
                if (!PermissionUtil.hasPermission(player, "edit.badges", true))
                    return false;

                final File folder = new File(CrownMain.getInstance().getDataFolder().getPath() + "/playerFiles");

                if (!folder.exists())
                    return false;

                if (folder.listFiles() == null)
                    return false;

                for (final Badge badge : badgeHandler.getBadgeMap().values()) {
                    badge.setOwned(0);
                }

                executorService.submit(() -> {

                    for (File file : Objects.requireNonNull(folder.listFiles())) {
                        final YamlConfiguration cfg = FileUtil.getCfg(file);

                        /*
                        badges:
                            BETA:
                                receiveddate: 27.12.2022 6:21

                         */

                        if (cfg.contains("badges")) {
                            final Set<String> names = cfg.getConfigurationSection("badges").getKeys(false);

                            if (!names.isEmpty()) {
                                for (String name : names) {
                                    badgeHandler.getBadgeFromName(name).add();
                                }
                            }
                        }
                    }
                });

                messageUtil.sendMessage(player, "Badge count wurde neu gezählt§8.");

                return false;
            }

            if (!messageUtil.hasPlayedBefore(sender, args[0]))
                return false;

            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            userHandler.getUser(target.getUniqueId()).thenAcceptAsync(targetUser -> targetUser.getBadges().openBadgeInventory(player));

            return false;
        }

        if (!PermissionUtil.hasPermission(player, "edit.badges", true))
            return false;

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {

                final String name = args[1];

                if (badgeHandler.getBadgeFromName(name) != null) {
                    messageUtil.sendMessage(player, "§c§oEs existiert bereits eine Badge mit dem Namen§8. ( §f§o" + name + " §8)");
                    return false;
                }

                badgeHandler.createNewBadge(name);
                messageUtil.sendMessage(player, "Du hast die Badge " + name + " erstellt§8.");

                return false;
            }

            if (args[0].equalsIgnoreCase("delete")) {

                final String name = args[1];

                if (badgeHandler.getBadgeFromName(name) == null) {
                    messageUtil.sendMessage(player, "§c§oEs existiert keine Badge mit dem Namen§8. ( §f§o" + name + " §8)");
                    return false;
                }

                badgeHandler.deleteBadge(name);
                messageUtil.sendMessage(player, "Du hast die Badge " + name + " gelöscht§8.");

                return false;
            }

            if (args[0].equalsIgnoreCase("get")) {

                final String name = args[1];

                if (badgeHandler.getBadgeFromName(name) == null) {
                    messageUtil.sendMessage(player, "§c§oEs existiert keine Badge mit dem Namen§8. ( §f§o" + name + " §8)");
                    return false;
                }

                final Badge bage = badgeHandler.getBadgeFromName(name);

                player.getInventory().addItem(new ItemBuilder(Material.NETHER_STAR)
                                .setDisplayname("§f§lBADGE §8(" + bage.getPrefix() + "§8)")
                                .setLore("",
                                        "§f§lRECHTSKLICK",
                                        "§8 - §7Löst diese Badge Permanent ein§8.",
                                        "§7",
                                        "§0" + name)
                        .build());

                return false;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("givebadge")) {

                if (!messageUtil.hasPlayedBefore(sender, args[1]))
                    return false;

                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                final Badge badge = badgeHandler.getBadgeFromName(args[2]);

                if (badge == null) {
                    messageUtil.sendMessage(sender, "Die Badge " + args[2] + " existiert nicht§8.");
                    return false;
                }

                userHandler.getUser(offlinePlayer.getUniqueId()).thenAcceptAsync(user -> {
                    if (user.getBadges().getBadges().containsKey(badge.getName())) {
                        messageUtil.sendMessage(sender, "Der Spieler hat die " + args[2] + " Badge schon§8.");
                        return;
                    }

                    user.getBadges().addBadge(badge.getName());
                    messageUtil.sendMessage(sender, offlinePlayer.getName() + " hat die " + badge.getPrefix() + "§7 Badge erhalten§8.");
                });

                return false;
            }

            if (args[0].equalsIgnoreCase("removebadge")) {

                if (!messageUtil.hasPlayedBefore(sender, args[1]))
                    return false;

                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                final Badge badge = badgeHandler.getBadgeFromName(args[2]);

                if (badge == null) {
                    messageUtil.sendMessage(sender, "Die Badge " + args[2] + " existiert nicht§8.");
                    return false;
                }

                userHandler.getUser(offlinePlayer.getUniqueId()).thenAcceptAsync(user -> {
                    if (!user.getBadges().getBadges().containsKey(badge.getName())) {
                        messageUtil.sendMessage(sender, "Der Spieler hat die " + args[2] + " Badge nicht§8.");
                        return;
                    }

                    user.getBadges().removeBadge(badge.getName());
                    messageUtil.sendMessage(sender, "Die " + badge.getPrefix() + "§7 Badge wurde " + offlinePlayer.getName() + " entzogen§8.");
                });

                return false;
            }
        }

        final Badge badge = badgeHandler.getBadgeFromName(args[1]);

        if (badge == null) {
            messageUtil.sendMessage(player, "Die Badge " + args[1] + " existiert nicht§8.");
            return false;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setitem")) {

                if (!InventoryUtil.hasItemInHand(player)) {
                    messageUtil.sendMessage(player, "Du musst ein Item in der Hand halten§8.");
                    return false;
                }

                badge.setShowItem(player.getItemInHand());
                messageUtil.sendMessage(player, "Du hast das ShowItem für die Badge " + badge.getName() + " geupdated§8.");

                return false;
            }

            if (args[0].equalsIgnoreCase("giveall")) {

                Bukkit.getOnlinePlayers().forEach(online -> userHandler.getUserInstant(online.getUniqueId()).getBadges().addBadge(badge.getName()));
                messageUtil.broadcast("Alle Spieler haben " + badge.getPrefix() + "§7 Badge erhalten§.");

                return false;
            }

            if (args[0].equalsIgnoreCase("removeall")) {

                final File folder = new File(CrownMain.getInstance().getDataFolder().getPath() + "/playerFiles");

                if (!folder.exists())
                    return false;

                if (folder.listFiles() == null)
                    return false;

                executorService.submit(() -> {

                    for (User user : userHandler.getUserCache().values()) {
                        user.getBadges().removeBadge(badge.getName());
                    }
                    
                    for (File file : Objects.requireNonNull(folder.listFiles())) {
                        final YamlConfiguration cfg = FileUtil.getCfg(file);

                        if (cfg.contains("badges." + badge.getName())) {
                            cfg.set("badges." + badge.getName(), null);
                        }
                    }
                });

                messageUtil.sendMessage(player, "Jetzt hat keiner mehr die " + badge.getName() + " Badge§8.");

                return false;
            }
        }

        if (args.length >= 3) {
            String text = args[2];

            for (int i = 3; i < args.length; i++) {
                text = text + " " + args[i];
            }

            if (args[0].equalsIgnoreCase("setprefix")) {
                badge.setPrefix(ChatColor.translateAlternateColorCodes('&', text));
                messageUtil.sendMessage(player, "Du hast den Prefix der Badge " + badge.getName() + " geupdatet§8.");
                messageUtil.sendMessage(player, badge.getPrefix());

                return false;
            }

            if (args[0].equalsIgnoreCase("setdate")) {
                badge.setCreationDate(ChatColor.translateAlternateColorCodes('&', text));
                messageUtil.sendMessage(player, "Du hast das Date der Badge " + badge.getName() + " geupdatet§8.");
                messageUtil.sendMessage(player, badge.getCreationDate());

                return false;
            }

            if (args[0].equalsIgnoreCase("setdesc")) {
                badge.setDescription(text);
                messageUtil.sendMessage(player, "Du hast die Beschreibung der Badge " + badge.getName() + " geupdatet§8.");
                messageUtil.sendMessage(player, badge.getDescription());
                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/badge list",
                "/badge help",
                "/badge resetbadgecount",
                "/badge create <name>",
                "/badge delete <name>",
                "/badge setitem <name>",
                "/badge setdate <name> <text>",
                "/badge setprefix <name> <text>",
                "/badge setdesc <name> <text>",
                "/badge giveall <name>",
                "/badge givebade <player> <name>",
                "/badge removebadge <player> <name>"
        );

        return false;
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        if (!InventoryUtil.isItemInHandStartsWith(player, "§f§lBADGE"))
            return;

        event.setCancelled(true);

        final String name = ChatColor.stripColor(player.getItemInHand().getItemMeta().getLore().get(4));
        final Badge badge = badgeHandler.getBadgeFromName(name);

        if (badge == null) {
            messageUtil.sendMessage(player, "Die Badge " + name + " existiert nicht§8.");
            return;
        }

        userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
            if (user.getBadges().getBadges().containsKey(badge.getName())) {
                messageUtil.sendMessage(player, "Du hast die " + name + " Badge schon§8.");
                return;
            }

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            user.getBadges().addBadge(badge.getName());
            InventoryUtil.removeItemInHand(player, 1);
        });

    }

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if (!InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§f§lBADGES§7 "))
            return;

        event.setCancelled(true);

        if (!event.isLeftClick())
            return;

        if (!InventoryUtil.startsWithInventoryTitle(event.getClickedInventory(), "§f§lBADGES§7 "))
            return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR ||
                !event.getCurrentItem().hasItemMeta())
            return;

        if (event.getCurrentItem().getType() == Material.BARRIER)
            return;

        final Player player = (Player) event.getWhoClicked();

        if (!event.getClickedInventory().getName().split(" ")[1].equalsIgnoreCase(player.getName()))
            return;

        final Badge badge = badgeHandler.getBadgeFromPrefix(event.getCurrentItem().getItemMeta().getDisplayName());

        if (badge == null)
            return;

        if (cooldowns.containsKey(player.getUniqueId())) {
            if (System.currentTimeMillis() < cooldowns.get(player.getUniqueId())) {
                messageUtil.sendMessage(player, "Bitte warte noch " + MathUtil.getMinutesAndSecondsFromSeconds((cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000) + "§8.");
                return;
            }
        }

        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (1000 * 10));

        new MessageBuilder("§f§lBADGES§8 × §7" + player.getName() + " zeigt§8: " + badge.getPrefix())
                .addHover(badge.getPrefix() + "§8 ( §7" + player.getName() + " §8)\n\n"
                        + "§7Beschreibung§8: §f§o" + badge.getDescription() + "\n" +
                        "\n" +
                        "§7Freigeschaltet am§8: §f§o" + userHandler.getUserInstant(player.getUniqueId()).getBadges().getBadges().get(badge.getName()).getReceivedDate() +
                        "\n"
                ).broadcast();
    }
}
