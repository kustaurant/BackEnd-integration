package com.kustaurant.kustaurant.common.infrastructure;

import com.kustaurant.kustaurant.common.service.port.ClockHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SystemClockHolder implements ClockHolder {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
