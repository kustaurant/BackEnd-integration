package com.kustaurant.mainapp.global.exception.exception.user;

import com.kustaurant.mainapp.global.exception.ErrorCode;
import com.kustaurant.mainapp.global.exception.exception.BusinessException;

public class UserNotFoundException extends BusinessException {
  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }
}
