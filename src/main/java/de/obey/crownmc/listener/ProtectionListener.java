package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       29.10.2022 / 18:04

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.commands.FreezeCommand;
import de.obey.crownmc.commands.GodCommand;
import de.obey.crownmc.handler.CombatHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.handler.WorldProtectionHandler;
import de.obey.crownmc.objects.pvp.Combat;
import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public final class ProtectionListener implements Listener {

    @NonNull
    private final MessageUtil messageUtil;
    @NonNull
    private final CombatHandler combatHandler;
    @NonNull
    private final WorldProtectionHandler worldProtectionHandler;
    @NonNull
    private final UserHandler userHandler;

    @EventHandler
    public void on(final ProjectileLaunchEvent event) {

        if(worldProtectionHandler.getWorldProtection(event.getEntity().getWorld()) != null) {
            if (!worldProtectionHandler.getWorldProtection(event.getEntity().getWorld()).isProjectiles()) {

                if(event.getEntity() instanceof ThrownExpBottle || event.getEntity() instanceof ThrownPotion)
                    return;

                event.setCancelled(true);
            }
        }
    }

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
    public void on(final ItemDespawnEvent event) {
        if(event.getEntity().getTicksLived() >= 20*60*20)
            event.setCancelled(true);
    }

    @EventHandler
    public void on(final PlayerDropItemEvent event) {
        if(!worldProtectionHandler.canDrop(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void on(final PlayerLeashEntityEvent event) {

        if(!worldProtectionHandler.canBuild(event.getPlayer()))
            event.setCancelled(true);

        if (worldProtectionHandler.getWorldProtection(event.getEntity().getWorld()) == null ||
                worldProtectionHandler.getWorldProtection(event.getEntity().getWorld()).isInteract())
            return;

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
    public void on(final CreatureSpawnEvent event) {
        if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM ||
                event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
            return;

        if (worldProtectionHandler.getWorldProtection(event.getEntity().getWorld()) == null ||
                worldProtectionHandler.getWorldProtection(event.getEntity().getWorld()).isMobspawn())
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

    @EventHandler (priority = EventPriority.HIGHEST)
    public void on(final PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
            // Cancel Bucket , AmorStand, Frame

            if (event.getItem().getType().name().toLowerCase().contains("bucket") ||
                    event.getItem().getType() == Material.ARMOR_STAND ||
                    event.getItem().getType() == Material.ITEM_FRAME ||
                    event.getItem().getType() == Material.PAINTING) {

                if (!worldProtectionHandler.canBuild(event.getPlayer()))
                    event.setCancelled(true);

                return;
            }

            if (event.getItem().getType() == Material.SNOW_BALL || event.getItem().getType() == Material.EYE_OF_ENDER) {

                if (!worldProtectionHandler.canProjectile(player.getWorld())) {
                    event.setCancelled(true);
                    event.getPlayer().updateInventory();
                    return;
                }

                if (!InventoryUtil.isItemInHandWithDisplayname(player, "§3§lSwitcher"))
                    return;

                event.setCancelled(true);
                player.updateInventory();

                InventoryUtil.removeItemInHand(player, 1);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        final Vector direction = player.getLocation().getDirection();
                        direction.multiply(1.5);
                        final Snowball snowball = player.launchProjectile(Snowball.class, direction);
                        snowball.setCustomName("switcher");
                        snowball.setShooter(player);
                    }
                }.runTaskLater(CrownMain.getInstance(), 1);

                return;
            }

            if (event.getItem().getType() == Material.ENDER_PEARL) {
                if (!worldProtectionHandler.canEp(player)) {
                    event.setCancelled(true);
                    player.updateInventory();
                    return;
                }

                return;
            }

            if (event.getItem().getType() == Material.ARMOR_STAND) {
                if (!worldProtectionHandler.canBuild(player)) {
                    event.setCancelled(true);
                    return;
                }
            }

        }

        if (event.getClickedBlock() != null && (
                event.getClickedBlock().getType().name().contains("CHEST") ||
                        event.getClickedBlock().getType().name().contains("DOOR") ||
                        event.getClickedBlock().getType() == Material.ITEM_FRAME ||
                        event.getClickedBlock().getType() == Material.HOPPER ||
                        event.getClickedBlock().getType() == Material.FURNACE ||
                        event.getClickedBlock().getType() == Material.DISPENSER ||
                        event.getClickedBlock().getType() == Material.DROPPER ||
                        event.getClickedBlock().getType() == Material.BEACON ||
                        event.getClickedBlock().getType() == Material.BREWING_STAND ||
                        event.getClickedBlock().getType() == Material.WORKBENCH ||
                        event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE ||
                        event.getClickedBlock().getType() == Material.ANVIL ||
                        event.getClickedBlock().getType() == Material.ARMOR_STAND)) {

            if (!worldProtectionHandler.canInteract(event.getPlayer()))
                event.setCancelled(true);
        }
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
        if(!(event.getEntity() instanceof Player))
            return;

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

        if(userHandler.getUserInstant(attacker.getUniqueId()).getUserPeace().hasPeaceWith(damaged)) {
            event.setCancelled(true);
            messageUtil.sendMessage(attacker, "Zwischen dir und " + damaged.getName() + " herrscht Frieden§8.");
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
