package de.obey.crownmc.handler;

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import de.obey.crownmc.objects.luckyfishing.RewardLevel;
import de.obey.crownmc.objects.luckyfishing.RodLevel;
import de.obey.crownmc.util.Config;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

    Map<RewardLevel, List<ItemStack>> rewards;

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

    public boolean isFishingRod(ItemStack itemStack) {
        if (!itemStack.getItemMeta().hasDisplayName()) return false;
        if (itemStack.getItemMeta().getDisplayName().startsWith("§7Fischerangel §8(§f§oLevel §a§o")) return false;
        if (!itemStack.getItemMeta().hasLore()) return false;
        if (itemStack.getItemMeta().getLore().size() != 3) return false;
        return itemStack.getItemMeta().getLore().get(1).startsWith("§7Level §a§o");
    }

    public RodLevel getRodLevel(ItemStack itemStack) {
        if (!isFishingRod(itemStack)) return null;
        int level = Ints.tryParse(itemStack.getItemMeta().getLore().get(1).replace("§7Level §a§o", "").replace("§8/§2§l§o" + RodLevel.getMaxLevel().getDisplayName() + "§8)", ""));
        return Arrays.stream(RodLevel.values()).filter(rodLevel -> rodLevel.getDisplayName().equalsIgnoreCase(String.valueOf(level))).findFirst().orElse(RodLevel.ZERO);
    }

    public ItemStack getRod(RodLevel rodLevel) {
        return new ItemBuilder(Material.FISHING_ROD).setDisplayname("§7Fischerangel §8(§f§oLevel §a§o" + rodLevel.getDisplayName() + "§8)")
                .addLore(
                        "§r",
                        "§7Level §a§o" + rodLevel.getDisplayName() + "§8/§2§l§o" + RodLevel.getMaxLevel().getDisplayName(),
                        "§r"
                ).build();
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
