package com.project.Ipubly.Controller;

import com.project.Ipubly.Config.ExceptionAPI;
import com.project.Ipubly.Model.DTO.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
