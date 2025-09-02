package com.kustaurant.kustaurant.common.dto;

import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.UserIconResolver;
import lombok.Builder;

@Builder
public record UserSummary(
        Long userId,
        String nickname,
        long evalCount,
        String iconUrl
){
    public static UserSummary from(User user) {
        return UserSummary.builder()
                .userId(user.getId())
                .nickname(user.getNickname().getValue())
                .evalCount(user.getEvalCount())
                .iconUrl(UserIconResolver.resolve(user.getEvalCount()))
                .build();
    }
}
