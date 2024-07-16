package com.theodoro.loginservice.domains.exceptions;

import com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends HttpException {

    public ForbiddenException(final String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(final ExceptionMessagesEnum exceptionMessagesEnum) {
        super(exceptionMessagesEnum.getCode(), HttpStatus.FORBIDDEN, exceptionMessagesEnum.getMessage());
    }
}