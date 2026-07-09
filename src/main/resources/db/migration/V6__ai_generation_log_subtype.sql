-- ai_generation_log: 성공 시 생성된 회차 세부 유형(subtype) 기록.
-- 스케줄러가 최근 N개 로그의 subtype을 모아 pastTypes(중복 방지 힌트)로 넘기는 데 쓴다.
-- pastStatements(과거 지문)는 problem.content에서 뽑을 수 있어 별도 컬럼 불필요.

ALTER TABLE ai_generation_log
    ADD COLUMN subtype VARCHAR(100) NULL AFTER status;
