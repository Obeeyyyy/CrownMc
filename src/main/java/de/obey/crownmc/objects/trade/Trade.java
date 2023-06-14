package de.obey.crownmc.objects.trade;
/*

    Author - EntixOG -> SkySlayer-v4
       04.11.2022 / 15:52

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
public final class Trade {

    private final Initializer initializer = CrownMain.getInstance().getInitializer();

    public final List<Integer> own_Slots = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38, 39);
    public final List<Integer> target_Slots = Arrays.asList(14, 15, 16, 23, 24, 25, 32, 33, 34, 41, 42, 43);
    public final List<Integer> ironFence_Slots = Arrays.asList(13, 22, 31, 40);

    private final HashMap<Player, PlayerOffer> playerOfferCache = new HashMap<>(2);
    private final HashMap<Player, Inventory> tradeInventory = new HashMap<>(2);

    private final Player target;

    public Trade(Player player, Player target) {
        playerOfferCache.put(player, new PlayerOffer(player, this));
        playerOfferCache.put(target, new PlayerOffer(target, this));

        tradeInventory.put(player, createInventory(player));
        tradeInventory.put(target, createInventory(target));

        this.target = target;

        updateInventory(player);
        updateInventory(target);
    }

    public PlayerOffer findOfferByPlayer(final Player player) {
        return this.playerOfferCache.getOrDefault(player, null);
    }

    public Player findOppositeByPlayer(final Player player) {
        return this.playerOfferCache.keySet().stream().filter(searchedTarget -> !searchedTarget.equals(player)).findFirst().orElse(null);
    }

    public Inventory getTradeInventory(Player player) {
        return this.tradeInventory.getOrDefault(player, null);
    }

    public int getItemIndexBySlot(int slot) {
        for (int searchedSlot = 0; searchedSlot < own_Slots.size(); searchedSlot++) {
            int currentSlot = own_Slots.get(searchedSlot);
            if (currentSlot == slot) {
                return searchedSlot;
            }
        }
        return -1;
    }

    public Inventory createInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Trade mit " + findOppositeByPlayer(player).getName());

        for (int slot = 0; slot < inventory.getSize(); slot++)
            inventory.setItem(slot, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setDisplayname(" ").build());

        ironFence_Slots.forEach(ironFence_Slots -> inventory.setItem(ironFence_Slots, new ItemBuilder(Material.IRON_FENCE).setDisplayname(" ").build()));

        inventory.setItem(4, new ItemBuilder(Material.REDSTONE).setDisplayname("§8►§4► §c§lAbbrechen").setLore("§7§oKlicke, um den Trade zu beenden.").build());

        own_Slots.forEach(playerSlot -> inventory.setItem(playerSlot, new ItemBuilder(Material.AIR).build()));
        target_Slots.forEach(playerSlot -> inventory.setItem(playerSlot, new ItemBuilder(Material.AIR).build()));

        if (player.getItemOnCursor() != null && player.getItemOnCursor().getType() != Material.AIR) {
            InventoryUtil.addItem(player, player.getItemOnCursor());
            player.setItemOnCursor(null);
        }

        player.openInventory(inventory);
        return inventory;
    }

    public void updateInventory(Player player) {
        if (!tradeInventory.containsKey(player)) {
            return;
        }

        final Inventory ownHalfInventory = getTradeInventory(player);
        final Inventory targetHalfInventory = getTradeInventory(findOppositeByPlayer(player));

        own_Slots.forEach(own_Slots -> ownHalfInventory.setItem(own_Slots, new ItemBuilder(Material.AIR).build()));
        target_Slots.forEach(target_Slots -> targetHalfInventory.setItem(target_Slots, new ItemBuilder(Material.AIR).build()));

        final PlayerOffer playerOffer = findOfferByPlayer(player);

        for (int slot = 0; slot < playerOffer.getItemsToTrade().size(); slot++) {
            ownHalfInventory.setItem(own_Slots.get(slot), playerOffer.getItemsToTrade().get(slot));
            targetHalfInventory.setItem(target_Slots.get(slot), playerOffer.getItemsToTrade().get(slot));
        }

        if (playerOffer.getCoins() <= 0) {
            ownHalfInventory.setItem(0, new ItemBuilder(Material.PAPER)
                    .setDisplayname("§8►§e► §6§lCoin hinzufügen")
                    .setLore(" §7§oKlicke, um Coins zum Trade hinzuzufügen.")
                    .build());
        } else {
            ownHalfInventory.setItem(0, new ItemBuilder(Material.PAPER)
                    .setDisplayname("§8►§e► §6§lCoins: " + initializer.getMessageUtil().formatLong(playerOffer.getCoins()))
                    .setLore(" §7§oKlicke, um den Betrag zu ändern.", "", " §8►§4► §c§lINFO: §e§oGebe 0 an, um den Betrag zurückzuziehen.")
                    .addEnchantment(Enchantment.ARROW_DAMAGE)
                    .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    .build());
        }

        if (playerOffer.getCoins() <= 0) {
            targetHalfInventory.setItem(8, new ItemBuilder(Material.PAPER)
                    .setDisplayname("§8►§4► §cKeine Coins enthalten")
                    .setLore(" §7§oAktuell wurden keine Coins hinzuzufügen.")
                    .build());
        } else {
            targetHalfInventory.setItem(8, new ItemBuilder(Material.PAPER)
                    .setDisplayname("§8►§e► §6§lCoins: " + initializer.getMessageUtil().formatLong(playerOffer.getCoins()))
                    .addEnchantment(Enchantment.ARROW_DAMAGE)
                    .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    .build());
        }


        if (!(playerOffer.isReady())) {
            ownHalfInventory.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 5)
                    .setDisplayname("§8►§2► §aBestätigen")
                    .setLore("§7§oKlicke, um deinen Inhalt zu bestätigen.")
                    .build());

            targetHalfInventory.setItem(7, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 14)
                    .setDisplayname("§8►§6► §eWarte auf Bestätigung")
                    .setLore("§7§oDer Spieler muss seinen Inhalt noch", "§7§obestätigen, um den Handel abzuschließen.")
                    .build());
        } else {
            ownHalfInventory.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 14)
                    .setDisplayname("§8►§c► §4Bestätigung zurücknehmen")
                    .setLore("§7§oKlicke, um deinen Inhalt anpassen zu", "§7§okönnen. Danach wird eine erneute", "§7§oBestätigung benötigt.")
                    .build());

            targetHalfInventory.setItem(7, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 5)
                    .setDisplayname("§8►§2► §aInhalt Bestätigt")
                    .setLore("§7§oDer Inhalt wurde vom Spieler bestätigt.", "§7Um den Handel abzuschließen, musst du", "§7§odeinen Inhalt auch bestätigen.")
                    .build());
        }
    }

    public void handelOfferHasChange(final Player player) {
        final Player oppositeTarget = findOppositeByPlayer(player);
        final PlayerOffer targetOffer = findOfferByPlayer(oppositeTarget);

        if (targetOffer.isReady()) {
            targetOffer.updateStatus(false);
            player.sendMessage("§a" + player.getName() + " §7hat das den Inhalt vom Trade geändert.");
        }

        final PlayerOffer playerOffer = findOfferByPlayer(player);

        if (playerOffer.isReady()) {
            playerOffer.updateStatus(false);
            player.sendMessage("§7Dein Inhalt vom Trade hat sich geändert, bestätige den Trade erneut.");
        }
    }

    public void checkHandel(final Player player) {
        final Player target = findOppositeByPlayer(player);

        final PlayerOffer playerOffer = findOfferByPlayer(player);
        final PlayerOffer targetOffer = findOfferByPlayer(target);

        final User playerUser = initializer.getUserHandler().getUserInstant(player.getUniqueId());
        final User targetUser = initializer.getUserHandler().getUserInstant(target.getUniqueId());

        /**
         * Hier müssen das ExampleMoney mit dem wirklichen
         * Geld vom Spieler ausgetauscht werden.
         * **/
        if (playerOffer.isReady() && targetOffer.isReady()) {
            if (playerOffer.getCoins() >= 0) {
                if (!initializer.getMessageUtil().hasEnougthMoney(playerUser, playerOffer.getCoins())) {
                    playerOffer.updateStatus(false);
                    targetOffer.updateStatus(false);

                    target.sendMessage("§a" + player.getName() + " §cmuss seine Coin Angabe ändern, da er nicht genug Geld hat§8.");
                    return;
                }
            }

            if (targetOffer.getCoins() > 0) {
                if (!initializer.getMessageUtil().hasEnougthMoney(targetUser, targetOffer.getCoins())) {
                    playerOffer.updateStatus(false);
                    targetOffer.updateStatus(false);

                    player.sendMessage("§a" + target.getName() + " §cmuss seine Coin Angabe ändern, da er nicht genug Geld hat§8.");
                    return;
                }
            }

            playerUser.removeLong(DataType.MONEY, playerOffer.getCoins());
            playerUser.addLong(DataType.MONEY, targetOffer.getCoins());

            targetUser.removeLong(DataType.MONEY, targetOffer.getCoins());
            targetUser.addLong(DataType.MONEY, playerOffer.getCoins());

            playerOfferCache.keySet().forEach(tradePlayer -> {
                final Player oppositeTarget = findOppositeByPlayer(tradePlayer);
                final PlayerOffer offerOpposite = findOfferByPlayer(oppositeTarget);
                /** Hier musst du das Geld hinzufügen und entfernen
                 * tradePlayer = add Money
                 * oppositePlayer = remove Money
                 * **/

                if (!offerOpposite.getItemsToTrade().isEmpty()) {
                    for (ItemStack itemStack : offerOpposite.getItemsToTrade()) {
                        if (itemStack == null || itemStack.getType() == Material.AIR) {
                            continue;
                        }
                        InventoryUtil.addItem(tradePlayer, itemStack);
                    }
                }

                initializer.getTradeHandler().endTrade(this);
            });
        }
    }

}
