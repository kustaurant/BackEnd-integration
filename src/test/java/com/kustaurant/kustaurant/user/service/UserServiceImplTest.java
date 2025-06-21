package com.kustaurant.kustaurant.user.service;

import com.kustaurant.kustaurant.user.domain.User;
import com.kustaurant.kustaurant.user.domain.enums.UserStatus;
import com.kustaurant.kustaurant.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.user.domain.enums.UserRole;
import com.kustaurant.kustaurant.mock.FakeUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UserServiceImplTest {

    private UserServiceImpl userServiceImpl;
    @BeforeEach
    void init(){
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        this.userServiceImpl = new UserServiceImpl(fakeUserRepository);

        //UserStatus가 ACTIVE 상태인 유저
        fakeUserRepository.save(User.builder()
                .id(1)
                .providerId("aaaa")
                .loginApi("NAVER")
                .nickname(new Nickname("TESTUSER01"))
                .email("test01@test.com")
                .phoneNumber(new PhoneNumber("01012340000"))
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .role(UserRole.USER)
                .build());

        //UserSTatus가 DELETED 상태인 유저
        fakeUserRepository.save(User.builder()
                .id(2)
                .providerId("aaaa")
                .loginApi("NAVER")
                .nickname(new Nickname("TESTUSER02"))
                .email("test02@test.com")
                .phoneNumber(new PhoneNumber("01023450000"))
                .status(UserStatus.DELETED)
                .createdAt(LocalDateTime.now())
                .role(UserRole.USER)
                .build());
    }

    @Test
    void getActiveUserById는_ACTIVE상태의_유저만_찾는다() {
        //g
        //w
        User result=userServiceImpl.getActiveUserById(1);
        //t
        assertThat(result.getNickname().getValue()).isEqualTo("TESTUSER01");
    }

    @Test
    void getActiveUserById는_DELETED상태의_유저는_찾아선_안된다() {
        //g
        //w
        //t
        assertThatThrownBy(()->{
            userServiceImpl.getActiveUserById(2);
        }).isInstanceOf(DataNotFoundException.class);
    }
}