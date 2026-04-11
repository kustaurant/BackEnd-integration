package com.kustaurant.crawler.aianalysis.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "OPEN_AI_MODEL=gpt-4o-mini"
})
class AnalyzeReviewServiceImplTest {

//    @Autowired
//    private AiAnalysisJobRepo aiAnalysisJobRepo;
//
//    @Autowired
//    private AnalyzeReviewService analyzeReviewService;
//
//    @Autowired
//    private RestaurantJpaRepository restaurantJpaRepository;
//
//    @Test
//    void 병렬_처리_데드락_테스트() throws ExecutionException, InterruptedException {
//        ExecutorService WORKER_POOL = new ThreadPoolExecutor(
//                20, 20, 0L,
//                TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<>()
//        );
//
//        RestaurantEntity restaurantEntity = new RestaurantEntity(null, "가나다", "한식", "중문~어대", "경기도", "000",
//                "www", "www", 0, "한식", 33.0, 33.0, null, "ACTIVE");
//        long restaurantId = restaurantJpaRepository.save(restaurantEntity).getRestaurantId();
//
//        LocalDateTime now = LocalDateTime.now(
//                Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC")));
//        AiAnalysisJob job = AiAnalysisJob.create(restaurantId, now);
//        long jobId = aiAnalysisJobRepo.save(job);
//
//        List<Future<?>> futures = new ArrayList<>();
//        int count = 200;
//        for (int i = 0; i < count; i++) {
//            futures.add(WORKER_POOL.submit(() -> analyzeReviewService.analyzeAndSave(
//                    new AiAnalysisReq(jobId, restaurantId, "너무 너무 맛있어요. 근데 조금 아쉽기도 하고. 근데 좋았습니다."))));
//        }
//
//        for (Future<?> f : futures) f.get();
//
//        WORKER_POOL.shutdown();
//        WORKER_POOL.awaitTermination(10, TimeUnit.SECONDS);
//
//        Assertions.assertThat(aiAnalysisJobRepo.findJob(jobId).getProcessedReviews())
//                .isEqualTo(count);
//    }
}