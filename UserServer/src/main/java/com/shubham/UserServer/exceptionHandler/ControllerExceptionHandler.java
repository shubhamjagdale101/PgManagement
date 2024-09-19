package com.shubham.UserServer.exceptionHandler;

import com.shubham.UserServer.responseEntity.Response;
import org.json.JSONObject;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ControllerExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> responseException(Exception e){
        Response res = Response.builder()
                .error(e.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
}
