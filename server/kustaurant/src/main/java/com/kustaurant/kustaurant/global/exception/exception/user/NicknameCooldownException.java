package com.kustaurant.kustaurant.global.exception.exception.user;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.BusinessException;

public class NicknameCooldownException extends BusinessException {
    public NicknameCooldownException() {
        super(ErrorCode.NICKNAME_COOLDOWN);
    }
}
