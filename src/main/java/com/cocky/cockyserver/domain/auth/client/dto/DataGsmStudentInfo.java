package com.cocky.cockyserver.domain.auth.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** DataGSM 사용자 정보 응답의 student 서브 객체. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DataGsmStudentInfo(
        String name,
        Integer grade,
        Integer classNum,
        Integer number,
        String studentNumber,
        String major,
        Boolean isLeaveSchool,
        String role
) {
}