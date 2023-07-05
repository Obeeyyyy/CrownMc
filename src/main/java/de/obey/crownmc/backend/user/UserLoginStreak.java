package de.obey.crownmc.backend.user;
/*

    Author - Obey -> CrownMc
       01.07.2023 / 00:23

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public final class UserLoginStreak {

    private final User user;

    @Getter
    private final Inventory inventory = Bukkit.createInventory(null, 9*6, "§d§lLoginStreak");

    private final int maxStreak = 34;

    public UserLoginStreak(final User user) {
        this.user = user;
        InventoryUtil.fill(inventory, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 8).setDisplayname("§7-§8/§7-").build());

        updateInventory();
    }

    private int getSlotFromDay(final int day) {

        switch (day) {
            case 1 : return 0;
            case 2: return 1;
            case 3: return 2;
            case 4: return 3;
            case 5: return 4;
            case 6: return 5;
            case 7: return 6;
            case 8: return 7;
            case 9: return 8;
            case 10: return 17;
            case 11: return 26;
            case 12: return 35;
            case 13: return 34;
            case 14: return 33;
            case 15: return 24;
            case 16: return 23;
            case 17: return 22;
            case 18: return 31;
            case 19: return 30;
            case 20: return 29;
            case 21: return 20;
            case 22: return 19;
            case 23: return 18;
            case 24: return 27;
            case 25: return 36;
            case 26: return 45;
            case 27: return 46;
            case 28: return 47;
            case 29: return 48;
            case 30: return 49;
            case 31: return 50;
            case 32: return 51;
            case 33: return 52;
            case 34: return 53;
        }

        return -1;
    }

    public boolean isRewardSlot(final int slot) {

        switch (slot) {
            case 0 :
            case 3 :
            case 6 :

            case 17 :
            case 34 :
            case 23 :
            case 30 :
            case 19 :

            case 36 :
            case 47 :
            case 50 :
            case 53 :
                return true;
        }

        return false;
    }

    public void updateInventory() {
        long streak = user.getLong(DataType.LOGINSTREAK);
        final long lastRedeemed = user.getLong(DataType.LOGINLASTREWARD);

        if(streak > maxStreak)
            streak = streak - maxStreak;

        for (int i = 1; i <= maxStreak; i++) {
            final int slot = getSlotFromDay(i);

            // Streak noch nicht erreicht
            if(i > streak) {
                if(isRewardSlot(slot)) {
                    inventory.setItem(slot, getRewardItemNotReady(i, streak));
                    continue;
                }

                inventory.setItem(slot, getFence(i, streak));
                continue;
            }

            //Streak schon erreicht
            if(isRewardSlot(slot)) {

                // reward schon eingelöst
                if(System.currentTimeMillis() - 86400000L <= lastRedeemed * ((streak - i) == 0 ? 1 : streak - i)) {
                    inventory.setItem(slot, getRewardItemClaimed(i, streak));
                    continue;
                }

                // reward noch nicht eingelöst
                inventory.setItem(slot, getRewardItemReady(i, streak));
                continue;
            }

            inventory.setItem(slot, getGreen(i, streak));
        }

    }

    private ItemStack getGreen(final int day, final long streak) {
        return new ItemBuilder(Material.STAINED_GLASS_PANE,1, (byte) 5)
                .setDisplayname("§a§lTag §8(§7 " + day + " §8)")
                .setLore("",
                        "§8▰§7▱ §2§lInformation",
                        "§8  - §7Du bist bei Tag§8: §f" + streak,
                        "")
                .build();
    }

    private ItemStack getFence(final int day, final long streak) {
        return new ItemBuilder(Material.IRON_FENCE)
                .setDisplayname("§c§lTag §8(§7 " + day + " §8)")
                .setLore("",
                        "§8▰§7▱ §c§lInformation",
                        "§8  - §7Du bist bei Tag§8: §f" + streak,
                        "")
                .build();
    }

    private ItemStack getRewardItemReady(final int day, final long streak) {
        return new ItemBuilder(Material.STORAGE_MINECART)
                .setDisplayname("§a§lTag §8(§7 " + day + " §8)")
                .setLore("",
                        "§a§lInformation",
                        "§8  - §7Du bist bei Tag§8: §f" + streak,
                        "",
                        "§a§lLinksklick",
                        "§8  - §7Erhalte deine Belohnung§8.",
                        "",
                        "§a§lRechtsklick",
                        "§8  - §7Preview der Belohnung§8.",
                        "")
                .build();
    }

    private ItemStack getRewardItemClaimed(final int day, final long streak) {
        return new ItemBuilder(Material.HOPPER_MINECART)
                .setDisplayname("§a§lTag §8(§7 " + day + " §8)")
                .setLore("",
                        "§a§lInformation",
                        "§8  - §7Du bist bei Tag§8: §f" + streak,
                        "§8  - §7Du hast diese Belohnung bereits eingesammelt§8.",
                        "",
                        "§a§lRechtsklick",
                        "§8  - §7Preview der Belohnung§8.",
                        "")
                .build();
    }

    private ItemStack getRewardItemNotReady(final int day, final long streak) {
        return new ItemBuilder(Material.POWERED_MINECART)
                .setDisplayname("§c§lTag §8(§7 " + day + " §8)")
                .setLore("",
                        "§c§lInformation",
                        "§8  - §7Du bist bei Tag§8: §f" + streak,
                        "§8  - §7Noch §f" + (day - streak) + "§7 Tag" + (day - streak > 1 ? "e" : "") + "§8.",
                        "",
                        "§c§lRechtsklick",
                        "§8  - §7Preview der Belohnung§8.",
                        "")
                .build();
    }

    public void claimReward(final int day) {
        final long streak = user.getLong(DataType.LOGINSTREAK);

        if(streak > day) {
            user.setLong(DataType.LOGINLASTREWARD, System.currentTimeMillis() - ((streak-day) * 86400000L ));
        } else {
            user.setLong(DataType.LOGINLASTREWARD, System.currentTimeMillis());
        }

        updateInventory();

        final ArrayList<ItemStack> items = getReward(day);

        if(items.isEmpty())
            return;

        for (final ItemStack item : items)
            InventoryUtil.addItem(user.getOfflinePlayer().getPlayer(), item);

    }

    private ArrayList<ItemStack> getReward(final int day) {
        final YamlConfiguration cfg = user.getInitializer().getServerConfig().getCfg();
        return cfg.contains("loginreward." + day) ? (ArrayList<ItemStack>) cfg.getList("loginreward." + day) : new ArrayList<>();
    }

    public void openPreview(final Player player, final int day) {
        final Inventory inventory = Bukkit.createInventory(null, 9*6, "§7Preview Tag §9§l" + day);


        final ArrayList<ItemStack> items = getReward(day);

        if(items.isEmpty())
            return;

        for (final ItemStack item : items)
            inventory.addItem(item);

        inventory.setItem(53, new ItemBuilder(Material.BARRIER).setDisplayname("§c§lZurück").build());

        player.closeInventory();
        player.openInventory(inventory);
        player.updateInventory();
    }

}
