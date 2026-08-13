package com.kustaurant.crawler.RestaurantSync.service.single;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustaurant.crawler.RestaurantSync.service.zone.ZoneResultPolicy;
import com.kustaurant.crawler.infrastructure.crawler.playwright.PlaywrightManager;
import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.RestaurantRaw;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "ZONE_DETAIL_BENCHMARK", matches = "true")
class ZoneDetailPerformanceBenchmarkTest {
   private static final Map<ZoneType, List<String>> PLACE_IDS = Map.of(
           ZoneType.ENTRANCE_TO_MIDDLE, List.of(
                   "1147316117", "1942535759", "1017400694", "1472207522", "11574647",
                   "20502394", "1491159689", "1031081155", "1651919158", "1870282832"
           ),
           ZoneType.MIDDLE_TO_PARK, List.of(
                   "1108218837", "1308720943", "1220429339", "36259116", "1717778616",
                   "371256669", "1336548884", "1096868928", "1003680357", "1043470699"
           ),
           ZoneType.BACK_GATE, List.of(
                   "1025538278", "1768962114", "1995275174", "1854582204", "1160743896",
                   "1084941476", "1502246754", "1731256186", "1778931681", "1198483581"
           ),
           ZoneType.FRONT_GATE, List.of(
                   "1680939918", "1426675615", "2076955322", "1690309548", "11717380",
                   "2066467057", "38229720", "1645755407", "37211524", "37851081"
           ),
           ZoneType.GUI_STATION, List.of(
                   "11489305", "1440507721", "2057162493", "1292962436", "1128282294",
                   "1884031247", "1585565927", "11807283", "1452640597", "1808134871"
           )
   );

   @Test
   void comparesOriginalLegacyAndCurrentApolloDetailFlow() throws Exception {
      PlaywrightManager manager = new PlaywrightManager(new SimpleMeterRegistry());
      ZoneResultPolicy policy = new ZoneResultPolicy();
      RestaurantSingleCrawler crawler = new RestaurantSingleCrawler(
              manager,
              new RestaurantPageDriver(),
              new RestaurantResponseCollector(),
              new NaverApolloStateParser(new ObjectMapper()),
              new RestaurantInfoExtractor(),
              new RestaurantMenuExtractor(),
              new RestaurantMenuDataResolver(),
              policy
      );

      ZoneType zone = ZoneType.valueOf(System.getenv("ZONE_DETAIL_BENCHMARK_SCOPE"));
      List<String> ids = PLACE_IDS.get(zone);
      BenchmarkResult legacy = runLegacy(crawler, policy, ids);
      BenchmarkResult current = runCurrent(manager, crawler, policy, ids);

      Map<String, Object> row = new LinkedHashMap<>();
      row.put("zone", zone.name());
      row.put("sampleCount", ids.size());
      row.put("legacyDurationSeconds", legacy.durationSeconds());
      row.put("legacyMeaningfulCount", legacy.meaningfulCount());
      row.put("currentDurationSeconds", current.durationSeconds());
      row.put("currentMeaningfulCount", current.meaningfulCount());
      row.put("reductionPercent", round((legacy.durationSeconds() - current.durationSeconds())
              / legacy.durationSeconds() * 100.0));
      System.out.println("DETAIL_BENCHMARK=" + new ObjectMapper().writeValueAsString(row));

      Path outputDirectory = Path.of("build", "benchmark");
      Files.createDirectories(outputDirectory);
      String runId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
      Path output = outputDirectory.resolve("detail-" + zone + "-" + runId + ".json");
      new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(output.toFile(), row);
      System.out.println("DETAIL_BENCHMARK_OUTPUT=" + output.toAbsolutePath());

      assertThat(row).containsEntry("sampleCount", 10);
   }

   private BenchmarkResult runLegacy(
           RestaurantSingleCrawler crawler,
           ZoneResultPolicy policy,
           List<String> ids
   ) throws Exception {
      setMode(crawler, "LEGACY");
      long started = System.nanoTime();
      int meaningful = 0;
      for (String id : ids) {
         RestaurantRaw result = crawler.crawl(placeUrl(id));
         if (policy.isMeaningfulResult(result)) meaningful++;
         Thread.sleep(ThreadLocalRandom.current().nextLong(3_000L, 5_001L));
      }
      return new BenchmarkResult(elapsedSeconds(started), meaningful);
   }

   private BenchmarkResult runCurrent(
           PlaywrightManager manager,
           RestaurantSingleCrawler crawler,
           ZoneResultPolicy policy,
           List<String> ids
   ) throws Exception {
      setMode(crawler, "APOLLO_FIRST");
      long started = System.nanoTime();
      int meaningful = manager.withReusableBrowser(() -> {
         int count = 0;
         for (String id : ids) {
            RestaurantRaw result = crawler.crawl(placeUrl(id));
            if (policy.isMeaningfulResult(result)) count++;
         }
         return count;
      });
      return new BenchmarkResult(elapsedSeconds(started), meaningful);
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   private void setMode(RestaurantSingleCrawler crawler, String mode) throws Exception {
      Field field = RestaurantSingleCrawler.class.getDeclaredField("detailMode");
      field.setAccessible(true);
      Class<? extends Enum> enumType = (Class<? extends Enum>) field.getType();
      field.set(crawler, Enum.valueOf(enumType, mode));
   }

   private String placeUrl(String id) {
      return "https://map.naver.com/p/entry/place/" + id;
   }

   private double elapsedSeconds(long started) {
      return round((System.nanoTime() - started) / 1_000_000_000.0);
   }

   private double round(double value) {
      return Math.round(value * 1_000.0) / 1_000.0;
   }

   private record BenchmarkResult(double durationSeconds, int meaningfulCount) {
   }
}
