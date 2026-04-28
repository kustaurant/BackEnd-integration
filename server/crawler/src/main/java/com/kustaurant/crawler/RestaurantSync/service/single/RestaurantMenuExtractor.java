package com.kustaurant.crawler.RestaurantSync.service.single;

import com.kustaurant.restaurantSync.RestaurantRawMenu;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMenuExtractor {

   private static final int MAX_MENU_COUNT = 50;

   public List<RestaurantRawMenu> extractMenus(Document menuDoc, Page page) {
      List<RestaurantRawMenu> menus = extractMenusFromMenuHtml(menuDoc);
      return menus.isEmpty() ? extractMenusFromLiveDom(page) : menus;
   }

   private List<RestaurantRawMenu> extractMenusFromLiveDom(Page page) {
      Optional<Frame> entryFrame = findEntryFrame(page);
      if (entryFrame.isPresent()) {
         List<RestaurantRawMenu> fromFrame = extractMenusFromLocatorSource(entryFrame.get()::locator);
         if (!fromFrame.isEmpty()) {
            return fromFrame;
         }
      }
      return extractMenusFromLocatorSource(page::locator);
   }

   private List<RestaurantRawMenu> extractMenusFromLocatorSource(Function<String, Locator> locatorFactory) {
      Map<String, RestaurantRawMenu> dedupe = new LinkedHashMap<>();

      for (String selector : List.of(
              "div.place_section_content li",
              "div[class*='menu'] li",
              "ul[class*='menu'] li",
              "div[role='tabpanel'] li"
      )) {
         try {
            Locator locator = locatorFactory.apply(selector);
            int count = Math.min(locator.count(), MAX_MENU_COUNT);
            for (int i = 0; i < count; i++) {
               Locator row = locator.nth(i);
               String name = firstRowText(row,
                       "span.lPzHi",
                       "div.place_bluelink",
                       "strong",
                       "span[class*='name']",
                       "div[class*='title']");
               String price = firstRowText(row,
                       "div.GXS1X",
                       "span[class*='price']",
                       "em",
                       "div[class*='price']");
               if (!isBlank(name) && !isNoiseMenu(name)) {
                  String key = normalize(name) + "|" + normalize(price);
                  dedupe.putIfAbsent(key, new RestaurantRawMenu(
                          normalize(name),
                          normalize(price),
                          firstRowAttribute(row, "src", "img")
                  ));
               }
            }
            if (!dedupe.isEmpty()) {
               return new ArrayList<>(dedupe.values());
            }
         } catch (Exception ignored) {
         }
      }

      return List.of();
   }

   private List<RestaurantRawMenu> extractMenusFromMenuHtml(Document doc) {
      if (doc == null) {
         return List.of();
      }

      Map<String, RestaurantRawMenu> dedupe = new LinkedHashMap<>();
      for (Element row : doc.select("div.place_section_content li, div[class*=menu] li, ul[class*=menu] li, div[role=tabpanel] li")) {
         String name = firstNonBlank(
                 text(row, "span.lPzHi"),
                 text(row, "div.place_bluelink"),
                 text(row, "strong"),
                 text(row, "span[class*=name]"),
                 text(row, "div[class*=title]")
         );
         String price = firstNonBlank(
                 text(row, "div.GXS1X"),
                 text(row, "span[class*=price]"),
                 text(row, "em"),
                 text(row, "div[class*=price]")
         );
         if (!isBlank(name) && !isNoiseMenu(name)) {
            String key = normalize(name) + "|" + normalize(price);
            dedupe.putIfAbsent(key, new RestaurantRawMenu(
                    normalize(name),
                    normalize(price),
                    attr(row, "img", "src")
            ));
         }
      }
      return new ArrayList<>(dedupe.values());
   }

   private boolean isNoiseMenu(String name) {
      String normalized = normalize(name);
      return normalized.contains("전체 메뉴")
              || normalized.contains("즐겨찾는 서비스")
              || normalized.contains("정보")
              || normalized.contains("리뷰")
              || normalized.contains("사진")
              || normalized.length() < 2;
   }

   private Optional<Frame> findEntryFrame(Page page) {
      try {
         return page.frames().stream().filter(frame -> "entryIframe".equals(frame.name())).findFirst();
      } catch (Exception ignored) {
         return Optional.empty();
      }
   }

   private String firstRowText(Locator row, String... selectors) {
      for (String selector : selectors) {
         try {
            Locator locator = row.locator(selector);
            if (locator.count() > 0) {
               String value = locator.first().innerText();
               if (!isBlank(value)) {
                  return normalize(value);
               }
            }
         } catch (Exception ignored) {
         }
      }
      return null;
   }

   private String firstRowAttribute(Locator row, String attributeName, String selector) {
      try {
         Locator locator = row.locator(selector);
         return locator.count() == 0 ? null : normalize(locator.first().getAttribute(attributeName));
      } catch (Exception ignored) {
         return null;
      }
   }

   private String text(Element root, String css) {
      Element el = root.selectFirst(css);
      return el == null ? null : normalize(el.text());
   }

   private String attr(Element root, String css, String attr) {
      Element el = root.selectFirst(css);
      return el == null ? null : normalize(el.attr(attr));
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
}
