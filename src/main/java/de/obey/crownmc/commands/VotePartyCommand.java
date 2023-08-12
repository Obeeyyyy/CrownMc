package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       26.06.2023 / 00:42

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.VotePartyHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor @NonNull
public final class VotePartyCommand implements CommandExecutor, Listener {

    @NonNull
    private final MessageUtil messageUtil;
    private final VotePartyHandler votePartyHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        if(args.length == 1) {
            if (args[0].equalsIgnoreCase("cc")) {

                final double[] currentSum = {0};

                votePartyHandler.getItems().forEach(item -> currentSum[0] += votePartyHandler.getChanceFromItem(item));

                final DecimalFormat format = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));

                sender.sendMessage("Alle Chancen ergeben §8:");
                sender.sendMessage(format.format(currentSum[0]) + "%");

                return false;
            }

            if(args[0].equalsIgnoreCase("items")) {
                final Inventory inventory = Bukkit.createInventory(null, 9*7, "Voteparty Items");
                final ArrayList<ItemStack> items = votePartyHandler.getItems();

                if(!items.isEmpty()) {
                    for (ItemStack item : items) {
                        if(item.getType() != Material.AIR) {
                            inventory.addItem(item);
                        }
                    }
                }

                player.openInventory(inventory);

                return false;
            }

            if (args[0].equalsIgnoreCase("chance")) {

                final Inventory inventory = Bukkit.createInventory(null, 9*7, "Voteparty Chance");

                if(votePartyHandler.getItems().size() > 0) {
                    final AtomicInteger slot = new AtomicInteger();
                    votePartyHandler.getItems().forEach(item -> inventory.setItem(slot.getAndIncrement(), item));
                }

                player.openInventory(inventory);

                return false;
            }

            if(args[0].equalsIgnoreCase("reloadloc")) {
                votePartyHandler.loadLocations();
                messageUtil.sendMessage(sender, votePartyHandler.getLocations().size() + " Locations geladen§8.");
                return false;
            }

            if (args[0].equalsIgnoreCase("start")) {
                votePartyHandler.startVoteParty();
                return false;
            }

            if (args[0].equalsIgnoreCase("boss")) {
                votePartyHandler.spawnBoss(player.getLocation());
                return false;
            }
        }

        if(args.length == 2) {
            if (args[0].equalsIgnoreCase("drop")) {
                try {
                    final int amount = Integer.parseInt(args[1]);

                    new BukkitRunnable() {

                        int itemDrops = 0, ticks = 0;

                        @Override
                        public void run() {

                            if(itemDrops >= amount) {
                                cancel();
                                return;
                            }

                            ticks++;

                            if(ticks == 3) {
                                ticks = 0;
                                itemDrops++;

                                for (final Location location : votePartyHandler.getLocations()) {
                                    votePartyHandler.playeSoundForEveryOne(location, Sound.FIREWORK_LARGE_BLAST2);
                                    votePartyHandler.playEffectForEveryone(location, Effect.ENDER_SIGNAL);

                                    final ItemStack stack = votePartyHandler.getRandomItem();
                                    final Item item = location.getWorld().dropItem(location, stack);
                                    item.setCustomNameVisible(true);
                                    item.setCustomName(stack.hasItemMeta() ? stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : stack.getType().name() : stack.getType().name());
                                    item.setVelocity(votePartyHandler.getRandomVelocity(new Random()));
                                }
                            }
                        }
                    }.runTaskTimer(CrownMain.getInstance(), 5, 5);

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an§8.");
                }
                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/voteparty items",
                "/voteparty reloadloc",
                "/voteparty start",
                "/voteparty boss",
                "/voteparty drop <zahl>"
        );

        return false;
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if (InventoryUtil.isInventoryTitle(event.getInventory(), "Voteparty Chance")) {
            event.setCancelled(true);

            if (!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "Voteparty Chance"))
                return;

            final ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR)
                return;

            ArrayList<String> lore = new ArrayList<>();

            if (item.hasItemMeta() && item.getItemMeta().hasLore())
                lore = (ArrayList<String>) item.getItemMeta().getLore();

            final Player player = (Player) event.getWhoClicked();
            final DecimalFormat format = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
            final double chance = Double.parseDouble(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1).split(" ")[1].replace("%", ""));

            // Chance hoch
            if (event.getClick().isLeftClick()) {
                if (event.getClick().isShiftClick()) {
                    lore.remove(lore.size() - 1);
                    lore.add("Chance " + format.format((chance + 0.01)) + "%");
                } else {
                    lore.remove(lore.size() - 1);
                    lore.add("Chance " + format.format((chance + 1)) + "%");
                }

                final ItemMeta meta = item.getItemMeta();

                meta.setLore(lore);
                item.setItemMeta(meta);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);

                return;
            }

            // Chance runter
            if (event.getClick().isRightClick()) {
                if (event.getClick().isShiftClick()) {
                    lore.remove(lore.size() - 1);
                    lore.add("Chance " + format.format((chance - 0.01)) + "%");
                } else {
                    lore.remove(lore.size() - 1);
                    lore.add("Chance " + format.format((chance - 1)) + "%");
                }

                final ItemMeta meta = item.getItemMeta();

                meta.setLore(lore);
                item.setItemMeta(meta);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            }
        }
    }

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if(InventoryUtil.isInventoryTitle(event.getInventory(), "Voteparty Items") ||
            InventoryUtil.isInventoryTitle(event.getInventory(), "Voteparty Chance")) {
            votePartyHandler.setItems(event.getInventory());
            messageUtil.sendMessage(event.getPlayer(), "Items gesetzt§8.");
        }
    }

    @EventHandler
    public void on(final EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Giant))
            return;

        event.getEntity().setCustomName(((Giant) event.getEntity()).getHealth() + "§c§l❤");
    }

    @EventHandler
    public void on(final EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Giant))
            return;

        if(!(event.getDamager() instanceof Player))
            return;

        messageUtil.sendActionBar((Player) event.getDamager(), "Noch§8: §f§o" + ((Giant) event.getEntity()).getHealth() + "§c§l❤");
        votePartyHandler.getParties().get(votePartyHandler.getParties().size() - 1).damageBoss((Player) event.getDamager(), event.getDamage());
    }

    @EventHandler
    public void on(final EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Giant))
            return;

        if(!event.getEntity().isCustomNameVisible())
            return;

        votePartyHandler.getParties().get(votePartyHandler.getParties().size() - 1).end();
    }
}
