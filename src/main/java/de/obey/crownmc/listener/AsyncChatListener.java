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
import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

        /* Setting coinflip start*/
        if (coinflipHandler.isCreatingCoinflip(player, message))
            return;

        if (coinflipHandler.isCreatingServerCoinflip(player, message))
            return;
        /* Setting coinflip end */

        /* Setting prefix start*/
        if (PrefixListener.isSettingChatPrefix(player, message))
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

        /* Setting Jackpot start*/
        if (jackPotHandler.isCreatingJackpot(player, message))
            return;
        /* Setting Jackpot coins end */

        /* Joining Roulette*/
        if (rouletteHandler.isJoiningRoulette(player, message))
            return;
        /* Joining Roulette */

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

        if(message.contains("&") && message.length() <= 2) {
            return;
        }

        message = checkForPlayerNames(player, event.getMessage(), rang);

        if (PermissionUtil.hasPermission(player, "chatcolor", false))
            message = ChatColor.translateAlternateColorCodes('&', message);

        final String prefix = rang != null ? rang.getChatPrefix() : "";
        final String suffix = rang != null ? " " + rang.getChatSuffix() : "";

        String preLine = prefix + player.getName() + suffix + rang.getChatcolor();

        final String activePrefix = user.getPrefix().getActivePrefix();

        if (!activePrefix.equalsIgnoreCase(""))
            preLine = "§8(" + activePrefix + "§8)" + user.getPrefix().getNameColor() + " " + player.getName() + suffix + rang.getChatcolor();

        message = checkForItem(player, message);

        final String line = ChatColor.translateAlternateColorCodes('&', preLine + message);

        /* SupportChat Start */
        final SupportChat supportChat = SupportCommand.isInSupportChat(player.getUniqueId());

        if (supportChat != null) {
            if (supportChat.getState() == 1) {
                //supportChat.sendMessageToMemebers(textComponent.getText());
                supportChat.sendMessageToMemebers(line);
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


        /* ChatFilter check */
        if (!chatFilterHandler.runChatFilterCheck(player, message))
            return;

        messageUtil.log("(CHAT) " + player.getName() + " -> " + ChatColor.stripColor(message));

        Bukkit.getOnlinePlayers().forEach(online -> {

            if (SupportCommand.isInSupportChat(online.getUniqueId()) != null && SupportCommand.isInSupportChat(online.getUniqueId()).getState() == 1)
                return;

            if (userHandler.getUserInstant(online.getUniqueId()).getList(DataType.IGNORES).contains(player.getUniqueId().toString()))
                return;

            if (PermissionUtil.hasPermission(player, "chatlines", false) && userHandler.getUserInstant(player.getUniqueId()).is(DataType.CHATLINESSTATE)) {
                online.sendMessage("§8»");
                //online.spigot().sendMessage(textComponent);
                online.sendMessage(line.replace("§a§o@" + online.getName(), "§c§o§n@" + online.getName()));
                online.sendMessage("§8»");
            } else {
                //online.spigot().sendMessage(textComponent);
                online.sendMessage(line.replace("§a§o@" + online.getName(), "§c§o§n@" + online.getName()));
            }
        });

        // add xp
        final int xpChance = new Random().nextInt(100);

        if (xpChance < 50)
            user.addXP(50);
    }

    private String checkForItem(final Player player, String message) {
        if (message.contains("<i>")) {
            if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {

                String displayname = player.getItemInHand().getType().toString();

                if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName())
                    displayname = player.getItemInHand().getItemMeta().getDisplayName();

                message = message.replaceAll("<i>", displayname + "§7");
            }
        }

        return message;
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
                        message = message + " " + "§a§o@" + check.getName() + rang.getChatcolor();
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
