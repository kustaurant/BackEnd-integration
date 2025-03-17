package com.kustaurant.kustaurant.common.post.domain;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    @Schema(description="유저 이름",example = "임재형")
    private String userNickname;
    @Schema(description="평가수에 따른 이미지",example = "/img/ranking/소_평가10개미만.png")
    private String rankImg;
    @Schema(description="유저가 평가한 식당 수",example = "75")
    private Integer evaluationCount;
    @Schema(description="유저의 랭킹",example = "2")

    Integer Rank;
    public static UserDTO convertUserToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserNickname(user.getUserNickname());
        dto.setRankImg(user.getRankImg());
        dto.setEvaluationCount(user.getEvaluationList().size());;

        return dto;
    }
}
