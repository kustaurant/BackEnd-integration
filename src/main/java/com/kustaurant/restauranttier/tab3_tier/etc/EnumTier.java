
package com.kustaurant.restauranttier.tab3_tier.etc;

import lombok.Getter;

@Getter
public enum EnumTier {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), NONE(-1);

    private final Integer value;

    EnumTier(Integer value) { this.value = value; }

    public static EnumTier calculateTierOfRestaurant(Double averageScore) {
        if (averageScore >= 6.0) {
            return EnumTier.ONE;
        } else if (averageScore >= 5.5) {
            return EnumTier.TWO;
        } else if (averageScore > 4.5) {
            return EnumTier.THREE;
        } else if (averageScore > 3.0) {
            return EnumTier.FOUR;
        } else if (averageScore >= 1.0) {
            return EnumTier.FIVE;
        } else {
            return EnumTier.NONE;
        }
    }

    public static EnumTier calculateSituationTierOfRestaurant(Double averageScore) {
        if (averageScore >= 4.62) {
            return EnumTier.ONE;
        } else if (averageScore >= 4.2) {
            return EnumTier.TWO;
        } else if (averageScore >= 3.7) {
            return EnumTier.THREE;
        } else if (averageScore >= 3.2) {
            return EnumTier.FOUR;
        } else if (averageScore >= 1.0) {
            return EnumTier.FIVE;
        } else {
            return EnumTier.NONE;
        }
    }

    public static EnumTier fromValue(Integer tier) {
        for (EnumTier myEnum : EnumTier.values()) {
            if (myEnum.value.equals(tier)) {
                return myEnum;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + tier);
    }
}
