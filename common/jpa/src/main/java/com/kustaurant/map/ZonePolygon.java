package com.kustaurant.map;

import java.util.List;

public record ZonePolygon(
        ZoneType zoneType,
        List<CoordinateV2> coordinates
) {}