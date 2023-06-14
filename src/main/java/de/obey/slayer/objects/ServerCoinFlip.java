package de.obey.slayer.objects;
/*

    Author - Obey -> SkySlayer-v4
       05.12.2022 / 10:04

*/

import de.obey.slayer.SlayerMain;
import de.obey.slayer.backend.enums.DataType;
import de.obey.slayer.util.InventoryUtil;
import de.obey.slayer.util.ItemBuilder;
import de.obey.slayer.util.MessageUtil;
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
import java.util.UUID;

@Getter
public final class ServerCoinFlip {

    private final OfflinePlayer player;

    private int state = 0;
    private final long amount;

    private Inventory inventory;
    private BukkitTask runnable;

    public ServerCoinFlip(final Player player, final long amount) {
        this.player = player;
        this.amount = amount;

        start();
    }

    public long getWinAmount() {
        return  amount*2;
    }

    public void start() {
        if (state != 0)
            return;

        inventory = Bukkit.createInventory(null, 9 * 6, "§6§lCF §7" + player.getName() + " §8vs §4§lSERVER");

        InventoryUtil.fillSideRows(inventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());

        state = 1;

        if (player.isOnline()) {
            player.getPlayer().openInventory(inventory);
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CHEST_OPEN, 1, 1);
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

        final ItemStack skull1 = new ItemBuilder(Material.SKULL_ITEM, 2, (byte) 3)
                .setDisplayname("§7Kopf von§8:§6§l " + player.getName())
                .setSkullOwner(player.getName())
                .build();

        final ItemStack skull2 = new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("MzRkYmJjZGRjYzQ0ODE2MGI2OWMwYzdiYWQ2M2VkODIzNjExNmE0ODQ5MTJmYzdjODcxNWM4N2I0Mzc0NTZmIn19fQ==", UUID.randomUUID())
                .setDisplayname("§4§lSERVER")
                .build();

        inventory.setItem(12, skull2);
        inventory.setItem(13, skull1);
        inventory.setItem(14, skull2);

        inventory.setItem(20, skull2);
        inventory.setItem(29, skull2);
        inventory.setItem(38, skull2);

        inventory.setItem(24, skull2);
        inventory.setItem(33, skull2);
        inventory.setItem(42, skull2);

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

                ticks++;
            }
        }.runTaskTimerAsynchronously(SlayerMain.getInstance(), 20, 10);
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

                    if (player.isOnline()) {
                        if (player.getPlayer().getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(inventory.getTitle()))
                            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 1, 1);
                    }
                }

                tempTicks++;
                ticks++;
            }
        }.runTaskTimerAsynchronously(SlayerMain.getInstance(), 1, 1);
    }

    public void endCoinFlip() {
        if (state == 3)
            return;

        final ItemStack winItem = inventory.getItem(13);
        final OfflinePlayer winner = winItem.getItemMeta().getDisplayName().equalsIgnoreCase("§4§lSERVER") ? null : player;

        state = 3;

        runnable = new BukkitRunnable() {
            @Override
            public void run() {

                final MessageUtil messageUtil = SlayerMain.getInstance().getInitializer().getMessageUtil();

                if(winner == player) { // player won

                    if(player.isOnline()) {
                        messageUtil.sendMessage(winner.getPlayer(), "Du hast §e§o" + NumberFormat.getInstance().format(getWinAmount()) + "§6§l$ §7im CoinFlip gegen den §4§lSERVER§a§o gewonnen§8.");
                        winner.getPlayer().playSound(winner.getPlayer().getLocation(), Sound.LEVEL_UP, 0.5f, 1);
                        player.getPlayer().closeInventory();
                    }

                    SlayerMain.getInstance().getInitializer().getUserHandler().getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                        user.addLong(DataType.MONEY, getWinAmount());
                    });
                } else { // server won
                    if(player.isOnline()) {
                        messageUtil.sendMessage(player.getPlayer(), "Du hast §e§o" + NumberFormat.getInstance().format(getAmount()) + "§6§l$ §7im CoinFlip gegen den §4§lSERVER§c§o verloren§8.");
                        player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.EXPLODE, 0.5f, 1);
                        player.getPlayer().closeInventory();
                    }

                    SlayerMain.getInstance().getInitializer().getDailyPotHandler().addMoney(getAmount()/2);
                }
            }
        }.runTaskLater(SlayerMain.getInstance(), 40);
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
    }

}
