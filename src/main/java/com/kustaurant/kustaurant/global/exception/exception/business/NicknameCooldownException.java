package com.kustaurant.kustaurant.global.exception.exception.business;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

public class NicknameCooldownException extends BusinessException {
    public NicknameCooldownException() {
        super(ErrorCode.NICKNAME_COOLDOWN);
    }
}
