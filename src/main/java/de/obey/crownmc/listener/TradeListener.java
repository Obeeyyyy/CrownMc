package de.obey.crownmc.listener;
/*

    Author - EntixOG -> SkySlayer-v4
       04.11.2022 / 16:22

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.CrownMain;
import de.obey.crownmc.objects.trade.PlayerOffer;
import de.obey.crownmc.objects.trade.Trade;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@RequiredArgsConstructor
public final class TradeListener implements Listener {

    @NonNull
    private final Initializer initializer;

    private static final HashMap<Player, Trade> settingCoins = new HashMap<>();

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {

            final Player player = (Player) event.getWhoClicked();

            final Inventory inventory = event.getInventory();
            if (!inventory.getName().startsWith("Trade mit ")) {
                return;
            }

            event.setCancelled(true);

            if (!initializer.getTradeHandler().hasTrade(player)) {
                return;
            }

            final InventoryAction action = event.getAction();
            if (action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD) {
                return;
            }

            final ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                return;
            }

            final Trade trade = initializer.getTradeHandler().getPlayerTrade(player);
            final PlayerOffer playerOffer = trade.findOfferByPlayer(player);

            if (event.getRawSlot() >= 0 && event.getRawSlot() <= 53) {
                int clickedSlot = event.getRawSlot();

                if (clickedSlot == 0) {
                    settingCoins.put(player, trade);
                    player.closeInventory();
                    initializer.getMessageUtil().sendMessage(player, "Wie viel Geld möchtest du traden §8?");
                    trade.handelOfferHasChange(player);
                    return;
                }

                if (clickedSlot == 1) {
                    if (!(trade.findOfferByPlayer(trade.findOppositeByPlayer(player)).isReady()))
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.5f, 1);

                    playerOffer.updateStatus(!playerOffer.isReady());
                    return;
                }

                if (clickedSlot == 4) {
                    player.sendMessage("§7Du hast den Trade abgebrochen§8.");

                    Player oppositeTarget = trade.findOppositeByPlayer(player);
                    oppositeTarget.sendMessage("§a" + player.getName() + " §7hat den Trade abgebrochen§8.");
                    initializer.getTradeHandler().closeTrade(trade);
                    return;
                }

                if (!trade.getOwn_Slots().contains(clickedSlot)) {
                    return;
                }

                int index = trade.getItemIndexBySlot(clickedSlot);
                if (index == -1L) {
                    return;
                }

                InventoryUtil.addItem(player, itemStack);
                playerOffer.removeItem(index);
            } else {
                if (playerOffer.getItemsToTrade().size() >= trade.getOwn_Slots().size()) {
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 0.5f, 1);
                    return;
                }

                playerOffer.addItem(itemStack);
                event.setCurrentItem(new ItemBuilder(Material.AIR).build());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            final Player player = (Player) event.getPlayer();
            if (!initializer.getTradeHandler().hasTrade(player)) {
                return;
            }

            if (settingCoins.containsKey(player))
                return;

            final Trade trade = initializer.getTradeHandler().getPlayerTrade(player);
            final Player target = trade.findOppositeByPlayer(player);

            target.sendMessage("§a" + player.getName() + " §7hat den Trade beendet§8.");
            player.sendMessage("§7Du hast dein Inventar geschlossen, der Trade wurde beendet§8.");

            initializer.getTradeHandler().closeTrade(trade);
        }
    }

    public static boolean settingTradeCoins(final Player player, final String mesasge) {
        if (!settingCoins.containsKey(player))
            return false;

        final Trade trade = settingCoins.get(player);
        final PlayerOffer playerOffer = trade.findOfferByPlayer(player);

        if (mesasge.equalsIgnoreCase("cancel")) {
            settingCoins.remove(player);
            player.openInventory(trade.getTradeInventory(player));
            return true;
        }

        final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

        try {
            final long amount = Long.parseLong(mesasge);

            if (amount < 0) {
                messageUtil.sendMessage(player, "Bitte gebe eine Zahl an die größer als -1 ist§8.");
                messageUtil.sendMessage(player, "Schreibe cancel um abzubrechen§8.");
                return true;
            }

            messageUtil.sendMessage(trade.getTarget(), player.getName() + " hat §e§o" + messageUtil.formatLong(amount) + "§6§l$§7 gesetzt§8.");
            messageUtil.sendMessage(player, "Du hast §e§o" + messageUtil.formatLong(amount) + "§6§l$§7 gesetzt§8.");

            playerOffer.setCoins(amount);
            settingCoins.remove(player);
            trade.updateInventory(player);
            trade.updateInventory(trade.getTarget());
            player.openInventory(trade.getTradeInventory(player));

        } catch (final NumberFormatException exception) {
            messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
            messageUtil.sendMessage(player, "Schreibe cancel um abzubrechen§8.");
            return true;
        }

        return true;
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (!initializer.getTradeHandler().hasTrade(player)) {
            return;
        }

        final Trade trade = initializer.getTradeHandler().getPlayerTrade(player);
        final Player target = trade.findOppositeByPlayer(player);

        settingCoins.remove(player);
        settingCoins.remove(target);

        target.sendMessage("§a" + player.getName() + " §7hat den Server verlassen, der Trade wurde abgerbochen§8.");
        initializer.getTradeHandler().closeTrade(trade);
    }

}
