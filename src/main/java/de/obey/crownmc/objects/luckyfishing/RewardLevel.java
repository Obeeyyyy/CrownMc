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
    UNCOMMON("§a§lUNCOMMON§r", 30),
    RARE("§3§lRARE§r", 10),
    EPIC("§5§lEPIC§r", 8),
    LEGENDARY("§4§lLEGENDARY§r", 2);

    final String displayName;
    final int chance;

    RewardLevel(final String displayName, int chance) {
        this.displayName = displayName;
        this.chance = chance;
    }

    public static RewardLevel getOrDefault(final String name, final RewardLevel def) {
        return Arrays.stream(values()).filter(rewardLevel -> rewardLevel.name().equalsIgnoreCase(name)).findFirst().orElse(def);
    }

    public static RewardLevel getByChance(int chance) {
        if (chance >= 50) return COMMON;
        if (chance >= 30) return UNCOMMON;
        if (chance >= 10) return RARE;
        if (chance >= 8) return EPIC;
        return LEGENDARY;
    }

}
