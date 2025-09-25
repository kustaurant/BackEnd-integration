package com.kustaurant.kustaurant.restaurant.restaurant.constants;

import java.util.List;

public abstract class MapConstants {
    public static final Integer MIN_ZOOM = 13;

    public static final List<List<Coordinate>> LIST_OF_COORD_LIST = List.of(
            // 건입~정문
            List.of(
                    new Coordinate(37.5401732,127.062852),
                    new Coordinate(37.5378977,127.0696049),
                    new Coordinate(37.5421627,127.071636),
                    new Coordinate(37.5427753,127.0710213),
                    new Coordinate(37.5422156,127.0707644),
                    new Coordinate(37.5441201,127.0651452)
            ),
            // 정문~어대
            List.of(
                    new Coordinate(37.5421627,127.071636),
                    new Coordinate(37.5427753,127.0710213),
                    new Coordinate(37.5422156,127.0707644),
                    new Coordinate(37.5441201,127.0651452),
                    new Coordinate(37.5482696,127.0674957),
                    new Coordinate(37.5478196,127.0716092),
                    new Coordinate(37.5472574,127.0740324),
                    new Coordinate(37.5459136,127.0733675)
            ),
            // 후문
            List.of(
                    new Coordinate(37.5445367,127.0728555),
                    new Coordinate(37.5444815,127.0731477),
                    new Coordinate(37.5447132,127.0739129),
                    new Coordinate(37.5445797,127.0747749),
                    new Coordinate(37.544736,127.0754595),
                    new Coordinate(37.5445765,127.0755668),
                    new Coordinate(37.5449818,127.0800863),
                    new Coordinate(37.545327,127.0799778),
                    new Coordinate(37.5453925,127.0793721),
                    new Coordinate(37.5458133,127.0773484),
                    new Coordinate(37.547219,127.0741961)
            ),
            // 정문
            List.of(
                    new Coordinate(37.5397225,127.0708216),
                    new Coordinate(37.5385701,127.0750329),
                    new Coordinate(37.5393212,127.0752326),
                    new Coordinate(37.5392034,127.0769366),
                    new Coordinate(37.5390193,127.0771502),
                    new Coordinate(37.5387767,127.0798368),
                    new Coordinate(37.540182,127.0827185),
                    new Coordinate(37.5400757,127.0831523),
                    new Coordinate(37.5369981,127.083432),
                    new Coordinate(37.5360015,127.0837294),
                    new Coordinate(37.5358341,127.0827587),
                    new Coordinate(37.5359128,127.0788435),
                    new Coordinate(37.5390731,127.0704382)
            ),
            // 구의역
            List.of(
                    new Coordinate(37.536197,127.0837349),
                    new Coordinate(37.5370672,127.0876883),
                    new Coordinate(37.538271,127.0871438),
                    new Coordinate(37.5387071,127.0865054),
                    new Coordinate(37.5397109,127.0864035),
                    new Coordinate(37.5396575,127.0832533),
                    new Coordinate(37.5384175,127.0833418),
                    new Coordinate(37.5373818,127.0834571),
                    new Coordinate(37.5365395,127.0835778)
            )
    );

    public static List<Double> findMinMaxCoordinates(List<List<Coordinate>> listOfCoordList) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (List<Coordinate> coordList : listOfCoordList) {
            for (Coordinate coord : coordList) {
                if (coord.getX() < minX) minX = coord.getX();
                if (coord.getY() < minY) minY = coord.getY();
                if (coord.getX() > maxX) maxX = coord.getX();
                if (coord.getY() > maxY) maxY = coord.getY();
            }
        }

        return List.of(minY, maxY, minX, maxX);
    }
}
