package com.kustaurant.kustaurant.post.post.controller.response;

import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.UserIconResolver;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    @Schema(description="유저 이름",example = "임재형")
    private String nickname;
    @Schema(description="평가수에 따른 이미지",example = "/img/ranking/소_평가10개미만.png")
    private String rankImg;
    @Schema(description="유저가 평가한 식당 수",example = "75")
    private Integer evaluationCount;
    @Schema(description="유저의 랭킹",example = "2")

    Integer Rank;
    public static UserDTO convertUserToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        int evalcount = user.getEvalCount();

        dto.nickname=user.getNickname().getValue();
        dto.rankImg= UserIconResolver.resolve(evalcount);
        dto.evaluationCount=evalcount;

        return dto;
    }

    public static UserDTO from(User user) {
        UserDTO dto = new UserDTO();
        dto.nickname=user.getNickname().getValue();
        dto.rankImg = user.getRankImg();
        dto.evaluationCount = user.getEvalCount();

        return dto;
    }
}
