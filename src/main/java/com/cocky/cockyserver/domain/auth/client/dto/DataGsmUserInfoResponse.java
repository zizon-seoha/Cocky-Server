package com.cocky.cockyserver.domain.auth.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** DataGSM 사용자 정보 응답(GET /userinfo). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DataGsmUserInfoResponse(
        Long id,
        String email,
        String role,
        Boolean isStudent,
        DataGsmStudentInfo student
) {
}