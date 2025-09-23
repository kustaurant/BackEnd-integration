package com.kustaurant.mainapp.v1.community.dto;

import com.kustaurant.mainapp.common.util.UserIconResolver;
import com.kustaurant.mainapp.user.user.domain.User;
import lombok.Data;

@Data
public class UserDTO {
    private String userNickname;
    private String rankImg;
    private Integer evaluationCount;
    Integer rank;

    public static UserDTO convertUserToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserNickname(user.getNickname().getValue());
        dto.setRankImg(UserIconResolver.resolve(user.getEvalCount()));
        dto.setEvaluationCount(user.getEvalCount());;

        return dto;
    }
}
