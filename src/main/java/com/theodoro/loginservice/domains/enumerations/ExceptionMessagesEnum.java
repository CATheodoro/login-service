package com.theodoro.loginservice.domains.enumerations;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ExceptionMessagesEnum {

    //400
    BAD_CREDENTIALS(400001, BAD_REQUEST, "E-mail or Password is incorrect."),
    TOKEN_RESEND_EXPIRED(400002, BAD_REQUEST, "Resending Token. Sending new token email."),
    TOKEN_CAN_NOT_RESEND(400003, BAD_REQUEST, "Wait a minute for resend token"),
    TOKEN_EXPIRED(400004, BAD_REQUEST, "Activation token has expired. A new token has been sent"),
    REFRESH_TOKEN_EXPIRED(400006, BAD_REQUEST, "Refresh token has expired."),
    WRONG_PASSWORD(400007, BAD_REQUEST, "Current password is incorrect."),
    WRONG_CONFIRMATION_PASSWORD(400008, BAD_REQUEST, "Confirmation password and new password are not the same."),
    SAME_CURRENT_AND_NEW_PASSWORD(400009, BAD_REQUEST, "New password and current password are the same."),
    PASSWORD_LENGTH_BAD_REQUEST(400010, BAD_REQUEST, "Password must be at least 8 characters."),
    PASSWORD_UPPERCASE_BAD_REQUEST(400011, BAD_REQUEST, "Password must contain at least one uppercase letter."),
    PASSWORD_LOWERCASE_BAD_REQUEST(400012, BAD_REQUEST, "Password must contain at least one lowercase letter."),
    PASSWORD_SYMBOL_BAD_REQUEST(400013, BAD_REQUEST, "Password must contain at least one symbol. (@#$%^&+=!)"),
    PASSWORD_DIGIT_BAD_REQUEST(400014, BAD_REQUEST, "Password must contain at least one digit."),
    USER_NOT_AUTHENTICATED_BAD_REQUEST(400015, BAD_REQUEST, "Current user is not authenticated or is not a User Account."),
    DUPLICATED_FIELD_BAD_REQUEST(400016, BAD_REQUEST, "Duplicate field found in the request."),
    USER_NOT_HAVE_ROLE_BAD_REQUEST(400017, BAD_REQUEST, "User does not have this rule."),
    JSON_MALFORMED(400018, BAD_REQUEST,"Malformed JSON request."),
    ACCOUNT_AlREADY_ACTIVATE_BAD_REQUEST(400019, BAD_REQUEST, "Account already activated"),

    //404
    ACCOUNT_EMAIL_NOT_FOUND(404001, NOT_FOUND, "User account not found for email informed."),
    ACCOUNT_ID_NOT_FOUND(404002, NOT_FOUND, "User account not found for id informed."),
    TOKEN_NOT_FOUND(404003, NOT_FOUND, "Token informed not found, cannot activate account."),
    ROLE_ID_NOT_FOUND(404004, NOT_FOUND, "Role id informed not found."),
    ROLE_NOT_INITIALIZED_NOT_FOUND(404005, NOT_FOUND, "Role was not initialized."),

    //403
    NOT_AUTHORIZED(403001, FORBIDDEN,"Not authorized."),
    ACCOUNT_LOCKED(403002, FORBIDDEN, "User account is locked."),
    ACCOUNT_DISABLED(403003, FORBIDDEN, "User account is disabled."),
    JWT_NOT_VALIDITY(403004, FORBIDDEN, "JWT validity cannot be asserted and should not be trusted"),

    //409
    USER_ALREADY_EXISTS(409001, CONFLICT, "E-mail already registered."),
    ROLE_ALREADY_EXISTS(409002, CONFLICT, "Role already registered."),
    USER_ALREADY_HAVE_ROLE(409003, CONFLICT, "User already has role"),

    //500
    NULL_POINTER_EXCEPTION(500001, INTERNAL_SERVER_ERROR,"A null pointer exception occurred.");


    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ExceptionMessagesEnum(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}