package de.obey.crownmc.objects;
/*

    Author - Obey -> SkySlayer-v4
       05.12.2022 / 10:04

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.NumberFormat;
import java.util.Random;

@Getter
public final class CoinFlip {

    private final OfflinePlayer player;
    private OfflinePlayer opponent;

    private int state = 0;
    private final int id;
    private final long amount;

    private Inventory inventory;
    private BukkitTask runnable;

    public CoinFlip(final Player player, final long amount, final int id) {
        this.player = player;
        this.amount = amount;
        this.id = id;
    }

    public long getWinAmount() {
        return  (amount*2) *9 / 10;
    }

    public void join(final Player joined) {
        if (state != 0) {
            joined.openInventory(inventory);
            return;
        }

        inventory = Bukkit.createInventory(null, 9 * 6, "§6§lCF §7" + player.getName() + " §8vs §7" + joined.getName());

        InventoryUtil.fillSideRows(inventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());

        state = 1;
        opponent = joined;

        if (player.isOnline()) {
            player.getPlayer().openInventory(inventory);
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CHEST_OPEN, 1, 1);
        }

        if (opponent.isOnline()) {
            opponent.getPlayer().openInventory(inventory);
            opponent.getPlayer().playSound(opponent.getPlayer().getLocation(), Sound.CHEST_OPEN, 1, 1);
        }

        inventory.setItem(4, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 5)
                .setDisplayname("§a§lGEWINNER")
                .setLore("",
                        "§8▰§7▱ §a§lGewinn§8:",
                        "§8 -§7 Menge§8: §a§o" + NumberFormat.getInstance().format(getWinAmount()),
                        "")
                .build());

        inventory.setItem(22, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 5)
                .setDisplayname("§a§lGEWINNER")
                .setLore("",
                        "§8▰§7▱ §a§lGewinn§8:",
                        "§8 -§7 Menge§8: §a§o" + NumberFormat.getInstance().format(getWinAmount()),
                        "")
                .build());

        final ItemStack skull1 = new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setDisplayname("§7Kopf von§8:§6§l " + player.getName())
                .setSkullOwner(player.getName())
                .build();

        final ItemStack skull2 = new ItemBuilder(Material.SKULL_ITEM, 2, (byte) 3)
                .setDisplayname("§7Kopf von§8:§6§l " + opponent.getName())
                .setSkullOwner(opponent.getName())
                .build();

        inventory.setItem(12, skull2);
        inventory.setItem(13, skull1);
        inventory.setItem(14, skull2);

        inventory.setItem(20, skull1);
        inventory.setItem(29, skull2);
        inventory.setItem(38, skull1);

        inventory.setItem(24, skull1);
        inventory.setItem(33, skull2);
        inventory.setItem(42, skull1);

        inventory.setItem(48, skull2);
        inventory.setItem(49, skull1);
        inventory.setItem(50, skull2);

        runnable = new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (ticks == 3) {
                    cancel();
                    startSpinning();
                    return;
                }

                if (player.isOnline())
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.NOTE_PLING, 1, 1);

                if (opponent.isOnline())
                    opponent.getPlayer().playSound(opponent.getPlayer().getLocation(), Sound.NOTE_PLING, 1, 1);

                ticks++;
            }
        }.runTaskTimerAsynchronously(CrownMain.getInstance(), 10, 10);
    }

    public void startSpinning() {
        if (state != 1)
            return;

        state = 2;

        runnable = new BukkitRunnable() {
            int ticks = 0;
            int tempTicks = 0;
            int roundsSpinned = 0;
            int delay = 2;
            final int endRound = 20 + new Random().nextInt(12) + new Random().nextInt(12);

            @Override
            public void run() {
                if (roundsSpinned >= endRound) {
                    cancel();
                    endCoinFlip();
                    return;
                }

                if (tempTicks >= delay) {
                    spin();

                    tempTicks = 0;
                    roundsSpinned++;

                    if (roundsSpinned >= 15)
                        delay++;

                    if (opponent.isOnline()) {
                        if (opponent.getPlayer().getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(inventory.getTitle()))
                            opponent.getPlayer().playSound(opponent.getPlayer().getLocation(), Sound.CLICK, 1, 1);
                    }

                    if (player.isOnline()) {
                        if (player.getPlayer().getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(inventory.getTitle()))
                            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 1, 1);
                    }
                }

                tempTicks++;
                ticks++;
            }
        }.runTaskTimerAsynchronously(CrownMain.getInstance(), 1, 1);
    }

    public void endCoinFlip() {
        if (state == 3)
            return;

        final ItemStack winItem = inventory.getItem(13);
        final OfflinePlayer winner = winItem.getItemMeta().getDisplayName().split(" ")[2].equalsIgnoreCase(player.getName()) ? player : opponent;
        final OfflinePlayer looser = player == winner ? opponent : player;

        state = 3;

        runnable = new BukkitRunnable() {
            @Override
            public void run() {

                final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

                if (winner.isOnline()) {
                    winner.getPlayer().playSound(winner.getPlayer().getLocation(), Sound.LEVEL_UP, 1, 1);
                    winner.getPlayer().closeInventory();
                    messageUtil.sendMessage(winner.getPlayer(), "Du hast §e§o" + NumberFormat.getInstance().format(getWinAmount()) + "§6§l$ §7im CoinFlip gegen §6§l" + looser.getName() + "§a§o gewonnen§8.");
                }

                if (looser.isOnline()) {
                    looser.getPlayer().playSound(looser.getPlayer().getLocation(), Sound.LEVEL_UP, 1, 1);
                    looser.getPlayer().closeInventory();
                    messageUtil.sendMessage(looser.getPlayer(), "Du hast §e§o" + NumberFormat.getInstance().format(getAmount()) + "§6§l$ §7im CoinFlip gegen §6§l" + winner.getName() + "§c§o verloren§8.");
                }

                CrownMain.getInstance().getInitializer().getDailyPotHandler().addMoney(amount/10);
                CrownMain.getInstance().getInitializer().getCoinflipHandler().endCoinFlip(CoinFlip.this, winner);
            }
        }.runTaskLater(CrownMain.getInstance(), 40);
    }

    private void spin() {
        if (state != 2)
            return;

        final ItemStack winItem = inventory.getItem(13);

        inventory.setItem(13, inventory.getItem(12));
        inventory.setItem(12, inventory.getItem(20));
        inventory.setItem(20, inventory.getItem(29));
        inventory.setItem(29, inventory.getItem(38));
        inventory.setItem(38, inventory.getItem(48));
        inventory.setItem(48, inventory.getItem(49));
        inventory.setItem(49, inventory.getItem(50));
        inventory.setItem(50, inventory.getItem(42));
        inventory.setItem(42, inventory.getItem(33));
        inventory.setItem(33, inventory.getItem(24));
        inventory.setItem(24, inventory.getItem(14));
        inventory.setItem(14, winItem);

        if (player.isOnline())
            player.getPlayer().updateInventory();

        if (opponent.isOnline())
            opponent.getPlayer().updateInventory();
    }

}
