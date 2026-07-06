package com.cocky.cockyserver.global.entity;

/**
 * feedback, ranking_snapshot이 공유하는 집계 주기. 배치 스케줄러가 두 테이블을 같은 주기로 함께 갱신하므로 공용으로 둔다.
 */
public enum PeriodType {
    TWO_DAY,
    WEEKLY,
    MONTHLY
}
