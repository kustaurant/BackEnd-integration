package com.kustaurant.mainapp.common.dto;

import com.kustaurant.mainapp.user.user.domain.User;
import com.kustaurant.mainapp.common.util.UserIconResolver;
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
