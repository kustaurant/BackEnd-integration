package com.kustaurant.kustaurant.user.mypage.service;

import com.kustaurant.kustaurant.global.exception.exception.user.NicknameCooldownException;
import com.kustaurant.kustaurant.global.exception.exception.user.NicknameDuplicateException;
import com.kustaurant.kustaurant.global.exception.exception.user.PhoneDuplicateException;
import com.kustaurant.kustaurant.mock.user.FakeUserRepository;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.enums.UserStatus;
import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.user.domain.vo.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UserProfileValidatorTest {
    private FakeUserRepository userRepo;
    private UserProfileValidator validator;
    @BeforeEach
    void init(){
        userRepo = new FakeUserRepository();
        this.validator = new UserProfileValidator(userRepo);

        userRepo.save(User.builder() // 최근에 닉네임을 변경한 유저 -> 닉네임변경 불가
                .id(1L)
                .nickname(new Nickname("한국의일론매식기"))
                .phoneNumber(new PhoneNumber("01012345678"))
                .createdAt(LocalDateTime.now().minusMonths(1))
                .updatedAt(LocalDateTime.now().minusDays(5)) // 5일전 업데이트 이력
                .status(UserStatus.ACTIVE)
                .build());
        userRepo.save(User.builder() // 한참전에 닉네임을 변경 -> 다시 닉네임 변경 가능
                .id(2L)
                .nickname(new Nickname("경보그룹회장님"))
                .phoneNumber(null)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now().minusMonths(2)) // 2달전 업데이트 이력
                .status(UserStatus.ACTIVE)
                .build());

    }

    @Test
    @DisplayName("닉네임 변경 30일이 안지나고 변경요청 시 NicknameCooldownException 발생")
    void validateNicknameChange_WithCooldown_ThrowsException() {
        // g
        User user = userRepo.findById(1L).orElseThrow();
        Nickname newNick = new Nickname("새로운닉네임");

        // w & t
        assertThatThrownBy(() -> validator.validateNicknameChange(user, newNick))
                .isInstanceOf(NicknameCooldownException.class);
    }

    @Test
    @DisplayName("이미 존재하는 닉네임으로 변경 시 NicknameDuplicateException 발생")
    void validateNicknameChange_WithDuplicate_ThrowsException() {
        // g
        User user = userRepo.findById(2L).orElseThrow();
        Nickname duplicate = new Nickname("한국의일론매식기"); // id=1 이 보유

        // w & t
        assertThatThrownBy(() -> validator.validateNicknameChange(user, duplicate))
                .isInstanceOf(NicknameDuplicateException.class);
    }

    @Test
    @DisplayName("이미 존재하는 전화번호로 변경 시 PhoneDuplicateException 발생")
    void validatePhoneNumberChange_WithDuplicate_ThrowsException() {
        // g
        PhoneNumber duplicate = new PhoneNumber("01012345678"); // id=1 가 보유

        // w & t
        assertThatThrownBy(() -> validator.validatePhoneNumberChange(duplicate))
                .isInstanceOf(PhoneDuplicateException.class);
    }

    @Test
    @DisplayName("중복·쿨타임 조건이 없으면 예외 없이 통과")
    void validateNicknameChange_NoProblem_Passes() {
        // given
        User user = userRepo.findById(2L).orElseThrow();        // 닉네임: 경보그룹회장님
        Nickname okNick = new Nickname("완전새닉네임");

        // when & then
        assertThatNoException().isThrownBy(() -> validator.validateNicknameChange(user, okNick));
    }

}