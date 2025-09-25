package com.kustaurant.kustaurant.global.exception.exception.auth;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

public class AccessDeniedException extends JwtAuthException {

  public AccessDeniedException() {
    super(ErrorCode.ACCESS_DENIED);
  }

  /** 디버깅용 상세 사유 전달 */
  public AccessDeniedException(String reason) {
    super(ErrorCode.ACCESS_DENIED, reason);
  }

  public AccessDeniedException(Throwable cause) {
    super(ErrorCode.ACCESS_DENIED, cause);
  }

}
