package com.cocky.cockyserver.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SigninRequest(
        @NotBlank(message = "code는 필수입니다.") String code
) {
}