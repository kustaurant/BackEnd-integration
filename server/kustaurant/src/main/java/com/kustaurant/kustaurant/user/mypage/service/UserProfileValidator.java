package com.kustaurant.kustaurant.user.mypage.service;

import com.kustaurant.kustaurant.global.exception.exception.user.NicknameCooldownException;
import com.kustaurant.kustaurant.global.exception.exception.user.NicknameDuplicateException;
import com.kustaurant.kustaurant.global.exception.exception.user.PhoneDuplicateException;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.Nickname;
import com.kustaurant.kustaurant.user.user.domain.PhoneNumber;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserProfileValidator {
    private final UserRepository userRepository;

    public void validateNicknameChange(User user, Nickname newNick) {
        LocalDateTime last = user.getUpdatedAt() !=null ? user.getUpdatedAt() : user.getCreatedAt();

        if (last != null && last.isAfter(LocalDateTime.now().minusDays(30))) {
            throw new NicknameCooldownException();
        }

        if (userRepository.existsByNickname(newNick)) {
            throw new NicknameDuplicateException();
        }
    }

    public void validatePhoneNumberChange(PhoneNumber newPhoneNumber) {
        if (userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new PhoneDuplicateException();
        }
    }
}
