-- problem: difficulty는 제출(submission) 단위 속성이므로 제거하고,
-- 문제별 고정 언어(language)를 추가한다.

ALTER TABLE problem
    ADD COLUMN language VARCHAR(20) NOT NULL AFTER content;

ALTER TABLE problem
    DROP COLUMN difficulty;
