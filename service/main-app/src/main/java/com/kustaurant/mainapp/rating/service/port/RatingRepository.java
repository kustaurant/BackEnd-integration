package com.kustaurant.mainapp.rating.service.port;

import com.kustaurant.mainapp.rating.domain.model.Rating;
import java.util.List;

public interface RatingRepository {

    void saveAll(List<Rating> rating);
}
