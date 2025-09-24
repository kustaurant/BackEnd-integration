package com.kustaurant.mainapp.mock.common;

import com.kustaurant.mainapp.common.clockHolder.ClockHolder;

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
