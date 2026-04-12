package com.kustaurant.crawler.RestaurantSync.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class NaverPlaceInfoExtractor {

    private static final Pattern PLACE_ID_PATTERN = Pattern.compile("/place/(\\d+)");
    private static final Pattern LAT_PATTERN = Pattern.compile("\"(?:lat|latitude|y)\"\\s*:\\s*\"?(-?[0-9]+\\.[0-9]+)\"?");
    private static final Pattern LNG_PATTERN = Pattern.compile("\"(?:lng|longitude|x)\"\\s*:\\s*\"?(-?[0-9]+\\.[0-9]+)\"?");
    private static final Pattern PHONE_PATTERN = Pattern.compile("((?:0507|0\\d{1,3})-\\d{3,4}-\\d{4})");

    public record NaverPlaceBasicInfo(
            String placeName,
            String category,
            String restaurantAddress,
            String phoneNumber,
            Double latitude,
            Double longitude,
            String imageUrl
    ) {
    }

    public String extractPlaceId(String sourceUrl) {
        if (isBlank(sourceUrl)) {
            return null;
        }
        Matcher matcher = PLACE_ID_PATTERN.matcher(sourceUrl);
        return matcher.find() ? matcher.group(1) : null;
    }

    public NaverPlaceBasicInfo extract(Document homeDoc, String homeHtml) {
        String placeName = sanitizePlaceName(extractPlaceName(homeDoc));
        String category = sanitizeCategory(extractCategory(homeDoc));
        String restaurantAddress = extractRestaurantAddress(homeDoc);
        String phoneNumber = extractPhoneNumber(homeDoc);
        Double latitude = parseCoordinate(homeHtml, LAT_PATTERN);
        Double longitude = parseCoordinate(homeHtml, LNG_PATTERN);
        String imageUrl = extractMeta(homeDoc, "meta[property=og:image]", "content");

        return new NaverPlaceBasicInfo(
                placeName,
                category,
                restaurantAddress,
                phoneNumber,
                latitude,
                longitude,
                imageUrl
        );
    }

    private String extractPlaceName(Document doc) {
        if (doc == null) {
            return null;
        }

        return firstNonBlank(
                text(doc, "span.GHAhO"),
                text(doc, "div.zD5Nm em"),
                text(doc, "div.zD5Nm span"),
                text(doc, "h2"),
                extractMeta(doc, "meta[property=og:title]", "content")
        );
    }

    private String extractCategory(Document doc) {
        if (doc == null) {
            return null;
        }

        return firstNonBlank(
                text(doc, "span.lnJFt"),
                text(doc, "span.DJJvD"),
                text(doc, "div.PIbes")
        );
    }

    private String extractRestaurantAddress(Document doc) {
        return extractInfoValueByLabel(doc, "주소");
    }

    private String extractPhoneNumber(Document doc) {
        if (doc == null) {
            return null;
        }

        for (Element el : doc.select("div, li, span, a")) {
            String txt = normalize(el.text());
            if (isBlank(txt)) {
                continue;
            }

            Matcher matcher = PHONE_PATTERN.matcher(txt);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    private String extractInfoValueByLabel(Document doc, String label) {
        if (doc == null) {
            return null;
        }

        for (Element block : doc.select("li, div")) {
            String txt = normalize(block.text());
            if (isBlank(txt) || !txt.contains(label)) {
                continue;
            }

            if (txt.length() > 120) {
                continue;
            }

            if (isAddressCandidate(txt)) {
                String cleaned = txt.replace(label, "").trim();
                if (cleaned.contains("길") || cleaned.contains("로")) {
                    return normalize(cleaned);
                }
            }
        }

        return null;
    }

    private boolean isAddressCandidate(String txt) {
        return txt.contains("서울");
    }

    private String extractMeta(Document doc, String css, String attr) {
        if (doc == null) {
            return null;
        }
        Element el = doc.selectFirst(css);
        return el == null ? null : normalize(el.attr(attr));
    }

    private String text(Document doc, String css) {
        Element el = doc.selectFirst(css);
        return el == null ? null : normalize(el.text());
    }

    private Double parseCoordinate(String html, Pattern pattern) {
        if (isBlank(html)) {
            return null;
        }

        Matcher matcher = pattern.matcher(html);
        if (!matcher.find()) {
            return null;
        }

        try {
            return Double.parseDouble(matcher.group(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String sanitizePlaceName(String value) {
        if (isBlank(value)) {
            return null;
        }
        return normalize(
                value.replace(" : 네이버", "")
                        .replace(" - 네이버 지도", "")
                        .replace(" - 네이버 플레이스", "")
        );
    }

    private String sanitizeCategory(String value) {
        if (isBlank(value)) {
            return null;
        }
        String normalized = normalize(value);
        if (normalized.contains("모든 일정의 시작") || normalized.contains("네이버 지도")) {
            return null;
        }
        return normalized;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return normalize(value);
            }
        }
        return null;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\\s+", " ").trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
