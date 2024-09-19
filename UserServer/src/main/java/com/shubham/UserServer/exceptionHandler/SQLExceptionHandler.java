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
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SQLExceptionHandler {
    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> responseSQLException(Exception e){
        Response res = Response.builder()
                .error("Database Constraint Error.")
                .build();

        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
}
