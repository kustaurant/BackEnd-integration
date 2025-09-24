package com.kustaurant.kustaurant.global.exception.exception.auth;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

public class UnauthenticatedException extends JwtAuthException {

  public UnauthenticatedException() {
    super(ErrorCode.UNAUTHORIZED);
  }

  /** 디버깅용 상세 사유 전달 */
  public UnauthenticatedException(String reason) {
    super(ErrorCode.UNAUTHORIZED, reason);
  }

  public UnauthenticatedException(Throwable cause) {
    super(ErrorCode.UNAUTHORIZED, cause);
  }

}
