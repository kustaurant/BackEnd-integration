package com.kustaurant.kustaurant.admin.adminPage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsResponse {
    private Long totalRestaurants;
    private Long totalReports;
    private Long totalFeedback;
    private Long totalCommunityPosts;
    private Long totalEvaluations;
    private Long totalCommunityComments;
    private Long totalEvaluationComments;
    private Long totalUsers;
    private Long totalNaverUsers;
    private Long totalAppleUsers;
}