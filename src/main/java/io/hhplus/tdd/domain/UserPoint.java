package io.hhplus.tdd.domain;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    // 보유 가능한 최대 최소 포인트
    public static final long MAX_POINT = 30000;
    public static final long MIN_POINT = 0;

    // 충전,사용 가능한 최대 최소 포인트
    public static final long MAX_CHARGE_POINT = 10000;
    public static final long MIN_CHARGE_POINT = 100;
}
