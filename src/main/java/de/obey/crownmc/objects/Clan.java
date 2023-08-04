package de.obey.crownmc.objects;

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.util.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Getter @Setter
public class Clan {

    private final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

    private final File clanFile;
    private final YamlConfiguration cfg;
    private ArrayList<String> memberList = new ArrayList<>();
    private ArrayList<String> moderatorList = new ArrayList<>();
    private ArrayList<String> trustedList = new ArrayList<>();

    private String clanName, clanTag;
    private UUID ownerUUID;
    private Inventory clanChest, clanInfo, memberInventory, clanShop;

    private int kills, deaths, trophies, chestSlots, memberCap, xp, level;

    public Clan(final String name) {
        clanFile = FileUtil.getFile("/clanFiles/" + name + ".yml");
        cfg = FileUtil.getCfg(clanFile);

        this.clanName = name;
    }

    public void saveFileData() {
        cfg.set("clan.members", memberList);
        cfg.set("clan.mods", moderatorList);
        cfg.set("clan.trusted", trustedList);
        cfg.set("clan.tag", clanTag);
        cfg.set("clan.memberCap", memberCap);
        cfg.set("clan.chest.slots", chestSlots);

        final ArrayList<ItemStack> chestContents = new ArrayList<>();

        if(clanChest != null) {
            for (int i = 0; i < clanChest.getSize(); i++) {
                if (i + 1 <= chestSlots)
                    chestContents.add(clanChest.getItem(i));
            }
        }

        cfg.set("clan.chest.items", chestContents);

        FileUtil.saveToFile(clanFile, cfg);
    }

    public void loadFileData() {
        if (cfg.contains("clan.members"))
            memberList = (ArrayList<String>) cfg.getStringList("clan.members");

        if (cfg.contains("clan.mods"))
            moderatorList = (ArrayList<String>) cfg.getStringList("clan.mods");

        if (cfg.contains("clan.trusted"))
            trustedList = (ArrayList<String>) cfg.getStringList("clan.trusted");

        clanTag = cfg.getString("clan.tag", clanName);
        chestSlots = cfg.getInt("clan.chest.slots", 9);
        memberCap = cfg.getInt("clan.memberCap", 2);

        clanChest = Bukkit.createInventory(null, 9*6, "§7Chest §8(§f " + clanTag + "§8)");
        clanInfo = Bukkit.createInventory(null, 9*5, "§7Clan §8(§f" + clanTag + "§8)");
        memberInventory = Bukkit.createInventory(null, 9*6, "§7Member §8(§f" + clanTag + "§8)");
        clanShop = Bukkit.createInventory(null, 9*6, "§7ClanShop");

        InventoryUtil.fillSideRows(clanInfo, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());
        InventoryUtil.fillSideRows(clanShop, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());

        loadChestContents();
        updateClanChest();
        updateClanInfo();
        updateClanShop();
    }

    public void loadChestContents() {
        if (cfg.contains("clan.chest.items")) {
            final ArrayList<ItemStack> contents = (ArrayList<ItemStack>) cfg.getList("clan.chest.items");
            final AtomicInteger slot = new AtomicInteger();

            if (!contents.isEmpty()) {
                contents.forEach(item -> {
                    if(slot.get() + 1 > chestSlots)
                        return;

                    if (item == null) {
                        clanChest.setItem(slot.get(), new ItemStack(Material.AIR));
                    } else {
                        clanChest.setItem(slot.get(), item);
                    }

                    slot.getAndAdd(1);
                });
            }
        }
    }

    public void updateClanChest() {
        for (int i = 0; i < clanChest.getSize(); i++) {
            if(i + 1 > chestSlots) {
                clanChest.setItem(i, new ItemBuilder(Material.BARRIER)
                                .setDisplayname("§c§onicht freigeschaltet")
                        .build());
            }

        }
    }

