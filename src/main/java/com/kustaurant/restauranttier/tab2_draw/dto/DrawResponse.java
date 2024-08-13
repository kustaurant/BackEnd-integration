package com.kustaurant.restauranttier.tab2_draw.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DrawResponse<T> {
    private String message;
    private T data;

    public DrawResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }


}
