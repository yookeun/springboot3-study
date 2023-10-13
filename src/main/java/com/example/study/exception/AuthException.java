package com.example.study.exception;

public class AuthException extends RuntimeException {
    public AuthException() {
    }

    public AuthException(String msg) {
        super(msg);
    }
}
