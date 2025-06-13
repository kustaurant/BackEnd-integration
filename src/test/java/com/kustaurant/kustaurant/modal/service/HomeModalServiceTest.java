package com.kustaurant.kustaurant.modal.service;

import com.kustaurant.kustaurant.modal.infrastructure.HomeModalEntity;
import com.kustaurant.kustaurant.modal.infrastructure.HomeModalRepository;
import com.kustaurant.kustaurant.global.service.port.ClockHolder;
import com.kustaurant.kustaurant.mock.TestClockHolder;
import com.kustaurant.kustaurant.modal.service.HomeModalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HomeModalServiceTest {

    private HomeModalRepository homeModalRepository;
    private HomeModalService homeModalService;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        homeModalRepository = mock(HomeModalRepository.class);

        testTime = LocalDateTime.of(2025, 4, 3, 12, 0);
        ClockHolder testClockHolder = new TestClockHolder(testTime);

        homeModalService = new HomeModalService(homeModalRepository, testClockHolder);
    }

    @Test
    void 만료되지_않은_modal은_조회된다(){
        //g
        HomeModalEntity modal = new HomeModalEntity();
        modal.setTitle("유효한 모달");
        modal.setCreatedAt(testTime.minusDays(1));
        modal.setExpiredAt(testTime.plusDays(1));

        when(homeModalRepository.findById(1)).thenReturn(Optional.of(modal));

        //w
        HomeModalEntity result = homeModalService.get();

        //t
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("유효한 모달");
    }

    @Test
    void 만료된_모달은_null을_반환한다() {
        // given
        HomeModalEntity expiredModal = new HomeModalEntity();
        expiredModal.setTitle("만료된 모달");
        expiredModal.setCreatedAt(testTime.minusDays(2));
        expiredModal.setExpiredAt(testTime.minusHours(1)); // 만료됨

        when(homeModalRepository.findById(1)).thenReturn(Optional.of(expiredModal));

        // when
        HomeModalEntity result = homeModalService.get();

        // then
        assertThat(result).isNull();
    }
}