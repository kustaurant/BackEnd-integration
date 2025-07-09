package com.kustaurant.kustaurant.global.exception.exception.auth;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

public class ForbiddenException extends JwtAuthException {

  public ForbiddenException() {
    super(ErrorCode.ACCESS_DENIED);
  }

  /** 디버깅용 상세 사유 전달 */
  public ForbiddenException(String reason) {
    super(ErrorCode.ACCESS_DENIED, reason);
  }

  public ForbiddenException(Throwable cause) {
    super(ErrorCode.ACCESS_DENIED, cause);
  }

}
