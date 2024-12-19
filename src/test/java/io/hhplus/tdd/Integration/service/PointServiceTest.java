package io.hhplus.tdd.Integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.service.PointService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PointServiceTest {

  @Autowired
  UserPointTable userPointTable;
  @Autowired
  PointHistoryTable pointHistoryTable;
  @Autowired
  private PointService pointService;

  @Test
  void 유저_포인트_조회_성공() {
    // given
    UserPoint userPoint = userPointTable.insertOrUpdate(1L, 100L);
    // when
    UserPoint result = pointService.getUserPoint(1L);
    // then
    assertThat(userPoint).isEqualTo(result);
  }

  @Test
  void 유저_포인트_내역_조회_여러건_성공() {
    // given
    List<PointHistory> l = new ArrayList<>();
    l.add(pointHistoryTable.insert(2L, 50L, TransactionType.CHARGE, 100));
    l.add(pointHistoryTable.insert(2L, 50L, TransactionType.CHARGE, 200));
//  테스트 순서가 보장되지 않아 오류 수정 ( ID )
//     List<PointHistory> ph = List.of(
//         new PointHistory(1,2L, 100L, TransactionType.CHARGE, 100),
//         new PointHistory(2,2L, 50L, TransactionType.CHARGE, 200)
//     );

    // when
    List<PointHistory> result = pointService.getPointHistories(2L);
    // then
    assertThat(result).isEqualTo(l);
  }

  @Test
  void 유저_포인트_내역_조회_내역없음_성공() {
    // given

    List<PointHistory> ph = List.of();
    // when
    List<PointHistory> result = pointService.getPointHistories(5L);
    // then
    assertThat(result).isEqualTo(ph);
  }

  @Test
  void 유저_포인트_충전_성공() {
    // given
    UserPoint userPoint = userPointTable.insertOrUpdate(3L, 100L);
    // when
    UserPoint result = pointService.pointCharge(3L, 100L);
    // then
    assertThat(result.point()).isEqualTo(200L);
  }

  @Test
  void 유저_포인트_사용_성공() {
    // given
    UserPoint userPoint = userPointTable.insertOrUpdate(4L, 200L);
    // when
    UserPoint result = pointService.pointUse(4L, 150L);
    // then
    assertThat(result.point()).isEqualTo(50L);
  }

  @Test
  void 동시성_제어_테스트() throws InterruptedException {
    int count = 10;

    ExecutorService executorService = Executors.newFixedThreadPool(5);
    CountDownLatch countDownLatch = new CountDownLatch(count);
    // given
    // when
    for (int i = 0; i < count; i++) {
      executorService.submit(() -> {
        try {
          pointService.pointCharge(6L, 100L);
          pointService.pointUse(6L, 100L);
        } finally {
          countDownLatch.countDown();
        }
      });
    }
    countDownLatch.await();
    executorService.shutdown();
    Long point = pointService.getUserPoint(6L).point();
    // then
    // 최종 포인트 정합성 검사
    assertEquals(0, point);
    // 호출 횟수 확인
    Map<TransactionType, Long> typeCount = pointService.getPointHistories(6L).stream()
        .collect(Collectors.groupingBy(PointHistory::type, Collectors.counting()));

    assertEquals(count, typeCount.getOrDefault(TransactionType.CHARGE, 0L));
    assertEquals(count, typeCount.getOrDefault(TransactionType.USE, 0L));
  }
}
