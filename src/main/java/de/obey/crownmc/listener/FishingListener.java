package de.obey.crownmc.listener;

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.LuckyFishingHandler;
import de.obey.crownmc.objects.luckyfishing.RodLevel;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@NonNull
public class FishingListener implements Listener {

    private final MessageUtil messageUtil;
    private final LuckyFishingHandler luckyFishingHandler;
    private final Map<Player, Long> fishBiteTimes = new HashMap<>();

    @EventHandler
    public void handlePlayerFishEvent(final PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equalsIgnoreCase("islands")) return;
        ItemStack itemStack = player.getItemInHand();
        if (itemStack == null || luckyFishingHandler.isFishingRod(itemStack)) return;
        RodLevel level = luckyFishingHandler.getRodLevel(itemStack);
        if (event.getState() == PlayerFishEvent.State.FISHING) {
            long currentTime = System.currentTimeMillis();
            long biteTime = currentTime + MathUtil.getRandom(3000, 30000);
            fishBiteTimes.put(player, biteTime);
            event.getHook().setCustomNameVisible(true);
            long seconds = Long.parseLong(MathUtil.getSecondsFromMillis((biteTime - currentTime)));
            event.getHook().setCustomName("§7Beißt an in§8: §e§o" + seconds + "s");
            new BukkitRunnable() {
                @Override
                public void run() {
                    long seconds = Long.parseLong(MathUtil.getSecondsFromMillis((biteTime - currentTime)));
                    event.getHook().setCustomName("§7Beißt an in§8: §e§o" + seconds + "s");
                    if (fishBiteTimes.containsKey(player) && fishBiteTimes.get(player) == biteTime) {
                        event.getHook().setCustomName("§7Beißt an in§8: §e§o§ljetzt");
                        handleFishBiting(player, event.getHook());
                        this.cancel();
                    }
                }
            }.runTaskTimer(CrownMain.getInstance(), 0L, 20L);
        }
    }

    public static final ItemStack FISH_LEVEL_ONE = new ItemBuilder(Material.RAW_FISH, 1, (byte) 0).setDisplayname("§7Roher Fisch").build();
    public static final ItemStack FISH_LEVEL_TWO = new ItemBuilder(Material.RAW_FISH, 1, (byte) 1).setDisplayname("§eRoher Lachs").build();
    public static final ItemStack FISH_LEVEL_THREE = new ItemBuilder(Material.RAW_FISH, 1, (byte) 2).setDisplayname("§cNemo").build();
    public static final ItemStack FISH_LEVEL_FOUR = new ItemBuilder(Material.RAW_FISH, 1, (byte) 3).setDisplayname("§5Pufferfisch").build();


    @EventHandler
    public void handlePlayerInteractEvent(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equalsIgnoreCase("islands")) return;
        ItemStack itemStack = player.getItemInHand();
        if (itemStack == null || luckyFishingHandler.isFishingRod(itemStack)) return;
        RodLevel level = luckyFishingHandler.getRodLevel(itemStack);
        if (fishBiteTimes.containsKey(player)) {
            task.cancel();
            fishBiteTimes.remove(player);
            int random = Math.toIntExact(MathUtil.getRandom(1, 100));
            messageUtil.sendMessage(player, "§7Du hast durch das Fischen folgendes erhalten§8:");
            switch (level) {
                default:
                case ZERO:
                    if (random >= level.getFishRate()) {
                        int howMany = Math.toIntExact(MathUtil.getRandom(1, 5));
                        messageUtil.sendMessage(player, "§8- §e§o" + howMany + "§8§ox §7Fische");
                        for (int i = 0; i < howMany; i++) {
                            int randomTwo = Math.toIntExact(MathUtil.getRandom(1, 100));
                            if (randomTwo <= 80) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_ONE).amount(howMany).build());
                            }
                            if (randomTwo > 80) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_TWO).amount(howMany).build());
                            }
                        }
                    }
                    random = Math.toIntExact(MathUtil.getRandom(1, 100));
                    if (random >= level.getItemRate()) {
                        int howMany = Math.toIntExact(MathUtil.getRandom(1, 5));
                        messageUtil.sendMessage(player, "§8- §e§o" + howMany + "§8§ox §7Besondere Items");
                        //TODO: Items adden
                    }
                    break;
                case ONE:
                    if (random >= level.getFishRate()) {
                        int howMany = Math.toIntExact(MathUtil.getRandom(2, 5));
                        messageUtil.sendMessage(player, "§8- §e§o" + howMany + "§8§ox §7Fische");
                        for (int i = 0; i < howMany; i++) {
                            int randomTwo = Math.toIntExact(MathUtil.getRandom(1, 100));
                            if (randomTwo <= 60) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_ONE).amount(howMany).build());
                            }
                            if (randomTwo > 60 && randomTwo <= 90) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_TWO).amount(howMany).build());
                            }
                            if (randomTwo > 90) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_THREE).amount(howMany).build());
                            }
                        }
                    }
                    random = Math.toIntExact(MathUtil.getRandom(1, 100));
                    if (random >= level.getItemRate()) {
                        int howMany = Math.toIntExact(MathUtil.getRandom(2, 5));
                        messageUtil.sendMessage(player, "§8- §e§o" + howMany + "§8§ox §7Besondere Items");
                        //TODO: Items adden
                    }
                    break;
                case TWO:
                    if (random >= level.getFishRate()) {
                        int howMany = Math.toIntExact(MathUtil.getRandom(3, 5));
                        messageUtil.sendMessage(player, "§8- §e§o" + howMany + "§8§ox §7Fische");
                        for (int i = 0; i < howMany; i++) {
                            int randomTwo = Math.toIntExact(MathUtil.getRandom(1, 100));
                            if (randomTwo <= 60) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_ONE).amount(howMany).build());
                            }
                            if (randomTwo > 60 && randomTwo <= 90) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_TWO).amount(howMany).build());
                            }
                            if (randomTwo > 90) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_THREE).amount(howMany).build());
                            }
                        }
                    }
                    random = Math.toIntExact(MathUtil.getRandom(1, 100));
                    if (random >= level.getItemRate()) {
                        int howMany = Math.toIntExact(MathUtil.getRandom(3, 5));
                        messageUtil.sendMessage(player, "§8- §e§o" + howMany + "§8§ox §7Besondere Items");
                        //TODO: Items adden
                    }
                    break;
                case THREE:
                    if (random >= level.getFishRate()) {
                        int howMany = Math.toIntExact(MathUtil.getRandom(4, 5));
                        messageUtil.sendMessage(player, "§8- §e§o" + howMany + "§8§ox §7Fische");
                        for (int i = 0; i < howMany; i++) {
                            int randomTwo = Math.toIntExact(MathUtil.getRandom(1, 100));
                            if (randomTwo <= 60) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_TWO).amount(howMany).build());
                            }
                            if (randomTwo > 60 && randomTwo <= 90) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_THREE).amount(howMany).build());
                            }
                            if (randomTwo > 90) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_FOUR).amount(howMany).build());
                            }
                        }
                    }
                    random = Math.toIntExact(MathUtil.getRandom(1, 100));
                    if (random >= level.getItemRate()) {
                        int howMany = Math.toIntExact(MathUtil.getRandom(4, 5));
                        messageUtil.sendMessage(player, "§8- §e§o" + howMany + "§8§ox §7Besondere Items");
                        //TODO: Items adden
                    }
                    break;
                case FOUR:
                    if (random >= level.getFishRate()) {
                        int howMany = Math.toIntExact(MathUtil.getRandom(5, 10));
                        messageUtil.sendMessage(player, "§8- §e§o" + howMany + "§8§ox §7Fische");
                        for (int i = 0; i < howMany; i++) {
                            int randomTwo = Math.toIntExact(MathUtil.getRandom(1, 100));
                            if (randomTwo <= 50) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_TWO).amount(howMany).build());
                            }
                            if (randomTwo > 50 && randomTwo <= 80) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_THREE).amount(howMany).build());
                            }
                            if (randomTwo > 80) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_FOUR).amount(howMany).build());
                            }
                        }
                    }
                    random = Math.toIntExact(MathUtil.getRandom(1, 100));
                    if (random >= level.getItemRate()) {
                        int howMany = Math.toIntExact(MathUtil.getRandom(5, 10));
                        messageUtil.sendMessage(player, "§8- §e§o" + howMany + "§8§ox §7Besondere Items");
                        //TODO: Items adden
                    }
                    break;
                case FIVE:
                    if (random >= level.getFishRate()) {
                        int howMany = Math.toIntExact(MathUtil.getRandom(7, 10));
                        messageUtil.sendMessage(player, "§8- §e§o" + howMany + "§8§ox §7Fische");
                        for (int i = 0; i < howMany; i++) {
                            int randomTwo = Math.toIntExact(MathUtil.getRandom(1, 100));
                            if (randomTwo <= 45) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_TWO).amount(howMany).build());
                            }
                            if (randomTwo > 45 && randomTwo <= 70) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_THREE).amount(howMany).build());
                            }
                            if (randomTwo > 70) {
                                player.getInventory().addItem(new ItemBuilder(FISH_LEVEL_FOUR).amount(howMany).build());
                            }
                        }
                    }
                    random = Math.toIntExact(MathUtil.getRandom(1, 100));
                    if (random >= level.getItemRate()) {
                        int howMany = Math.toIntExact(MathUtil.getRandom(7, 10));
                        messageUtil.sendMessage(player, "§8- §e§o" + howMany + "§8§ox §7Besondere Items");
                        //TODO: Items adden
                    }
                    break;
            }
        }
    }



    BukkitTask task;

    private void handleFishBiting(Player player, FishHook hook) {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                fishBiteTimes.remove(player);
                messageUtil.sendMessage(player, "Du hast zu lange gebraucht.");
                hook.setCustomNameVisible(false);
                hook.setCustomName(null);
                hook.remove();
            }
        }.runTaskLater(CrownMain.getInstance(), 20*3L);
    }

}
