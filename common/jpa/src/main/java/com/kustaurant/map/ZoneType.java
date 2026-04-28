package com.kustaurant.map;

public enum ZoneType {
    ENTRANCE_TO_MIDDLE("건입~중문"),
    MIDDLE_TO_PARK("중문~어대"),
    BACK_GATE("후문"),
    FRONT_GATE("정문"),
    GUI_STATION("구의역"),
    OUT_OF_ZONE("구역밖");

    private final String description;

    ZoneType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
