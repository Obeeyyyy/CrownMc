package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       29.10.2022 / 18:04

*/

import de.obey.crownmc.commands.FreezeCommand;
import de.obey.crownmc.commands.GodCommand;
import de.obey.crownmc.handler.CombatHandler;
import de.obey.crownmc.handler.WorldProtectionHandler;
import de.obey.crownmc.objects.pvp.Combat;
import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public final class ProtectionListener implements Listener {

    @NonNull
    private final MessageUtil messageUtil;
    @NonNull
    private final CombatHandler combatHandler;
    @NonNull
    private final WorldProtectionHandler worldProtectionHandler;

    @EventHandler
    public void on(final BlockPlaceEvent event) {
        if(!worldProtectionHandler.canBuild(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void on(final BlockBreakEvent event) {
        if(!worldProtectionHandler.canBuild(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void on(final BlockDamageEvent event) {

        if (worldProtectionHandler.getWorldProtection(event.getBlock().getWorld()) == null ||
                worldProtectionHandler.getWorldProtection(event.getBlock().getWorld()).isBuild())
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void on(final BlockExplodeEvent event) {

        if (worldProtectionHandler.getWorldProtection(event.getBlock().getWorld()) == null ||
                worldProtectionHandler.getWorldProtection(event.getBlock().getWorld()).isBlockexplosion())
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void on(final SpawnerSpawnEvent event) {

        if (worldProtectionHandler.getWorldProtection(event.getEntity().getWorld()) == null ||
                worldProtectionHandler.getWorldProtection(event.getEntity().getWorld()).isMobspawn())
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void on(final EntitySpawnEvent event) {

        if (worldProtectionHandler.getWorldProtection(event.getEntity().getWorld()) == null ||
                worldProtectionHandler.getWorldProtection(event.getEntity().getWorld()).isMobspawn())
            return;

        if (event.getEntity() instanceof ArmorStand || event.getEntity() instanceof Item)
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void on(final PlayerInteractAtEntityEvent event) {

        if(event.getRightClicked() instanceof EnderCrystal ||
                event.getRightClicked() instanceof ItemFrame) {
            if(!worldProtectionHandler.canBuild(event.getPlayer()))
                event.setCancelled(true);

            return;
        }

        if(!worldProtectionHandler.canBuild(event.getPlayer()) && !worldProtectionHandler.canInteract(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {

        if(event.isCancelled())
            return;

        // Cancel Bucket , AmorStand, Frame
        if(event.getItem() != null && event.getItem().getType() != Material.AIR) {
            if (event.getItem().getType().name().toLowerCase().contains("bucket") ||
                    event.getItem().getType() == Material.ARMOR_STAND ||
                    event.getItem().getType() == Material.ITEM_FRAME ||
                    event.getItem().getType() == Material.PAINTING) {
                if (!worldProtectionHandler.canBuild(event.getPlayer()))
                    event.setCancelled(true);

                return;

                // Cancel Enderpearls
            } else if (event.getItem() != null && event.getItem().getType() != Material.AIR && event.getItem().getType() == Material.ENDER_PEARL) {
                if (!worldProtectionHandler.canEp(event.getPlayer()))
                    event.setCancelled(true);

                return;
            }
        }

        if(!worldProtectionHandler.canBuild(event.getPlayer()) && !worldProtectionHandler.canInteract(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void on(final PaintingPlaceEvent event) {
        if(!worldProtectionHandler.canBuild(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void on(final PaintingBreakByEntityEvent event) {
        if(!worldProtectionHandler.canBuild(event.getRemover().getWorld()))
            event.setCancelled(true);
    }

    @EventHandler
    public void on(final EntityDamageEvent event) {
        if(GodCommand.godmode.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);

        if (event.getCause() == EntityDamageEvent.DamageCause.STARVATION)
            event.setCancelled(true);
    }

    @EventHandler
    public void on(final EntityDamageByEntityEvent event) {

        if (event.isCancelled())
            return;

        // IF Item Itemframe oder ArmorStand
        if (event.getEntity() instanceof Item || event.getEntity() instanceof ItemFrame || event.getEntity() instanceof ArmorStand) {
            if(!worldProtectionHandler.canBuild(event.getEntity().getWorld()))
                event.setCancelled(true);

            return;
        }

        // IF damaged = mob
        if (event.getEntity() instanceof Monster || event.getEntity() instanceof Animals || event.getEntity() instanceof Slime) {
            if(!worldProtectionHandler.canFightMobs(event.getEntity().getWorld()))
                event.setCancelled(true);

            return;
        }

        if(!(event.getEntity() instanceof Player))
            return;

        final Player damaged = (Player) event.getEntity();

        if(!worldProtectionHandler.canFightPlayers(damaged)) {
            event.setCancelled(true);
            return;
        }

        Player attacker;

        if (event.getDamager() instanceof Arrow) {
            if (!(((Arrow) event.getDamager()).getShooter() instanceof Player))
                return;

            attacker = ((Player) ((Arrow) event.getDamager()).getShooter()).getPlayer();

        } else if (event.getDamager() instanceof Snowball) {
            if (!(((Snowball) event.getDamager()).getShooter() instanceof Player))
                return;

            attacker = ((Player) ((Snowball) event.getDamager()).getShooter()).getPlayer();

        } else  if (event.getDamager() instanceof Egg) {
            if (!(((Egg) event.getDamager()).getShooter() instanceof Player))
                return;

            attacker = ((Player) ((Egg) event.getDamager()).getShooter()).getPlayer();

        } else {
            if (!(event.getDamager() instanceof Player))
                return;

            attacker = (Player) event.getDamager();
        }

        if (FreezeCommand.getFreezed().contains(event.getEntity().getUniqueId()) ||
                FreezeCommand.getFreezed().contains(damaged.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (GodCommand.godmode.contains(damaged.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if(attacker == damaged)
            return;

        if (!Bools.pvp && !PermissionUtil.hasPermission(attacker, "toggle.bypass", false)) {
            messageUtil.sendMessage(attacker, "PvP ist §c§odeaktiviert§7.");
            event.setCancelled(true);
            return;
        }

        Combat combat = combatHandler.isInCombat(damaged);

        if (combat == null)
            combat = new Combat(damaged);

        combat.hit(attacker);

        combat = combatHandler.isInCombat(attacker);

        if (combat == null)
            combat = new Combat(attacker);

        combat.hit(damaged);
    }
}
