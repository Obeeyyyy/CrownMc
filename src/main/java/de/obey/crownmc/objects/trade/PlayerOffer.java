package de.obey.crownmc.objects.trade;
/*

    Author - EntixOG -> SkySlayer-v4
       04.11.2022 / 15:53

*/

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class PlayerOffer {

    @NonNull Player player;
    @NonNull Trade trade;
    boolean ready = false;
    @Setter
    long coins = 0;
    final List<ItemStack> itemsToTrade = new ArrayList<>(12);

    public void updateStatus(boolean ready) {
        this.ready = ready;
        this.trade.updateInventory(player);
        this.trade.checkHandel(player);
    }

    public void addItem(ItemStack itemStack) {
        if (this.itemsToTrade.size() >= 12) {
            return;
        }

        this.itemsToTrade.add(itemStack);
        this.trade.updateInventory(player);
        this.trade.handelOfferHasChange(player);
    }

    public void removeItem(int itemIndex) {
        if (this.itemsToTrade.get(itemIndex) == null) {
            return;
        }
        this.itemsToTrade.remove(itemIndex);
        this.trade.updateInventory(player);
        this.trade.handelOfferHasChange(player);
    }

}
