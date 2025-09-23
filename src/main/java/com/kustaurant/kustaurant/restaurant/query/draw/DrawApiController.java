package com.kustaurant.kustaurant.restaurant.query.draw;

import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.ChartCond;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DrawApiController implements DrawApiDoc {

    private final RestaurantDrawService drawService;

    @GetMapping("/api/v2/draw")
    public ResponseEntity<Object> getRestaurantListForCuisine(
            @ChartCond ChartCondition condition
    ) {
        return new ResponseEntity<>(drawService.draw(condition), HttpStatus.OK);
    }

}
