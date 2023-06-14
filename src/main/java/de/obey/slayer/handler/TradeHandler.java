package de.obey.slayer.handler;
/*

    Author - EntixOG -> SkySlayer-v4
       04.11.2022 / 15:51

*/

import de.obey.slayer.objects.trade.PlayerOffer;
import de.obey.slayer.objects.trade.Trade;
import de.obey.slayer.util.InventoryUtil;
import de.obey.slayer.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class TradeHandler {

    @NonNull
    private final MessageUtil messageUtil;

    @Getter
    private final Map<Player, List<UUID>> requests = new HashMap<>();
    private final Map<Player, Trade> playerTradeCache = new HashMap<>();

    public void openTrade(Player player, Player target) {
        if (playerTradeCache.containsKey(player) || playerTradeCache.containsKey(target))
            return;


        final Trade trade = new Trade(player, target);
        playerTradeCache.put(player, trade);
        playerTradeCache.put(target, trade);
    }

    public void closeTrade(final Trade trade) {
        if (!playerTradeCache.containsValue(trade))
            return;

        for (Map.Entry<Player, PlayerOffer> tradeEntity : trade.getPlayerOfferCache().entrySet()) {
            Player tradePlayer = tradeEntity.getKey();
            PlayerOffer offerPlayer = tradeEntity.getValue();

            for (ItemStack itemStack : offerPlayer.getItemsToTrade()) {
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }
                InventoryUtil.addItem(tradePlayer, itemStack);
            }

            playerTradeCache.remove(tradePlayer);
            tradePlayer.closeInventory();
            tradePlayer.updateInventory();
        }
    }

    public void endTrade(final Trade trade) {
        if (!playerTradeCache.containsValue(trade))
            return;

        for (Map.Entry<Player, PlayerOffer> tradeEntity : trade.getPlayerOfferCache().entrySet()) {
            final Player tradePlayer = tradeEntity.getKey();

            playerTradeCache.remove(tradePlayer);
            tradePlayer.closeInventory();
            tradePlayer.updateInventory();
            tradePlayer.playSound(tradePlayer.getLocation(), Sound.NOTE_PLING, 1, 1);

            messageUtil.sendMessage(tradePlayer, "Der Trade wurde §a§oerfolgreich§7 abgeschlossen§8.");
        }
    }

    public void shutdown() {
        for (Map.Entry<Player, Trade> tradeEntry : playerTradeCache.entrySet()) {
            Trade trade = tradeEntry.getValue();
            for (Player tradePlayer : trade.getPlayerOfferCache().keySet()) {
                tradePlayer.sendMessage("§cDer Handel wurde abgebrochen.");
            }
            closeTrade(trade);
        }
    }

    public boolean hasTrade(final Player player) {
        return playerTradeCache.containsKey(player);
    }

    public Trade getPlayerTrade(final Player player) {
        return playerTradeCache.get(player);
    }

}
