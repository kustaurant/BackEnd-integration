package com.kustaurant.kustaurant.evaluation.comment.service;

import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.restaurant.restaurant.service.constants.RestaurantConstants;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentResponse;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.projection.EvalCommentProjection;
import com.kustaurant.kustaurant.user.user.service.UserIconResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EvalCommentMapper {
    public EvalCommentResponse toDto(
            EvalCommentProjection v,
            int likeStatus,
            boolean mine,
            List<EvalCommentResponse> replies
    ) {

        String userIconUrl = UserIconResolver.resolve(v.getRatedCnt());

        String commentImgUrl = (v.getImgUrl() == null || "no_img".equals(v.getImgUrl()))
                ? RestaurantConstants.REPLACE_IMG_URL
                : v.getImgUrl();

        return new EvalCommentResponse(
                v.getCommentId(),
                v.getScore(),
                userIconUrl,
                v.getNickname(),
                TimeAgoUtil.toKor(v.getWrittenAt()),
                commentImgUrl,
                v.getBody(),
                likeStatus,
                v.getLikeCnt(),
                v.getDislikeCnt(),
                mine,
                replies
        );
    }
}
