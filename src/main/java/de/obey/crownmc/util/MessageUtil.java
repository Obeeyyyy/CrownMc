package de.obey.crownmc.util;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 23:09

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.commands.AfkCommand;
import de.obey.crownmc.commands.VanishCommand;
import de.obey.crownmc.handler.UserHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.logging.Level;

@RequiredArgsConstructor
public final class MessageUtil {

    @NonNull
    private final ServerConfig serverConfig;

    private UserHandler userHandler;

    public boolean isOnline(final CommandSender sender, final String name) {
        if (userHandler == null)
            userHandler = CrownMain.getInstance().getInitializer().getUserHandler();

        final Player player = Bukkit.getPlayer(name);

        if (player == null || !player.isOnline() || (!PermissionUtil.hasPermission(sender, "team", false) && VanishCommand.vanished.contains(player))) {
            sender.sendMessage(serverConfig.getPrefix() + "Der Spieler §8'§f§o" + name + "§8'§7 ist §c§onicht §7online§8.");

            if (sender instanceof Player)
                ((Player) sender).playSound(((Player) sender).getLocation(), Sound.EXPLODE, 0.5f, 1);

            return false;
        }

        if (AfkCommand.afkList.contains(player) && !PermissionUtil.hasPermission(sender, "team", false)) {
            sender.sendMessage(serverConfig.getPrefix() + "Der Spieler §8'§f§o" + name + "§8'§7 ist §f§lAFK§8.");
            return false;
        }

        return true;
    }

    public boolean hasPlayedBefore(final String name) {
        if (userHandler == null)
            userHandler = CrownMain.getInstance().getInitializer().getUserHandler();

        if (!name.matches("[a-zA-Z0-9_]*"))
            return false;

        final OfflinePlayer player = Bukkit.getOfflinePlayer(name);

        return userHandler.isRegistered(player);
    }

    public boolean hasPlayedBefore(final CommandSender sender, final String name) {
        if (!hasPlayedBefore(name)) {
            sender.sendMessage(serverConfig.getPrefix() + "Der Spieler §8'§f§o" + name + "§8'§7 war noch §c§onie §7auf dem Server§8.");

            if (sender instanceof Player)
                ((Player) sender).playSound(((Player) sender).getLocation(), Sound.EXPLODE, 0.5f, 1);
            return false;
        }

        return true;
    }

    public void sendMessageToTeamMembers(final String message) {
        for (final Player team : Bukkit.getOnlinePlayers()) {
            if (PermissionUtil.hasPermission(team, "team", false))
                team.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public void sendMessage(final CommandSender target, final String message) {
        target.sendMessage(serverConfig.getPrefix() + ChatColor.translateAlternateColorCodes('&', message));
    }

    public boolean hasEnougthMoney(final User user, final Long amount) {

        if (user.getLong(DataType.MONEY) < amount) {
            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.EXPLODE, 0.5f, 1);
            sendMessage(user.getPlayer(), "Du hast nicht genug Geld§8. (§c§o-" + formatLong(amount - user.getLong(DataType.MONEY)) + "§4§o$§8)");
            return false;
        }

        return true;
    }

    public boolean hasEnougthCrowns(final User user, final int amount) {

        if (user.getLong(DataType.CROWNS) < amount) {
            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.EXPLODE, 0.5f, 1);
            sendMessage(user.getPlayer(), "Du hast nicht genug Crowns§8. (§c§o-" + formatLong(amount - user.getLong(DataType.CROWNS)) + "§8)");
            return false;
        }

        return true;
    }

    public void broadcast(final String message) {
        Bukkit.broadcastMessage(serverConfig.getPrefix() + ChatColor.translateAlternateColorCodes('&', message));
    }

    public void broadcastNoPrefix(final String message) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void log(final String message) {
        //Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        Bukkit.getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', message));
    }

    public void warn(final String message) {
        Bukkit.getLogger().log(Level.WARNING, ChatColor.translateAlternateColorCodes('&', message));
    }

    public void sendHoverTextCommandToTeamMembers(final String message, final String command) {
        for (final Player team : Bukkit.getOnlinePlayers()) {
            if (PermissionUtil.hasPermission(team, "team", false))
                sendHoverTextCommand(team, "§c§lTEAMINFO §8x§7 " + message, command);
        }
    }

    public void sendHoverTextCommand(final Player player, final String text, final String command) {
        final TextComponent tc = new TextComponent();
        tc.setText(text);
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(text).create()));
        player.spigot().sendMessage(tc);
    }

    public void sendSyntax(final CommandSender sender, final String... lines) {
        if (sender instanceof Player)
            ((Player) sender).playSound(((Player) sender).getLocation(), Sound.EXPLODE, 0.2f, 1);

        sender.sendMessage("§8┃> §f§oBitte nutze:");
        for (String line : lines) {
            sender.sendMessage("§8┃> §f" + line);
        }
    }

    public String formatLong(final long value) {
        return NumberFormat.getInstance().format(value);
    }

    public String getColoredPercentage(final int percentage) {
        return percentage > 60 ? "§a" + percentage : (percentage > 40 ? "§e" + percentage : (percentage > 15 ? "§c" + percentage : "§4" + percentage));
    }

    public String getArmorDurability(final ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return "§c§oleer";

        // xm -> 100
        // x -> ??


        final int percentage = 100 - (item.getDurability() * 100 / item.getType().getMaxDurability());

        if(percentage <= 0)
            return "§c§oleer";

        return getColoredPercentage(percentage) + "%";
    }
}
