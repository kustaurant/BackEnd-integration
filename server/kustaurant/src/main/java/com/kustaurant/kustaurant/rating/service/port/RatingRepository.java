package com.kustaurant.kustaurant.rating.service.port;

import com.kustaurant.kustaurant.rating.domain.model.Rating;
import java.util.List;

public interface RatingRepository {

    void saveAll(List<Rating> rating);
}
