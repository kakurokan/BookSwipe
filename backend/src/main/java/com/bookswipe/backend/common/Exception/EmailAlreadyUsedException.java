package com.bookswipe.backend.common.Exception;

public class EmailAlreadyUsedException extends ApiException {
    public EmailAlreadyUsedException() {
        super("This email is already in use", ErrorCode.EMAIL_ALREADY_USED);
    }
}
