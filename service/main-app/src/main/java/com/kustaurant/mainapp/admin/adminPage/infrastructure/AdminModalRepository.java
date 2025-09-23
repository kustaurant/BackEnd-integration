package com.kustaurant.mainapp.admin.adminPage.infrastructure;

import com.kustaurant.mainapp.admin.adminPage.controller.response.HomeModalResponse;
import com.kustaurant.mainapp.admin.modal.HomeModalEntity;
import com.kustaurant.mainapp.admin.modal.HomeModalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class AdminModalRepository {

    private final HomeModalRepository homeModalRepository;
    private static final int MODAL_ID_IS_ONLY_ONE = 1;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public HomeModalResponse getCurrentModal() {
        HomeModalEntity entity = homeModalRepository.findById(MODAL_ID_IS_ONLY_ONE)
                .orElse(null);
        
        if (entity == null) {
            return null;
        }
        
        boolean isActive = entity.getExpiredAt() != null && entity.getExpiredAt().isAfter(LocalDateTime.now());
        
        return new HomeModalResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getBody(),
                entity.getCreatedAt(),
                entity.getExpiredAt(),
                isActive
        );
    }

    public HomeModalResponse updateModal(String title, String body, LocalDateTime expiredAt) {
        HomeModalEntity entity = homeModalRepository.findById(MODAL_ID_IS_ONLY_ONE)
                .orElse(new HomeModalEntity());
        
        entity.setTitle(title);
        entity.setBody(body);
        entity.setExpiredAt(expiredAt);
        
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        
        entity = homeModalRepository.save(entity);
        
        boolean isActive = entity.getExpiredAt() != null && entity.getExpiredAt().isAfter(LocalDateTime.now());
        
        return new HomeModalResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getBody(),
                entity.getCreatedAt(),
                entity.getExpiredAt(),
                isActive
        );
    }

    public void deleteModal() {
        homeModalRepository.findById(MODAL_ID_IS_ONLY_ONE)
                .ifPresent(entity -> {
                    // 만료일을 현재 시간 이전으로 설정하여 비활성화
                    entity.setExpiredAt(LocalDateTime.now().minusDays(1));
                    homeModalRepository.save(entity);
                });
    }
}