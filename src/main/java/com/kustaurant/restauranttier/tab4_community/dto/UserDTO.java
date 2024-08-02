package com.kustaurant.restauranttier.tab4_community.dto;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String userNickname;
    private String rankImg;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserNickname(user.getUserNickname());
        dto.setRankImg(user.getRankImg());
        return dto;
    }
}
