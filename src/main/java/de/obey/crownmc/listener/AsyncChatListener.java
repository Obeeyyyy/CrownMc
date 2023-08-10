package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       14.10.2022 / 16:44

*/

import com.intellectualcrafters.plot.api.PlotAPI;
import com.plotsquared.bukkit.util.BukkitUtil;
import de.obey.crownmc.backend.Rang;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.commands.AfkCommand;
import de.obey.crownmc.commands.SupportCommand;
import de.obey.crownmc.commands.VanishCommand;
import de.obey.crownmc.handler.*;
import de.obey.crownmc.objects.SupportChat;
import de.obey.crownmc.util.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Random;

@RequiredArgsConstructor
@NonNull
public final class AsyncChatListener implements Listener {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final RangHandler rangHandler;
    private final ChatFilterHandler chatFilterHandler;
    private final CoinflipHandler coinflipHandler;
    private final CrashHandler crashHandler;
    private final JackPotHandler jackPotHandler;
    private final RouletteHandler rouletteHandler;
    private final PlotAPI plotAPI;
    private final WarpAugeListener warpAugeListener;
    private final WordScrambleHandler wordScrambleHandler;
    private final ClanHandler clanHandler;

    private boolean checkPlotChat(final Player player) {
        if (Bukkit.getPluginManager().getPlugin("PlotSquared") == null)
            return false;

        if (!plotAPI.isInPlot(player))
            return false;

        return BukkitUtil.getPlayer(player).getAttribute("chat");
    }

    @EventHandler
    public void on(final AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        final Player player = event.getPlayer();

        if (checkPlotChat(player))
            return;

        String message = event.getMessage();

        /* ChatFilter check */
        if (!chatFilterHandler.runChatFilterCheck(player, message)) {
            messageUtil.sendMessage(player, "§c§oDeine Nachricht wurde nicht abgesendet§8.");
            return;
        }

        /*
        Word Scramlbe check
         */
        wordScrambleHandler.isInScramble(player, message);

        /* Setting coinflip start*/
        if (coinflipHandler.isCreatingCoinflip(player, message))
            return;

        if (coinflipHandler.isCreatingServerCoinflip(player, message))
            return;
        /* Setting coinflip end */

        /* Setting prefix start*/
        if (PrefixListener.isSettingChatPrefix(player, message))
            return;

        if (PrefixListener.isSettingTmote(player, message))
            return;
        /* Setting prefix end */

        /* Trade setting coins start*/
        if (TradeListener.settingTradeCoins(player, message))
            return;
        /* Trade setting coins end */

        /* Joining Crash start*/
        if (crashHandler.isJoiningCrash(player, message))
            return;
        /* Joining Crash coins end */

        /* Clan start*/
        if (clanHandler.isCreatingClan(player, message))
            return;

        if (clanHandler.isInviting(player, message))
            return;
        /* Clan end */

        /* Setting Jackpot start*/
        if (jackPotHandler.isCreatingJackpot(player, message))
            return;
        /* Setting Jackpot coins end */

        /* Joining Roulette*/
        if (rouletteHandler.isJoiningRoulette(player, message))
            return;
        /* Joining Roulette */

        /* WarpAuge check */
        if(warpAugeListener.isSettingLocation(player, event.getMessage()))
            return;
        /* WarpAuge check done */

        final User user = userHandler.getUserInstant(player.getUniqueId());

        if (user == null)
            return;

        if(user.getPunishment().isMuted()) {
            messageUtil.sendMessage(player, "Du bist noch für §8'§e" + MathUtil.getHoursAndMinutesAndSecondsFromSeconds(user.getPunishment().getRemainingMuteMillis()/1000) + "§8'§7 gemutet§8.");
            return;
        }

        final Rang rang = rangHandler.getPlayerRang(player);

        if(rang == null) {
            event.setCancelled(false);
            return;
        }

        if(message.contains("&") && message.length() <= 2)
            return;

        message = checkForPlayerNames(player, event.getMessage(), rang);

        final String prefix = rang.getChatPrefix();
        final String suffix = " " + rang.getChatSuffix();
        final String chatColor = ChatColor.translateAlternateColorCodes('&', rang.getChatcolor());

        String preLine = prefix + player.getName() + suffix + chatColor;

        final String activePrefix = user.getPrefix().getActivePrefix();

        if (!activePrefix.equalsIgnoreCase(""))
            preLine = "§8(" + activePrefix + "§8)" + user.getPrefix().getNameColor() + " " + player.getName() + suffix + chatColor;

        preLine = ChatColor.translateAlternateColorCodes('&', preLine);

        if(PermissionUtil.hasPermission(player, "chatcolor", false))
            message = ChatColor.translateAlternateColorCodes('&', message);

        final MessageBuilder messageBuilder = new MessageBuilder().addHoverShowText(preLine, getUserStatsMessage(user));

        if(checkForItem(player, message)) {
            final String[] splitted = message.split("<i>");

            messageBuilder.add(chatColor + splitted[0]);

            final String displayname = player.getItemInHand().hasItemMeta() ? (player.getItemInHand().getItemMeta().hasDisplayName() ? player.getItemInHand().getItemMeta().getDisplayName() : player.getItemInHand().getType().name()) : player.getItemInHand().getType().name();

            String hover = "";

            if(player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasLore()) {
                for (String s : player.getItemInHand().getItemMeta().getLore()) {
                    hover = hover + s + "\n";
                }
            }

            messageBuilder.addHoverShowText( displayname, hover);

            if(splitted.length > 1)
                messageBuilder.add(chatColor + splitted[1]);

        } else {
            messageBuilder.add(chatColor + message);
        }

        //final String line = preLine + message;

        /* SupportChat Start */
        final SupportChat supportChat = SupportCommand.isInSupportChat(player.getUniqueId());

        if (supportChat != null) {
            if (supportChat.getState() == 1) {
                supportChat.sendMessageToMemebers(messageBuilder.getText());
                return;
            }
        }
        /* SupportChat End */

        //GlobalMute Start
        if (!Bools.chat) {
            if (!PermissionUtil.hasPermission(player, "team", false)) {
                messageUtil.sendMessage(player, "Du kannst keine Nachrichten senden, Globalmute ist aktiv§8.");
                return;
            }
        }
        //GlobalMute End

        messageUtil.log("(CHAT) " + player.getName() + " -> " + ChatColor.stripColor(message));

        Bukkit.getOnlinePlayers().forEach(online -> {

            if (SupportCommand.isInSupportChat(online.getUniqueId()) != null && SupportCommand.isInSupportChat(online.getUniqueId()).getState() == 1)
                return;

            if (PermissionUtil.hasPermission(player, "chatlines", false) && userHandler.getUserInstant(player.getUniqueId()).is(DataType.CHATLINESSTATE)) {
                online.sendMessage("§8»");
                //online.spigot().sendMessage(textComponent);
                //online.sendMessage(line.replace("§a§o@" + online.getName(), "§c§o§n@" + online.getName()));
                messageBuilder.send(online);
                online.sendMessage("§8»");
            } else {
                //online.spigot().sendMessage(textComponent);
                //online.sendMessage(line.replace("§a§o@" + online.getName(), "§c§o§n@" + online.getName()));
                messageBuilder.send(online);
            }
        });

        // add xp
        final int xpChance = new Random().nextInt(100);

        if (xpChance < 50)
            user.addXP(50);
    }

