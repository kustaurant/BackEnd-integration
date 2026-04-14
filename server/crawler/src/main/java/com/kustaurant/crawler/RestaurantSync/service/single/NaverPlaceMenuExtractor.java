package com.kustaurant.crawler.RestaurantSync.service.single;

import com.kustaurant.naverplace.NaverPlaceMenu;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class NaverPlaceMenuExtractor {
   private static final int MAX_MENU_COUNT = 50;

   public List extractMenus(Document menuDoc, Page page) {
      List<NaverPlaceMenu> menus = this.extractMenusFromMenuHtml(menuDoc);
      return menus.isEmpty() ? this.extractMenusFromLiveDom(page) : menus;
   }

   private List extractMenusFromLiveDom(Page page) {
      Optional<Frame> entryFrame = this.findEntryFrame(page);
      if (entryFrame.isPresent()) {
         Frame var10001 = (Frame)entryFrame.get();
         Objects.requireNonNull(var10001);
         List<NaverPlaceMenu> fromFrame = this.extractMenusFromLocatorSource(var10001::locator);
         if (!fromFrame.isEmpty()) {
            return fromFrame;
         }
      }

      Objects.requireNonNull(page);
      return this.extractMenusFromLocatorSource(page::locator);
   }

   private List extractMenusFromLocatorSource(Function locatorFactory) {
      Map<String, NaverPlaceMenu> dedupe = new LinkedHashMap();

      for(String selector : List.of("div.place_section_content li", "div[class*='menu'] li", "ul[class*='menu'] li", "div[role='tabpanel'] li")) {
         try {
            Locator locator = (Locator)locatorFactory.apply(selector);
            int count = Math.min(locator.count(), 50);

            for(int i = 0; i < count; ++i) {
               Locator row = locator.nth(i);
               String name = this.firstRowText(row, "span.lPzHi", "div.place_bluelink", "strong", "span[class*='name']", "div[class*='title']");
               String price = this.firstRowText(row, "div.GXS1X", "span[class*='price']", "em", "div[class*='price']");
               if (!this.isBlank(name) && !this.isNoiseMenu(name)) {
                  String var10000 = this.normalize(name);
                  String key = var10000 + "|" + this.normalize(price);
                  dedupe.putIfAbsent(key, new NaverPlaceMenu(this.normalize(name), this.normalize(price), this.firstRowAttribute(row, "src", "img")));
               }
            }

            if (!dedupe.isEmpty()) {
               return new ArrayList(dedupe.values());
            }
         } catch (Exception var12) {
         }
      }

      return List.of();
   }

   private List extractMenusFromMenuHtml(Document doc) {
      if (doc == null) {
         return List.of();
      } else {
         Map<String, NaverPlaceMenu> dedupe = new LinkedHashMap();

         for(Element row : doc.select("div.place_section_content li, div[class*=menu] li, ul[class*=menu] li, div[role=tabpanel] li")) {
            String name = this.firstNonBlank(this.text(row, "span.lPzHi"), this.text(row, "div.place_bluelink"), this.text(row, "strong"), this.text(row, "span[class*=name]"), this.text(row, "div[class*=title]"));
            String price = this.firstNonBlank(this.text(row, "div.GXS1X"), this.text(row, "span[class*=price]"), this.text(row, "em"), this.text(row, "div[class*=price]"));
            if (!this.isBlank(name) && !this.isNoiseMenu(name)) {
               String var10000 = this.normalize(name);
               String key = var10000 + "|" + this.normalize(price);
               dedupe.putIfAbsent(key, new NaverPlaceMenu(this.normalize(name), this.normalize(price), this.attr(row, "img", "src")));
            }
         }

         return new ArrayList(dedupe.values());
      }
   }

   private boolean isNoiseMenu(String name) {
      String normalized = this.normalize(name);
      return normalized.contains("전체 펼침") || normalized.contains("즐겨찾는 서비스") || normalized.contains("정보") || normalized.contains("리뷰") || normalized.contains("사진") || normalized.length() < 2;
   }

   private Optional findEntryFrame(Page page) {
      try {
         return page.frames().stream().filter((frame) -> "entryIframe".equals(frame.name())).findFirst();
      } catch (Exception var3) {
         return Optional.empty();
      }
   }

   private String firstRowText(Locator row, String... selectors) {
      for(String selector : selectors) {
         try {
            Locator locator = row.locator(selector);
            if (locator.count() != 0) {
               String value = locator.first().innerText();
               if (!this.isBlank(value)) {
                  return this.normalize(value);
               }
            }
         } catch (Exception var9) {
         }
      }

      return null;
   }

   private String firstRowAttribute(Locator row, String attributeName, String selector) {
      try {
         Locator locator = row.locator(selector);
         return locator.count() == 0 ? null : this.normalize(locator.first().getAttribute(attributeName));
      } catch (Exception var5) {
         return null;
      }
   }

   private String text(Element root, String css) {
      Element el = root.selectFirst(css);
      return el == null ? null : this.normalize(el.text());
   }

   private String attr(Element root, String css, String attr) {
      Element el = root.selectFirst(css);
      return el == null ? null : this.normalize(el.attr(attr));
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
