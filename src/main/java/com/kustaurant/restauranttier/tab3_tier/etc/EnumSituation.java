package com.kustaurant.restauranttier.tab3_tier.etc;

import lombok.Getter;

@Getter
public enum EnumSituation {
    ONE("혼밥"), TWO("2~4인"), THREE("5인 이상"), FOUR("단체 회식"),
    FIVE("배달"), SIX("야식"), SEVEN("친구 초대"), EIGHT("데이트"), NINE("소개팅");

    private final String value;

    EnumSituation(String value) { this.value = value; }
}
