package com.kustaurant.mainapp.admin.modal;

import com.kustaurant.mainapp.common.clockHolder.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeModalService {
    private final HomeModalRepository homeModalRepository;
    private final ClockHolder clockHolder;
    private static final int MODAL_ID_IS_ONLY_ONE = 1;

    public HomeModalEntity get() {
        return homeModalRepository.findById(MODAL_ID_IS_ONLY_ONE)
                .filter(modal -> modal.getExpiredAt().isAfter(clockHolder.now()))
                .orElse(null);
    }
}
