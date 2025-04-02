package com.kustaurant.kustaurant.common.feedback.infrastructure;

import com.kustaurant.kustaurant.common.feedback.domain.Feedback;
import com.kustaurant.kustaurant.common.feedback.service.port.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryImple implements FeedbackRepository {
    private final FeedbackJpaRepository feedbackJpaRepository;

    @Override
    public List<Feedback> findAllByUserId(Integer userId) {
        return feedbackJpaRepository.findAllByUser_UserId(userId)
                .stream().map(FeedbackEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public Feedback save(Feedback feedback) {
        return null;
    }
}
