ALTER TABLE submission
    ADD COLUMN time_score        DECIMAL(5,2) NULL AFTER score,
    ADD COLUMN readability_score DECIMAL(5,2) NULL AFTER time_score,
    ADD COLUMN originality_score DECIMAL(5,2) NULL AFTER readability_score,
    ADD COLUMN feedback_comment  TEXT         NULL AFTER originality_score;
