package com.kustaurant.kustaurant.common.modal.service;

import com.kustaurant.kustaurant.common.modal.infrastructure.HomeModalEntity;
import com.kustaurant.kustaurant.common.modal.infrastructure.HomeModalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HomeModalService {
    private final HomeModalRepository homeModalRepository;

    public HomeModalEntity get() {
        return homeModalRepository.findById(1)
                .filter(modal -> modal.getExpiredAt().isAfter(LocalDateTime.now()))
                .orElse(null);
    }
}
