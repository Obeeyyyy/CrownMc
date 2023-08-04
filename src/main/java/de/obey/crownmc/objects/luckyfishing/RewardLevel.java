package de.obey.crownmc.objects.luckyfishing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum RewardLevel {

    COMMON("§7§lCOMMON§r"),
    UNCOMMON("§a§lUNCOMMON§r"),
    RARE("§3§lRARE§r"),
    VERY_RARE("§9§lVERY RARE§r"),
    LEGENDARY("§6§k;§e §lLEGENDARY§6 §k;§r");

    String displayName;

    RewardLevel(String displayName) {
        this.displayName = displayName;
    }

    public static RewardLevel getOrDefault(String name, RewardLevel def) {
        return Arrays.stream(values()).filter(rewardLevel -> rewardLevel.name().equalsIgnoreCase(name)).findFirst().orElse(def);
    }

}
