package com.cocky.cockyserver.global.exception;

/** 400/401/403 등 에러 응답의 공통 JSON 형태. */
public record ErrorResponse(String code, String message) {
}