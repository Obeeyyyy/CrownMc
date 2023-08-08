package de.obey.crownmc.commands;

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.ClanHandler;
import de.obey.crownmc.handler.ScoreboardHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.objects.Clan;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@RequiredArgsConstructor @NonNull
public class ClanCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final ClanHandler clanHandler;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 2) {

            if (args[0].equalsIgnoreCase("accept")) {

                if (!clanHandler.getInvites().containsKey(player.getUniqueId()))
                    return false;

                if (clanHandler.isInClan(player)) {
                    messageUtil.sendMessage(player, "Du bist bereits in einem Clan§8.");
                    return false;
                }

                final Clan clan = clanHandler.getClan(clanHandler.getInvites().get(player.getUniqueId()).toLowerCase());

                if(clan == null) {
                    messageUtil.sendMessage(player, "§c§oEin Fehler ist aufgetreten, bitte kontaktieren den Support§8.");
                    clanHandler.getInvites().remove(player.getUniqueId());
                    return false;
                }

                if(clan.getMemberList().size() >= clan.getMemberCap()) {
                    messageUtil.sendMessage(player, "Der Clan hat das member Limit erreicht§8.");
                    return false;
                }

                final User user = userHandler.getUserInstant(player.getUniqueId());

                clan.newMemberJoin(player);
                user.setClan(clan);
                user.setString(DataType.CLANNAME, clan.getClanName());
                clanHandler.getInvites().remove(player.getUniqueId());

                return false;
            }

