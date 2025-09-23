package com.kustaurant.mainapp.admin.adminPage.service;

import com.kustaurant.mainapp.admin.adminPage.controller.request.HomeModalUpdateRequest;
import com.kustaurant.mainapp.admin.adminPage.controller.response.*;
import com.kustaurant.mainapp.admin.adminPage.infrastructure.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminStatsQueryRepository adminStatsQueryRepository;
    private final AdminUserQueryRepository adminUserQueryRepository;
    private final AdminRestaurantQueryRepository adminRestaurantQueryRepository;
    private final AdminFeedbackQueryRepository adminFeedbackQueryRepository;
    private final AdminModalRepository adminModalRepository;

    public AdminStatsResponse getAdminStats() {
        return adminStatsQueryRepository.getAdminStats();
    }

    // 유저 관련 메서드들
    public PagedUserResponse getNewUsers(Pageable pageable) {
        return adminUserQueryRepository.getNewUsers(pageable);
    }

    public PagedUserResponse getAllUsers(Pageable pageable) {
        return adminUserQueryRepository.getAllUsers(pageable);
    }

    public Long getNewUsersCount() {
        return adminUserQueryRepository.getNewUsersCount();
    }

    // 음식점 관련 메서드들
    public PagedRestaurantResponse getAllRestaurants(Pageable pageable) {
        return adminRestaurantQueryRepository.getAllRestaurants(pageable);
    }

    // 피드백 관련 메서드들
    public PagedFeedbackResponse getAllFeedbacks(Pageable pageable) {
        return adminFeedbackQueryRepository.getAllFeedbacks(pageable);
    }

    // 모달 관련 메서드들
    public HomeModalResponse getCurrentModal() {
        return adminModalRepository.getCurrentModal();
    }

    @Transactional
    public HomeModalResponse updateModal(HomeModalUpdateRequest request) {
        return adminModalRepository.updateModal(
                request.title(),
                request.body(),
                request.expiredAt()
        );
    }

    @Transactional
    public void deleteModal() {
        adminModalRepository.deleteModal();
    }
}