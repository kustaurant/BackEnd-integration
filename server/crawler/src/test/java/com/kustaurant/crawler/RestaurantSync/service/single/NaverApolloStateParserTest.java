package com.kustaurant.crawler.RestaurantSync.service.single;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class NaverApolloStateParserTest {

   private final NaverApolloStateParser parser = new NaverApolloStateParser(new ObjectMapper());

   @Test
   void parsesPlaceAndMenusFromSerializedApolloCache() {
      String html = """
              <html><head><script>
              window.__APOLLO_STATE__ = {
                "PlaceDetailBase:1183785127": {
                  "__typename": "PlaceDetailBase",
                  "id": "1183785127",
                  "name": "고베규카츠 건대점",
                  "category": "돈가스",
                  "address": "서울 광진구 화양동 3-73",
                  "roadAddress": "서울 광진구 아차산로33길 64",
                  "virtualPhone": "0507-1442-4620",
                  "coordinate": {"__typename":"Coordinate","x":"127.0711644","y":"37.5430071"}
                },
                "Menu:1183785127_0": {
                  "__typename": "Menu",
                  "id": "1183785127_0",
                  "name": "규카츠 정식",
                  "price": "19000",
                  "description": "중괄호 } 와 따옴표 \\"가 포함된 설명",
                  "images": ["https://example.com/menu.jpg"]
                },
                "ROOT_QUERY": {
                  "__typename": "Query",
                  "placeDetail({\\\"input\\\":{\\\"id\\\":\\\"1183785127\\\"}})": {
                    "__typename": "PlaceDetail",
                    "base": {"__ref": "PlaceDetailBase:1183785127"},
                    "phoneInfo": {"phone": "02-123-4567"},
                    "images": {"images": [{"origin": "https://example.com/place.jpg"}]},
                    "menus({\\\"source\\\":[\\\"tpirates\\\"]})": [
                      {"__ref": "Menu:1183785127_0"}
                    ]
                  }
                }
              };
              window.__PLACE_STATE__ = {"unrelated": true};
              </script></head><body></body></html>
              """;

      var result = parser.parse(html, "1183785127");

      assertThat(result).isPresent();
      var data = result.orElseThrow();
      assertThat(data.placeName()).isEqualTo("고베규카츠 건대점");
      assertThat(data.category()).isEqualTo("돈가스");
      assertThat(data.restaurantAddress()).isEqualTo("서울 광진구 아차산로33길 64");
      assertThat(data.phoneNumber()).isEqualTo("02-123-4567");
      assertThat(data.latitude()).isEqualTo(37.5430071);
      assertThat(data.longitude()).isEqualTo(127.0711644);
      assertThat(data.imageUrl()).isEqualTo("https://example.com/place.jpg");
      assertThat(data.menuDataPresent()).isTrue();
      assertThat(data.menus()).singleElement().satisfies(menu -> {
         assertThat(menu.menuName()).isEqualTo("규카츠 정식");
         assertThat(menu.menuPrice()).isEqualTo("19,000원");
         assertThat(menu.menuImageUrl()).isEqualTo("https://example.com/menu.jpg");
      });
   }

   @Test
   void treatsAnExplicitEmptyMenuListAsAuthoritative() {
      String html = """
              <script>
              window.__APOLLO_STATE__ = {
                "PlaceDetailBase:42": {
                  "__typename":"PlaceDetailBase", "id":"42", "name":"메뉴 없는 식당"
                },
                "ROOT_QUERY": {
                  "placeDetail": {
                    "__typename":"PlaceDetail",
                    "base":{"__ref":"PlaceDetailBase:42"},
                    "menus":[]
                  }
                }
              };
              </script>
              """;

      var data = parser.parse(html, "42").orElseThrow();

      assertThat(data.menuDataPresent()).isTrue();
      assertThat(data.menus()).isEmpty();
   }

   @Test
   void preservesLegacyLabelsForFreeAndVariablePrices() {
      String html = """
              <script>
              window.__APOLLO_STATE__ = {
                "PlaceDetailBase:42": {
                  "__typename":"PlaceDetailBase", "id":"42", "name":"가격 표기 식당"
                },
                "Menu:42_0": {
                  "__typename":"Menu", "id":"42_0", "name":"무료 서비스", "price":"0"
                },
                "Menu:42_1": {
                  "__typename":"Menu", "id":"42_1", "name":"시가 메뉴", "price":null
                },
                "ROOT_QUERY": {
                  "placeDetail": {
                    "__typename":"PlaceDetail",
                    "base":{"__ref":"PlaceDetailBase:42"},
                    "menus":[{"__ref":"Menu:42_0"},{"__ref":"Menu:42_1"}]
                  }
                }
              };
              </script>
              """;

      var data = parser.parse(html, "42").orElseThrow();

      assertThat(data.menus()).extracting(menu -> menu.menuPrice())
              .containsExactly("무료", "변동");
   }

   @Test
   void ignoresApolloStateForAnotherPlace() {
      String html = """
              <script>
              window.__APOLLO_STATE__ = {
                "PlaceDetailBase:999": {
                  "__typename":"PlaceDetailBase", "id":"999", "name":"다른 식당"
                }
              };
              </script>
              """;

      assertThat(parser.parse(html, "42")).isEmpty();
   }

   @Test
   void doesNotTreatBrokenMenuReferencesAsAuthoritative() {
      String html = """
              <script>
              window.__APOLLO_STATE__ = {
                "PlaceDetailBase:42": {
                  "__typename":"PlaceDetailBase", "id":"42", "name":"참조 깨진 식당"
                },
                "ROOT_QUERY": {
                  "placeDetail": {
                    "__typename":"PlaceDetail",
                    "base":{"__ref":"PlaceDetailBase:42"},
                    "menus":[{"__ref":"Menu:42_missing"}]
                  }
                }
              };
              </script>
              """;

      var data = parser.parse(html, "42").orElseThrow();

      assertThat(data.menuDataPresent()).isFalse();
      assertThat(data.menus()).isEmpty();
   }

   @Test
   void returnsEmptyWhenApolloStateIsMissingOrMalformed() {
      assertThat(parser.parse("<html><body>일반 문서</body></html>", "42")).isEmpty();
      assertThat(parser.parse("<script>window.__APOLLO_STATE__ = {</script>", "42")).isEmpty();
   }
}
