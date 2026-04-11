package com.kustaurant.naverplace;

import com.kustaurant.map.ZoneType;

public enum CrawlScopeType {
    ENTRANCE_TO_MIDDLE("건입~중문"),
    MIDDLE_TO_PARK("중문~어대"),
    BACK_GATE("후문"),
    FRONT_GATE("정문"),
    GUI_STATION("구의역"),
    SINGLE("단건");

    private final String description;

    CrawlScopeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static CrawlScopeType fromZoneType(ZoneType zoneType) {
        return switch (zoneType) {
            case ENTRANCE_TO_MIDDLE -> ENTRANCE_TO_MIDDLE;
            case MIDDLE_TO_PARK -> MIDDLE_TO_PARK;
            case BACK_GATE -> BACK_GATE;
            case FRONT_GATE -> FRONT_GATE;
            case GUI_STATION -> GUI_STATION;
        };
    }
}
