package com.kustaurant.restauranttier.tab3_tier.constants;

import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantComment;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

public class RestaurantConstants {
    public static final String REPLACE_IMG_URL ="https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/%EC%BF%A0%EC%8A%A4%ED%86%A0%EB%9E%91%EB%A1%9C%EA%B3%A0.png";

    public static final List<StarComment> STAR_COMMENTS = List.of(
            new StarComment(0.5, "다시 갈 것 같진 않아요."),
            new StarComment(1.0, "많이 아쉬워요"),
            new StarComment(1.5, "다른데 갈껄"),
            new StarComment(2.0, "조금 아쉬워요."),
            new StarComment(2.5, "무난했어요."),
            new StarComment(3.0, "괜찮았어요."),
            new StarComment(3.5, "만족스러웠어요."),
            new StarComment(4.0, "무조건 한번 더 올 것 같아요."),
            new StarComment(4.5, "행복했습니다."),
            new StarComment(5.0, "인생 최고의 식당입니다.")
    );

    public static String getIconImgUrl(RestaurantComment restaurantComment, boolean isIOS) {
        if (restaurantComment == null) {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/level1icon" + (isIOS ? ".svg" : ".png");
        }
        User user = restaurantComment.getUser();
        if (user.getEvaluationList().size() > 60) {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/level3icon" + (isIOS ? ".svg" : ".png");
        } else if (user.getEvaluationList().size() > 30) {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/level2icon" + (isIOS ? ".svg" : ".png");
        } else {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/level1icon" + (isIOS ? ".svg" : ".png");
        }
    }

    @Data
    public static class StarComment {
        @Schema(description = "별점", example = "4.5")
        private final double star;
        @Schema(description = "문구", example = "인생 최고의 식당입니다.")
        private final String comment;
    }
}
