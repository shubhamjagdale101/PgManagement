package com.shubham.UserServer.exceptionHandler;

import com.shubham.UserServer.responseEntity.Response;
import org.json.JSONObject;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ControllerExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> responseException(Exception e){
        Response response = Response.builder()
                .error(true)
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
