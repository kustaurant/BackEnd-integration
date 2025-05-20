package com.kustaurant.kustaurant.common.modal.service;

import com.kustaurant.kustaurant.common.modal.infrastructure.HomeModalEntity;
import com.kustaurant.kustaurant.common.modal.infrastructure.HomeModalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HomeModalServiceTest {

    private HomeModalRepository homeModalRepository;
    private HomeModalService homeModalService;

    @BeforeEach
    void setUp() {
        homeModalRepository = mock(HomeModalRepository.class);
        homeModalService = new HomeModalService(homeModalRepository);
    }

    @Test
    void 만료되지_않은_modal은_조회된다(){
        //g
        //테스트시간
        LocalDateTime testTime = LocalDateTime.of(2026, 4, 3, 12, 0);

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
        expiredModal.setCreatedAt(LocalDateTime.now().minusDays(2));
        expiredModal.setExpiredAt(LocalDateTime.now().minusHours(1)); // 만료됨

        when(homeModalRepository.findById(1)).thenReturn(Optional.of(expiredModal));

        // when
        HomeModalEntity result = homeModalService.get();

        // then
        assertThat(result).isNull();
    }
}