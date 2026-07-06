package com.cocky.cockyserver.domain.auth.dto;

public record SigninResponse(String accessToken, String refreshToken) {
}