package de.obey.crownmc.listener;

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.LuckyFishingHandler;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
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
    public void handlePlayerFishEvent(final PlayerFishEvent event) {
        final Player player = event.getPlayer();

        if (!player.getWorld().getName().equalsIgnoreCase("islands")) return;

        final long currentTime = System.currentTimeMillis();
        long timeToWait = 30000;

        final ItemStack itemStack = player.getItemInHand();

        if (itemStack.hasItemMeta()) {
            if (itemStack.getItemMeta().hasEnchant(Enchantment.LUCK)) {
                final int level = itemStack.getItemMeta().getEnchantLevel(Enchantment.LUCK);
                timeToWait -= 5000L * level;
            }
        }

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            final long biteTime = currentTime + timeToWait;
            final long seconds = Long.parseLong(MathUtil.getSecondsFromMillis((biteTime - currentTime)));
            fishBiteTimes.put(player, biteTime);

            event.getHook().setCustomNameVisible(true);
            event.getHook().setCustomName("§7Beißt an in§8: §e§o" + seconds + "s");
            new BukkitRunnable() {
                @Override
                public void run() {
                    final long seconds = Long.parseLong(MathUtil.getSecondsFromMillis((biteTime - currentTime)));

                    event.getHook().setCustomName("§7Beißt an in§8: §e§o" + seconds + "s");

                    if (fishBiteTimes.containsKey(player) && fishBiteTimes.get(player) == biteTime) {
                        event.getHook().setCustomName("§a§lEtwas hat angebissen!");
                        handleFishBiting(player, event.getHook());
                        this.cancel();
                    }
                }
            }.runTaskTimer(CrownMain.getInstance(), 0L, 20L);
        }
    }

    @EventHandler
    public void handleInventoryCloseEvent(final InventoryCloseEvent event) {
        luckyFishingHandler.saveFishingRewards(event.getView());
    }

    @EventHandler
    public void handlePlayerInteractEvent(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!player.getWorld().getName().equalsIgnoreCase("islands")) return;

        final ItemStack itemStack = player.getItemInHand();
        int level = 1;
        if (itemStack.hasItemMeta()) {
            if (itemStack.getItemMeta().hasEnchant(Enchantment.LURE))
                level = itemStack.getItemMeta().getEnchantLevel(Enchantment.LURE) + 1;
        }

        if (fishBiteTimes.containsKey(player)) {
            task.cancel();
            fishBiteTimes.remove(player);
            messageUtil.sendMessage(player, "§7Du hast durch das Fischen folgendes erhalten§8:");


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
        }.runTaskLater(CrownMain.getInstance(), 20*3L);
    }

}
