package com.theodoro.loginservice.domains.exceptions;

import com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class ConflictException extends HttpException {

    private URI location;

    public ConflictException(final String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ConflictException(final ExceptionMessagesEnum exceptionMessagesEnum, final URI locationURI) {
        super(exceptionMessagesEnum.getCode(), HttpStatus.CONFLICT, exceptionMessagesEnum.getMessage());
        this.location = locationURI;
    }

    public ConflictException(final ExceptionMessagesEnum exceptionMessagesEnum) {
        super(exceptionMessagesEnum.getCode(), HttpStatus.CONFLICT, exceptionMessagesEnum.getMessage());
    }

    public URI getLocation() {
        return location;
    }
}