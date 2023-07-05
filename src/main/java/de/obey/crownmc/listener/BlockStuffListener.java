package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       17.10.2022 / 14:33

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.commands.VanishCommand;
import de.obey.crownmc.handler.CombatHandler;
import de.obey.crownmc.handler.LocationHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.handler.WorldProtectionHandler;
import de.obey.crownmc.objects.WorldProtection;
import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
@NonNull
public final class BlockStuffListener implements Listener {

    private final MessageUtil messageUtil;
    private final LocationHandler locationHandler;
    private final CombatHandler combatHandler;
    private final UserHandler userHandler;
    private final ServerConfig serverConfig;
    private final WorldProtectionHandler worldProtectionHandler;
    private final HashMap<UUID, Long> enderpearlCooldown = new HashMap<>();

    @EventHandler
    public void on(final LeavesDecayEvent event) {

        if (event.getBlock().getWorld().getName().equalsIgnoreCase("plots") ||
                event.getBlock().getWorld().getName().equalsIgnoreCase("farmwelt") ||
                event.getBlock().getWorld().getName().equalsIgnoreCase("world"))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void on(final BlockFromToEvent event) {
        if (event.getBlock().getWorld().getName().equalsIgnoreCase("plots") ||
                event.getBlock().getWorld().getName().equalsIgnoreCase("farmwelt") ||
                event.getBlock().getWorld().getName().equalsIgnoreCase("world"))
            return;

        if(event.getBlock().getType() == Material.WATER)
            event.setCancelled(true);
    }

    @EventHandler
    public void on(final ChunkUnloadEvent event) {
        if (event.getWorld().getName().equalsIgnoreCase("plots"))
            return;

        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof ArmorStand) {
                event.setCancelled(true);
            }
        }
    }

    // INSTAND RESPAWN
    @EventHandler
    public void onDie(final PlayerDeathEvent event) {
        final Player player = event.getEntity();

        event.setDeathMessage("");

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    final PacketPlayInClientCommand packet = new PacketPlayInClientCommand(
                            PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN);
                    ((CraftPlayer) player).getHandle().playerConnection.a(packet);

                    locationHandler.teleportToLocationNameInstant(player, "spawn");

                    User user = userHandler.getUserInstant(player.getUniqueId());
                    if (user.is(DataType.RESPAWNKIT)) {
                        user.getRespawnKit().equipRespawnKit();
                    }

                } catch (NullPointerException ignored) {
                }
            }
        }.runTaskLater(CrownMain.getInstance(), 10);
    }

    @EventHandler
    public void on(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        final Location pvp = locationHandler.getLocation("pvp");
        final WorldProtection protection = worldProtectionHandler.getWorldProtection(player.getWorld());

        if (protection != null) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                if (!protection.isFly()) {
                    if (player.isFlying() || player.getAllowFlight() && !VanishCommand.vanished.contains(player)) {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                    }
                }
            }
        }

        if (pvp != null) {
            if (player.getLocation().getWorld() == pvp.getWorld() && player.getLocation().getY() <= (pvp.getY() - 150)) {
                player.damage(200);
            }
        }

        if (event.getTo().getY() > 300) {
            event.setCancelled(true);
            locationHandler.teleportToLocationNameInstant(player, "spawn");
            messageUtil.sendMessageToTeamMembers("§c§o" + player.getName() + " bewegt sich zu weit oben.");
        }
    }

    @EventHandler
    public void onToggle(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                || event.getAction() == Action.LEFT_CLICK_BLOCK
                || event.getAction() == Action.RIGHT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_AIR) {
            if (player.getItemInHand() != null) {
                if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
                    if (!Bools.ep && !PermissionUtil.hasPermission(event.getPlayer(), "toggle.bypass", false)) {
                        event.setCancelled(true);
                        player.playSound(player.getLocation(), Sound.EXPLODE, 1, 1);
                        player.updateInventory();
                        messageUtil.sendMessage(player, "Enderperlen sind §c§odeaktiviert§7.");
                    }

                    if (enderpearlCooldown.containsKey(player.getUniqueId()) && enderpearlCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                        event.setCancelled(true);
                        player.playSound(player.getLocation(), Sound.EXPLODE, 0.1f, 1);
                        return;
                    } else {
                        enderpearlCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (1000 * 3));
                    }
                }
                if (player.getItemInHand().getType() == Material.POTION) {
                    if (!Bools.potions && !PermissionUtil.hasPermission(event.getPlayer(), "toggle.bypass", false)) {
                        event.setCancelled(true);
                        player.playSound(player.getLocation(), Sound.EXPLODE, 1, 1);
                        player.updateInventory();
                        messageUtil.sendMessage(player, "Potions sind §c§odeaktiviert§7.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void on(final PlayerDropItemEvent event) {
        if (!Bools.drop && !PermissionUtil.hasPermission(event.getPlayer(), "toggle.bypass", false)) {
            messageUtil.sendMessage(event.getPlayer(), "ItemDrops sind §c§odeaktiviert§7.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(final PlayerPickupItemEvent event) {
        if (!Bools.pickup && !PermissionUtil.hasPermission(event.getPlayer(), "toggle.bypass", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCrashSign(final SignChangeEvent event) {
        final Player player = event.getPlayer();

        if (event.getLine(0).length() > 49 || event.getLine(1).length() > 49 || event.getLine(2).length() > 49 || event.getLine(3).length() > 49) {
            event.setLine(0, "");
            event.setLine(1, "§4Nope");
            event.setLine(2, "§6-_-");
            event.setLine(3, "");
            return;
        }

        if (!PermissionUtil.hasPermission(player, "colorsigns", false))
            return;

        event.setLine(0, ChatColor.translateAlternateColorCodes('&', event.getLine(0)));
        event.setLine(1, ChatColor.translateAlternateColorCodes('&', event.getLine(1)));
        event.setLine(2, ChatColor.translateAlternateColorCodes('&', event.getLine(2)));
        event.setLine(3, ChatColor.translateAlternateColorCodes('&', event.getLine(3)));
    }

    @EventHandler
    public void onWeatherChange(final WeatherChangeEvent event) {
        boolean rain = event.toWeatherState();
        if (rain)
            event.setCancelled(true);
    }

    @EventHandler
    public void handleAchievement(final PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();

        if (userHandler.getUserInstant(player.getUniqueId()) == null) {
            event.setCancelled(true);
            return;
        }

        final String cmd = event.getMessage().toLowerCase();

        if (Bukkit.getServer().getHelpMap().getHelpTopic(event.getMessage().split(" ")[0]) == null) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5f, 0.5f);
            messageUtil.sendMessage(player, "Der Befehl §8'§e§o" + event.getMessage().split(" ")[0] + "§8'§7 existiert nicht§8.");
            return;
        }

        if (combatHandler.isInCombat(player) != null) {
            if (serverConfig.getBlockedCombatCommands().contains(cmd.split(" ")[0])) {
                event.setCancelled(true);
                messageUtil.sendMessage(player, "Der Command §e§o" + event.getMessage() + "§7 ist im Kampf §c§overboten§8.");
                return;
            }
        }

        if (serverConfig.getBlockedCommands().contains(cmd.split(" ")[0])) {
            event.setCancelled(true);
            messageUtil.sendMessage(player, "Der Command §e§o" + cmd.split(" ")[0] + "§7 ist blockiert§8.");
            return;
        }

        if (cmd.startsWith("/minecraft:")
                || cmd.contains("/pex")
                || cmd.contains("/permission")
                || cmd.contains("/lp")
                || cmd.contains("/luckperms")
                || (cmd.contains("/we") && !cmd.toLowerCase().startsWith("/werbung"))
                || cmd.contains("/worldedit")
                || cmd.contains("/icanhasbukkit")
                || cmd.startsWith("/about")
                || cmd.startsWith("//calc")
                || cmd.startsWith("//to")
                || cmd.startsWith("//eval")
                || cmd.startsWith("/help")
                || cmd.startsWith("/spark")
                || cmd.startsWith("/tps")
                || cmd.startsWith("/tell")
                || cmd.startsWith("/say")
                || cmd.startsWith("/ver")
                || cmd.startsWith("/me")
                || cmd.equalsIgnoreCase("/pl")
                || cmd.startsWith("/version")
                || cmd.startsWith("/?")
                || cmd.startsWith("/bukkit:")
                || cmd.startsWith("/plugins")
                || cmd.startsWith("/!")) {

            if (player.hasPermission("*") || player.hasPermission("slayer.command.bypass"))
                return;

            player.playSound(player.getLocation(), Sound.ZOMBIE_WOODBREAK, 1, 1);
            event.setCancelled(true);

            messageUtil.sendMessage(player, "Der Befehl §8'§e§o" + cmd + "§8'§7 existiert nicht.");

            for (final Player teamler : Bukkit.getOnlinePlayers()) {
                if (PermissionUtil.hasPermission(teamler, "cppf", false) && teamler != player) {
                    if (userHandler.getUserInstant(teamler.getUniqueId()).is(DataType.COMMANDWATCHSTATE))
                        teamler.sendMessage("§c§lCPPF §8> §f§o" + player.getName() + "§8: §c§o" + cmd);
                }
            }

            return;
        }

        for (final Player teamler : Bukkit.getOnlinePlayers()) {
            if (PermissionUtil.hasPermission(teamler, "commandwatch", false) && teamler != player) {
                if (userHandler.getUserInstant(teamler.getUniqueId()).is(DataType.COMMANDWATCHSTATE))
                    teamler.sendMessage("§f§lCW §8> §c§o" + player.getName() + "§8: §7" + cmd);
            }
        }
    }

    @EventHandler
    public void onEnchant(final EnchantItemEvent event) {
        final Player player = event.getEnchanter();
        final ItemStack item = event.getItem();

        if (item.getAmount() > 1) {
            event.setCancelled(true);
            messageUtil.sendMessage(player, "Bitte verzaubere nur ein Item zur selben Zeit§8.");
            messageUtil.sendMessageToTeamMembers("§c§o" + player.getName() + " hat den Enchantbug versucht.");
        }
    }

    @EventHandler
    public void onHunger(final FoodLevelChangeEvent event) {
        final Player player = (Player) event.getEntity();

        final Location casino = locationHandler.getLocation("casino");

        if(casino == null)
            return;

        if(player.getWorld() == casino.getWorld())
            event.setCancelled(true);
    }

    @EventHandler
    public void onAnvil(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory inv = event.getInventory();

        if (inv instanceof AnvilInventory) {
            try {
                if (inv.getItem(2).getAmount() > 1) {
                    event.setCancelled(true);
                    messageUtil.sendMessage(player, "Bitte lege nur ein Item in den amboss§8.");
                    messageUtil.sendMessageToTeamMembers("§c§o" + player.getName() + " hat den Anvilbug versucht.");
                }
            } catch (final NullPointerException ignored) {}
            return;
        }

        if (event.getInventory() instanceof BrewerInventory) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (int i = 0; i <= 2; i++) {
                        if (inv.getItem(i) != null && inv.getItem(i).getAmount() > 1) {
                            final int over = inv.getItem(i).getAmount() - 1;
                            inv.getItem(i).setAmount(1);
                            for (int ix = 0; ix < over; ix++) {
                                player.getInventory().addItem(inv.getItem(i));
                            }
                            player.updateInventory();
                        }
                    }
                }
            }.runTaskLater(CrownMain.getInstance(), 2);
        }
    }

    @EventHandler
    public void onRedstone(final BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.REDSTONE ||
                event.getBlock().getType() == Material.REDSTONE_BLOCK ||
                event.getBlock().getType() == Material.REDSTONE_COMPARATOR ||
                event.getBlock().getType() == Material.REDSTONE_WIRE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChatTabComplete(final PlayerChatTabCompleteEvent event) {
        for (Player player : VanishCommand.vanished)
            event.getTabCompletions().remove(player.getName());
    }

    @EventHandler
    public void onKick(final PlayerKickEvent event) {
        if (event.getReason().toLowerCase().contains("disconnect.spam"))
            event.setCancelled(true);
    }
}
