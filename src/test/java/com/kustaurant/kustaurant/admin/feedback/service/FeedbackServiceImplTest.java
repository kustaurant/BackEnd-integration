package com.kustaurant.kustaurant.admin.feedback.service;

import com.kustaurant.kustaurant.admin.feedback.controller.Request.FeedbackRequest;
import com.kustaurant.kustaurant.admin.feedback.domain.Feedback;
import com.kustaurant.kustaurant.common.clockHolder.ClockHolder;
import com.kustaurant.kustaurant.mock.admin.FakeFeedbackRepository;
import com.kustaurant.kustaurant.mock.common.TestClockHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FeedbackServiceImplTest {
    private FeedbackServiceImpl feedbackService;
    private FakeFeedbackRepository fakeFeedbackRepository;
    private LocalDateTime testTime;

    @BeforeEach
    void init(){
        testTime = LocalDateTime.of(2025, 6, 26, 0, 12);
        ClockHolder testClockHolder = new TestClockHolder(testTime);
        fakeFeedbackRepository = new FakeFeedbackRepository();
        feedbackService = new FeedbackServiceImpl(fakeFeedbackRepository, testClockHolder);
    }

    @Test
    @DisplayName("피드백 생성 성공후 저장됨")
    void testFeedbackCreate() {
        //g
        Long userId = 1L;
        String comment = "테스트 입니다";
        FeedbackRequest req = new FeedbackRequest(comment);

        //w
        feedbackService.create(userId, req);

        //t
        List<Feedback> saved = fakeFeedbackRepository.findAllByUserId(userId);
        assertThat(saved).hasSize(1);

        Feedback feedback = saved.get(0);
        assertThat(feedback.getWriter()).isEqualTo(userId);
        assertThat(feedback.getComment()).isEqualTo(comment);
        assertThat(feedback.getCreatedAt()).isEqualTo(testTime);
        assertThat(feedback.getId()).isNotNull();
    }

}