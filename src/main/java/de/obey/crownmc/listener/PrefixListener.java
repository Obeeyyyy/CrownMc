package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       06.11.2022 / 17:54

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class PrefixListener implements Listener {

    @NonNull
    private final MessageUtil messageUtil;
    @NonNull
    private final UserHandler userHandler;

    private static final ArrayList<UUID> settingChatPrefix = new ArrayList<>();
    private static final Map<Player, String> confirming = new HashMap<>();

    public static boolean isSettingChatPrefix(final Player player, final String message) {
        final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
        final UserHandler userHandler = CrownMain.getInstance().getInitializer().getUserHandler();

        if (settingChatPrefix.contains(player.getUniqueId())) {
            if (message.equalsIgnoreCase("cancel")) {
                settingChatPrefix.remove(player.getUniqueId());
                player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                messageUtil.sendMessage(player, "Abgebrochen, du hast deinen Gutschein zurück bekommen§8.");
                InventoryUtil.addItem(player, new ItemBuilder(Material.PAPER)
                        .setDisplayname("§8» §6§lCUSTOM §e§lPREFIX")
                        .setLore("",
                                "§8▰§7▱  §6§lRechtsklick",
                                "  §8- §7Schreibe den gewünschten Prefix",
                                "  §8- §7in den Chat und bestätige ihn§8.",
                                "")
                        .build());
                return true;
            }

            if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message)).length() > 15 ||
                    ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message)).length() <= 3) {
                messageUtil.sendMessage(player, "Der Prefix ist zu lang/kurz, bitte wähle einen Prefix der weniger als 16 und mehr als 3 Zeichen hat§8.");
                player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                return true;
            }

            if(!CrownMain.getInstance().getInitializer().getChatFilterHandler().runChatFilterCheck(player, message)) {
                messageUtil.sendMessage(player, "Der Prefix §8'§r" + message + "§8'§7 enthält verbotene Wörter oder Buchstaben§8.");
                return true;
            }

            settingChatPrefix.remove(player.getUniqueId());
            confirming.put(player, message);

            messageUtil.sendMessage(player, "Schreibe §8'§c§ocancel§8'§7 um den Vorgang abzubrechen§8.");
            messageUtil.sendMessage(player, "Schreibe §8'§c§oNEIN§8' §7um einen neuen Prefix zu wählen§8.");
            messageUtil.sendMessage(player, "Schreibe §8'§a§oJA§8' §7um einen neuen Prefix zu bestätigen§8.");
            messageUtil.sendMessage(player, "Preview§8:§r " + confirming.get(player) + " " + player.getName());

            return true;
        }

        if (confirming.containsKey(player)) {

            if (message.equalsIgnoreCase("ja")) {
                if (!userHandler.getUserInstant(player.getUniqueId()).getPrefix().addPrefix(confirming.get(player))) {
                    messageUtil.sendMessage(player, "Du hast diesen Prefix bereits, wähle einen anderen§8.");
                    return true;
                }
                messageUtil.sendMessage(player, "Du hast den Prefix§8: §r" + confirming.get(player) + "§7 erstellt§8.");
                userHandler.getUserInstant(player.getUniqueId()).getPrefix().setActivePrefix(confirming.get(player));

                confirming.remove(player);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                return true;
            }

            if (message.equalsIgnoreCase("nein")) {
                messageUtil.sendMessage(player, "Schreibe §8'§c§ocancel§8'§7 um den Vorgang abzubrechen§8,§7 oder wähle einen neuen Prefix§8.");
                settingChatPrefix.add(player.getUniqueId());
                confirming.remove(player);
                return true;
            }

            if (message.equalsIgnoreCase("cancel")) {
                confirming.remove(player);
                player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                messageUtil.sendMessage(player, "Abgebrochen, du hast deinen Gutschein zurück bekommen§8.");
                InventoryUtil.addItem(player, new ItemBuilder(Material.PAPER)
                        .setDisplayname("§8» §6§lCUSTOM §e§lPREFIX")
                        .setLore("",
                                "§8▰§7▱  §6§lRechtsklick",
                                "  §8- §7Schreibe den gewünschten Prefix",
                                "  §8- §7in den Chat und bestätige ihn§8.",
                                "")
                        .build());
                return true;
            }

            messageUtil.sendMessage(player, "Schreibe §8'§c§ocancel§8'§7 um den Vorgang abzubrechen§8.");
            messageUtil.sendMessage(player, "Schreibe §8'§c§oNEIN§8' §7um einen neuen Prefix zu wählen§8.");
            messageUtil.sendMessage(player, "Schreibe §8'§a§oJA§8' §7um einen neuen Prefix zu bestätigen§8.");
            messageUtil.sendMessage(player, "Preview§8:§r " + confirming.get(player) + " " + player.getName());

            return true;
        }

        return false;
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (InventoryUtil.isItemInHandWithDisplayname(event.getPlayer(), "§8» §6§lCUSTOM §e§lPREFIX")) {
            event.setCancelled(true);

            if (settingChatPrefix.contains(player.getUniqueId())) {
                messageUtil.sendSyntax(player, "Du setzt bereits einen CustomPrefix§8.");
                return;
            }

            InventoryUtil.removeItemInHand(player, 1);
            messageUtil.sendMessage(player, "Gebe deinen gewünschten Prefix in den Chat ein§8.");
            messageUtil.sendMessage(player, "Nutze Farbcodes wie §f&§f6 §7um Farben zu nutzen§8.");
            messageUtil.sendMessage(player, "Schreibe §8'§c§ocancel§8'§7 um abzubrechen§8.");

            settingChatPrefix.add(player.getUniqueId());
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

            return;
        }

        if (InventoryUtil.isItemInHandWithDisplayname(event.getPlayer(), "§8» §f§lPrefix Gutschein")) {
            event.setCancelled(true);

            final String prefix = event.getPlayer().getItemInHand().getItemMeta().getLore().get(2).replace("  §8- §7Prefix§8: ", "");

            final User user = userHandler.getUserInstant(player.getUniqueId());

            if (user == null)
                return;

            if (user.getPrefix().getPrefixList().contains(prefix)) {
                messageUtil.sendMessage(player, "Du hast diesen Prefix schon freigeschlatet§8.");
                player.playSound(player.getLocation(), Sound.EXPLODE, 1, 1);
                return;
            }

            InventoryUtil.removeItemInHand(player, 1);
            messageUtil.sendMessage(player, "Du hast den Prefix§8: " + prefix + "§7 freigeschlatet§8.");
            user.getPrefix().addPrefix(prefix);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        }
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if (!InventoryUtil.isInventoryTitle(event.getInventory(), "§f§lPREFIX"))
            return;

        event.setCancelled(true);

        if (!event.isLeftClick())
            return;

        if (!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§f§lPREFIX"))
            return;

        final Player player = (Player) event.getWhoClicked();
        final User user = userHandler.getUserInstant(player.getUniqueId());

        if (event.getSlot() == 36) {
            user.getPrefix().setActivePrefix("");
            user.getPrefix().updatePrefixInventory(event.getClickedInventory());
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            messageUtil.sendMessage(player, "Dein Prefix wurde zurückgesetzt§8.");
            return;
        }

        final ItemStack prefixItem = event.getCurrentItem();

        if (prefixItem != null &&
                prefixItem.getType() != Material.AIR &&
                prefixItem.getType() == Material.PAPER &&
                prefixItem.hasItemMeta()) {

            if (!user.getPrefix().getActivePrefix().equalsIgnoreCase(prefixItem.getItemMeta().getDisplayName())) {
                user.getPrefix().setActivePrefix(prefixItem.getItemMeta().getDisplayName());
                user.getPrefix().updatePrefixInventory(event.getClickedInventory());
                player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 1, 1);
                messageUtil.sendMessage(player, "Du hast den Prefix " + prefixItem.getItemMeta().getDisplayName() + "§7 aktiviert§8.");
                return;
            }

            user.getPrefix().setActivePrefix("");
            user.getPrefix().updatePrefixInventory(event.getClickedInventory());
            player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 1, 1);
            messageUtil.sendMessage(player, "Du hast den Prefix " + prefixItem.getItemMeta().getDisplayName() + "§7 deaktiviert§8.");
        }
    }

}
