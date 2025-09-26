package com.kustaurant.kustaurant.admin.adminPage.controller.response;

public record AdminStatsResponse (
        Long totalRestaurants,
        Long totalReports,
        Long totalFeedback,
        Long totalCommunityPosts,
        Long totalEvaluations,
        Long totalCommunityComments,
        Long totalEvaluationComments,
        Long totalUsers,
        Long totalNaverUsers,
        Long totalAppleUsers
) {}