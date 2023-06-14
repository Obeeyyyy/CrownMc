package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       22.10.2022 / 15:24

*/

import de.obey.slayer.Initializer;
import de.obey.slayer.backend.enums.DataType;
import de.obey.slayer.backend.user.User;
import de.obey.slayer.util.InventoryUtil;
import de.obey.slayer.util.ItemBuilder;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@RequiredArgsConstructor
public final class SettingsCommand implements CommandExecutor, Listener {

    @NonNull
    private final Initializer initializer;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        Inventory inventory = Bukkit.createInventory(null, 9 * 4, "§7Slayer Settings");

        if (PermissionUtil.hasPermission(player, "team", false))
            inventory = Bukkit.createInventory(null, 9 * 6, "§7Slayer Settings");

        updateInventory(initializer.getUserHandler().getUserInstant(player.getUniqueId()), inventory);

        player.openInventory(inventory);

        return false;
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {

        if (InventoryUtil.isInventoryTitle(event.getInventory(), "§7Slayer Settings"))
            event.setCancelled(true);

        if (!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§7Slayer Settings"))
            return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;

        final Player player = (Player) event.getWhoClicked();
        final User user = initializer.getUserHandler().getUserInstant(player.getUniqueId());

        if (user == null)
            return;

        if (event.getSlot() == 11) {
            user.setBoolean(DataType.MSGSTATE, !user.is(DataType.MSGSTATE));

        } else if (event.getSlot() == 12) {
            user.setBoolean(DataType.TPASTATE, !user.is(DataType.TPASTATE));

        } else if (event.getSlot() == 13) {
            user.setBoolean(DataType.SCOREBOARDSTATE, !user.is(DataType.SCOREBOARDSTATE));
            initializer.getScoreboardHandler().setScoreboard(player);

        } else if (event.getSlot() == 14) {
            user.setBoolean(DataType.KILLHOLOSTATE, !user.is(DataType.KILLHOLOSTATE));

        } else if (event.getSlot() == 21) {
            user.setBoolean(DataType.SPAWNTELEPORT, !user.is(DataType.SPAWNTELEPORT));

        } else if (event.getSlot() == 22) {
            user.setBoolean(DataType.RESPAWNKIT, !user.is(DataType.RESPAWNKIT));

        } else if (event.getSlot() == 15) {
            Bukkit.dispatchCommand(player, "c settings");

        } else if (event.getSlot() == 20) {
            if (!PermissionUtil.hasPermission(player, "chatlines", true))
                return;

            user.setBoolean(DataType.CHATLINESSTATE, !user.is(DataType.CHATLINESSTATE));

        } else if (event.getSlot() == 38) {
            if (!PermissionUtil.hasPermission(player, "commandwatch", true))
                return;

            user.setBoolean(DataType.COMMANDWATCHSTATE, !user.is(DataType.COMMANDWATCHSTATE));

        } else if (event.getSlot() == 39) {
            if (!PermissionUtil.hasPermission(player, "msgspy", true))
                return;

            user.setBoolean(DataType.MSGSPYSTATE, !user.is(DataType.MSGSPYSTATE));

        } else if (event.getSlot() == 40) {
            if (!PermissionUtil.hasPermission(player, "autovanish", true))
                return;

            user.setBoolean(DataType.AUTOVANISHSTATE, !user.is(DataType.AUTOVANISHSTATE));

        } else {
            return;
        }

        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);

        updateInventory(user, event.getInventory());
    }

