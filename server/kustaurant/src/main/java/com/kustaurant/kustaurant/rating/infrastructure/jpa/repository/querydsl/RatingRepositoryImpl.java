package com.kustaurant.kustaurant.rating.infrastructure.jpa.repository.querydsl;

import com.kustaurant.jpa.rating.entity.RatingEntity;
import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.infrastructure.jpa.mapper.RatingMapper;
import com.kustaurant.jpa.rating.repository.RatingJpaRepository;
import com.kustaurant.kustaurant.rating.service.port.RatingRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RatingRepositoryImpl implements RatingRepository {

    private final RatingJpaRepository ratingJpaRepository;

    @Override
    public void saveAll(List<Rating> rating) {
        List<Long> ids = rating.stream().map(Rating::getRestaurantId).distinct().toList();
        List<RatingEntity> existed = ratingJpaRepository.findAllById(ids);
        Map<Long, RatingEntity> existedMap = new HashMap<>();
        for (RatingEntity ratingEntity : existed) {
            existedMap.put(ratingEntity.getRestaurantId(), ratingEntity);
        }

        List<RatingEntity> created = new ArrayList<>();

        for (Rating r : rating) {
            if (existedMap.containsKey(r.getRestaurantId())) {
                existedMap.get(r.getRestaurantId()).updateRatingData(
                        r.getScore(), r.getTier().getValue(), r.isTemp(), r.getRatedAt(), r.getFinalScore()
                );
            } else {
                created.add(RatingMapper.from(r));
            }
        }

        ratingJpaRepository.saveAll(created);
    }
}
