package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       05.12.2022 / 09:01

*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.objects.CoinFlip;
import de.obey.crownmc.objects.ServerCoinFlip;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public final class CoinflipHandler {

    @NonNull
    private final MessageUtil messageUtil;

    @NonNull
    private final UserHandler userHandler;

    @Getter
    private final HashMap<UUID, CoinFlip> coinflips = new HashMap<>();

    @Getter
    private final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§6§lCoinFlips");

    public CoinFlip getCoinflipFromPlayer(final Player player) {
        return coinflips.get(player.getUniqueId());
    }

    public void shutdown() {
        if (coinflips.isEmpty())
            return;

        coinflips.values().forEach(coinFlip -> {
            if (coinFlip.getRunnable() != null)
                coinFlip.getRunnable().cancel();

            if (coinFlip.getPlayer() != null)
                userHandler.getUserInstant(coinFlip.getPlayer().getUniqueId()).addLong(DataType.MONEY, coinFlip.getAmount());

            if (coinFlip.getOpponent() != null)
                userHandler.getUserInstant(coinFlip.getOpponent().getUniqueId()).addLong(DataType.MONEY, coinFlip.getAmount());
        });
    }

    @Getter
    private final ArrayList<Player> creatingServerCoinflip = new ArrayList<>();

    @Getter
    private final ArrayList<Player> creatingCoinflip = new ArrayList<>();

    public boolean isCreatingCoinflip(final Player player, final String text) {

        if (creatingCoinflip.contains(player)) {

            if (text.equalsIgnoreCase("cancel")) {
                creatingCoinflip.remove(player);
                messageUtil.sendMessage(player, "Vorgang wurde abgebrochen§8.");
                return true;
            }

            long amount = 0L;

            try {
                amount = Long.parseLong(text);

                if (amount < 100) {
                    messageUtil.sendMessage(player, "Der Wetteinsatz muss mind. 100$ hoch sein§8.");
                    return true;
                }

                createCoinFlip(player, amount);

            } catch (final NumberFormatException exception) {

                amount = MathUtil.getLongFromStringwithSuffix(text);

                if (amount <= 0) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen§8. (§7k, m, mrd, b, brd, t§8)");
                    messageUtil.sendMessage(player, "Um abzubrechen schreibe §c§ocancel§8.");
                    return true;
                }

                if (amount < 100) {
                    messageUtil.sendMessage(player, "Der Wetteinsatz muss mind. 100$ hoch sein§8.");
                    return true;
                }

                createCoinFlip(player, amount);
            }

            return true;
        }

        return false;
    }

    public boolean isCreatingServerCoinflip(final Player player, final String text) {

        if (creatingServerCoinflip.contains(player)) {

            if (text.equalsIgnoreCase("cancel")) {
                creatingCoinflip.remove(player);
                messageUtil.sendMessage(player, "Vorgang wurde abgebrochen§8.");
                return true;
            }

            long amount = 0L;

            try {
                amount = Long.parseLong(text);

                if (amount < 100) {
                    messageUtil.sendMessage(player, "Der Wetteinsatz muss mind. 100$ hoch sein§8.");
                    return true;
                }

                if(amount > 100000000) {
                    messageUtil.sendMessage(player, "Der Wetteinsatz darf nicht höher als 100M sein§8.");
                    return true;
                }

            } catch (final NumberFormatException exception) {

                amount = MathUtil.getLongFromStringwithSuffix(text);

                if (amount <= 0) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen§8. (§7k, m, mrd, b, brd, t§8)");
                    messageUtil.sendMessage(player, "Um abzubrechen schreibe §c§ocancel§8.");
                    return true;
                }

                if (amount < 100) {
                    messageUtil.sendMessage(player, "Der Wetteinsatz muss mind. 100$ hoch sein§8.");
                    return true;
                }

                if(amount > 100000000) {
                    messageUtil.sendMessage(player, "Der Wetteinsatz darf nicht höher als 100M sein§8.");
                    return true;
                }
            }

            final long finalAmount = amount;
            userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                if(!messageUtil.hasEnougthMoney(user, finalAmount))
                    return;

                user.removeLong(DataType.MONEY, finalAmount);
                new ServerCoinFlip(player, finalAmount);
                creatingServerCoinflip.remove(player);
            });

            return true;
        }

        return false;
    }

    public boolean isInCoinFlip(final Player player) {
        return coinflips.containsKey(player.getUniqueId());
    }

    public void updateInventory() {
        InventoryUtil.fillSideRows(inventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());

        inventory.setItem(9, new ItemBuilder(Material.ITEM_FRAME)
                .setDisplayname("§6§lInformation")
                .setLore("",
                        "§8▰§7▱ §6Linksklick",
                        "§8 - §7Erstelle einen Coinflip§8.",
                        "",
                        "§8▰§7▱ §6Wie lösche ich meinen Coinflip ?",
                        "§8 - §7Klicke auf deinen Coinflip um ihn zu §c§oschließen§8.",
                        "")
                .build());

        inventory.setItem(36, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("MzRkYmJjZGRjYzQ0ODE2MGI2OWMwYzdiYWQ2M2VkODIzNjExNmE0ODQ5MTJmYzdjODcxNWM4N2I0Mzc0NTZmIn19fQ==", UUID.randomUUID())
                .setDisplayname("§6§lServer Coinflip")
                .setLore("",
                        "§8▰§7▱ §6Linksklick",
                        "§8 - §7Spiele einen Coinflip gegen den Server§8.",
                        "")
                .build());

        if (coinflips.isEmpty())
            return;

        coinflips.values().forEach(coinflip -> {
            inventory.setItem(coinflip.getId(), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setSkullOwner(coinflip.getPlayer().getName())
                    .setDisplayname("§7CoinFlip von§6§l " + coinflip.getPlayer().getName())
                    .setLore("",
                            "§8▰§7▱ §6§lInformationen",
                            "§8 - §7Betrag§8: §e§o" + messageUtil.formatLong(coinflip.getAmount()) + "§6§l$",
                            "§8 - §7Status§8: §7" + (coinflip.getState() == 0 ? "§a§oOFFEN" : (coinflip.getState() < 3 ? "§e§oLÄUFT" : "§c§oBEENDET")),
                            "",
                            "§8▰§7▱ §6§lLinksklick",
                            "§8 - §7" + (coinflip.getState() == 0 ? "Beitreten§8." : "Zuschauen§8."),
                            "")
                    .build());
        });

    }

    public void removeCoinflip(final CoinFlip coinFlip) {
        coinflips.remove(coinFlip.getPlayer().getUniqueId());
        inventory.setItem(coinFlip.getId(), null);
        userHandler.getUserInstant(coinFlip.getPlayer().getUniqueId()).addLong(DataType.MONEY, coinFlip.getAmount());
    }

    public void endCoinFlip(final CoinFlip coinFlip, final OfflinePlayer winner) {
        coinflips.remove(coinFlip.getPlayer().getUniqueId());
        coinflips.remove(coinFlip.getOpponent().getUniqueId());
        inventory.setItem(coinFlip.getId(), null);
        userHandler.getUserInstant(winner.getUniqueId()).addLong(DataType.MONEY, coinFlip.getWinAmount());
    }

    public void createCoinFlip(final Player player, final long amount) {
        if (coinflips.containsKey(player.getUniqueId())) {
            messageUtil.sendMessage(player, "Du bist bereits in einem CoinFlip§8.");
            return;
        }

        final User user = userHandler.getUserInstant(player.getUniqueId());

        if (!messageUtil.hasEnougthMoney(user, amount))
            return;

        creatingCoinflip.remove(player);
        coinflips.put(player.getUniqueId(), new CoinFlip(player, amount, inventory.firstEmpty()));
        user.removeLong(DataType.MONEY, amount);

        updateInventory();

        messageUtil.sendMessage(player, "Dein CoinFlip wurde erstellt§8. (§e§o" + messageUtil.formatLong(amount) + "§6§l$§8)");
    }

    public void joinCoinflip(final Player player, final CoinFlip coinFlip) {
        if (isInCoinFlip(player)) {
            messageUtil.sendMessage(player, "Du bist bereits in einem CoinFlip§8.");
            return;
        }

        if (coinFlip == null)
            return;

        if (coinFlip.getState() == 0) {

            final User user = userHandler.getUserInstant(player.getUniqueId());

            if (!messageUtil.hasEnougthMoney(user, coinFlip.getAmount()))
                return;

            user.removeLong(DataType.MONEY, coinFlip.getAmount());
        }

        coinFlip.join(player);
    }

    public CoinFlip getCoinflipFromPressedItem(final ItemStack item) {
        final String ownerName = item.getItemMeta().getDisplayName().split(" ")[2];

        if(!coinflips.containsKey(Bukkit.getPlayer(ownerName).getUniqueId())) {
            item.setType(Material.AIR);
            return null;
        }

        return coinflips.get(Bukkit.getPlayer(ownerName).getUniqueId());
    }

}
