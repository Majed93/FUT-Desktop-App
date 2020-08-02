package com.fut.api.fut.rest;

import com.fut.desktop.app.domain.FutError;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.exceptions.FutException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class FutIOExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FutErrorException.class)
    public FutError handelFutErrorException(FutErrorException ex) {
        throw ex;
    }

    @ExceptionHandler(FutException.class)
    public ResponseEntity<FutError> handelFutException(FutException ex) {
        FutErrorException futEx = (FutErrorException) ex.getCause();
        return ResponseEntity.status(futEx.getFutError().getCode().getFutErrorCode()).body(futEx.getFutError());
    }
}
