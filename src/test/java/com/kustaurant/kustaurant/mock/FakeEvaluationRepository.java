package com.kustaurant.kustaurant.mock;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationCommandRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationQueryRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FakeEvaluationRepository implements EvaluationQueryRepository, EvaluationCommandRepository {
    private final Map<Long, Evaluation> data = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);
    @Override
    public Long create(Evaluation evaluation) {
        Long id = evaluation.getId() == null ? seq.getAndIncrement() : evaluation.getId();
        Evaluation saved = evaluation.toBuilder().id(id).build();
        data.put(id, saved);
        return id;
    }

    public void saveAll(Collection<Evaluation> list) {
        list.forEach(this::create);
    }

    @Override
    public void reEvaluate(Evaluation evaluation) {

    }

    @Override
    public void react(Evaluation evaluation) {

    }

    @Override
    public Evaluation findActiveById(Long id) {
        return null;
    }

    @Override
    public boolean existsByUserAndRestaurant(Long userId, Integer restaurantId) {
        return false;
    }

    @Override
    public boolean existsByRestaurantAndEvaluation(Integer restaurantId, Long evaluationId) {
        return false;
    }

    @Override
    public Optional<Evaluation> findActiveByUserAndRestaurant(Long userId, Integer restaurantId) {
        return Optional.empty();
    }

    @Override
    public int countByStatus(String status) {
        return 0;
    }

    @Override
    public List<Evaluation> findByRestaurantIdOrderByCreatedAtDesc(Integer restaurantId) {
        return data.values().stream()
                .filter(ev -> ev.getRestaurantId().equals(restaurantId))
                .sorted(Comparator.comparing(Evaluation::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Evaluation> findByRestaurantIdOrderByLikeCountDesc(Integer restaurantId) {
        return data.values().stream()
                .filter(ev -> ev.getRestaurantId().equals(restaurantId))
                .sorted(Comparator.comparing(Evaluation::getLikeCount).reversed())
                .toList();
    }
}
