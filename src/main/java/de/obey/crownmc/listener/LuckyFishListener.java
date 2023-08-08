package de.obey.crownmc.listener;

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.LuckyFishingHandler;
import de.obey.crownmc.objects.luckyfishing.RewardLevel;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@NonNull
public class LuckyFishListener implements Listener {

    private final MessageUtil messageUtil;
    private final LuckyFishingHandler luckyFishingHandler;
    private final Map<Player, Long> fishBiteTimes = new HashMap<>();

    @EventHandler
    public void on(final PlayerFishEvent event) {
        final Player player = event.getPlayer();

        if (!player.getWorld().getName().equalsIgnoreCase("islands")) return;

        long timeToWait = MathUtil.getRandom(20000, 30000);

        final ItemStack itemStack = player.getItemInHand();

        if (itemStack.hasItemMeta()) {
            if (itemStack.getItemMeta().hasEnchant(Enchantment.LUCK)) {
                final int level = itemStack.getItemMeta().getEnchantLevel(Enchantment.LUCK);
                timeToWait -= 5000L * level;
            }
        }

        if(fishBiteTimes.containsKey(player))
            return;

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            final long biteTime = System.currentTimeMillis() + timeToWait;
            fishBiteTimes.put(player, biteTime);

            event.getHook().setCustomNameVisible(true);
            event.getHook().setCustomName("§7Noch§8: §e§o" + MathUtil.getSecondsFromMillis((biteTime - System.currentTimeMillis())));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(event.getHook() == null) {
                        fishBiteTimes.remove(player);
                        cancel();
                        return;
                    }

                    event.getHook().setCustomName("§7Noch§8: §e§o" + MathUtil.getSecondsFromMillis((biteTime - System.currentTimeMillis())));

                    if (fishBiteTimes.containsKey(player) && System.currentTimeMillis() >= fishBiteTimes.get(player)) {
                        event.getHook().setCustomName("§a§lEtwas hat angebissen!");
                        player.playSound(player.getLocation(), Sound.SPLASH, 1, 1);
                        handleFishBiting(player, event.getHook());
                        cancel();
                    }
                }
            }.runTaskTimer(CrownMain.getInstance(), 0L, 5L);
        }
    }

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        luckyFishingHandler.saveFishingRewards(event.getView());
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!player.getWorld().getName().equalsIgnoreCase("islands")) return;

        if (fishBiteTimes.containsKey(player) && System.currentTimeMillis() >= fishBiteTimes.get(player)) {

            final ItemStack itemStack = player.getItemInHand();
            int level = 1;
            if (itemStack.hasItemMeta()) {
                if (itemStack.getItemMeta().hasEnchant(Enchantment.LURE))
                    level = itemStack.getItemMeta().getEnchantLevel(Enchantment.LURE) + 1;
            }

            task.cancel();
            fishBiteTimes.remove(player);
            messageUtil.sendMessage(player, "§7Du hast durch das Fischen folgendes erhalten§8:");

            for (int i = 0; i < level; i++) {
                final RewardLevel rewardLevel = RewardLevel.getByChance(Math.toIntExact(MathUtil.getRandom(0, 100)));

                if(luckyFishingHandler.getRewards().isEmpty())
                    return;

                if (luckyFishingHandler.getRewards().get(rewardLevel).isEmpty())
                    continue;

                final ItemStack reward = luckyFishingHandler.getRewards().get(rewardLevel).stream().findAny().orElse(null);
                if (reward == null)
                    continue;

                final String name = reward.hasItemMeta() && reward.getItemMeta().hasDisplayName() ? reward.getItemMeta().getDisplayName() : reward.getType().name().toUpperCase();

                if (name.toLowerCase().contains("niete"))
                    continue;

                messageUtil.sendMessage(player, "§8- '§f" + name + "§8' §fx" + reward.getAmount());
                player.getInventory().addItem(reward);
            }
        }
    }

    private BukkitTask task;

    private void handleFishBiting(final Player player, final FishHook hook) {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                fishBiteTimes.remove(player);
                messageUtil.sendMessage(player, "Du hast zu lange gebraucht.");
                hook.setCustomNameVisible(false);
                hook.setCustomName(null);
                hook.remove();
            }
        }.runTaskLater(CrownMain.getInstance(), 20 * 3L);
    }

}
