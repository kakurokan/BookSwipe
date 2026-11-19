package com.bookswipe.backend.common;

import com.bookswipe.backend.common.Exception.ApiException;
import com.bookswipe.backend.common.Exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ExceptionController {
    private static final Logger log = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getErrorCode().status()).body(ex.errorResponse());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldViolation> details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ErrorResponse.FieldViolation(e.getField(), e.getDefaultMessage()))
                .toList();

        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.status())
                .body(new ErrorResponse(ErrorCode.VALIDATION_ERROR.name(), "Invalid request", details));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        log.error("Unexpected error", ex);
        ErrorResponse error = new ErrorResponse(
                ErrorCode.INTERNAL_ERROR.name(),
                "Unexpected server error"
        );
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.status()).body(error);
    }
}
