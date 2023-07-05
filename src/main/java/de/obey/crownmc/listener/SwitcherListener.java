package de.obey.crownmc.listener;
/*

    Author - Obey -> CrownMc
       05.07.2023 / 17:33

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@RequiredArgsConstructor @NonNull
public final class SwitcherListener implements Listener {

    private final MessageUtil messageUtil;

    @EventHandler
    public void on(final EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Snowball && event.getEntity() instanceof Player) {
            final Snowball snowball = (Snowball) event.getDamager();

            if (!(snowball.getShooter() instanceof Player))
                return;

            if (snowball.getCustomName() == null || !snowball.getCustomName().equalsIgnoreCase("switcher"))
                return;

            final Player shooter = (Player) snowball.getShooter();
            final Player hitPlayer = (Player) event.getEntity();

            if(shooter.getName().equals(hitPlayer.getName()))
                return;

            final Location shooterLocation = shooter.getLocation();

            shooter.teleport(hitPlayer.getLocation());
            hitPlayer.teleport(shooterLocation);

            shooter.playSound(shooter.getLocation(), Sound.ENDERMAN_TELEPORT, 0.5f, 1);
            hitPlayer.playSound(hitPlayer.getLocation(), Sound.ENDERMAN_TELEPORT, 0.5f, 1);

            messageUtil.sendMessage(shooter, "Du hast die Position mit " + hitPlayer.getName() + " getauscht§8.");
            messageUtil.sendMessage(hitPlayer, "Switched!");

            event.setCancelled(true);
        }
    }
}
