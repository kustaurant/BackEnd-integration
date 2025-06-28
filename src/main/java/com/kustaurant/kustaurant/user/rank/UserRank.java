package com.kustaurant.kustaurant.user.rank;

import com.kustaurant.kustaurant.user.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class UserRank {
    private Integer rank;
    private User user;
}
