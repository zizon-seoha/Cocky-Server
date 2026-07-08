package com.cocky.cockyserver.infra.judge0.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Judge0 POST /submissions 요청 바디(base64_encoded=true 전제). */
public record Judge0SubmissionRequest(
        @JsonProperty("source_code") String sourceCodeBase64,
        @JsonProperty("language_id") int languageId,
        @JsonProperty("stdin") String stdinBase64,
        @JsonProperty("expected_output") String expectedOutputBase64,
        @JsonProperty("cpu_time_limit") double cpuTimeLimitSeconds,
        @JsonProperty("memory_limit") int memoryLimitKb
) {
}
