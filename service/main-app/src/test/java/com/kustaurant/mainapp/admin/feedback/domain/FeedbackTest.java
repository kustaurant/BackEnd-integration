package com.kustaurant.mainapp.admin.feedback.domain;

import com.kustaurant.mainapp.admin.feedback.controller.Request.FeedbackRequest;
import com.kustaurant.mainapp.mock.common.TestClockHolder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FeedbackTest {
    private final LocalDateTime testTime = LocalDateTime.of(2025, 6, 26, 12, 12);

    @Test
    void Feedback_from_메서드는_정상적으로_객체를_생성한다() {
        //g
        Long writerId=1L;
        String comment = "테스트용";
        FeedbackRequest req = new FeedbackRequest(comment);

        //w
        Feedback feedback = Feedback.from(writerId, req, new TestClockHolder(testTime));

        //t
        assertThat(feedback.getComment()).isEqualTo(comment);
        assertThat(feedback.getWriter()).isEqualTo(writerId);

    }

}