    private boolean checkForItem(final Player player, String message) {
        if (message.contains("<i>")) {
            return player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR;
        }
        return false;
    }

    private String getUserStatsMessage(final User user) {
        return "§6§l" + user.getOfflinePlayer().getName() + "\n" +
                "§8» §7Clan§8: §f" + (user.getClan() == null ? "§c§oKein Clan": user.getClan().getClanName() + "§8 (§f§o" + user.getClan().getClanTag() + "§8)") + "\n" +
                "§8» §7Level§8: §f" + messageUtil.formatLong(user.getLong(DataType.LEVEL)) + " §8(§f§o" + messageUtil.formatLong(user.getLong(DataType.XP)) +  " XP§8)\n" +
                "§8» §7Money§8: §e§o" + messageUtil.formatLong(user.getLong(DataType.MONEY)) + "§6§l$\n" +
                "§8» §7Kills§8: §a§o" + messageUtil.formatLong(user.getLong(DataType.KILLS)) + "\n" +
                "§8» §7Tode§8: §c§o" + messageUtil.formatLong(user.getLong(DataType.DEATHS)
        );
    }

    private String checkForPlayerNames(final Player player, String message, final Rang rang) {

        final String[] words = message.split(" ");

        final ArrayList<String> pingedNames = new ArrayList<>();

        message = "";

        for (final String word : words) {
            if (word.length() > 3) {
                final Player check = Bukkit.getPlayer(word);

                if (check != null && check.isOnline() && check.getName().equalsIgnoreCase(word) && !VanishCommand.vanished.contains(check)) {
                    if (!pingedNames.contains(check.getName().toLowerCase())) {
                        if (AfkCommand.afkList.contains(check)) {
                            messageUtil.sendMessage(player, check.getName() + " ist §c§lAFK§8.");
                        }
                        pingedNames.add(check.getName().toLowerCase());
                        message = message + " " + "§a§o@" + check.getName() + ChatColor.translateAlternateColorCodes('&', rang.getChatcolor());
                        check.playSound(check.getLocation(), Sound.SUCCESSFUL_HIT, 3f, 10);
                    } else {
                        message = message + " " + word;
                    }
                } else {
                    message = message + " " + word;
                }
            } else {
                message = message + " " + word;
            }

        }

        return message;
    }
}
