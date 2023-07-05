package de.obey.crownmc.objects.gambling;
/*

    Author - Obey -> CrownMc
       30.06.2023 / 00:52

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.DailyPotHandler;
import de.obey.crownmc.handler.JackPotHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class JackPot {

    private final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
    private final JackPotHandler jackPotHandler = CrownMain.getInstance().getInitializer().getJackPotHandler();
    private final UserHandler userHandler = CrownMain.getInstance().getInitializer().getUserHandler();
    private final DailyPotHandler dailyPotHandler = CrownMain.getInstance().getInitializer().getDailyPotHandler();

    @Getter @Setter
    private int state = 1, ticks = 0; //  1 = aus, 2 = spinning

    @Getter
    private Player owner;

    @Getter
    private final ArrayList<Player> teilnehmer = new ArrayList<>();

    @Getter
    private long pot = 0, einsatz;

    @Getter
    private Inventory inventory;

    @Getter
    private BukkitTask runnable;

    public JackPot(final Player player, final long einsatz) {
        this.owner = player;
        this.einsatz = einsatz;

        messageUtil.broadcast(player.getName() + " hat einen §6§lJackpot§7 gestartet§8,§7 nutze §8/§7Jackpot um mit zu spielen§8.");

        inventory = Bukkit.createInventory(null, 9*4, "§6§lJackpot §7von§e " + owner.getName());

        InventoryUtil.fill(inventory, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 8).setDisplayname("§7-§8/§7-").build());
        InventoryUtil.fillSideRows(inventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());
        updateInventory();
        startJackpot();
    }

    private void message(final String message) {
        for (Player t : teilnehmer) {
            messageUtil.sendMessage(t, "§8( §6§lJackpot §8)§7 " + message);
        }
    }

    public boolean joinPlayer(final Player player) {
        if(state != 1) {
            messageUtil.sendMessage(player, "Der Jackpot hat bereits angefangen§8.");
            return false;
        }

        if (teilnehmer.contains(player)) {
            messageUtil.sendMessage(player, "Du nimmst bereits am Jackpot teil§8.");
            return false;
        }

        teilnehmer.add(player);
        pot += einsatz;

        message(player.getName() + " hat den Pot betreten§8. §a+§f§o" + messageUtil.formatLong(einsatz) + "§6§l$");
        updateInventory();

        return true;
    }

    private void startJackpot() {
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                ticks++;

                if(ticks < 40) {
                    updateInventory();

                    if(ticks >= 35) {
                        message("§7Die Auslosung startet in §f" + (40 - ticks) + "s§8.");
                    }

                    return;
                }


                state = 2;
                startAnimation();
                cancel();
            }
        }.runTaskTimer(CrownMain.getInstance(), 20, 20);
    }

    private void startAnimation() {

        if(teilnehmer.size() <= 1) {
            message("Der Jackpot wurde abgebrochen§8, §7nicht genug Teilnehmer§8.");
            jackPotHandler.shutdown();
            return;
        }

        inventory.clear();

        inventory = Bukkit.createInventory(null, 9*6, "§7Auslosung ...");

        inventory.setItem(31, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 1)
                        .setDisplayname("§6§lGewinner")
                .build());

        inventory.setItem(49, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 1)
                .setDisplayname("§6§lGewinner")
                .build());

        for (Player he : teilnehmer) {
            he.closeInventory();
            he.openInventory(inventory);
        }

        runnable = new BukkitRunnable() {

            int ticks, delay = 10, delayTick, stage = 0;

            @Override
            public void run() {

                if(delayTick < delay) {
                    delayTick++;
                    return;
                }

                inventory.setItem(1, inventory.getItem(10));
                inventory.setItem(10, inventory.getItem(19));
                inventory.setItem(19, inventory.getItem(29));
                inventory.setItem(29, inventory.getItem(39));
                inventory.setItem(39, inventory.getItem(40));
                inventory.setItem(40, inventory.getItem(41));
                inventory.setItem(41, inventory.getItem(33));
                inventory.setItem(33, inventory.getItem(25));
                inventory.setItem(25, inventory.getItem(16));
                inventory.setItem(16, inventory.getItem(7));
                inventory.setItem(7, getRandomHead());

                if(stage == 0) {
                    for (HumanEntity viewer : inventory.getViewers())
                        ((Player) viewer).playSound(viewer.getLocation(), Sound.NOTE_PIANO, 1, 1);

                    if (ticks == 11) {
                        delay = 0;
                        stage = 1;
                    }
                } else if(stage == 1) {
                    for (HumanEntity viewer : inventory.getViewers()) {
                        ((Player) viewer).playSound(viewer.getLocation(), Sound.CLICK, 1, 0.2f);
                        ((Player) viewer).updateInventory();
                    }

                    switch (ticks) {
                        case 20 : delay = 3; break;
                        case 30 : delay = 7; break;
                        case 50 : delay = 15; break;
                        case 55 : delay = 22; break;
                        case 65 : end(); break;
                    }

                }

                delayTick = 0;
                ticks++;

            }
        }.runTaskTimerAsynchronously(CrownMain.getInstance(), 1, 1);
    }

    private void end() {
        runnable.cancel();

        for (Player player : teilnehmer)
            player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1, 0.2f);

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                final Player winner = Bukkit.getPlayer(ChatColor.stripColor(inventory.getItem(40).getItemMeta().getDisplayName()));

                if(winner == null) {
                    jackPotHandler.shutdown();
                    return;
                }

                final long winAmount = 90 * pot / 100;
                final long dpAmount = pot - winAmount;

                message(winner.getName() + " hat §f§o" + messageUtil.formatLong(winAmount) + "§6§l$§7 gewonnen§8!");
                message("§f§o" + messageUtil.formatLong(dpAmount) + "§6§l$§7 gehen in den §9DailyPot§8.");

                userHandler.getUser(winner.getUniqueId()).thenAcceptAsync(user -> user.addLong(DataType.MONEY, winAmount));
                dailyPotHandler.addMoney(dpAmount);

                jackPotHandler.endJackpot();
            }
        }.runTaskLater(CrownMain.getInstance(), 40);
    }

    private ItemStack getRandomHead() {
        final Player player = teilnehmer.get(new Random().nextInt(teilnehmer.size()));

        return new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setDisplayname("§e" + player.getName())
                .setSkullOwner(player.getName())
                .build();
    }

    public void updateInventory() {
        if(state == 1) {
            inventory.setItem(13, new ItemBuilder(Material.ITEM_FRAME)
                    .setDisplayname("§6§lJackpot §7Informationen")
                    .setLore("§7",
                            "§8▰§7▱ §e§l" + owner.getName(),
                            "§8  - §7Teilnehmer§8:§f " + teilnehmer.size(),
                            "§8  - §7Im Pot§8:§f " + messageUtil.formatLong(pot) + "§6§l$",
                            "§8  - §7Einsatz§8:§f " + messageUtil.formatLong(einsatz) + "§6§l$",
                            "§8  - §7Start in§8: §f" + (40 - ticks) + "s",
                            "")
                    .build());

            inventory.setItem(20, new ItemBuilder(Material.SKULL_ITEM,1,  (byte) 3)
                            .setDisplayname("§8» §7Beitreten")
                            .setLore("",
                                    "§8▰§7▱ §6§lLinksklick",
                                    "§8  - §7Trete dem Jackpot bei§8.",
                                    "")
                            .setTextur("ZTFlZGYxNmM0MWQxOTRjNzMxZTMzZmRkOWMyYjllNWVkZDQ1MGJjMzNjYTcwNDM2NTI4YTA1Mzg5ZDdmY2RhMiJ9fX0=", UUID.randomUUID())
                    .build());
        }
    }

}
