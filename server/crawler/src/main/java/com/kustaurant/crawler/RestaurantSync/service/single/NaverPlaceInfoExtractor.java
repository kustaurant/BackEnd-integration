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
   private static final Pattern METER_TOKEN_PATTERN = Pattern.compile("\\d+m\\uBBF8\\uD130");

   public String extractPlaceId(String sourceUrl) {
      if (this.isBlank(sourceUrl)) {
         return null;
      } else {
         Matcher matcher = PLACE_ID_PATTERN.matcher(sourceUrl);
         return matcher.find() ? matcher.group(1) : null;
      }
   }

   public NaverPlaceBasicInfo extract(Document homeDoc, String homeHtml) {
      String placeName = this.sanitizePlaceName(this.extractPlaceName(homeDoc));
      String category = this.sanitizeCategory(this.extractCategory(homeDoc));
      String restaurantAddress = this.sanitizeRestaurantAddress(this.extractRestaurantAddress(homeDoc));
      String phoneNumber = this.extractPhoneNumber(homeDoc);
      Double latitude = this.parseCoordinate(homeHtml, LAT_PATTERN);
      Double longitude = this.parseCoordinate(homeHtml, LNG_PATTERN);
      String imageUrl = this.extractMeta(homeDoc, "meta[property=og:image]", "content");
      return new NaverPlaceBasicInfo(placeName, category, restaurantAddress, phoneNumber, latitude, longitude, imageUrl);
   }

   private String extractPlaceName(Document doc) {
      return doc == null ? null : this.firstNonBlank(this.text(doc, "span.GHAhO"), this.text(doc, "div.zD5Nm em"), this.text(doc, "div.zD5Nm span"), this.text(doc, "h2"), this.extractMeta(doc, "meta[property=og:title]", "content"));
   }

   private String extractCategory(Document doc) {
      return doc == null ? null : this.firstNonBlank(this.text(doc, "span.lnJFt"), this.text(doc, "span.DJJvD"), this.text(doc, "div.PIbes"));
   }

   private String extractRestaurantAddress(Document doc) {
      return this.extractInfoValueByLabel(doc, "주소");
   }

   private String extractPhoneNumber(Document doc) {
      if (doc == null) {
         return null;
      } else {
         for(Element el : doc.select("div, li, span, a")) {
            String txt = this.normalize(el.text());
            if (!this.isBlank(txt)) {
               Matcher matcher = PHONE_PATTERN.matcher(txt);
               if (matcher.find()) {
                  return matcher.group(1);
               }
            }
         }

         return null;
      }
   }

   private String extractInfoValueByLabel(Document doc, String label) {
      if (doc == null) {
         return null;
      } else {
         for(Element block : doc.select("li, div")) {
            String txt = this.normalize(block.text());
            if (!this.isBlank(txt) && txt.contains(label) && txt.length() <= 400) {
               String cleaned = this.normalize(txt.replace(label, "").replace(":", "").replace("|", "").trim());
               String sanitized = this.sanitizeRestaurantAddress(cleaned);
               if (this.isAddressCandidate(sanitized)) {
                  return sanitized;
               }
            }
         }

         return null;
      }
   }

   private boolean isAddressCandidate(String txt) {
      if (this.isBlank(txt)) {
         return false;
      } else {
         return txt.contains("서울") || txt.contains("로") || txt.contains("길") || txt.contains("동") || txt.contains("미터");
      }
   }

   private String sanitizeRestaurantAddress(String value) {
      if (this.isBlank(value)) {
         return null;
      } else {
         String normalized = this.normalize(value);
         normalized = this.trimBeforeSeoul(normalized);
         if (this.isBlank(normalized)) {
            return null;
         } else {
            Matcher meterMatcher = METER_TOKEN_PATTERN.matcher(normalized);
            if (meterMatcher.find()) {
               normalized = this.normalize(normalized.substring(0, meterMatcher.end()));
               normalized = this.trimBeforeSeoul(normalized);
            } else {
               int cutIndex = this.firstKeywordIndex(normalized, "영업시간", "정보 수정", "수정 제안", "추가", "사장님", "플레이스", "권한 받기");
               if (cutIndex > 0) {
                  normalized = this.normalize(normalized.substring(0, cutIndex));
               }
            }

            normalized = this.trimBeforeSeoul(normalized);
            return this.isBlank(normalized) ? null : normalized;
         }
      }
   }

   private String trimBeforeSeoul(String text) {
      if (this.isBlank(text)) {
         return text;
      } else {
         int seoulIndex = text.indexOf("서울");
         return seoulIndex >= 0 ? this.normalize(text.substring(seoulIndex)) : text;
      }
   }

   private int firstKeywordIndex(String text, String... keywords) {
      int min = -1;

      for(String keyword : keywords) {
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
      } else {
         Element el = doc.selectFirst(css);
         return el == null ? null : this.normalize(el.attr(attr));
      }
   }

   private String text(Document doc, String css) {
      Element el = doc.selectFirst(css);
      return el == null ? null : this.normalize(el.text());
   }

   private Double parseCoordinate(String html, Pattern pattern) {
      if (this.isBlank(html)) {
         return null;
      } else {
         Matcher matcher = pattern.matcher(html);
         if (!matcher.find()) {
            return null;
         } else {
            try {
               return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException var5) {
               return null;
            }
         }
      }
   }

   private String sanitizePlaceName(String value) {
      return this.isBlank(value) ? null : this.normalize(value);
   }

   private String sanitizeCategory(String value) {
      if (this.isBlank(value)) {
         return null;
      } else {
         String normalized = this.normalize(value);
         return normalized.contains("네이버지도") ? null : normalized;
      }
   }

   private String firstNonBlank(String... values) {
      for(String value : values) {
         if (!this.isBlank(value)) {
            return this.normalize(value);
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
}
