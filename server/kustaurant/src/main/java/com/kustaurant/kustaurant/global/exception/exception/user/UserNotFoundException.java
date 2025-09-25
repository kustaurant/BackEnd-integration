package com.kustaurant.kustaurant.global.exception.exception.user;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.BusinessException;

public class UserNotFoundException extends BusinessException {
  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }
}
