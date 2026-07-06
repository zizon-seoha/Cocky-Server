-- COCKY 초기 스키마
-- user, topic, round, problem, test_case, submission, feedback, ranking_snapshot, notification, ai_generation_log

CREATE TABLE `user`
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    datagsm_id    BIGINT       NOT NULL,
    email         VARCHAR(100) NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    grade         TINYINT NOT NULL,
    class_no      TINYINT NOT NULL,
    number        TINYINT NOT NULL,
    department    VARCHAR(20)  NOT NULL,
    role          ENUM ('ADMIN', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
    refresh_token VARCHAR(512) NULL,
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME     NOT NULL,
    CONSTRAINT uk_user_datagsm_id UNIQUE (datagsm_id),
    CONSTRAINT uk_user_email UNIQUE (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE topic
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    week_order TINYINT NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE round
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    topic_id   BIGINT   NOT NULL,
    round_date DATE     NOT NULL,
    open_at    DATETIME NOT NULL,
    close_at   DATETIME NOT NULL,
    is_active  BOOLEAN  NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_round_topic FOREIGN KEY (topic_id) REFERENCES topic (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE problem
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    round_id        BIGINT       NOT NULL,
    title           VARCHAR(200) NOT NULL,
    content         TEXT         NOT NULL,
    difficulty      VARCHAR(10)  NOT NULL,
    is_ai_generated BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      DATETIME     NOT NULL,
    CONSTRAINT fk_problem_round FOREIGN KEY (round_id) REFERENCES round (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE test_case
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    problem_id      BIGINT  NOT NULL,
    input           TEXT    NOT NULL,
    expected_output TEXT    NOT NULL,
    is_sample       BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_test_case_problem FOREIGN KEY (problem_id) REFERENCES problem (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE submission
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT        NOT NULL,
    problem_id   BIGINT        NOT NULL,
    language     ENUM ('PYTHON', 'C', 'JAVA') NOT NULL,
    code         TEXT          NOT NULL,
    verdict      ENUM ('AC', 'WA', 'TLE', 'RE', 'CE', 'PENDING') NOT NULL DEFAULT 'PENDING',
    score        DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    is_latest    BOOLEAN       NOT NULL DEFAULT TRUE,
    submitted_at DATETIME      NOT NULL,
    CONSTRAINT fk_submission_user FOREIGN KEY (user_id) REFERENCES `user` (id),
    CONSTRAINT fk_submission_problem FOREIGN KEY (problem_id) REFERENCES problem (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE feedback
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT        NOT NULL,
    period_type  ENUM ('TWO_DAY', 'WEEKLY', 'MONTHLY') NOT NULL,
    content      TEXT          NOT NULL,
    score        DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    period_start DATE          NOT NULL,
    period_end   DATE          NOT NULL,
    created_at   DATETIME      NOT NULL,
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES `user` (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE ranking_snapshot
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT        NOT NULL,
    period_type   ENUM ('TWO_DAY', 'WEEKLY', 'MONTHLY') NOT NULL,
    scope_type    ENUM ('SCHOOL', 'GRADE', 'CLASS_VS_CLASS', 'WITHIN_CLASS') NOT NULL,
    `rank`        INT           NOT NULL,
    score         DECIMAL(5, 2) NOT NULL,
    calculated_at DATETIME      NOT NULL,
    CONSTRAINT fk_ranking_snapshot_user FOREIGN KEY (user_id) REFERENCES `user` (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE notification
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    content    VARCHAR(255) NOT NULL,
    is_read    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at DATETIME     NOT NULL,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES `user` (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE ai_generation_log
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    round_id      BIGINT NOT NULL,
    problem_id    BIGINT NULL,
    sequence_no   TINYINT NOT NULL,
    status        ENUM ('SUCCESS', 'FAILED') NOT NULL,
    error_message TEXT NULL,
    retry_count   TINYINT NOT NULL DEFAULT 0,
    created_at    DATETIME NOT NULL,
    CONSTRAINT fk_ai_generation_log_round FOREIGN KEY (round_id) REFERENCES round (id),
    CONSTRAINT fk_ai_generation_log_problem FOREIGN KEY (problem_id) REFERENCES problem (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
