package com.kustaurant.crawler.RestaurantSync.service.single;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class NaverPlaceInfoExtractor {

   private static final String ADDRESS_LABEL = "주소";
   private static final String SEOUL_PREFIX = "서울";

   private static final Pattern PLACE_ID_PATTERN = Pattern.compile("/place/(\\d+)");
   private static final Pattern LAT_PATTERN = Pattern.compile("\"(?:lat|latitude|y)\"\\s*:\\s*\"?(-?[0-9]+\\.[0-9]+)\"?");
   private static final Pattern LNG_PATTERN = Pattern.compile("\"(?:lng|longitude|x)\"\\s*:\\s*\"?(-?[0-9]+\\.[0-9]+)\"?");
   private static final Pattern PHONE_PATTERN = Pattern.compile("((?:0507|0\\d{1,3})-\\d{3,4}-\\d{4})");
   private static final Pattern METER_TOKEN_PATTERN = Pattern.compile("\\d+m미터");

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
      String restaurantAddress = sanitizeRestaurantAddress(extractRestaurantAddress(homeDoc));
      String phoneNumber = extractPhoneNumber(homeDoc);
      Double latitude = parseCoordinate(homeHtml, LAT_PATTERN);
      Double longitude = parseCoordinate(homeHtml, LNG_PATTERN);
      String imageUrl = extractMeta(homeDoc, "meta[property=og:image]", "content");
      return new NaverPlaceBasicInfo(placeName, category, restaurantAddress, phoneNumber, latitude, longitude, imageUrl);
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
      return extractInfoValueByLabel(doc, ADDRESS_LABEL);
   }

   private String extractPhoneNumber(Document doc) {
      if (doc == null) {
         return null;
      }
      for (Element el : doc.select("div, li, span, a")) {
         String txt = normalize(el.text());
         if (!isBlank(txt)) {
            Matcher matcher = PHONE_PATTERN.matcher(txt);
            if (matcher.find()) {
               return matcher.group(1);
            }
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
         if (!isBlank(txt) && txt.contains(label) && txt.length() <= 400) {
            String cleaned = normalize(txt.replace(label, "").replace(":", "").replace("|", "").trim());
            String sanitized = sanitizeRestaurantAddress(cleaned);
            if (isAddressCandidate(sanitized)) {
               return sanitized;
            }
         }
      }
      return null;
   }

   private boolean isAddressCandidate(String txt) {
      if (isBlank(txt)) {
         return false;
      }
      return txt.contains(SEOUL_PREFIX)
              || txt.contains("로")
              || txt.contains("길")
              || txt.contains("번지")
              || txt.contains("미터");
   }

   private String sanitizeRestaurantAddress(String value) {
      if (isBlank(value)) {
         return null;
      }
      String normalized = normalize(value);
      normalized = trimBeforeSeoul(normalized);
      if (isBlank(normalized)) {
         return null;
      }

      Matcher meterMatcher = METER_TOKEN_PATTERN.matcher(normalized);
      if (meterMatcher.find()) {
         normalized = normalize(normalized.substring(0, meterMatcher.end()));
         normalized = trimBeforeSeoul(normalized);
      } else {
         int cutIndex = firstKeywordIndex(
                 normalized,
                 "영업시간",
                 "정보 수정",
                 "수정 제안",
                 "추가",
                 "사장님",
                 "플레이스",
                 "권한 받기"
         );
         if (cutIndex > 0) {
            normalized = normalize(normalized.substring(0, cutIndex));
         }
      }

      normalized = trimBeforeSeoul(normalized);
      return isBlank(normalized) ? null : normalized;
   }

   private String trimBeforeSeoul(String text) {
      if (isBlank(text)) {
         return text;
      }
      int seoulIndex = text.indexOf(SEOUL_PREFIX);
      return seoulIndex >= 0 ? normalize(text.substring(seoulIndex)) : text;
   }

   private int firstKeywordIndex(String text, String... keywords) {
      int min = -1;
      for (String keyword : keywords) {
         int idx = text.indexOf(keyword);
         if (idx >= 0 && (min < 0 || idx < min)) {
            min = idx;
         }
      }
      return min;
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
      } catch (NumberFormatException ignored) {
         return null;
      }
   }

   private String sanitizePlaceName(String value) {
      return isBlank(value) ? null : normalize(value);
   }

   private String sanitizeCategory(String value) {
      if (isBlank(value)) {
         return null;
      }
      String normalized = normalize(value);
      return normalized.contains("네이버") ? null : normalized;
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
      return value == null ? null : value.replaceAll("\\s+", " ").trim();
   }

   private boolean isBlank(String value) {
      return value == null || value.isBlank();
   }

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
}
