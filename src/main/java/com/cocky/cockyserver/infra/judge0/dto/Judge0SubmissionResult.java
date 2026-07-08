package com.cocky.cockyserver.infra.judge0.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Judge0 POST /submissions 응답(wait=true, base64_encoded=true). time은 초 단위 문자열. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Judge0SubmissionResult(
        Judge0Status status,
        String stdout,
        String stderr,
        @JsonProperty("compile_output") String compileOutput,
        String message,
        String time,
        Integer memory
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Judge0Status(int id, String description) {
    }
}
