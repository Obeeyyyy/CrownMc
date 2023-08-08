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
    VERY_RARE("§5§lEPIC§r"),
    LEGENDARY("§4§lLEGENDARY§r");

    final String displayName;

    RewardLevel(final String displayName) {
        this.displayName = displayName;
    }

    public static RewardLevel getOrDefault(final String name, final RewardLevel def) {
        return Arrays.stream(values()).filter(rewardLevel -> rewardLevel.name().equalsIgnoreCase(name)).findFirst().orElse(def);
    }

}
