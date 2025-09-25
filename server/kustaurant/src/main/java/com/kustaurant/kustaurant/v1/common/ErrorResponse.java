package com.kustaurant.kustaurant.v1.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String status;
    private String message;
}