            if (args[0].equalsIgnoreCase("deny")) {

                if (!clanHandler.getInvites().containsKey(player.getUniqueId()))
                    return false;

                if (clanHandler.isInClan(player)) {
                    messageUtil.sendMessage(player, "Du bist bereits in einem Clan§8.");
                    return false;
                }

                final Clan clan = clanHandler.getClan(clanHandler.getInvites().get(player.getUniqueId()).toLowerCase());

                if(clan == null) {
                    messageUtil.sendMessage(player, "§c§oEin Fehler ist aufgetreten, bitte kontaktieren den Support§8.");
                    clanHandler.getInvites().remove(player.getUniqueId());
                    return false;
                }

                clan.sendMessageToAllClanMembers(player.getName() + " hat die Claneinladung §c§oabgelehnt§8.");
                messageUtil.sendMessage(sender, "Du hast die Einladung von " + clan.getClanName() + " abgelehnt§8.");
                clanHandler.getInvites().remove(player.getUniqueId());

                return false;
            }
        }

        if(!PermissionUtil.hasPermission(sender, "admin", false)) {
            clanHandler.openClanCommandInventory(player);
            return false;
        }

        if(args.length == 0) {
            clanHandler.openClanCommandInventory(player);
            return false;
        }

        if(args.length == 3) {
            if (!clanHandler.exists(sender, args[1]))
                return false;

            final int amount = messageUtil.getInt(sender, args[2]);

            if (amount < 0)
                return false;

            final Clan clan = clanHandler.getClan(args[1]);

            if (args[0].equalsIgnoreCase("setkills")) {
                clan.setKills(amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " kills -> " + clan.getKills());
                return false;
            }

            if (args[0].equalsIgnoreCase("addkills")) {
                clan.setKills(clan.getKills() + amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " kills -> " + clan.getKills());
                return false;
            }

            if (args[0].equalsIgnoreCase("setdeaths")) {
                clan.setKills(amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " deaths -> " + clan.getDeaths());
                return false;
            }

            if (args[0].equalsIgnoreCase("adddeaths")) {
                clan.setDeaths(clan.getDeaths() + amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " deaths -> " + clan.getDeaths());
                return false;
            }

            if (args[0].equalsIgnoreCase("settrophies")) {
                clan.setTrophies(amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " trophies -> " + clan.getTrophies());
                return false;
            }

            if (args[0].equalsIgnoreCase("addtrophies")) {
                clan.setTrophies(clan.getTrophies() + amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " trophies -> " + clan.getTrophies());
                return false;
            }

            if (args[0].equalsIgnoreCase("setchestslots")) {
                clan.setChestSlots(amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " chest slots -> " + clan.getChestSlots());
                return false;
            }

            if (args[0].equalsIgnoreCase("addchestslots")) {
                clan.setChestSlots(clan.getChestSlots() + amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " chest slots -> " + clan.getChestSlots());
                return false;
            }

            if (args[0].equalsIgnoreCase("setmembercap")) {
                clan.setMemberCap(amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " member cap -> " + clan.getMemberCap());
                return false;
            }

            if (args[0].equalsIgnoreCase("addmembercap")) {
                clan.setMemberCap(clan.getMemberCap() + amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " member cap -> " + clan.getMemberCap());
                return false;
            }

            if (args[0].equalsIgnoreCase("setxp")) {
                clan.setXp(amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " XP -> " + clan.getXp());
                return false;
            }

            if (args[0].equalsIgnoreCase("addxp")) {
                clan.addXP(amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " XP -> " + clan.getXp());
                return false;
            }

            if (args[0].equalsIgnoreCase("setlevel")) {
                clan.setLevel(amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " level -> " + clan.getLevel());
                return false;
            }

            if (args[0].equalsIgnoreCase("addlevel")) {
                clan.setLevel(clan.getLevel() + amount);
                messageUtil.sendMessage(sender, clan.getClanName() + " level -> " + clan.getLevel());
                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/clan setkills <name> <wert>",
                "/clan setdeaths <name> <wert>",
                "/clan setmembercap <name> <wert>",
                "/clan setchestslots <name> <wert>",
                "/clan setxp <name> <wert>",
                "/clan setlevel <name> <wert>",
                "/clan addkills <name> <wert>",
                "/clan adddeaths <name> <wert>",
                "/clan addmembercap <name> <wert>",
                "/clan addchestslots <name> <wert>",
                "/clan addxp <name> <wert>",
                "/clan addlevel <name> <wert>"
        );

        return false;
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();

        // Clan erstellen
        if(InventoryUtil.isInventoryTitle(event.getInventory(), "§c§oKein Clan")) {
            event.setCancelled(true);

            if(!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§c§oKein Clan"))
                return;

            if(event.getSlot() == 13) {
                if(!clanHandler.getCreatingClan().contains(player.getUniqueId()))
                    clanHandler.getCreatingClan().add(player.getUniqueId());

                player.closeInventory();
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 0.5f, 1);
                messageUtil.sendMessage(player, "Schreibe den Clannamen und den Clantag in den Chat§8.");
                messageUtil.sendMessage(player, "Nutze folgendes Format§8: §fclanname clantag");
                messageUtil.sendMessage(player, "Schreibe §c§ocancel§7 um den Vorgang abzubrechen§8.");
            }

            return;
        }

        // Clan Info Inventory
        if(InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§7Clan ")) {
            event.setCancelled(true);

            if(!InventoryUtil.startsWithInventoryTitle(event.getClickedInventory(), "§7Clan "))
                return;

            final Clan clan = userHandler.getUserInstant(player.getUniqueId()).getClan();

            if(clan == null)
                return;

            // Open clan member list
            if(event.getSlot() == 29) {
                player.closeInventory();
                clan.openMemberInventory(player);
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.25f, 1);
                return;
            }

            // Open clan chest
            if(event.getSlot() == 30) {

                if(clan.isTrusted(player.getUniqueId()) ||
                    clan.isMod(player.getUniqueId()) ||
                    clan.isLeader(player.getUniqueId())) {

                    player.closeInventory();
                    player.openInventory(clan.getClanChest());
                    player.updateInventory();
                    player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.25f, 1);
                    return;
                }

                messageUtil.sendMessage(player, "Du kannst nicht auf die Clantruhe zugreifen§8.");
                player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);

                return;
            }

            // Open clan shop
            if(event.getSlot() == 31) {
                player.closeInventory();
                player.openInventory(clan.getClanShop());
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.25f, 1);
                return;
            }

            // delete clan
            if(event.getSlot() == 33) {

                if(!clan.isLeader(player.getUniqueId())) {
                    messageUtil.sendMessage(player, "Du bist nicht dazu berechtigt§8.");
                    return;
                }

                player.closeInventory();
                player.openInventory(InventoryUtil.getConfirmation("§c§oClan löschen"));
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.5f, 1);
                return;
            }

            return;
        }

        // Chest Inventory
        if(InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§7Chest ")) {

            if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER)
                event.setCancelled(true);

            return;
        }

        // Clan Inventory
        if(InventoryUtil.isInventoryTitle(event.getInventory(), "§7ClanShop")) {
            event.setCancelled(true);

            if(!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§7ClanShop"))
                return;

            final Clan clan = userHandler.getUserInstant(player.getUniqueId()).getClan();

            if(clan == null)
                return;

            if(event.getSlot() == 52) {
                player.closeInventory();
                player.openInventory(clan.getClanInfo());
                player.updateInventory();
                return;
            }

            if(event.isLeftClick() && !event.isShiftClick()) {
                if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR ||
                !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasLore())
                    return;

                // Member slot
                if(event.getSlot() == 1) {
                    final long price = Long.parseLong(event.getCurrentItem().getItemMeta().getLore().get(2).split(" ")[2].replace("§6§l$", "").replace(",", ""));

                    userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                        if(!messageUtil.hasEnougthMoney(user, price))
                            return;

                        user.removeLong(DataType.MONEY, price);
                        clan.buyMemberSlot(player, price);

                    });
                }

                // Clanchest slot
                if(event.getSlot() == 2) {
                    final long price = Long.parseLong(event.getCurrentItem().getItemMeta().getLore().get(2).split(" ")[2].replace("§6§l$", "").replace(",", ""));

                    userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                        if(!messageUtil.hasEnougthMoney(user, price))
                            return;

                        user.removeLong(DataType.MONEY, price);
                        clan.buyChestSlot(player, price);
                    });
                }
            }

            return;
        }

        // Member inventory
        if(InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§7Member ")) {
            event.setCancelled(true);

            final Clan clan = userHandler.getUserInstant(player.getUniqueId()).getClan();

            if(clan == null)
                return;

            if(event.getSlot() == 52) {
                player.closeInventory();
                player.openInventory(clan.getClanInfo());
                player.updateInventory();
                return;
            }

            if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
                return;

            if(!event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName())
                return;

            // Invite new member
            if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§a§oMitglied hinzufügen")) {

                if(!clan.isLeader(player.getUniqueId()) && !clan.isMod(player.getUniqueId())) {
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.25f, 1);
                    messageUtil.sendMessage(player, "Du bist nicht dazu berechtigt neue Mitglieder einzulanden§8.");
                    return;
                }

                if(!clanHandler.getInvitingMember().contains(player.getUniqueId()))
                    clanHandler.getInvitingMember().add(player.getUniqueId());

                player.closeInventory();

                player.playSound(player.getLocation(), Sound.CAT_MEOW, 0.5f, 1);
                messageUtil.sendMessage(player, "Schreibe den Spielernamen in den Chat§8.");
                messageUtil.sendMessage(player, "Nutze §c§ocancel §7um den Vorgang abzubrechen§8.");

                return;
            }

            if(event.getCurrentItem().getType() != Material.SKULL_ITEM)
                return;

            // kick player
            if(event.isRightClick() && !event.isShiftClick()) {
                if(!clan.isLeader(player.getUniqueId()) && !clan.isMod(player.getUniqueId())) {
                    messageUtil.sendMessage(player, "Du bist nicht dazu berechtigt Mitglieder zu kicken§8.");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return;
                }

                final String targetName = event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1];
                final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

                if(clan.isLeader(target.getUniqueId())) {
                    messageUtil.sendMessage(player, "Der Clanleader kann nicht gekickt werden§8.");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return;
                }

                if(clan.isMod(player.getUniqueId()) && clan.isMod(target.getUniqueId())) {
                    messageUtil.sendMessage(player, "Du bist nicht dazu berechtigt dieses Mitglieder zu kicken§8.");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return;
                }

                player.openInventory(InventoryUtil.getConfirmation("§c§oKick " + target.getName()));
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.5f, 1);

                return;
            }

            // demote player
            if(event.isRightClick() && event.isShiftClick()) {
                if(!clan.isLeader(player.getUniqueId()) && !clan.isMod(player.getUniqueId())) {
                    messageUtil.sendMessage(player, "Du bist nicht dazu berechtigt Mitglieder zu demoten§8.");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return;
                }

                final String targetName = event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1];
                final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

                if(clan.isLeader(target.getUniqueId())) {
                    messageUtil.sendMessage(player, "Der Clanleader kann nicht demotet werden§8.");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return;
                }

                if(!clan.isMod(target.getUniqueId()) && !clan.isTrusted(target.getUniqueId()))
                    return;

                if(clan.isMod(player.getUniqueId()) && clan.isMod(target.getUniqueId())) {
                    messageUtil.sendMessage(player, "Du bist nicht dazu berechtigt dieses Mitglied zu demoten§8.");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return;
                }

                if(clan.isMod(target.getUniqueId())) {
                    clan.getModeratorList().remove(target.getUniqueId().toString());
                    clan.getTrustedList().add(target.getUniqueId().toString());
                    clan.sendMessageToAllClanMembers("§f" + target.getName() + " §7wurde von §f" + player.getName() + " §7demotet§8. (§5§lMod §7-> §a§oTrusted§8)");
                } else {
                    clan.getTrustedList().remove(target.getUniqueId().toString());
                    clan.sendMessageToAllClanMembers("§f" + target.getName() + " §7wurde von §f" + player.getName() + " §7demotet§8. (§a§oTrusted §7-> Member§8)");
                }

                clan.updateMemberInventory();
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.5f, 1);

                return;
            }

            // promote player
            if(event.isLeftClick() && event.isShiftClick()) {
                if(!clan.isLeader(player.getUniqueId()) && !clan.isMod(player.getUniqueId())) {
                    messageUtil.sendMessage(player, "Du bist nicht dazu berechtigt Mitglieder zu promoten§8.");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return;
                }

                final String targetName = event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1];
                final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

                if(clan.isLeader(target.getUniqueId())) {
                    messageUtil.sendMessage(player, "Der Clanleader kann nicht promotet werden§8.");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                    return;
                }

                if(clan.isMod(target.getUniqueId())) {
                    messageUtil.sendMessage(player, "Clanmods können nicht promotet werden§8.");
                    return;
                }

                if(clan.isTrusted(target.getUniqueId())) {
                    clan.getModeratorList().add(target.getUniqueId().toString());
                    clan.getTrustedList().remove(target.getUniqueId().toString());
                    clan.sendMessageToAllClanMembers("§f" + target.getName() + " §7wurde von §f" + player.getName() + " §7promotet§8. (§a§oTrusted §7-> §5§lMod§8)");
                } else {
                    clan.getTrustedList().add(target.getUniqueId().toString());
                    clan.sendMessageToAllClanMembers("§f" + target.getName() + " §7wurde von §f" + player.getName() + " §7promotet§8. (§7Member -> §a§oTrusted§8)");
                }

                clan.updateMemberInventory();
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.5f, 1);

                return;
            }

        }

        // Clan löschen
        if(InventoryUtil.isInventoryTitle(event.getInventory(), "§c§oClan löschen")) {
            event.setCancelled(true);

            if(!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§c§oClan löschen"))
                return;

            if(event.getCurrentItem() == null)
                return;

            if(event.getCurrentItem().getType() != Material.STAINED_CLAY)
                return;

            System.out.println(event.getCurrentItem().getData());

            final User user = userHandler.getUserInstant(player.getUniqueId());
            final Clan clan = user.getClan();

            // grün
            if(event.getCurrentItem().getData().getData() == 5) {

                clanHandler.deleteClan(clan);
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);

                return;
            }

            // rot
            if(event.getCurrentItem().getData().getData() == 14) {
                player.closeInventory();
                player.openInventory(clan.getClanInfo());
            }
        }

        // Member kicken
        if(InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§c§oKick")) {
            event.setCancelled(true);

            if(!InventoryUtil.startsWithInventoryTitle(event.getClickedInventory(), "§c§oKick "))
                return;

            final User user = userHandler.getUserInstant(player.getUniqueId());
            final Clan clan = user.getClan();

            // grün
            if(event.getCurrentItem().getData().getData() == 5) {
                final String targetName = event.getInventory().getTitle().split(" ")[1];
                final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

                clan.sendMessageToAllClanMembers("§f" + target.getName() + "§7 wurde von §f" + player.getName() + "§7 aus dem Clan §c§ogekickt§8.");

                clan.getMemberList().remove(target.getUniqueId().toString());

                if (clan.isMod(target.getUniqueId()))
                    clan.getModeratorList().remove(target.getUniqueId().toString());

                if (clan.isTrusted(target.getUniqueId()))
                    clan.getTrustedList().remove(target.getUniqueId().toString());

                final User targetUser = userHandler.getUserInstant(target.getUniqueId());
                if (targetUser != null) {
                    targetUser.setClan(null);
                    targetUser.setString(DataType.CLANNAME, "-");
                    targetUser.addXP(0);
                }

                clan.updateClanInfo();
                clan.updateMemberInventory();
                player.closeInventory();
                player.openInventory(clan.getMemberInventory());

                player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.5f,1);

                return;
            }

            // rot
            if(event.getCurrentItem().getData().getData() == 14) {
                player.closeInventory();
                player.openInventory(clan.getClanInfo());
            }
        }
    }
}
