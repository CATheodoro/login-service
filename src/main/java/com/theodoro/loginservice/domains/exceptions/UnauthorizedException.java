package com.theodoro.loginservice.domains.exceptions;

import com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends HttpException {

    public UnauthorizedException(final String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public UnauthorizedException(final ExceptionMessagesEnum exceptionMessagesEnum) {
        super(exceptionMessagesEnum.getCode(), HttpStatus.FORBIDDEN, exceptionMessagesEnum.getMessage());
    }
}