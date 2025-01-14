package com.theodoro.loginservice.domains.exceptions;

import org.springframework.http.HttpStatus;

public class HttpException extends RuntimeException {

    private Integer code;
    private final HttpStatus httpStatus;

    public HttpException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpException(final Integer code, final HttpStatus httpStatus, final String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public Integer getCode() {
        if (code != null) {
            return code;
        }
        return httpStatus.value();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}