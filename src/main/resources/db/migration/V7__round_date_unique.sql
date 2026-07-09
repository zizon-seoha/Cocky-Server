-- round_date에 대한 동시성 방지용 유니크 제약.
-- 스케줄러가 동시에 두 번 실행되어도(수동 트리거 + 크론 겹침 등) 같은 날짜의
-- round가 중복 저장되지 않도록 DB 레벨에서 최종 방어선을 둔다.

ALTER TABLE round
    ADD CONSTRAINT uq_round_date UNIQUE (round_date);
