package com.kustaurant.kustaurant.admin.naverPlaceCrawl.service;

import com.kustaurant.kustaurant.admin.naverPlaceCrawl.exception.InvalidNaverPlaceUrlException;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.stereotype.Component;

@Component
public class NaverPlaceUrlValidator {

    private static final String INVALID_URL_MESSAGE = "잘못된 경로입니다. 네이버 플레이스 URL을 입력해 주세요.";

    public void validateOrThrow(String placeUrl) {
        if (placeUrl == null || placeUrl.isBlank()) {
            throw new InvalidNaverPlaceUrlException(INVALID_URL_MESSAGE);
        }

        URI uri;
        try {
            uri = new URI(placeUrl.trim());
        } catch (URISyntaxException e) {
            throw new InvalidNaverPlaceUrlException(INVALID_URL_MESSAGE);
        }

        String scheme = uri.getScheme();
        String host = uri.getHost();
        String path = uri.getPath();

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new InvalidNaverPlaceUrlException(INVALID_URL_MESSAGE);
        }

        if (host == null || !host.endsWith("map.naver.com")) {
            throw new InvalidNaverPlaceUrlException(INVALID_URL_MESSAGE);
        }

        if (path == null || (!path.contains("/place/") && !path.contains("/entry/place/"))) {
            throw new InvalidNaverPlaceUrlException(INVALID_URL_MESSAGE);
        }
    }
}
