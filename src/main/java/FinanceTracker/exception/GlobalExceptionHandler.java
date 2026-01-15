package FinanceTracker.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> handleRuntimeException(RuntimeException e) {
        Map<String,Object> error = new HashMap<>();
        error.put("timestamp", LocalDate.now());
        error.put("message", e.getMessage());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Map<String,Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String,Object> error = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError->error.put(fieldError.getField(), fieldError.getDefaultMessage()));

        Map<String,Object> response = new HashMap<>();
        response.put("timestamp", LocalDate.now());
        response.put("validationErrors", error);
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> handleAccessDenied(Exception e) {
        Map<String,Object> error = new HashMap<>();
        error.put("timestamp", LocalDate.now());
        error.put("message", "Access Denied: You don't have permission to perform this action.");
        error.put("status", HttpStatus.FORBIDDEN.value());

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);

    }


}
