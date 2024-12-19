package io.hhplus.tdd.unit.service;

import static io.hhplus.tdd.domain.UserPoint.MAX_CHARGE_POINT;
import static io.hhplus.tdd.domain.UserPoint.MAX_POINT;
import static io.hhplus.tdd.domain.UserPoint.MIN_CHARGE_POINT;
import static io.hhplus.tdd.domain.UserPoint.MIN_POINT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PointServiceTest {

  // 호출했을 때 사전에 정의된 명세대로의 결과를 돌려주도록 미리 프로그램돼있는 테스트용 객체 (Mock 객체)를 생성
  @Mock
  UserPointTable userPointTable;
  @Mock
  PointHistoryTable pointHistoryTable;
  @Mock
  UserPoint userPoint;
  @Mock
  PointHistory pointHistory;

  @InjectMocks
  PointService pointService;

  @BeforeEach
  void setUp() {
    // Mock 객체를 초기화
    // @Mock 어노테이션이 붙은 객체들을 초기화하고 @InjectMocks 어노테이션이 붙은 객체에 주입
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void 유저_포인트_조회_성공() {
    // given
    when(userPointTable.selectById(1L)).thenReturn(userPoint);
    // when
    UserPoint result = pointService.getUserPoint(1L);
    // then
    assertEquals(userPoint, result);
  }

  @Test
  void 유저_포인트_조회_실패_아이디_오류() {
    // given
    Long InvalidId = 0L;
    // when
    Exception exception = assertThrows(RuntimeException.class,
        () -> pointService.getUserPoint(InvalidId));
    // then
    assertEquals("id는 0보다 커야합니다.", exception.getMessage());
  }

  @Test
  void 유저_포인트_내역_조회_성공() {
    // given
    List<PointHistory> pointHistories = List.of(pointHistory);
    when(pointHistoryTable.selectAllByUserId(1L)).thenReturn(pointHistories);
    // when
    List<PointHistory> result = pointService.getPointHistories(1L);
    // then
    assertEquals(pointHistories, result);
  }

  @Test
  void 유저_포인트_내역_조회_실패_아이디_오류() {
    // given
    Long InvalidId = 0L;
    // when
    Exception exception = assertThrows(RuntimeException.class,
        () -> pointService.getPointHistories(InvalidId));
    // then
    assertEquals("id는 0보다 커야합니다.", exception.getMessage());
  }

  @Test
  void 유저_포인트_충전_성공() {
    // given
    when(userPoint.point()).thenReturn(50L);
    when(userPointTable.selectById(1L)).thenReturn(userPoint);
    when(userPointTable.insertOrUpdate(1L, 150)).thenReturn(userPoint);
    // when
    UserPoint result = pointService.pointCharge(1L, 100L);
    // then
    assertEquals(userPoint, result);
  }

  @Test
  void 유저_포인트_충전_실패_최대충전포인트초과() {
    // give
    // when
    Exception exception = assertThrows(RuntimeException.class,
        () -> pointService.pointCharge(1L, MAX_CHARGE_POINT + 1));
    // then
    assertEquals("포인트는 최대 10000이하이어야 합니다.", exception.getMessage());
  }

  @Test
  void 유저_포인트_충전_실패_최대보유포인트초과() {
    // give
    when(userPointTable.selectById(1L)).thenReturn(userPoint);
    when(userPoint.point()).thenReturn(MAX_POINT);
    // when
    Exception exception = assertThrows(RuntimeException.class,
        () -> pointService.pointCharge(1L, 100L));
    // then
    assertEquals("보유한 포인트 한도를 초과하였습니다.", exception.getMessage());
  }

  @Test
  void 유저_포인트_사용_성공() {
    // given
    when(userPoint.point()).thenReturn(200L);
    when(userPointTable.selectById(1L)).thenReturn(userPoint);
    when(userPointTable.insertOrUpdate(1L, 100L)).thenReturn(userPoint);
    // when
    UserPoint result = pointService.pointUse(1L, 100L);
    // then
    assertEquals(userPoint, result);
  }

  @Test
  void 유저_포인트_사용_실패_최대사용포인트초과() {
    // give
    // when
    Exception exception = assertThrows(RuntimeException.class,
        () -> pointService.pointUse(1L, MIN_CHARGE_POINT - 1));
    // then
    assertEquals("포인트는 최소 100이상이어야 합니다.", exception.getMessage());
  }

  @Test
  void 유저_포인트_사용_실패_최소보유포인트오류() {
    // give
    when(userPointTable.selectById(1L)).thenReturn(userPoint);
    when(userPoint.point()).thenReturn(MIN_POINT);
    // when
    Exception exception = assertThrows(RuntimeException.class,
        () -> pointService.pointUse(1L, 100L));
    // then
    assertEquals("포인트가 부족합니다.", exception.getMessage());
  }

}
