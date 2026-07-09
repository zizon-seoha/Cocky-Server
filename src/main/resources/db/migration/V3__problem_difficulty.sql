-- problem: 제출 채점의 base 점수 산정을 위해 difficulty를 다시 추가한다.
-- (V2에서 "제출 단위 속성"이라는 이유로 제거했으나, base 점수는 문제 자체의 난이도에
--  귀속되는 것으로 재확정.)
--
-- 스키마 레벨 DEFAULT는 두지 않는다 — 기존 행만 NORMAL로 백필하고, 이후 INSERT(AI 생성기 등)가
-- difficulty를 빠뜨리면 조용히 NORMAL로 들어가지 않고 NOT NULL 제약 위반으로 즉시 실패하게 한다.

ALTER TABLE problem
    ADD COLUMN difficulty VARCHAR(10) NULL AFTER language;

UPDATE problem
SET difficulty = 'NORMAL'
WHERE difficulty IS NULL;

ALTER TABLE problem
    MODIFY COLUMN difficulty VARCHAR(10) NOT NULL;
