package com.coding.recimechallenge.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class Handler {

    @Value("${error.difficulty.required}")
    private String missingDifficultyMessage;

    @Value("${error.difficulty.invalid}")
    private String invalidDifficultyMessage;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(Exception ex,
                                         HttpServletRequest request, HttpServletResponse response) {
        if (ex instanceof IOException) {
            //dummy json data not found
            ex.printStackTrace();
        } else if(ex instanceof InvalidFilterValueException) {
            String message = invalidDifficultyMessage;
            if(ex instanceof FilterNotFoundException) {
                message = missingDifficultyMessage;
            }
            Map<String, Object> body = new HashMap<>();
            body.put("message", message);
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
