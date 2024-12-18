package io.hhplus.tdd.Integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.service.PointService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PointServiceTest {
  @Autowired
  private PointService pointService;

  @Autowired
  UserPointTable userPointTable;

  @Autowired
  PointHistoryTable pointHistoryTable;

  @Test
  void 유저_포인트_조회_성공(){
    // given
    UserPoint userPoint = userPointTable.insertOrUpdate(1L, 100L);
    // when
    UserPoint result = pointService.getUserPoint(1L);
    // then
    assertThat(userPoint).isEqualTo(result);
  }

  @Test
  void 유저_포인트_내역_조회_여러건_성공(){
    // given
    pointHistoryTable.insert(2L, 100L, TransactionType.CHARGE, 100);
    pointHistoryTable.insert(2L, 50L, TransactionType.CHARGE, 200);

    List<PointHistory> ph = List.of (
      new PointHistory(1L,2L, 100L, TransactionType.CHARGE, 100),
      new PointHistory(2L, 2L, 50L, TransactionType.CHARGE, 200)
    );
    // when
    List<PointHistory> result = pointService.getPointHistories(2L);
    // then
    assertThat(result).isEqualTo(ph);
  }

  @Test
  void 유저_포인트_내역_조회_내역없음_성공(){
    // given

    List<PointHistory> ph = List.of ();
    // when
    List<PointHistory> result = pointService.getPointHistories(2L);
    // then
    assertThat(result).isEqualTo(ph);
  }

  @Test
  void 유저_포인트_충전_성공(){
    // given
    UserPoint userPoint = userPointTable.insertOrUpdate(3L, 100L);
    // when
    UserPoint result = pointService.pointCharge(3L, 100L);
    // then
    assertThat(result.point()).isEqualTo(200L);
  }

  @Test
  void 유저_포인트_사용_성공(){
    // given
    UserPoint userPoint = userPointTable.insertOrUpdate(4L, 200L);
    // when
    UserPoint result = pointService.pointUse(4L, 150L);
    // then
    assertThat(result.point()).isEqualTo(50L);
  }
}
