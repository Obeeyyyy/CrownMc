package de.obey.slayer.handler;
/*

    Author - Obey -> SkySlayer-v4
       28.10.2022 / 21:22

*/

import de.obey.slayer.backend.user.User;
import de.obey.slayer.objects.LoginStreakReward;
import de.obey.slayer.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class LoginRewardHandler {

    @NonNull
    private final UserHandler userHandler;

    @NonNull
    private final MessageUtil messageUtil;

    private final Map<Player, Inventory> openInventories = new HashMap<>();
    private final Map<Integer, LoginStreakReward> rewardMap = new HashMap<>();

    public void loadRewards() {

    }

    public void save() {

    }

    public void openInventory(final Player player) {

        final User user = userHandler.getUserInstant(player.getUniqueId());

        if (user == null) {
            messageUtil.sendMessage(player, "§c§oBitte warte einen Moment ...");
            return;
        }

        final Inventory inventory = Bukkit.createInventory(null, 9 * 3, "§d§lLoginstreak");

        updateInventory(user, inventory);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
    }

    public void updateInventory(final User user, final Inventory inventory) {

    }

    public void runUpdateTick() {
        if (openInventories.isEmpty())
            return;

        for (final Player player : openInventories.keySet()) {

            if (player == null || !player.isOnline()) {
                openInventories.remove(player);
                return;
            }

            final Inventory inventory = openInventories.get(player);

            if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CHEST) {
                openInventories.remove(player);
                return;
            }

            // update the inventory
        }
    }

}
