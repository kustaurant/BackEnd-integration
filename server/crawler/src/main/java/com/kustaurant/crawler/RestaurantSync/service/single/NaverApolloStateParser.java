package com.kustaurant.crawler.RestaurantSync.service.single;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustaurant.restaurantSync.RestaurantRawMenu;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NaverApolloStateParser {

   private static final String APOLLO_STATE_MARKER = "window.__APOLLO_STATE__";
   private static final String PLACE_DETAIL_BASE_PREFIX = "PlaceDetailBase:";

   private final ObjectMapper objectMapper;

   public Optional<NaverPlaceData> parse(String html, String placeId) {
      if (isBlank(html) || isBlank(placeId)) {
         return Optional.empty();
      }

      try {
         String stateJson = extractAssignedJsonObject(html, APOLLO_STATE_MARKER);
         if (stateJson == null) {
            return Optional.empty();
         }

         JsonNode state = objectMapper.readTree(stateJson);
         JsonNode base = findPlaceDetailBase(state, placeId);
         if (base == null) {
            return Optional.empty();
         }

         JsonNode placeDetail = findPlaceDetail(state, placeId);
         MenuResult menuResult = extractMenus(state, placeDetail, placeId);

         return Optional.of(new NaverPlaceData(
                 text(base, "name"),
                 text(base, "category"),
                 firstNonBlank(text(base, "roadAddress"), text(base, "address")),
                 firstNonBlank(
                         textAt(placeDetail, "phoneInfo", "phone"),
                         text(base, "virtualPhone"),
                         text(base, "phone")
                 ),
                 doubleAt(base, "coordinate", "y"),
                 doubleAt(base, "coordinate", "x"),
                 extractRepresentativeImage(state, placeDetail),
                 menuResult.menus(),
                 menuResult.authoritative()
         ));
      } catch (Exception ignored) {
         return Optional.empty();
      }
   }

   private JsonNode findPlaceDetailBase(JsonNode state, String placeId) {
      JsonNode exact = state.get(PLACE_DETAIL_BASE_PREFIX + placeId);
      if (isTypeWithId(exact, "PlaceDetailBase", placeId)) {
         return exact;
      }

      for (JsonNode candidate : state) {
         if (isTypeWithId(candidate, "PlaceDetailBase", placeId)) {
            return candidate;
         }
      }
      return null;
   }

   private JsonNode findPlaceDetail(JsonNode state, String placeId) {
      String expectedBaseRef = PLACE_DETAIL_BASE_PREFIX + placeId;
      for (JsonNode candidate : state) {
         JsonNode found = findObject(candidate, node ->
                 "PlaceDetail".equals(text(node, "__typename"))
                         && expectedBaseRef.equals(textAt(node, "base", "__ref"))
         );
         if (found != null) {
            return found;
         }
      }
      return null;
   }

   private MenuResult extractMenus(JsonNode state, JsonNode placeDetail, String placeId) {
      JsonNode menuRefs = findFieldByLogicalName(placeDetail, "menus");
      if (menuRefs != null && menuRefs.isArray()) {
         List<RestaurantRawMenu> resolved = resolveMenus(state, menuRefs);
         boolean authoritative = menuRefs.isEmpty() || resolved.size() == menuRefs.size();
         return new MenuResult(resolved, authoritative);
      }

      Map<String, RestaurantRawMenu> discovered = new LinkedHashMap<>();
      Iterator<String> fields = state.fieldNames();
      while (fields.hasNext()) {
         String field = fields.next();
         JsonNode candidate = state.get(field);
         if (!isMenuForPlace(candidate, placeId)) {
            continue;
         }
         RestaurantRawMenu menu = toMenu(candidate);
         if (menu != null) {
            discovered.putIfAbsent(field, menu);
         }
      }
      return new MenuResult(new ArrayList<>(discovered.values()), false);
   }

   private List<RestaurantRawMenu> resolveMenus(JsonNode state, JsonNode menuRefs) {
      List<RestaurantRawMenu> menus = new ArrayList<>();
      for (JsonNode item : menuRefs) {
         JsonNode menuNode = item;
         String reference = text(item, "__ref");
         if (!isBlank(reference)) {
            menuNode = state.get(reference);
         }

         RestaurantRawMenu menu = toMenu(menuNode);
         if (menu != null) {
            menus.add(menu);
         }
      }
      return menus;
   }

   private RestaurantRawMenu toMenu(JsonNode menuNode) {
      if (menuNode == null || !"Menu".equals(text(menuNode, "__typename"))) {
         return null;
      }

      String name = text(menuNode, "name");
      if (isBlank(name)) {
         return null;
      }

      return new RestaurantRawMenu(
              name,
              formatMenuPrice(text(menuNode, "price")),
              firstArrayText(menuNode.get("images"))
      );
   }

   private String extractRepresentativeImage(JsonNode state, JsonNode placeDetail) {
      String image = firstNonBlank(
              textAt(placeDetail, "images", "images", 0, "origin"),
              textAt(placeDetail, "images", "images", 0, "url")
      );
      if (!isBlank(image)) {
         return image;
      }

      JsonNode topPhotos = placeDetail == null ? null : placeDetail.path("topPhotos").path("items");
      if (topPhotos != null && topPhotos.isArray()) {
         for (JsonNode item : topPhotos) {
            String reference = text(item, "__ref");
            JsonNode photo = isBlank(reference) ? item : state.get(reference);
            if (photo == null || !"business".equals(text(photo, "mediaSource"))) {
               continue;
            }
            image = firstNonBlank(text(photo, "originalUrl"), text(photo, "thumbnailUrl"));
            if (!isBlank(image)) {
               return image;
            }
         }
      }
      return null;
   }

   private JsonNode findFieldByLogicalName(JsonNode object, String logicalName) {
      if (object == null || !object.isObject()) {
         return null;
      }

      Iterator<String> fields = object.fieldNames();
      while (fields.hasNext()) {
         String field = fields.next();
         if (field.equals(logicalName) || field.startsWith(logicalName + "(")) {
            return object.get(field);
         }
      }
      return null;
   }

   private JsonNode findObject(JsonNode node, NodePredicate predicate) {
      if (node == null) {
         return null;
      }
      if (node.isObject() && predicate.matches(node)) {
         return node;
      }
      if (!node.isContainerNode()) {
         return null;
      }

      for (JsonNode child : node) {
         JsonNode found = findObject(child, predicate);
         if (found != null) {
            return found;
         }
      }
      return null;
   }

   private boolean isMenuForPlace(JsonNode candidate, String placeId) {
      return candidate != null
              && "Menu".equals(text(candidate, "__typename"))
              && text(candidate, "id") != null
              && text(candidate, "id").startsWith(placeId + "_");
   }

   private boolean isTypeWithId(JsonNode node, String typename, String id) {
      return node != null
              && typename.equals(text(node, "__typename"))
              && id.equals(text(node, "id"));
   }

   private String extractAssignedJsonObject(String source, String marker) {
      int markerIndex = source.indexOf(marker);
      if (markerIndex < 0) {
         return null;
      }

      int assignmentIndex = source.indexOf('=', markerIndex + marker.length());
      if (assignmentIndex < 0) {
         return null;
      }

      int objectStart = source.indexOf('{', assignmentIndex + 1);
      if (objectStart < 0) {
         return null;
      }

      boolean inString = false;
      boolean escaped = false;
      int depth = 0;
      for (int i = objectStart; i < source.length(); i++) {
         char current = source.charAt(i);
         if (inString) {
            if (escaped) {
               escaped = false;
            } else if (current == '\\') {
               escaped = true;
            } else if (current == '"') {
               inString = false;
            }
            continue;
         }

         if (current == '"') {
            inString = true;
         } else if (current == '{') {
            depth++;
         } else if (current == '}') {
            depth--;
            if (depth == 0) {
               return source.substring(objectStart, i + 1);
            }
         }
      }
      return null;
   }

   private String formatMenuPrice(String value) {
      if (isBlank(value)) {
         return "변동";
      }
      String normalized = value.trim();
      if ("0".equals(normalized)) {
         return "무료";
      }
      if (!normalized.matches("\\d+")) {
         return normalized;
      }
      try {
         return String.format("%,d원", Long.parseLong(normalized));
      } catch (NumberFormatException ignored) {
         return normalized;
      }
   }

   private String firstArrayText(JsonNode array) {
      if (array == null || !array.isArray() || array.isEmpty()) {
         return null;
      }
      return normalize(array.get(0).asText(null));
   }

   private Double doubleAt(JsonNode root, String objectField, String valueField) {
      String value = textAt(root, objectField, valueField);
      if (isBlank(value)) {
         return null;
      }
      try {
         return Double.parseDouble(value);
      } catch (NumberFormatException ignored) {
         return null;
      }
   }

   private String text(JsonNode node, String field) {
      if (node == null || field == null) {
         return null;
      }
      JsonNode value = node.get(field);
      return value == null || value.isNull() ? null : normalize(value.asText(null));
   }

   private String textAt(JsonNode root, Object... path) {
      JsonNode current = root;
      for (Object part : path) {
         if (current == null) {
            return null;
         }
         if (part instanceof String field) {
            current = current.get(field);
         } else if (part instanceof Integer index) {
            current = current.isArray() && current.size() > index ? current.get(index) : null;
         }
      }
      return current == null || current.isNull() ? null : normalize(current.asText(null));
   }

   private String firstNonBlank(String... values) {
      for (String value : values) {
         if (!isBlank(value)) {
            return value;
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

   @FunctionalInterface
   private interface NodePredicate {
      boolean matches(JsonNode node);
   }

   private record MenuResult(List<RestaurantRawMenu> menus, boolean authoritative) {
   }

   public record NaverPlaceData(
           String placeName,
           String category,
           String restaurantAddress,
           String phoneNumber,
           Double latitude,
           Double longitude,
           String imageUrl,
           List<RestaurantRawMenu> menus,
           boolean menuDataPresent
   ) {
   }
}
