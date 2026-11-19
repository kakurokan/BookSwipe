package com.bookswipe.backend.common;

import com.bookswipe.backend.common.Exception.ApiException;
import com.bookswipe.backend.common.Exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleValidation(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.status())
                .body(new ErrorResponse(ErrorCode.VALIDATION_ERROR.name(), "invalid body"));
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentTypeMismatchException ex) {
        List<ErrorResponse.FieldViolation> details =
                List.of(new ErrorResponse.FieldViolation(ex.getName(), "Invalid value"));
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.status())
                .body(new ErrorResponse(ErrorCode.VALIDATION_ERROR.name(), "invalid request", details));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MissingServletRequestParameterException ex) {
        List<ErrorResponse.FieldViolation> details =
                List.of(new ErrorResponse.FieldViolation(ex.getParameterName(), "Missing parameter"));
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.status())
                .body(new ErrorResponse(ErrorCode.VALIDATION_ERROR.name(), "invalid request", details));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(HandlerMethodValidationException ex) {
        List<ErrorResponse.FieldViolation> details = ex.getBeanResults().stream()
                .flatMap(beanErros -> beanErros.getFieldErrors().stream())
                .map(e -> new ErrorResponse.FieldViolation(e.getField(), e.getDefaultMessage()))
                .toList();

        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.status())
                .body(new ErrorResponse(ErrorCode.VALIDATION_ERROR.name(), "invalid request", details));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResources(NoResourceFoundException ex) {
        return ResponseEntity.status(ErrorCode.RESOURCE_NOT_FOUND.status())
                .body(new ErrorResponse(ErrorCode.RESOURCE_NOT_FOUND.name(), "Resource not found"));
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
