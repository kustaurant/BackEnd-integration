package com.kustaurant.crawler.RestaurantSync;

import com.kustaurant.map.BoundingBox;
import com.kustaurant.map.CoordinateV2;
import com.kustaurant.map.ZonePolygon;
import com.kustaurant.map.utils.PolygonUtils;
import java.util.ArrayList;
import java.util.List;

public final class GridGenerator {
    private static final double EPS = 1e-12;

    private GridGenerator() {
    }

    public static List<CrawlGrid> generate(ZonePolygon zonePolygon, double latStep, double lngStep) {
        List<CrawlGrid> result = new ArrayList<>();

        BoundingBox box = PolygonUtils.getBoundingBox(zonePolygon.coordinates());

        int row = 0;
        for (double lat = box.minLat(); lat <= box.maxLat(); lat += latStep) {
            int col = 0;
            for (double lng = box.minLng(); lng <= box.maxLng(); lng += lngStep) {
                double centerLat = lat + (latStep / 2.0);
                double centerLng = lng + (lngStep / 2.0);

                CoordinateV2 center = new CoordinateV2(centerLat, centerLng);
                double minLat = lat;
                double maxLat = lat + latStep;
                double minLng = lng;
                double maxLng = lng + lngStep;

                if (PolygonUtils.isPointInsidePolygon(center, zonePolygon.coordinates())
                        || isCellIntersectingPolygon(minLat, maxLat, minLng, maxLng, zonePolygon.coordinates())) {
                    result.add(new CrawlGrid(
                            zonePolygon.zoneType(),
                            row,
                            col,
                            centerLat,
                            centerLng
                    ));
                }
                col++;
            }
            row++;
        }

        return result;
    }

    private static boolean isCellIntersectingPolygon(
            double minLat,
            double maxLat,
            double minLng,
            double maxLng,
            List<CoordinateV2> polygon
    ) {
        if (polygon == null || polygon.size() < 3) {
            return false;
        }

        List<CoordinateV2> cellCorners = List.of(
                new CoordinateV2(minLat, minLng),
                new CoordinateV2(minLat, maxLng),
                new CoordinateV2(maxLat, maxLng),
                new CoordinateV2(maxLat, minLng)
        );

        int cornerInsideCount = 0;
        for (CoordinateV2 corner : cellCorners) {
            if (PolygonUtils.isPointInsidePolygon(corner, polygon)) {
                cornerInsideCount++;
            }
        }
        if (cornerInsideCount >= 2) {
            return true;
        }

        for (CoordinateV2 vertex : polygon) {
            if (isPointInsideRect(vertex, minLat, maxLat, minLng, maxLng)) {
                return true;
            }
        }

        for (int i = 0; i < polygon.size(); i++) {
            CoordinateV2 a = polygon.get(i);
            CoordinateV2 b = polygon.get((i + 1) % polygon.size());

            for (int j = 0; j < cellCorners.size(); j++) {
                CoordinateV2 c = cellCorners.get(j);
                CoordinateV2 d = cellCorners.get((j + 1) % cellCorners.size());
                if (segmentsIntersect(a, b, c, d)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isPointInsideRect(
            CoordinateV2 point,
            double minLat,
            double maxLat,
            double minLng,
            double maxLng
    ) {
        return point.latitude() >= minLat - EPS
                && point.latitude() <= maxLat + EPS
                && point.longitude() >= minLng - EPS
                && point.longitude() <= maxLng + EPS;
    }

    private static boolean segmentsIntersect(CoordinateV2 a, CoordinateV2 b, CoordinateV2 c, CoordinateV2 d) {
        double o1 = orientation(a, b, c);
        double o2 = orientation(a, b, d);
        double o3 = orientation(c, d, a);
        double o4 = orientation(c, d, b);

        if (o1 * o2 < 0 && o3 * o4 < 0) {
            return true;
        }

        if (isZero(o1) && onSegment(a, c, b)) return true;
        if (isZero(o2) && onSegment(a, d, b)) return true;
        if (isZero(o3) && onSegment(c, a, d)) return true;
        if (isZero(o4) && onSegment(c, b, d)) return true;

        return false;
    }

    private static double orientation(CoordinateV2 a, CoordinateV2 b, CoordinateV2 c) {
        double ax = a.longitude();
        double ay = a.latitude();
        double bx = b.longitude();
        double by = b.latitude();
        double cx = c.longitude();
        double cy = c.latitude();

        return (bx - ax) * (cy - ay) - (by - ay) * (cx - ax);
    }

    private static boolean onSegment(CoordinateV2 a, CoordinateV2 p, CoordinateV2 b) {
        double px = p.longitude();
        double py = p.latitude();
        return px >= Math.min(a.longitude(), b.longitude()) - EPS
                && px <= Math.max(a.longitude(), b.longitude()) + EPS
                && py >= Math.min(a.latitude(), b.latitude()) - EPS
                && py <= Math.max(a.latitude(), b.latitude()) + EPS;
    }

    private static boolean isZero(double value) {
        return Math.abs(value) <= EPS;
    }
}
