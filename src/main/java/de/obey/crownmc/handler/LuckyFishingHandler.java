package de.obey.crownmc.handler;

import com.google.common.collect.Maps;
import de.obey.crownmc.objects.luckyfishing.RewardLevel;
import de.obey.crownmc.util.Config;
import de.obey.crownmc.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LuckyFishingHandler {

    private final MessageUtil messageUtil;
    private final Config config;

    private Map<RewardLevel, List<ItemStack>> rewards;

    public LuckyFishingHandler(final MessageUtil messageUtil) {
        this.config = new Config("plugins/CrownMc/", "luckyFishing.yml");
        this.messageUtil = messageUtil;
        this.rewards = Maps.newConcurrentMap();
        if (this.config.getConfig().getConfigurationSection("rewards") == null) return;

        for (String rewardLevelName : this.config.getConfig().getConfigurationSection("rewards").getKeys(false)) {
            RewardLevel rewardLevel = RewardLevel.getOrDefault(rewardLevelName, RewardLevel.COMMON);
            final List<ItemStack> rewards = (List<ItemStack>) this.config.getConfig().getList("rewards." + rewardLevelName, new ArrayList<ItemStack>());
            if (rewards.isEmpty()) {
                messageUtil.log("! -> Fishing Rewards für RewardLevel." + rewardLevelName.toUpperCase() + " wurden noch nicht gesetzt!");
            }
            this.rewards.put(rewardLevel, rewards);
        }
    }

    public void editFishingRewards(Player player, RewardLevel rewardLevel) {
        final Inventory inventory = Bukkit.createInventory(null, 9*6, "Fishing-Rewards Edit: " + rewardLevel.name().toUpperCase());

        inventory.setContents(this.rewards.get(rewardLevel).toArray(new ItemStack[0]));

        player.openInventory(inventory);
    }

    public void saveFishingRewards(InventoryView inventoryView) {
        final Player player = (Player) inventoryView.getPlayer();

        if (inventoryView.getTitle().startsWith("Fishing-Rewards Edit: ")) {
            final RewardLevel rewardLevel = RewardLevel.getOrDefault(inventoryView.getTitle().replace("Fishing-Rewards Edit: ", ""), RewardLevel.COMMON);
            final List<ItemStack> contents = Arrays.stream(inventoryView.getTopInventory().getContents()).collect(Collectors.toList());
            this.rewards.put(rewardLevel, contents);
            this.config.getConfig().set("rewards." + rewardLevel.name().toLowerCase(), contents);
            this.config.saveConfig();
            messageUtil.sendMessage(player, "Du hast die Fishingrewards für das RewardLevel " + rewardLevel.getDisplayName() + "§7 gesetzt§8.");
            return;
        }

    }


}
