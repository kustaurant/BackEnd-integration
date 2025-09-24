package com.kustaurant.mainapp.user.user.domain.vo;

import com.kustaurant.mainapp.user.user.domain.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class PhoneNumberTest {
    @ParameterizedTest(name = "올바른 핸드폰번호 \"{0}\" 는 정상 생성")
    @ValueSource(strings = {
            "01012345678",
            "01122223333",
            "01999999999",
            "01234567890"
    })
    void createsValidPhoneNumber(String validNumber) {
        assertThatCode(() -> new PhoneNumber(validNumber))
                .doesNotThrowAnyException();
    }

    // ──────────────────────────────────────────────────────────
    @ParameterizedTest(name = "잘못된 핸드폰번호 \"{0}\" 는 IllegalArgumentException 발생")
    @ValueSource(strings = {
            "010-1234-5678", // - 포함
            "0101234567", // 10 자
            "010123456789", // 12 자
            "abcdefghijk", // 숫자가 아님
            "0101234abcd" // 숫자만 있는게 아님
    })
    void throwsExceptionForInvalidPhoneNumbers(String badNumber) {
        assertThatThrownBy(() -> new PhoneNumber(badNumber))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ──────────────────────────────────────────────────────────
    @Test
    @DisplayName("동일 value를 가진 PhoneNumber 객체는 equals/hashCode가 동일하다")
    void shouldHaveProperEqualsAndHashCode() {
        PhoneNumber p1 = new PhoneNumber("01077778888");
        PhoneNumber p2 = new PhoneNumber("01077778888");

        assertThat(p1).isEqualTo(p2).hasSameHashCodeAs(p2);
    }

}