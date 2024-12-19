package io.hhplus.tdd.unit.utils;

import io.hhplus.tdd.utils.ValidationUtils;
import org.junit.jupiter.api.Test;

import static io.hhplus.tdd.domain.UserPoint.MAX_CHARGE_POINT;
import static io.hhplus.tdd.domain.UserPoint.MAX_POINT;
import static io.hhplus.tdd.domain.UserPoint.MIN_CHARGE_POINT;
import static io.hhplus.tdd.domain.UserPoint.MIN_POINT;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilsTest {

  @Test
  void 아이디_체크_성공() {
    assertDoesNotThrow(() -> ValidationUtils.validateId(1L));
  }

  @Test
  void 아이디_체크_실패() {
    Exception exception = assertThrows(RuntimeException.class, () -> ValidationUtils.validateId(0L));
    assertEquals("id는 0보다 커야합니다.", exception.getMessage());
  }

  @Test
  void 사용_충전_포인트_체크_성공() {
    assertDoesNotThrow(() -> ValidationUtils.validateIdAndPoint(1L, 500L));
  }

  @Test
  void 사용_충전_포인트_체크_실패_최소_예외() {
    Exception exception = assertThrows(RuntimeException.class, () -> ValidationUtils.validateIdAndPoint(1L, MIN_CHARGE_POINT - 1));
    assertEquals("포인트는 최소 " + MIN_CHARGE_POINT + "이상이어야 합니다.", exception.getMessage());
  }

  @Test
  void 사용_충전_포인트_체크_실패_최대_예외() {
    Exception exception = assertThrows(RuntimeException.class, () -> ValidationUtils.validateIdAndPoint(1L, MAX_CHARGE_POINT + 1));
    assertEquals("포인트는 최대 " + MAX_CHARGE_POINT + "이하이어야 합니다.", exception.getMessage());
  }

  @Test
  void 포인트_한도_체크_성공() {
    assertDoesNotThrow(() -> ValidationUtils.validatePointLimit(500L));
  }

  @Test
  void 포인트_한도_체크_실패_최대_한도_예외() {
    Exception exception = assertThrows(RuntimeException.class, () -> ValidationUtils.validatePointLimit(MAX_POINT + 1));
    assertEquals("보유한 포인트 한도를 초과하였습니다.", exception.getMessage());
  }

  @Test
  void 포인트_한도_체크_실패_최소_한도_예외() {
    Exception exception = assertThrows(RuntimeException.class, () -> ValidationUtils.validatePointLimit(MIN_POINT-1));
    assertEquals("포인트가 부족합니다.", exception.getMessage());
  }
}