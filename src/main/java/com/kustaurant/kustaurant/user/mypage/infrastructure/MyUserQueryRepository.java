package com.kustaurant.kustaurant.user.mypage.infrastructure;

import com.kustaurant.kustaurant.user.mypage.controller.response.ProfileResponse;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface MyUserQueryRepository extends Repository<UserEntity, Long> {
    @Query("""
        select new com.kustaurant.kustaurant.user.mypage.controller.response.ProfileResponse(
            u.nickname.value,
            u.email,
            u.phoneNumber.value
        )
        from UserEntity u
        where u.id = :userId
    """)
    ProfileResponse findProfileByUserId(@Param("userId") Long userId);
}
