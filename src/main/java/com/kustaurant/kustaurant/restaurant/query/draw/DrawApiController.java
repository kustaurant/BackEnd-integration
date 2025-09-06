package com.kustaurant.kustaurant.restaurant.query.draw;

import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.CuisineList;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.LocationList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class DrawApiController implements DrawApiDoc {

    private final RestaurantDrawService drawService;

    @GetMapping("/api/v2/draw")
    public ResponseEntity<Object> getRestaurantListForCuisine(
            @CuisineList List<String> cuisines,
            @LocationList List<String> locations
    ) {
        return new ResponseEntity<>(drawService.draw(cuisines, locations), HttpStatus.OK);
    }

}
