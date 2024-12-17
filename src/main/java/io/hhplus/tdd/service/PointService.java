package io.hhplus.tdd.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.utils.ValidationUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 총 4가지 기본 기능 (포인트 조회, 포인트 충전/사용 내역 조회, 충전, 사용) 을 구현합니다.
@Service
@RequiredArgsConstructor
public class PointService {
  private final UserPointTable userPointTable;
  private final PointHistoryTable pointHistoryTable;

  // 포인트 조회
  public UserPoint getUserPoint(long id){
    // ID 유효성 검사
    ValidationUtils.ValidateId(id);
    return userPointTable.selectById(id);
  }

  // 포인트 내역 조회
  public List<PointHistory> getPointHistories(long id){
    // ID 유효성 검사
    ValidationUtils.ValidateId(id);
    return pointHistoryTable.selectAllByUserId(id);
  }

  // 포인트 충전
  public UserPoint pointCharge(long id, long amount) {
    // ID, amount 유효성 검사
    ValidationUtils.ValidateIdAndPoint(id, amount);

    // 유저의 보유 포인트 확인
    UserPoint userPoint = userPointTable.selectById(id);

    // 최대 충전 포인트 확인
    ValidationUtils.ValidatePointLimit(userPoint.point() + amount);

    // 포인트 충전
    UserPoint updateUserPoint = userPointTable.insertOrUpdate(id, userPoint.point() + amount);

    // 이력 저장
    pointHistoryTable.insert(updateUserPoint.id(), updateUserPoint.point(), TransactionType.CHARGE, updateUserPoint.updateMillis());
    return updateUserPoint;
  }

  public UserPoint pointUse(long id, long amount) {
    // ID, amount 유효성 검사
    ValidationUtils.ValidateIdAndPoint(id, amount);

    // 유저의 보유 포인트 확인
    UserPoint userPoint = userPointTable.selectById(id);

    // 최대 사용 가능 포인트 확인
    ValidationUtils.ValidatePointLimit(userPoint.point() - amount);

    // 포인트 사용
    UserPoint updateUserPoint = userPointTable.insertOrUpdate(id, userPoint.point() - amount);

    // 이력 저장
    pointHistoryTable.insert(updateUserPoint.id(), updateUserPoint.point(), TransactionType.CHARGE, updateUserPoint.updateMillis());
    return updateUserPoint;
  }
}
