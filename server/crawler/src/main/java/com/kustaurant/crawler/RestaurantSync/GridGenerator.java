package com.kustaurant.crawler.RestaurantSync;

import com.kustaurant.map.BoundingBox;
import com.kustaurant.map.CoordinateV2;
import com.kustaurant.map.ZonePolygon;
import com.kustaurant.map.utils.PolygonUtils;
import java.util.ArrayList;
import java.util.List;

public final class GridGenerator {

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

                if (PolygonUtils.isPointInsidePolygon(center, zonePolygon.coordinates())) {
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
}