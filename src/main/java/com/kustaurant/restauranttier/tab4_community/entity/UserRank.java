package com.kustaurant.restauranttier.tab4_community.entity;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRank {
    private Integer rank;
    private User user;
}
