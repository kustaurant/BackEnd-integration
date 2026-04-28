package com.kustaurant.map.utils;

import com.kustaurant.map.BoundingBox;
import com.kustaurant.map.CoordinateV2;

import java.util.List;

public final class PolygonUtils {

    private PolygonUtils() {
    }

    public static BoundingBox getBoundingBox(List<CoordinateV2> polygon) {
        double minLat = Double.MAX_VALUE;
        double maxLat = -Double.MAX_VALUE;
        double minLng = Double.MAX_VALUE;
        double maxLng = -Double.MAX_VALUE;

        for (CoordinateV2 point : polygon) {
            minLat = Math.min(minLat, point.latitude());
            maxLat = Math.max(maxLat, point.latitude());
            minLng = Math.min(minLng, point.longitude());
            maxLng = Math.max(maxLng, point.longitude());
        }

        return new BoundingBox(minLat, maxLat, minLng, maxLng);
    }

    public static BoundingBox getBoundingBoxFromPolygons(List<List<CoordinateV2>> polygons) {
        double minLat = Double.MAX_VALUE;
        double maxLat = -Double.MAX_VALUE;
        double minLng = Double.MAX_VALUE;
        double maxLng = -Double.MAX_VALUE;

        for (List<CoordinateV2> polygon : polygons) {
            for (CoordinateV2 coord : polygon) {
                minLat = Math.min(minLat, coord.latitude());
                maxLat = Math.max(maxLat, coord.latitude());
                minLng = Math.min(minLng, coord.longitude());
                maxLng = Math.max(maxLng, coord.longitude());
            }
        }

        return new BoundingBox(minLat, maxLat, minLng, maxLng);
    }

    public static boolean isPointInsidePolygon(CoordinateV2 point, List<CoordinateV2> polygon) {
        boolean inside = false;
        int n = polygon.size();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            CoordinateV2 pi = polygon.get(i);
            CoordinateV2 pj = polygon.get(j);

            boolean intersect =
                    ((pi.longitude() > point.longitude()) != (pj.longitude() > point.longitude()))
                            && (point.latitude() < (pj.latitude() - pi.latitude())
                            * (point.longitude() - pi.longitude())
                            / (pj.longitude() - pi.longitude())
                            + pi.latitude());

            if (intersect) {
                inside = !inside;
            }
        }

        return inside;
    }
}
