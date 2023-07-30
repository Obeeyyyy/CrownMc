package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       13.10.2022 / 13:26

*/

import de.obey.crownmc.backend.Rang;
import de.obey.crownmc.handler.RangHandler;
import de.obey.crownmc.handler.ScoreboardHandler;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

@RequiredArgsConstructor
@NonNull
public final class RangCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final RangHandler rangHandler;
    private final ScoreboardHandler scoreboardHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player) || PermissionUtil.hasPermission(sender, "*", true)) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {

                    if (rangHandler.getGroupMap().size() > 0) {

                        sender.sendMessage("  §8(§7x§8) §7Alle Ränge §8:");

                        final HashMap<Integer, Rang> sorted = new HashMap<>();

                        for (Rang rang : rangHandler.getGroupMap().values())
                            sorted.put(rang.getId(), rang);

                        for (int i = 0; i < 100; i++) {
                            if (sorted.containsKey(i)) {
                                final Rang rang = sorted.get(i);
                                sender.sendMessage("§e§l  - §7ID§8: §e§o" + rang.getId());
                                sender.sendMessage("§e§l  - §7Name§8: §e§o" + rang.getName() + "§7 Sort§8: §e§o" + rang.getSort());
                                sender.sendMessage("§8      >§7 Chat§8: §r" + ChatColor.translateAlternateColorCodes('&', rang.getChatPrefix() + sender.getName() + " " + rang.getChatSuffix()));
                                sender.sendMessage("§8      >§7 ChatColor§8: §r" + ChatColor.translateAlternateColorCodes('&', rang.getChatcolor()) + " test test");
                                sender.sendMessage("§8      >§7 Tab§8: §r" + ChatColor.translateAlternateColorCodes('&', rang.getTabPrefix() + sender.getName() + " " + rang.getTabSuffix()));
                                sender.sendMessage("§8      >§7 Tag§8: §r" + ChatColor.translateAlternateColorCodes('&', rang.getNtPrefix() + sender.getName() + " " + rang.getNtSuffix()));
                                sender.sendMessage("§8      >§7 ShowPrefix§8: §r" + rang.getShowprefix());
                            }
                        }

                        return false;
                    }

                    messageUtil.sendMessage(sender, "Es wurden noch keine Ränge erstellt.");

                    return false;
                }

                if (args[0].equalsIgnoreCase("reload")) {
                    rangHandler.getGroupMap().clear();
                    rangHandler.loadRangs();
                    messageUtil.sendMessage(sender, "Ränge neu geladen.");
                    return false;
                }
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("create")) {

                    if (rangHandler.getGroupMap().containsKey(args[1])) {
                        messageUtil.sendMessage(sender, "Der Rang " + args[1] + " existiert schon.");
                        return false;
                    }

                    rangHandler.createRang(args[1].toLowerCase());
                    messageUtil.sendMessage(sender, "Der Rang " + args[1] + " wurde erstellt.");

                    return false;
                }

                if (args[0].equalsIgnoreCase("delete")) {

                    if (!rangHandler.getGroupMap().containsKey(args[1])) {
                        messageUtil.sendMessage(sender, "Der Rang " + args[1] + " existiert nicht.");
                        return false;
                    }

                    rangHandler.deleteRang(args[1].toLowerCase());
                    messageUtil.sendMessage(sender, "Der Rang " + args[1] + " wurde gelöscht.");

                    return false;
                }

                if (args[0].equalsIgnoreCase("info")) {

                    if (!rangHandler.getGroupMap().containsKey(args[1])) {
                        messageUtil.sendMessage(sender, "Der Rang " + args[1] + " existiert nicht.");
                        return false;
                    }

                    final Rang rang = rangHandler.getGroupMap().get(args[1]);

                    sender.sendMessage("");
                    sender.sendMessage("§e§l  - §7ID§8: §e§o" + rang.getId());
                    sender.sendMessage("§e§l  - §7Name§8: §e§o" + rang.getName() + "§7 Sort§8: §e§o" + rang.getSort());
                    sender.sendMessage("§8      >§7 Chat§8: §r" + ChatColor.translateAlternateColorCodes('&', rang.getChatPrefix() + sender.getName() + " " + rang.getChatSuffix()));
                    sender.sendMessage("§8      >§7 ChatColor§8: §r" + ChatColor.translateAlternateColorCodes('&', rang.getChatcolor()) + " test test");
                    sender.sendMessage("§8      >§7 Tab§8: §r" + ChatColor.translateAlternateColorCodes('&', rang.getTabPrefix() + sender.getName() + " " + rang.getTabSuffix()));
                    sender.sendMessage("§8      >§7 Tag§8: §r" + ChatColor.translateAlternateColorCodes('&', rang.getNtPrefix() + sender.getName() + " " + rang.getNtSuffix()));
                    sender.sendMessage("§8      >§7 ShowPrefix§8: §r" + rang.getShowprefix());
                    sender.sendMessage("");

                    return false;
                }

                if (!messageUtil.hasPlayedBefore(sender, args[0]))
                    return false;

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

                rangHandler.setPlayerRang(target, args[1], sender);
                scoreboardHandler.updateEverythingForEveryone();

                return false;
            }

            if (args.length >= 3) {

                final Rang rang = rangHandler.getGroupMap().get(args[1]);

                if (rang == null) {
                    messageUtil.sendMessage(sender, "Der Rang " + args[1] + " existert nicht.");
                    return false;
                }

                String text = args[2];

                if (args.length > 3) {
                    for (int i = 3; i < args.length; i++) {
                        text = text + " " + args[i];
                    }
                }

                if (args[0].equalsIgnoreCase("chatprefix")) {
                    rang.setChatPrefix(text);
                    messageUtil.sendMessage(sender, "ChatPrefix für " + rang.getName() + " auf " + text + "§7 gesetzt.");
                    scoreboardHandler.updateEverythingForEveryone();
                    return false;
                }

                if (args[0].equalsIgnoreCase("chatcolor")) {
                    rang.setChatcolor(text);
                    messageUtil.sendMessage(sender, "ChatColor für " + rang.getName() + " auf " + text + "test §7gesetzt.");
                    scoreboardHandler.updateEverythingForEveryone();
                    return false;
                }

                if (args[0].equalsIgnoreCase("chatsuffix")) {
                    rang.setChatSuffix(text);
                    messageUtil.sendMessage(sender, "ChatSuffix für " + rang.getName() + " auf " + text + "§7 gesetzt.");
                    scoreboardHandler.updateEverythingForEveryone();
                    return false;
                }

                if (args[0].equalsIgnoreCase("tabprefix")) {
                    rang.setTabPrefix(text);
                    messageUtil.sendMessage(sender, "TabPrefix für " + rang.getName() + " auf " + text + "§7 gesetzt.");
                    scoreboardHandler.updateEverythingForEveryone();
                    return false;
                }

                if (args[0].equalsIgnoreCase("tabsuffix")) {
                    rang.setTabSuffix(text);
                    messageUtil.sendMessage(sender, "TabSuffix für " + rang.getName() + " auf " + text + "§7 gesetzt.");
                    scoreboardHandler.updateEverythingForEveryone();
                    return false;
                }

                if (args[0].equalsIgnoreCase("ntprefix")) {

                    if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text)).length() > 8) {
                        messageUtil.sendMessage(sender, "Zu lang !");
                        return false;
                    }

                    rang.setNtPrefix(text);
                    messageUtil.sendMessage(sender, "NameTagPrefix für " + rang.getName() + " auf " + text + "§7 gesetzt.");
                    scoreboardHandler.getTeams().clear();
                    scoreboardHandler.updateEverythingForEveryone();
                    return false;
                }

                if (args[0].equalsIgnoreCase("ntsuffix")) {
                    rang.setNtSuffix(text);
                    messageUtil.sendMessage(sender, "NameTagSuffix für " + rang.getName() + " auf " + text + "§7 gesetzt.");
                    scoreboardHandler.getTeams().clear();
                    scoreboardHandler.updateEverythingForEveryone();
                    return false;
                }

                if (args[0].equalsIgnoreCase("showprefix")) {
                    rang.setShowprefix(ChatColor.translateAlternateColorCodes('&', text));
                    messageUtil.sendMessage(sender, "ShowPrefix für " + rang.getName() + " auf " + text + "§7 gesetzt.");
                    return false;
                }

                if (args[0].equalsIgnoreCase("sort")) {
                    rang.setSort(text);
                    messageUtil.sendMessage(sender, "Sortierung für " + rang.getName() + " auf " + text + "§7 gesetzt.");
                    scoreboardHandler.getTeams().clear();
                    scoreboardHandler.updateEverythingForEveryone();
                    return false;
                }

                if (args[0].equalsIgnoreCase("id")) {
                    try {
                        rang.setId(Integer.parseInt(text));
                        messageUtil.sendMessage(sender, "ID für " + rang.getName() + " auf " + text + "§7 gesetzt.");
                    } catch (final NumberFormatException exception) {
                        messageUtil.sendMessage(sender, "Bitte gebe eine zahl an.");
                    }

                    return false;
                }
            }

            messageUtil.sendSyntax(sender, "/rang list",
                    "/rang reload",
                    "/rang create <name>",
                    "/rang delete <name>",
                    "/rang chatprefix <group> <text>",
                    "/rang chatsuffix <group> <text>",
                    "/rang chatcolor <group> <text>",
                    "/rang tabprefix <group> <text>",
                    "/rang tabsuffix <group> <text>",
                    "/rang ntprefix <group> <text>",
                    "/rang ntsuffix <group> <text>",
                    "/rang showprefix <group> <text>",
                    "/rang id <group> <zahl>",
                    "/rang sort <group> <001>",
                    "/rang info <group>",
                    "/rang <player> <group>");

            return false;
        } else {
            return false;
        }

    }
}
