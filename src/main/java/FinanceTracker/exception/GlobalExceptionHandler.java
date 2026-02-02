package FinanceTracker.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {

        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, Object> fieldErrors = new HashMap<>();
        ex.getBindingResult().
                getFieldErrors().
                forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                Map.of("fieldErrors", fieldErrors)
        );
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(Exception e) {

        return buildError(HttpStatus.FORBIDDEN,
                "Access Denied ",
                null);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {

        return buildError(HttpStatus.UNAUTHORIZED,
                "Invalid password or username",
                null);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {

        return buildError(HttpStatus.BAD_REQUEST
                , ex.getMessage(),
                null);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {

        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                null);
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status,
                                                           String message,
                                                           Map<String, Object> extra) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDate.now());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("status", status.value());

        if (extra != null) {
            body.putAll(extra);
        }

        return new ResponseEntity<>(body, status);
    }


}
