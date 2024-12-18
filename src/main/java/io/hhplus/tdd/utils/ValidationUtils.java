package io.hhplus.tdd.utils;

import static io.hhplus.tdd.domain.UserPoint.MAX_CHARGE_POINT;
import static io.hhplus.tdd.domain.UserPoint.MAX_POINT;
import static io.hhplus.tdd.domain.UserPoint.MIN_CHARGE_POINT;
import static io.hhplus.tdd.domain.UserPoint.MIN_POINT;

public final class ValidationUtils {
  private ValidationUtils() {
  }

  // ID 유효성 검사
  public static void validateId(Long id) {
    if(id <= 0){
      throw new RuntimeException("id는 0보다 커야합니다.");
    }
  }

  // 사용 및 충전 포인트 유효성 검사
  public static void validateIdAndPoint(Long id, Long point) {
    validateId(id);

    if(point < MIN_CHARGE_POINT){
      throw new RuntimeException("포인트는 최소 " + MIN_CHARGE_POINT + "이상이어야 합니다.");
    }
    else if(point > MAX_CHARGE_POINT){
      throw new RuntimeException("포인트는 최대 " + MAX_CHARGE_POINT + "이하이어야 합니다.");
    }
  }
  // 보유한 포인트 한도 초과
  public static void validatePointLimit(Long point) {
    if(point > MAX_POINT ){
      throw new RuntimeException("보유한 포인트 한도를 초과하였습니다.");
    } else if (point < MIN_POINT){
      throw new RuntimeException("포인트가 부족합니다.");
    }
  }


}
