package com.kustaurant.kustaurant.rating.infrastructure.jpa;

import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.infrastructure.jpa.entity.RatingEntity;
import com.kustaurant.kustaurant.rating.infrastructure.jpa.repository.RatingJpaRepository;
import com.kustaurant.kustaurant.rating.service.port.RatingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RatingRepositoryImpl implements RatingRepository {

    private final RatingJpaRepository ratingJpaRepository;

    @Override
    public void saveAll(List<Rating> rating) {
        ratingJpaRepository.saveAll(rating.stream().map(RatingEntity::from).toList());
    }
}
