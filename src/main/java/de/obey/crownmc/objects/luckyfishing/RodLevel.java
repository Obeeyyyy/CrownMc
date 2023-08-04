package de.obey.crownmc.objects.luckyfishing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum RodLevel {

    ZERO("0", 50.0D, 0.0D),
    ONE("1", 65.0D, 0.5D),
    TWO("2", 70.0D, 1.0D),
    THREE("3", 75.0D, 2.0D),
    FOUR("4", 85.0D, 2.0D),
    FIVE("5", 100.0D, 3.5D);

    String displayName;
    double fishRate;
    double itemRate;


    RodLevel(String displayName, double fishRate, double itemRate) {
        this.displayName = displayName;
        this.fishRate = fishRate;
        this.itemRate = itemRate;
    }

    public static RodLevel getMaxLevel() {
        return FIVE;
    }
}
