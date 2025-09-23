package com.kustaurant.mainapp.global.exception.exception.user;

import com.kustaurant.mainapp.global.exception.ErrorCode;
import com.kustaurant.mainapp.global.exception.exception.BusinessException;

public class NicknameCooldownException extends BusinessException {
    public NicknameCooldownException() {
        super(ErrorCode.NICKNAME_COOLDOWN);
    }
}
