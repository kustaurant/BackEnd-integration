package com.kustaurant.kustaurant.mock;

import com.kustaurant.kustaurant.global.service.port.ClockHolder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

public class TestClockHolder implements ClockHolder {
    private final LocalDateTime fixedTime;

    public TestClockHolder(LocalDateTime fixedTime) {
        this.fixedTime = fixedTime;
    }

    @Override
    public LocalDateTime now() {
        return fixedTime;
    }
}