    public void updateClanInfo() {
        clanInfo.setItem(13, new ItemBuilder(Material.ITEM_FRAME)
                        .setDisplayname("§7§lClan Informationen")
                        .setLore("",
                                "§8➬ §7Clan§8: §f§o" + clanName + "§8 (§r" + clanTag + "§8)",
                                "",
                                "§8  - §7Leader§8: §f§o" + Bukkit.getOfflinePlayer(ownerUUID).getName(),
                                "§8  - §7Mitglieder§8: §f" + memberList.size() + "§8/§f" + memberCap,
                                "§8  - §7Level§8: §f" + level + " §8(§f" + messageUtil.formatLong(xp) + "XP§8)",
                                "§8  - §7Kills§8: §a" + messageUtil.formatLong(kills),
                                "§8  - §7Tode§8: §c" + messageUtil.formatLong(deaths),
                                "")
                .build());

        clanInfo.setItem(29, new ItemBuilder(Material.PAPER)
                        .setDisplayname("§7Mitglieder Verwaltung")
                        .setLore("",
                                "§7§lLinksklick",
                                "§8  - §7Öffnet die mitglieder Verwaltung§8.",
                                "")
                .build());

        clanInfo.setItem(30, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setTextur("ZTIyM2I3YjhjZjI4NTg0ZDEzYTBlMTVmMjgzODE1YTRjMGQyNmY4NzBiMTdhZWMxMjQ5MDM5ZWEwM2Y3YzNlOCJ9fX0=", UUID.randomUUID())
                .setDisplayname("§7Clan Truhe")
                .setLore("",
                        "§7§lLinksklick",
                        "§8  - §7Öffnet die Clantruhe§8.",
                        "")
                .build());

        clanInfo.setItem(31, new ItemBuilder(Material.EMERALD)
                .setDisplayname("§7Clan Shop")
                .setLore("",
                        "§7§lLinksklick",
                        "§8  - §7Öffnet den Clanshop§8.",
                        "")
                .build());

        clanInfo.setItem(33, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setDisplayname("§c§lClan Löschen")
                        .setTextur("M2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ==", UUID.randomUUID())
                .setLore("",
                        "§7§lLinksklick",
                        "§8  - §7Öffnet den Clanshop§8.",
                        "")
                .build());
    }

