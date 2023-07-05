package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 23:54

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class EssentialCommands implements CommandExecutor {

    public final static Map<Player, Player> tpas = new HashMap<>();
    public final static Map<Player, Player> tpaHeres = new HashMap<>();

    @NonNull
    private final Initializer initializer;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("trash")) {

            player.openInventory(Bukkit.createInventory(null, 9 * 4, "§a§oMülleimer"));

            return false;
        }

        if (command.getName().equalsIgnoreCase("speed")) {

            if (!PermissionUtil.hasPermission(player, "speed", true))
                return false;

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("reset")) {
                    player.setWalkSpeed(0.2f);
                    player.setFlySpeed(0.1f);
                    initializer.getMessageUtil().sendMessage(player, "Deine Geschwindigkeit wurde angepasst§8.");
                    return false;
                }

                try {
                    final float level = (float) (Integer.parseInt(args[0])) / 10;

                    if (level > -0.1 && level < 1.1) {
                        player.setWalkSpeed(getRealMoveSpeed(level, false));
                        player.setFlySpeed(getRealMoveSpeed(level, false));
                        initializer.getMessageUtil().sendMessage(player, "Deine Geschwindigkeit wurde angepasst§8.");
                        return false;
                    }

                } catch (NumberFormatException ignored) {
                }

                initializer.getMessageUtil().sendMessage(player, "Bitte gebe eine Zahl von 1-10 an§8.");

                return false;
            }

            if (args.length == 2) {

                if (!PermissionUtil.hasPermission(player, "speed.others", true))
                    return false;

                if (!initializer.getMessageUtil().isOnline(sender, args[1]))
                    return false;

                final Player target = Bukkit.getPlayer(args[1]);

                if (args[0].equalsIgnoreCase("reset")) {
                    target.setWalkSpeed(0.2f);
                    target.setFlySpeed(0.2f);
                    initializer.getMessageUtil().sendMessage(player, "Deine Geschwindigkeit wurde angepasst§8.");
                    return false;
                }

                try {
                    final int level = Integer.parseInt(args[0]) / 10;

                    if (level > -0.1 && level < 1.1) {
                        target.setWalkSpeed(level);
                        target.setFlySpeed(level);
                        initializer.getMessageUtil().sendMessage(player, target.getName() + "'s Geschwindigkeit wurde angepasst§8.");
                        initializer.getMessageUtil().sendMessage(target, "Deine Geschwindigkeit wurde angepasst§8.");
                        return false;
                    }

                } catch (NumberFormatException ignored) {
                }

                initializer.getMessageUtil().sendMessage(player, "Bitte gebe eine Zahl von 1-10 an.");

                return false;
            }

            initializer.getMessageUtil().sendSyntax(player, "/speed <1-10>", "/speed <1-10> <Spieler>", "/speed reset");

            return false;
        }

        if (command.getName().equalsIgnoreCase("more")) {

            if (!PermissionUtil.hasPermission(player, "*", true))
                return false;

            if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                initializer.getMessageUtil().sendMessage(player, "Du musst ein Item in der Hand halten§8.");
                return false;
            }

            player.getItemInHand().setAmount(64);

            return false;
        }

        if (command.getName().equalsIgnoreCase("day")) {

            if (!PermissionUtil.hasPermission(player, "day", true))
                return false;

            for (World world : Bukkit.getWorlds()) {
                world.setTime(1000);
            }

            initializer.getMessageUtil().sendMessage(player, "Zeit wurde auf Tag (1.000) gesetzt§8.");

            return false;
        }

        if (command.getName().equalsIgnoreCase("night")) {

            if (!PermissionUtil.hasPermission(player, "night", true))
                return false;

            for (World world : Bukkit.getWorlds()) {
                world.setTime(13000);
            }

            initializer.getMessageUtil().sendMessage(player, "Zeit wurde auf Nacht (13.000) gesetzt§8.");

            return false;
        }

        if (command.getName().equalsIgnoreCase("time")) {

            if (!PermissionUtil.hasPermission(player, "time", true))
                return false;

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("day")) {
                    Bukkit.dispatchCommand(player, "day");
                    return false;
                }

                if (args[0].equalsIgnoreCase("night")) {
                    Bukkit.dispatchCommand(player, "night");
                    return false;
                }

                try {
                    final long millis = Long.parseLong(args[0]);

                    player.getWorld().setTime(millis);

                    initializer.getMessageUtil().sendMessage(player, "Zeit wurde auf (" + initializer.getMessageUtil().formatLong(millis) + ") gesetzt§8.");

                } catch (final NumberFormatException exception) {
                    initializer.getMessageUtil().sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }
                return false;
            }

            initializer.getMessageUtil().sendSyntax(player, "/time <millis>");

            return false;
        }

        if (command.getName().equalsIgnoreCase("clear")) {
            if (!PermissionUtil.hasPermission(player, "clear", true))
                return false;

            if (args.length == 0) {
                player.getInventory().clear();
                player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);

                initializer.getMessageUtil().sendMessage(player, "Du hast dein Inventar geleert§8.");
                return false;
            }

            if (args.length == 1) {

                if (!PermissionUtil.hasPermission(player, "clear.others", true))
                    return false;

                if (!initializer.getMessageUtil().isOnline(sender, args[0]))
                    return false;

                final Player target = Bukkit.getPlayer(args[0]);

                target.getInventory().clear();
                target.getInventory().setHelmet(null);
                target.getInventory().setChestplate(null);
                target.getInventory().setLeggings(null);
                target.getInventory().setBoots(null);

                initializer.getMessageUtil().sendMessage(player, "Du hast das Inventar von " + target.getName() + " geleert§8.");
                initializer.getMessageUtil().sendMessage(target, "Dein Inventar wurde von " + player.getName() + " geleert§8.");

                return false;
            }

            initializer.getMessageUtil().sendMessage(player, "/clear <spieler>");

            return false;
        }

        if (command.getName().equalsIgnoreCase("feed")) {

            if (!PermissionUtil.hasPermission(player, "feed", true))
                return false;

            if (args.length == 0) {
                player.setFoodLevel(20);
                player.setSaturation(20);
                player.playSound(player.getLocation(), Sound.BURP, 1, 1);

                initializer.getMessageUtil().sendMessage(sender, "Dein Hunger wurde gestillt§8.");
                return false;
            }

            if (!PermissionUtil.hasPermission(player, "feed.others", true))
                return false;

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("*")) {
                    Bukkit.getOnlinePlayers().forEach(online -> {
                        online.setFoodLevel(20);
                        online.setSaturation(20);
                        online.playSound(online.getLocation(), Sound.BURP, 1, 1);
                    });
                    initializer.getMessageUtil().sendMessage(sender, "Alle Spieler wurden gefüttert§8.");
                    return false;
                }

                if (!initializer.getMessageUtil().isOnline(sender, args[0]))
                    return false;

                final Player target = Bukkit.getPlayer(args[0]);

                target.setFoodLevel(20);
                target.setSaturation(20);
                target.playSound(target.getLocation(), Sound.BURP, 1, 1);

                initializer.getMessageUtil().sendMessage(target, "Dein Hunger wurde gestillt§8.");
                initializer.getMessageUtil().sendMessage(sender, target.getName() + "'s Hunger wurde gestillt§8.");

            }

            return false;
        }

        if (command.getName().equalsIgnoreCase("heal")) {

            if (!PermissionUtil.hasPermission(player, "heal", true))
                return false;

            if (args.length == 0) {
                player.setFoodLevel(20);
                player.setHealth(20);
                player.setFireTicks(0);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

                initializer.getMessageUtil().sendMessage(sender, "Du wurdest geheilt§8.");
                return false;
            }

            if (!PermissionUtil.hasPermission(player, "heal.others", true))
                return false;

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("*")) {
                    Bukkit.getOnlinePlayers().forEach(online -> {
                        online.setFoodLevel(20);
                        online.setHealth(20);
                        online.setFireTicks(0);
                        online.playSound(online.getLocation(), Sound.BURP, 1, 1);
                    });
                    initializer.getMessageUtil().sendMessage(sender, "Alle Spieler wurden gehealt§8.");
                    return false;
                }

                if (!initializer.getMessageUtil().isOnline(sender, args[0]))
                    return false;

                final Player target = Bukkit.getPlayer(args[0]);

                target.setFoodLevel(20);
                target.setHealth(20);
                target.setFireTicks(0);
                target.playSound(target.getLocation(), Sound.LEVEL_UP, 1, 1);

                initializer.getMessageUtil().sendMessage(target, "Du wurdest geheilt.");
                initializer.getMessageUtil().sendMessage(sender, target.getName() + " wurde geheilt§8.");
            }
            return false;
        }

        if (command.getName().equalsIgnoreCase("gamemode")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("0")) {

                    if (!PermissionUtil.hasPermission(player, "gamemode.0", true))
                        return false;

                    player.setGameMode(GameMode.SURVIVAL);
                    initializer.getMessageUtil().sendMessage(sender, "Dein Spielmodus wurde auf Survival gesetzt§8.");
                    return false;
                }

                if (args[0].equalsIgnoreCase("1")) {

                    if (!PermissionUtil.hasPermission(player, "gamemode.1", true))
                        return false;

                    player.setGameMode(GameMode.CREATIVE);
                    initializer.getMessageUtil().sendMessage(sender, "Dein Spielmodus wurde auf Creative gesetzt§8.");
                    return false;
                }

                if (args[0].equalsIgnoreCase("2")) {

                    if (!PermissionUtil.hasPermission(player, "gamemode.2", true))
                        return false;

                    player.setGameMode(GameMode.ADVENTURE);
                    initializer.getMessageUtil().sendMessage(sender, "Dein Spielmodus wurde auf Adventure gesetzt§8.");
                    return false;
                }

                if (args[0].equalsIgnoreCase("3")) {

                    if (!PermissionUtil.hasPermission(player, "gamemode.3", true))
                        return false;

                    player.setGameMode(GameMode.SPECTATOR);
                    initializer.getMessageUtil().sendMessage(sender, "Dein Spielmodus wurde auf Spectator gesetzt§8.");
                    return false;
                }
            }

            if (!PermissionUtil.hasPermission(player, "gamemode.others", true))
                return false;

            if (args.length == 2) {

                if (!initializer.getMessageUtil().isOnline(sender, args[1]))
                    return false;

                final Player target = Bukkit.getPlayer(args[1]);

                if (args[0].equalsIgnoreCase("0")) {
                    target.setGameMode(GameMode.SURVIVAL);
                    initializer.getMessageUtil().sendMessage(target, "Dein Spielmodus wurde auf Survival gesetzt§8.");
                    initializer.getMessageUtil().sendMessage(sender, "Der Spielmodus von " + target.getName() + " wurde auf Survival gesetzt§8.");
                    return false;
                }

                if (args[0].equalsIgnoreCase("1")) {
                    target.setGameMode(GameMode.CREATIVE);
                    initializer.getMessageUtil().sendMessage(target, "Dein Spielmodus wurde auf Creative gesetzt§8.");
                    initializer.getMessageUtil().sendMessage(sender, "Der Spielmodus von " + target.getName() + " wurde auf Creative gesetzt§8.");
                    return false;
                }

                if (args[0].equalsIgnoreCase("2")) {
                    target.setGameMode(GameMode.ADVENTURE);
                    initializer.getMessageUtil().sendMessage(target, "Dein Spielmodus wurde auf Adventure gesetzt§8.");
                    initializer.getMessageUtil().sendMessage(sender, "Der Spielmodus von " + target.getName() + " wurde auf Adventure gesetzt§8.");
                    return false;
                }

                if (args[0].equalsIgnoreCase("3")) {
                    target.setGameMode(GameMode.SPECTATOR);
                    initializer.getMessageUtil().sendMessage(target, "Dein Spielmodus wurde auf Spectator gesetzt§8.");
                    initializer.getMessageUtil().sendMessage(sender, "Der Spielmodus von " + target.getName() + " wurde auf Spectator gesetzt§8.");
                    return false;
                }
            }

            initializer.getMessageUtil().sendSyntax(sender, "/gamemode <0-3>", "/gamemode <player> <0-3>");
        }

        if (command.getName().equalsIgnoreCase("tpa")) {
            if (args.length == 1) {
                if (!initializer.getMessageUtil().isOnline(sender, args[0]))
                    return false;

                final Player target = Bukkit.getPlayer(args[0]);

                if (target == player) {
                    player.sendMessage("§8┃>§d§o ? LG Obey aus der Entwicklung :D");
                    return false;
                }

                final User targetUser = initializer.getUserHandler().getUserInstant(target.getUniqueId());

                if (!targetUser.is(DataType.TPASTATE)) {
                    initializer.getMessageUtil().sendMessage(sender, "Der Spieler " + target.getName() + " akzeptiert keine TP Anfragen§8.");
                    return false;
                }

                if (tpas.containsKey(player) && tpas.get(player) == target) {
                    initializer.getMessageUtil().sendMessage(player, "Du hast " + target.getName() + " schon eine Anfrage gesendet§8.");
                    return false;
                }

                /*
                final PlayerData playerData = DataCache.getPlayerData(target);

                if(!playerData.isTpa()) {
                    player.sendMessage(Util.prefix + target.getName() + " nimmt keine TPA's an.");
                    return false;
                }
                 */

                tpas.put(player, target);

                initializer.getMessageUtil().sendMessage(player, "Du hast " + target.getName() + " eine TPA gesendet§8.");
                if (initializer.getUserHandler().getUserInstant(target.getUniqueId()).getList(DataType.IGNORES).contains(player.getUniqueId().toString())) {
                    return true;
                }

                initializer.getMessageUtil().sendMessage(target, player.getName() + " möchte sich zu dir teleportieren§8.");

                target.sendMessage(" ");
                initializer.getMessageUtil().sendHoverTextCommand(target, initializer.getServerConfig().getPrefix() + "Klicke hier um §a§lanzunehmen§8.", "/tpa accept " + player.getName());
                target.sendMessage(" ");
                initializer.getMessageUtil().sendHoverTextCommand(target, initializer.getServerConfig().getPrefix() + "Klicke hier um §c§labzulehnen§8.", "/tpa deny " + player.getName());
                target.sendMessage(" ");

                return false;
            }

            if (args.length == 2) {

                if (!initializer.getMessageUtil().isOnline(sender, args[1]))
                    return false;

                final Player target = Bukkit.getPlayer(args[1]);

                if (!tpas.containsKey(target) || tpas.get(target) != player) {
                    initializer.getMessageUtil().sendMessage(player, target.getName() + " hat dir keine TPA gesendet§8.");
                    return false;
                }

                if (args[0].equalsIgnoreCase("accept")) {

                    final Location nether = initializer.getLocationHandler().getLocation("nether");
                    final Location end = initializer.getLocationHandler().getLocation("end");

                    if(nether != null && player.getLocation().getWorld() == nether.getWorld()) {
                        initializer.getMessageUtil().sendMessage(player, "Du befindest in der §c§lNether§7 Dimension§8, §7teleport Anfragen können hier nicht angenommen werden§8.");
                        player.playSound(player.getLocation(), Sound.EXPLODE, 0.2f, 1);
                        return false;
                    }

                    if(end != null && player.getLocation().getWorld() == end.getWorld()) {
                        initializer.getMessageUtil().sendMessage(player, "Du befindest in der §f§lEnd§7 Dimension§8, §7teleport Anfragen können hier nicht angenommen werden§8.");
                        player.playSound(player.getLocation(), Sound.EXPLODE, 0.2f, 1);
                        return false;
                    }


                    tpas.remove(target);

                    initializer.getLocationHandler().teleportToLocation(target, player.getLocation());
                    return false;
                }

                if (args[0].equalsIgnoreCase("deny")) {

                    tpas.remove(target);

                    initializer.getMessageUtil().sendMessage(target, player.getName() + " hat deine Anfrage abgelehnt§8.");
                    initializer.getMessageUtil().sendMessage(player, "Du hast " + target.getName() + "'s Anfrage abgelehnt§8.");

                    return false;
                }
                return false;
            }

            initializer.getMessageUtil().sendSyntax(player, "§8/§7tpa §8<§7spieler§8>", "§8/§7tpa accept §8<§7spieler§8>", "§8/§7tpa deny §8<§7spieler§8>");
            return false;
        }

        if (command.getName().equalsIgnoreCase("tpahere")) {

            if(!PermissionUtil.hasPermission(sender, "tpahere", true))
                return false;

            if (args.length == 1) {
                if (!initializer.getMessageUtil().isOnline(sender, args[0]))
                    return false;

                final Player target = Bukkit.getPlayer(args[0]);

                if (target == player) {
                    player.sendMessage("§8┃>§d§o ? LG Richard aus der Entwicklung :D");
                    return false;
                }

                final User targetUser = initializer.getUserHandler().getUserInstant(target.getUniqueId());

                if (!targetUser.is(DataType.TPASTATE)) {
                    initializer.getMessageUtil().sendMessage(sender, "Der Spieler " + target.getName() + " akzeptiert keine TPA-Here Anfragen§8.");
                    return false;
                }

                if (tpaHeres.containsKey(player) && tpaHeres.get(player) == target) {
                    initializer.getMessageUtil().sendMessage(player, "Du hast " + target.getName() + " schon eine Anfrage gesendet§8.");
                    return false;
                }

                tpaHeres.put(player, target);

                initializer.getMessageUtil().sendMessage(player, "Du hast " + target.getName() + " eine TPA-Here gesendet§8.");

                if (initializer.getUserHandler().getUserInstant(target.getUniqueId()).getList(DataType.IGNORES).contains(player.getUniqueId().toString())) {
                    return true;
                }

                initializer.getMessageUtil().sendMessage(target, player.getName() + " möchte dich zu ihm teleportieren§8.");

                target.sendMessage(" ");
                initializer.getMessageUtil().sendHoverTextCommand(target, initializer.getServerConfig().getPrefix() + "Klicke hier um §a§lanzunehmen§8.", "/tpahere accept " + player.getName());
                target.sendMessage(" ");
                initializer.getMessageUtil().sendHoverTextCommand(target, initializer.getServerConfig().getPrefix() + "Klicke hier um §c§labzulehnen§8.", "/tpahere deny " + player.getName());
                target.sendMessage(" ");

                return false;
            }

            if (args.length == 2) {

                if (!initializer.getMessageUtil().isOnline(sender, args[1]))
                    return false;

                final Player target = Bukkit.getPlayer(args[1]);

                if (!tpaHeres.containsKey(target) || tpaHeres.get(target) != player) {
                    initializer.getMessageUtil().sendMessage(player, target.getName() + " hat dir keine TPA-Here gesendet§8.");
                    return false;
                }

                if (args[0].equalsIgnoreCase("accept")) {
                    final Location nether = initializer.getLocationHandler().getLocation("nether");
                    final Location end = initializer.getLocationHandler().getLocation("end");

                    if(nether != null && target.getLocation().getWorld() == nether.getWorld()) {
                        initializer.getMessageUtil().sendMessage(player, target.getName() + " befindet sich in der §c§lNether§7 Dimension§8, §7du kannst nicht zu ihm teleportiert werden§8.");
                        player.playSound(player.getLocation(), Sound.EXPLODE, 0.2f, 1);
                        return false;
                    }

                    if(end != null && target.getLocation().getWorld() == end.getWorld()) {
                        initializer.getMessageUtil().sendMessage(player, target.getName() + " befindet sich in der §f§lEnd§7 Dimension§8, §7du kannst nicht zu ihm teleportiert werden§8.");
                        player.playSound(player.getLocation(), Sound.EXPLODE, 0.2f, 1);
                        return false;
                    }

                    tpaHeres.remove(target);
                    initializer.getLocationHandler().teleportToLocation(player, target.getLocation());
                    return false;
                }

                if (args[0].equalsIgnoreCase("deny")) {

                    tpaHeres.remove(target);

                    initializer.getMessageUtil().sendMessage(target, player.getName() + " hat deine TPA-Here Anfrage abgelehnt§8.");
                    initializer.getMessageUtil().sendMessage(player, "Du hast " + target.getName() + "'s TPA-Here Anfrage abgelehnt§8.");

                    return false;
                }
                return false;
            }

            initializer.getMessageUtil().sendSyntax(player, "§8/§7tpahere §8<§7spieler§8>", "§8/§7tpahere accept §8<§7spieler§8>", "§8/§7tpahere deny §8<§7spieler§8>");
            return false;
        }

        return false;
    }

    private float getRealMoveSpeed(final float userSpeed, final boolean isFly) {
        final float defaultSpeed = isFly ? 0.1f : 0.2f;
        float maxSpeed = 1f;

        if (userSpeed < 1f) {
            return defaultSpeed * userSpeed;
        }
        final float ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
        return ratio + defaultSpeed;
    }

}
