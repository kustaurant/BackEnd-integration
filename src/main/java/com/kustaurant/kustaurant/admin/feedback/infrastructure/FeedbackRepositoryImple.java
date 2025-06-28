package com.kustaurant.kustaurant.admin.feedback.infrastructure;

import com.kustaurant.kustaurant.admin.feedback.domain.Feedback;
import com.kustaurant.kustaurant.admin.feedback.service.port.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryImple implements FeedbackRepository {
    private final FeedbackJpaRepository feedbackJpaRepository;

    @Override
    public List<Feedback> findAllByUserId(Long userId) {
        return feedbackJpaRepository.findAllByUserId(userId)
                .stream().map(FeedbackEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public void save(Feedback feedback) {
        feedbackJpaRepository.save(FeedbackEntity.from(feedback.getWriter(), feedback));
    }
}
