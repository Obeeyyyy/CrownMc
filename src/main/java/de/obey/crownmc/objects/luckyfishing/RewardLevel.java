package de.obey.crownmc.objects.luckyfishing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum RewardLevel {

    COMMON("§7§lCOMMON§r", 50),
    UNCOMMON("§e§lUNCOMMON§r", 40),
    RARE("§3§lRARE§r", 14),
    EPIC("§5§lEPIC§r", 5),
    LEGENDARY("§6§lLEGENDARY§r", 1);

    final String displayName;
    final int chance;

    RewardLevel(final String displayName, final int chance) {
        this.displayName = displayName;
        this.chance = chance;
    }

    public static RewardLevel getOrDefault(final String name, final RewardLevel def) {
        return Arrays.stream(values()).filter(rewardLevel -> rewardLevel.name().equalsIgnoreCase(name)).findFirst().orElse(def);
    }

    public static RewardLevel getByChance(int chance) {
        if (chance >= COMMON.chance) return COMMON;
        if (chance >= UNCOMMON.chance) return UNCOMMON;
        if (chance >= RARE.chance) return RARE;
        if (chance >= EPIC.chance) return EPIC;
        return LEGENDARY;
    }

}
