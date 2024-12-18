package io.hhplus.tdd.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.utils.ValidationUtils;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 총 4가지 기본 기능 (포인트 조회, 포인트 충전/사용 내역 조회, 충전, 사용) 을 구현합니다.
@Service
@RequiredArgsConstructor
public class PointService {

  private final UserPointTable userPointTable;
  private final PointHistoryTable pointHistoryTable;

  // ID별 Lock을 관리하기 위해 ConcurrentHashMap을 사용
  private final ConcurrentHashMap<Long, Lock> map = new ConcurrentHashMap<>();

  // 포인트 조회
  public UserPoint getUserPoint(long id) {
    // ID 유효성 검사
    ValidationUtils.validateId(id);

    // ReentrantLock - sync = fair ? new FairSync() : new NonfairSync();
    // 공정성 옵션으로 Queue가 생성된다. ( 먼저 요청한 순서대로 동작 한다 )
    Lock lock = map.computeIfAbsent(id, k -> new ReentrantLock(true));

    lock.lock();
    try {
      return userPointTable.selectById(id);
    } finally {
      lock.unlock();
    }
  }

  // 포인트 내역 조회
  public List<PointHistory> getPointHistories(long id) {
    // ID 유효성 검사
    ValidationUtils.validateId(id);

    Lock lock = map.computeIfAbsent(id, k -> new ReentrantLock(true));
    lock.lock();
    try{
      return pointHistoryTable.selectAllByUserId(id);
    } finally {
      lock.unlock();
    }
  }

  // 포인트 충전
  public UserPoint pointCharge(long id, long amount) {
    // ID, amount 유효성 검사
    ValidationUtils.validateIdAndPoint(id, amount);

    Lock lock = map.computeIfAbsent(id, k -> new ReentrantLock(true));

    lock.lock();
    try {
      // 유저의 보유 포인트 확인
      UserPoint userPoint = userPointTable.selectById(id);

      // 최대 충전 포인트 확인
      ValidationUtils.validatePointLimit(userPoint.point() + amount);

      // 포인트 충전
      UserPoint updateUserPoint = userPointTable.insertOrUpdate(id, userPoint.point() + amount);

      // 이력 저장
      pointHistoryTable.insert(updateUserPoint.id(), updateUserPoint.point(),
          TransactionType.CHARGE,
          updateUserPoint.updateMillis());
      return updateUserPoint;
    } finally {
      lock.unlock();
    }
  }

  public UserPoint pointUse(long id, long amount) {
    // ID, amount 유효성 검사
    ValidationUtils.validateIdAndPoint(id, amount);

    Lock lock = map.computeIfAbsent(id, k -> new ReentrantLock(true));

    lock.lock();
    try {

      // 유저의 보유 포인트 확인
      UserPoint userPoint = userPointTable.selectById(id);

      // 최대 사용 가능 포인트 확인
      ValidationUtils.validatePointLimit(userPoint.point() - amount);

      // 포인트 사용
      UserPoint updateUserPoint = userPointTable.insertOrUpdate(id, userPoint.point() - amount);

      // 이력 저장
      pointHistoryTable.insert(updateUserPoint.id(), updateUserPoint.point(),
          TransactionType.USE,
          updateUserPoint.updateMillis());

      return updateUserPoint;
    }finally {
      lock.unlock();
    }

  }
}
