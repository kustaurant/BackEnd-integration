package com.kustaurant.kustaurant.user.user.domain.vo;

import com.kustaurant.kustaurant.user.user.domain.Nickname;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class NicknameTest {
    @ParameterizedTest(name = "올바른 닉네임(\"{0}\") 은 정상 생성")
    @ValueSource(strings = {
            "올바른닉네임",
            "!!##@@", // 특수기호도 허용
            "ㅁㄴㅇㅀㅂㅂ", // 자음만 있어도 허용
            "★☆※ㅎㅎㅎ" // 이런것도 허용
    })
    void createsValidNickname(String rightNickname) {
        assertThatCode(() -> new Nickname(rightNickname)).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "잘못된 닉네임(\"{0}\")은 IllegalArgumentException 발생")
    @ValueSource(strings = {
            "", // 빈 문자열
            " ", // 공백만
            "a", // 1글자
            "abcdefghijk" // 11글자
    })
    void throwsExceptionForInvalidNicknames (String badNickname) {
        assertThatThrownBy(() -> new Nickname(badNickname)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("이메일 prefix로 닉네임 생성 & 10자 초과 시 잘린다")
    void createsNicknameFromEmailPrefix () {
        //g
        Nickname nick1 = Nickname.fromEmail("wcwdfu@kku.ac.kr");
        Nickname nick2 = Nickname.fromEmail("verylongprefixemail@kku.ac.kr");

        //w+t
        assertThat(nick1.getValue()).isEqualTo("wcwdfu");
        assertThat(nick2.getValue()).isEqualTo("verylongpr").hasSize(10);
    }

    @Test
    @DisplayName("동일 value를 가진 Nickname 객체는 equals/hashCode가 동일하다")
    void implementsEqualsAndHashCodeCorrectly () {
        // g
        Nickname n1 = new Nickname("경보짱");
        Nickname n2 = new Nickname("경보짱");

        // w & t
        assertThat(n1).isEqualTo(n2).hasSameHashCodeAs(n2);
    }

}