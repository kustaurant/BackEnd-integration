package com.kustaurant.map;

import java.util.List;

public final class MapConstantsV2 {
    private MapConstantsV2() {}
    public static final Integer MIN_ZOOM = 13;

    public static final List<ZonePolygon> ZONES = List.of(
            new ZonePolygon( // 건입~중문
                    ZoneType.ENTRANCE_TO_MIDDLE,
                    List.of(
                            new CoordinateV2(37.5401732,127.062852),
                            new CoordinateV2(37.5378977,127.0696049),
                            new CoordinateV2(37.5422127,127.071636),
                            new CoordinateV2(37.5428253,127.0710213),
                            new CoordinateV2(37.5422656,127.0707644),
                            new CoordinateV2(37.5441701,127.0651452)
                    )
            ),
            new ZonePolygon( // 중문~어대
                    ZoneType.MIDDLE_TO_PARK,
                    List.of(
                            new CoordinateV2(37.5422127,127.071636),
                            new CoordinateV2(37.5428253,127.0710213),
                            new CoordinateV2(37.5422656,127.0707644),
                            new CoordinateV2(37.5441701,127.0651452),
                            new CoordinateV2(37.5482696,127.0674957),
                            new CoordinateV2(37.5478196,127.0716092),
                            new CoordinateV2(37.5472574,127.0740324),
                            new CoordinateV2(37.5459136,127.0733675)
                    )
            ),
            new ZonePolygon( // 후문
                    ZoneType.BACK_GATE,
                    List.of(
                            new CoordinateV2(37.5445367,127.0728555),
                            new CoordinateV2(37.5444815,127.0731477),
                            new CoordinateV2(37.5447132,127.0739129),
                            new CoordinateV2(37.5445797,127.0747749),
                            new CoordinateV2(37.544736,127.0754595),
                            new CoordinateV2(37.5445765,127.0755668),
                            new CoordinateV2(37.5449818,127.0800863),
                            new CoordinateV2(37.54536,127.0799778),
                            new CoordinateV2(37.5453925,127.0793721),
                            new CoordinateV2(37.5458133,127.0773484),
                            new CoordinateV2(37.547249,127.0741961)
                    )
            ),
            new ZonePolygon( // 정문
                    ZoneType.FRONT_GATE,
                    List.of(
                            new CoordinateV2(37.5397225,127.0708216),
                            new CoordinateV2(37.5385701,127.0750329),
                            new CoordinateV2(37.5393212,127.0752326),
                            new CoordinateV2(37.5392034,127.0769366),
                            new CoordinateV2(37.5390193,127.0771502),
                            new CoordinateV2(37.5387767,127.0798368),
                            new CoordinateV2(37.540182,127.0827185),
                            new CoordinateV2(37.5400757,127.0831523),
                            new CoordinateV2(37.5369981,127.083432),
                            new CoordinateV2(37.5360015,127.0837294),
                            new CoordinateV2(37.5358341,127.0827587),
                            new CoordinateV2(37.5359128,127.0788435),
                            new CoordinateV2(37.5390731,127.0704382)
                    )
            ),
            new ZonePolygon( // 구의역
                    ZoneType.GUI_STATION,
                    List.of(
                            new CoordinateV2(37.536197,127.0837349),
                            new CoordinateV2(37.5370672,127.0876883),
                            new CoordinateV2(37.538271,127.0871438),
                            new CoordinateV2(37.5387071,127.0865054),
                            new CoordinateV2(37.5397109,127.0864035),
                            new CoordinateV2(37.5396575,127.0832533),
                            new CoordinateV2(37.5384175,127.0833418),
                            new CoordinateV2(37.5373818,127.0834571),
                            new CoordinateV2(37.5365395,127.0835778)
                    )
            )
    );
}