    public void updateMemberInventory() {
        memberInventory.clear();

        InventoryUtil.fillSideRows(memberInventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());
        memberInventory.setItem(52, new ItemBuilder(Material.BARRIER).setDisplayname("§c§oZurück").build());

        for (final String text : memberList) {
            final UUID uuid = UUID.fromString(text);
            final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            final ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            final SkullMeta meta = (SkullMeta) item.getItemMeta();

            meta.setOwner(player.getName());
            meta.setDisplayName("§8»§7 " + player.getName() + (isLeader(uuid) ? " §8(§4§lLeader§8)" : (isMod(uuid) ? " §8(§5§lMod§8)" : isTrusted(uuid) ? " §8(§a§oTrusted§8)" : "")));

            final List<String> lore = new ArrayList<>();

            lore.add("");
            if(isMod(uuid)) {
                lore.add("§f§lRechtsklick");
                lore.add("§8  - §7Kickt diesen Spieler aus dem Clan§8.");
                lore.add("");
                lore.add("§f§lShift + Rechtsklick");
                lore.add("§8  - §7Demotet diesen Spieler§8.");
                lore.add("");
            } else if(isTrusted(uuid)) {
                lore.add("§f§lRechtsklick");
                lore.add("§8  - §7Kickt diesen Spieler aus dem Clan§8.");
                lore.add("");
                lore.add("§f§lShift + Rechtsklick");
                lore.add("§8  - §7Demotet diesen Spieler§8.");
                lore.add("");
                lore.add("§f§lShift + Linksklick");
                lore.add("§8  - §7Promotet diesen Spieler§8.");
                lore.add("");
            } else if (!isLeader(uuid)) {
                lore.add("§f§lRechtsklick");
                lore.add("§8  - §7Kickt diesen Spieler aus dem Clan§8.");
                lore.add("");
                lore.add("§f§lShift + Linksklick");
                lore.add("§8  - §7Promotet diesen Spieler§8.");
                lore.add("");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);

            memberInventory.addItem(item);
        }

        if(memberList.size() < memberCap) {
            memberInventory.addItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setDisplayname("§a§oMitglied hinzufügen")
                    .setTextur("M2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0=", UUID.randomUUID())
                    .build());
        } else {
            memberInventory.addItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setDisplayname("§c§oMember Limit erreicht")
                            .setLore("",
                                    "§7Das member Limit kann im Clanshop erweitert werden§8.",
                                    "")
                    .setTextur("M2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ==", UUID.randomUUID())
                    .build());
        }
    }

    public void updateClanShop() {
        clanShop.setItem(1, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§7Member Slot")
                        .setTextur("M2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0=", UUID.randomUUID())
                        .setLore("",
                                "§7§lInformation",
                                "§8- §7Preis§8:§e§o " + messageUtil.formatLong(memberCap * 2500L) + "§6§l$",
                                "",
                                "§7§lLinksklick",
                                "§8 -§7 Kauf einen member Slot für den Clan§8.",
                                "")
                .build());

        if(chestSlots < 53) {
            clanShop.setItem(2, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setDisplayname("§7Clantruhe Slot")
                    .setTextur("M2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0=", UUID.randomUUID())
                    .setLore("",
                            "§7§lInformation",
                            "§8- §7Preis§8:§e§o " + messageUtil.formatLong(chestSlots * 200L) + "§6§l$",
                            "",
                            "§7§lLinksklick",
                            "§8 -§7 Kauf einen Slot für die Clantruhe§8.",
                            "")
                    .build());
        }

        clanShop.setItem(52, new ItemBuilder(Material.BARRIER).setDisplayname("§c§oZurück").build());
    }

    public void buyMemberSlot(final Player player, final long amount) {
        memberCap++;

        updateMemberInventory();
        updateClanInfo();
        updateClanShop();

        sendMessageToAllClanMembers(player.getName() + " hat einen member Slot für " + messageUtil.formatLong(amount) + "§6§l$§7 gekauft§8.");
    }

    public void buyChestSlot(final Player player, final long amount) {
        chestSlots++;

        updateClanInfo();
        updateClanShop();
        updateClanChest();

        sendMessageToAllClanMembers(player.getName() + " hat einen clantruhen Slot für " + messageUtil.formatLong(amount) + "§6§l$§7 gekauft§8.");
    }

    public void newMemberJoin(final Player player) {
        memberList.add(player.getUniqueId().toString());

        sendMessageToAllClanMembers("§f§o" + player.getName() + "§7 hat den Clan betreten§8.");

        updateMemberInventory();
        updateClanInfo();
    }

    public void openMemberInventory(final Player player) {
        updateMemberInventory();
        player.openInventory(memberInventory);
    }

    public void sendMessageToAllClanMembers(final String message) {
        for (final String textUUID : memberList) {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(textUUID));

            if(offlinePlayer.isOnline())
                messageUtil.sendMessage(offlinePlayer.getPlayer(), message);
        }
    }

    public void addXP(long amount) {
        if (Bools.doubleXP)
            amount *= 2;

        xp += amount;
        ClanLevelUtil.checkForLevelUp(this);
    }

    public boolean isTrusted(final UUID uuid) {
        return trustedList.contains(uuid.toString());
    }

    public boolean isMod(final UUID uuid) {
        return moderatorList.contains(uuid.toString());
    }

    public boolean isLeader(final UUID uuid) {
        return ownerUUID.toString().equalsIgnoreCase(uuid.toString());
    }

    public boolean isNeeded() {
        return false;
    }

}
