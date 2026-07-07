package com.cocky.cockyserver.domain.round.exception;

/** 현재 열려있는(is_active=true 이고 open_at<=now<close_at) 회차가 없는 경우(404)를 나타낸다. */
public class RoundNotFoundException extends RuntimeException {

    public RoundNotFoundException(String message) {
        super(message);
    }
}
