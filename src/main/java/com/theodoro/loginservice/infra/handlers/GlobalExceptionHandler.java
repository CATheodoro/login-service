package com.theodoro.loginservice.infra.handlers;

import com.theodoro.loginservice.domains.exceptions.ConflictException;
import com.theodoro.loginservice.domains.exceptions.HttpException;
import com.theodoro.loginservice.api.rest.models.errors.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorModel> handleException(Exception ex) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(new ErrorModel(INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorModel> handleLockedException(LockedException ex) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(new ErrorModel(ACCOUNT_LOCKED.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorModel> handleDisabledException(DisabledException ex) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(new ErrorModel(ACCOUNT_DISABLED.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorModel> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ErrorModel(BAD_CREDENTIALS.getCode(), BAD_CREDENTIALS.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorModel> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorModel errorModel = new ErrorModel();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
            errorModel.addError(HttpStatus.BAD_REQUEST.value(), fieldError.getField() + " " + fieldError.getDefaultMessage())
        );
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(errorModel);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorModel> handleConflictException(ConflictException ex) {
        ErrorModel errorModel = new ErrorModel(ex.getCode(), ex.getMessage());
        return ResponseEntity
                .status(CONFLICT)
                .location(ex.getLocation())
                .body(errorModel);
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ErrorModel> httpHttpException(HttpException ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(new ErrorModel(ex.getCode(), ex.getMessage()));
    }
}

