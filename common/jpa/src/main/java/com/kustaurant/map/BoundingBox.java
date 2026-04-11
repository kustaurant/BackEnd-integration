package com.kustaurant.map;

public record BoundingBox(
        double minLat,
        double maxLat,
        double minLng,
        double maxLng
) {}
