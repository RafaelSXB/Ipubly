package com.project.Ipubly.Controller;

import com.project.Ipubly.Config.ExceptionAPI;
import com.project.Ipubly.Model.DTO.ErrorResponseDTO;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(ExceptionAPI.class)
    public ResponseEntity<ErrorResponseDTO> handleMyCustomException(ExceptionAPI ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("Error", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponseDTO error = new ErrorResponseDTO("Internal Error", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.error("NoResultException: {}", ex.getMessage(), ex);
        ErrorResponseDTO error = new ErrorResponseDTO("Not Found", "Resource not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("HTTP Method Not Supported: {}", ex.getMessage(), ex);
        ErrorResponseDTO error = new ErrorResponseDTO("Method Not Allowed", "HTTP method not supported.");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("Method Argument Type Mismatch: {}", ex.getMessage(), ex);
        ErrorResponseDTO error = new ErrorResponseDTO("Bad Request", "Invalid argument type provided: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("HTTP Message Not Readable: {}", ex.getMessage(), ex);
        ErrorResponseDTO error = new ErrorResponseDTO("Bad Request", "Malformed JSON request.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
