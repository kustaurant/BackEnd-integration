package com.kustaurant.kustaurant.common.service.port;

import java.time.LocalDateTime;

public interface ClockHolder {

    LocalDateTime now();
}
