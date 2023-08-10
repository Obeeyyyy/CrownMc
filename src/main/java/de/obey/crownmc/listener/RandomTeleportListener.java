package de.obey.crownmc.listener;
/*

    Author - Obey -> CrownMc
       01.08.2023 / 02:13

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.CombatHandler;
import de.obey.crownmc.handler.LocationHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

@RequiredArgsConstructor  @NonNull
public final class RandomTeleportListener implements Listener {

    private final MessageUtil messageUtil;
    private final CombatHandler combatHandler;
    private final LocationHandler locationHandler;

    @EventHandler
    public void on(final PlayerInteractEvent event) {
        if(!InventoryUtil.hasItemInHand(event.getPlayer(), false))
            return;

        final Player player = event.getPlayer();

        if(!InventoryUtil.isItemInHandWithDisplayname(player, "§a§lRandom Teleport"))
            return;

        event.setCancelled(true);

        if(combatHandler.isInCombat(player) != null) {
            messageUtil.sendMessage(player, "Du kannst dich im Kampf nicht teleportieren§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.2f, 1);
            return;
        }

        final Location loc = getRandomLocation(player);

        if(loc == null)
            return;

        loc.getChunk().load();

        InventoryUtil.removeItemInHand(player, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
        player.setVelocity(new Vector(0, 10, 0));

        new BukkitRunnable() {
            @Override
            public void run() {
                locationHandler.teleportToLocation(player, loc);

                for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                    player.removePotionEffect(activePotionEffect.getType());
                }

            }
        }.runTaskLater(CrownMain.getInstance(), 40);
    }

    private Location getRandomLocation(final Player player) {
        if(!player.getWorld().getName().equalsIgnoreCase("hardcore") &&
          !player.getWorld().getName().equalsIgnoreCase("farmwelt")) {
            messageUtil.sendMessage(player, "Bitte betrete die Farm oder Hardcore Welt§8.");
            return null;
        }

        final Random random = new Random();
        final int x = random.nextInt(100001) - 50000;
        final int z = random.nextInt(100001) - 50000;
        final int y = player.getWorld().getHighestBlockYAt(x, z) + 2;

        return new Location(player.getWorld(), x, y, z);
    }
}