    public void updateInventory(final User user, final Inventory inventory) {

        final ItemStack bar = new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build();

        InventoryUtil.fillSideRows(inventory, bar);

        if (user.is(DataType.MSGSTATE)) {
            inventory.setItem(11, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setTextur("OGFlN2JmNDUyMmIwM2RmY2M4NjY1MTMzNjNlYWE5MDQ2ZmRkZmQ0YWE2ZjFmMDg4OWYwM2MxZTYyMTZlMGVhMCJ9fX0=", UUID.randomUUID())
                    .setDisplayname("§8» §7MSG §8× §a§oAktiviert")
                    .setLore("", "§7  Klicke um private Nachrichten zu §c§odeaktivieren§7.")
                    .build());
        } else {
            inventory.setItem(11, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setTextur("Mzk5MzY2YTNmMjMzNTZkNDRjYjNhNzIyZjgxODdjN2QwN2JhOTc1MDFmNzZkMTVmMmIzMTFlN2ZmZTVhNGRhYyJ9fX0=", UUID.randomUUID())
                    .setDisplayname("§8» §7MSG §8× §c§oDeaktiviert")
                    .setLore("", "§7  Klicke um private Nachrichten zu §a§oaktivieren§7.")
                    .build());
        }


        if (user.is(DataType.TPASTATE)) {
            inventory.setItem(12, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setTextur("YTM4MjYxODFjZTkwMTJiNjY1ODY1ZjNhYzAwNjYzMDdiNGQwMmRhMjgxNTQwMTA0ZTA0NjFmZmVmYTc0NTlmZCJ9fX0=", UUID.randomUUID())
                    .setDisplayname("§8» §7TPA §8× §a§oAktiviert")
                    .setLore("", "§7  Klicke um TP Anfragen zu §c§odeaktivieren§7.")
                    .build());
        } else {
            inventory.setItem(12, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setTextur("Mzk5MzY2YTNmMjMzNTZkNDRjYjNhNzIyZjgxODdjN2QwN2JhOTc1MDFmNzZkMTVmMmIzMTFlN2ZmZTVhNGRhYyJ9fX0=", UUID.randomUUID())
                    .setDisplayname("§8» §7TPA §8× §c§oDeaktiviert")
                    .setLore("", "§7  Klicke um TP Anfragen zu §a§oaktivieren§7.")
                    .build());
        }

        if (user.is(DataType.SCOREBOARDSTATE)) {
            inventory.setItem(13, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setTextur("M2U0MWM2MDU3MmM1MzNlOTNjYTQyMTIyODkyOWU1NGQ2Yzg1NjUyOTQ1OTI0OWMyNWMzMmJhMzNhMWIxNTE3In19fQ==", UUID.randomUUID())
                    .setDisplayname("§8» §7Scoreboard §8× §a§oAktiviert")
                    .setLore("", "§7  Klicke um das Scoreboard zu §c§odeaktivieren§7.")
                    .build());
        } else {
            inventory.setItem(13, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setTextur("Mzk5MzY2YTNmMjMzNTZkNDRjYjNhNzIyZjgxODdjN2QwN2JhOTc1MDFmNzZkMTVmMmIzMTFlN2ZmZTVhNGRhYyJ9fX0=", UUID.randomUUID())
                    .setDisplayname("§8» §7Scoreboard §8× §c§oDeaktiviert")
                    .setLore("", "§7  Klicke um das Scoreboard zu §a§oaktivieren§7.")
                    .build());
        }

        inventory.setItem(15, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("NGNkM2M0NWQ3YjgzODRlOGExOTYzZTRkYTBhZTZiMmRhZWIyYTNlOTdhYzdhMjhmOWViM2QzOTU5NzI1Nzk5ZiJ9fX0=", UUID.randomUUID())
                .setDisplayname("§8» §7Crate Settings §8× §c§oÖffnen")
                .build());

        if (user.is(DataType.KILLHOLOSTATE)) {
            inventory.setItem(14, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setTextur("NzIxOTYxNjQyZDk4Y2I4MDFhMTc2MDhiYTRhMjMyOTc3YjQ2MmVmNjY3OWI5NzhjOWJiNjQ5NWQxNTE2MjczIn19fQ==", UUID.randomUUID())
                    .setDisplayname("§8» §7Killhologramm §8× §a§oAktiviert")
                    .setLore("", "§7  Klicke um das Killhologramm zu §c§odeaktivieren§7.")
                    .build());
        } else {
            inventory.setItem(14, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setTextur("Mzk5MzY2YTNmMjMzNTZkNDRjYjNhNzIyZjgxODdjN2QwN2JhOTc1MDFmNzZkMTVmMmIzMTFlN2ZmZTVhNGRhYyJ9fX0=", UUID.randomUUID())
                    .setDisplayname("§8» §7Killhologramm §8× §c§oDeaktiviert")
                    .setLore("", "§7  Klicke um das Killhologramm zu §a§oaktivieren§7.")
                    .build());
        }
        if (user.is(DataType.SPAWNTELEPORT)) {
            inventory.setItem(21, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setTextur("MTU2MmU4YzFkNjZiMjFlNDU5YmU5YTI0ZTVjMDI3YTM0ZDI2OWJkY2U0ZmJlZTJmNzY3OGQyZDNlZTQ3MTgifX19", UUID.randomUUID())
                    .setDisplayname("§8» §7Spawn Teleport §8× §a§oAktiviert")
                    .setLore("", "§7  Klicke um die Teleportation zum Spawn beim joinen zu §c§odeaktivieren§7.")
                    .build());
        } else {
            inventory.setItem(21, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setTextur("Mzk5MzY2YTNmMjMzNTZkNDRjYjNhNzIyZjgxODdjN2QwN2JhOTc1MDFmNzZkMTVmMmIzMTFlN2ZmZTVhNGRhYyJ9fX0=", UUID.randomUUID())
                    .setDisplayname("§8» §7Spawn Teleport §8× §c§oDeaktiviert")
                    .setLore("", "§7  Klicke um die Teleportation zum Spawn beim joinen zu §a§oaktivieren§7.")
                    .build());
        }
        if (user.is(DataType.RESPAWNKIT)) {
            inventory.setItem(22, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setTextur("OWE0MTgxYzFhNGM0NWVmNjExNDA2YzdkZDY4YTRkYzQ4MzEyMTgwODdkYWUxOWE2MmE0ZGVjYjJkY2RkMzVjMCJ9fX0=", UUID.randomUUID())
                    .setDisplayname("§8» §7Respawnkit §8× §a§oAktiviert")
                    .setLore("", "§7  Klicke um das Respawnkit zu §c§odeaktivieren§7.")
                    .build());
        } else {
            inventory.setItem(22, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setTextur("Mzk5MzY2YTNmMjMzNTZkNDRjYjNhNzIyZjgxODdjN2QwN2JhOTc1MDFmNzZkMTVmMmIzMTFlN2ZmZTVhNGRhYyJ9fX0=", UUID.randomUUID())
                    .setDisplayname("§8» §7Respawnkit §8× §c§oDeaktiviert")
                    .setLore("", "§7  Klicke um das Respawnkit zu §a§oaktivieren§7.")
                    .build());
        }

        if (PermissionUtil.hasPermission(user.getPlayer(), "chatlines", false)) {
            if (user.is(DataType.CHATLINESSTATE)) {
                inventory.setItem(20, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setTextur("MzE5ZjUwYjQzMmQ4NjhhZTM1OGUxNmY2MmVjMjZmMzU0MzdhZWI5NDkyYmNlMTM1NmM5YWE2YmIxOWEzODYifX19", UUID.randomUUID())
                        .setDisplayname("§8» §7ChatLines §8× §a§oAktiviert")
                        .setLore("", "§7  Klicke um ChatLines zu §c§odeaktivieren§7.")
                        .build());
            } else {
                inventory.setItem(20, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setTextur("Mzk5MzY2YTNmMjMzNTZkNDRjYjNhNzIyZjgxODdjN2QwN2JhOTc1MDFmNzZkMTVmMmIzMTFlN2ZmZTVhNGRhYyJ9fX0=", UUID.randomUUID())
                        .setDisplayname("§8» §7ChatLines §8× §c§oDeaktiviert")
                        .setLore("", "§7  Klicke um ChatLines zu §a§oaktivieren§7.")
                        .build());
            }
        }

        if (PermissionUtil.hasPermission(user.getPlayer(), "commandwatch", false)) {
            if (user.is(DataType.COMMANDWATCHSTATE)) {
                inventory.setItem(38, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setTextur("NjJmYmY0NGU1Yjg4MzFhOWU4Y2MzY2EzNzJiNjQyNmEwNjg1NmQyMTU5MTk0NDg2MzRmOGY5YjUxZWY5M2FmMCJ9fX0=", UUID.randomUUID())
                        .setDisplayname("§8» §7Commandwatch §8× §a§oAktiviert")
                        .setLore("", "§7  Klicke um Commandwatch zu §c§odeaktivieren§7.")
                        .build());
            } else {
                inventory.setItem(38, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§8» §7Commandwatch §8× §c§oDeaktiviert")
                        .setTextur("Mzk5MzY2YTNmMjMzNTZkNDRjYjNhNzIyZjgxODdjN2QwN2JhOTc1MDFmNzZkMTVmMmIzMTFlN2ZmZTVhNGRhYyJ9fX0=", UUID.randomUUID())
                        .setLore("", "§7  Klicke um Commandwatch zu §a§oaktivieren§7.")
                        .build());
            }
        }

        if (PermissionUtil.hasPermission(user.getPlayer(), "msgspy", false)) {
            if (user.is(DataType.MSGSPYSTATE)) {
                inventory.setItem(39, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setTextur("MzU1OWRhNDBhOWVlZWZhMjUzNjkzYTQ5ODEyZGQyNjlmNDVlYzMwNjI4YTIyZGY5ZTVmNGU4NWJiOWNiYjY1In19fQ==", UUID.randomUUID())
                        .setDisplayname("§8» §7MSG Spy §8× §a§oAktiviert")
                        .setLore("", "§7  Klicke um MSG Spy zu §c§odeaktivieren§7.")
                        .build());
            } else {
                inventory.setItem(39, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§8» §7MSG Spy §8× §c§oDeaktiviert")
                        .setTextur("Mzk5MzY2YTNmMjMzNTZkNDRjYjNhNzIyZjgxODdjN2QwN2JhOTc1MDFmNzZkMTVmMmIzMTFlN2ZmZTVhNGRhYyJ9fX0=", UUID.randomUUID())
                        .setLore("", "§7  Klicke um MSG Spy zu §a§oaktivieren§7.")
                        .build());
            }
        }

        if (PermissionUtil.hasPermission(user.getPlayer(), "autovanish", false)) {
            if (user.is(DataType.AUTOVANISHSTATE)) {
                inventory.setItem(40, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setTextur("NzJhN2RjYmY3ZWNhNmI2ZjYzODY1OTFkMjM3OTkxY2ExYjg4OGE0ZjBjNzUzZmY5YTMyMDJjZjBlOTIyMjllMyJ9fX0=", UUID.randomUUID())
                        .setDisplayname("§8» §7AutoVanish §8× §a§oAktiviert")
                        .setLore("", "§7  Klicke um AutoVanish zu §c§odeaktivieren§7.")
                        .build());
            } else {
                inventory.setItem(40, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§8» §7AutoVanish §8× §c§oDeaktiviert")
                        .setTextur("Mzk5MzY2YTNmMjMzNTZkNDRjYjNhNzIyZjgxODdjN2QwN2JhOTc1MDFmNzZkMTVmMmIzMTFlN2ZmZTVhNGRhYyJ9fX0=", UUID.randomUUID())
                        .setLore("", "§7  Klicke um AutoVanish zu §a§oaktivieren§7.")
                        .build());
            }
        }
    }
}
