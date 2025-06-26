package com.kustaurant.kustaurant.user.user.domain.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

class NicknameTest {
    @Test
    void 올바른_닉네임은_생성된다() {
        assertThatCode(() -> new Nickname("테스트유저"))
                .doesNotThrowAnyException();
    }

}