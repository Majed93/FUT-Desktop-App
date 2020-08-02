package com.fut.desktop.app.futservice.rest;

import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.domain.FutError;
import com.fut.desktop.app.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public FutError handleBadRequestException(BadRequestException e) {
        return new FutError(e.getMessage(), FutErrorCode.BadRequest, e.getMessage(), "", "");
    }

    @ExceptionHandler(value = CaptchaTriggeredException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public FutError handleCaptchaTriggeredException(CaptchaTriggeredException e) {
        return new FutError(e.getMessage(), FutErrorCode.CaptchaTriggered, e.getMessage(), "", "");
    }

    @ExceptionHandler(value = ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public FutError handleConflictException(ConflictException e) {
        return new FutError(e.getMessage(), FutErrorCode.Conflict, e.getMessage(), "", "");
    }

    @ExceptionHandler(value = DestinationFullException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public FutError handleDestinationFullException(DestinationFullException e) {
        return new FutError(e.getMessage(), FutErrorCode.DestinationFull, e.getMessage(), "", "");
    }

    @ExceptionHandler(value = ExpiredSessionException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public FutError handleExpiredSessionException(ExpiredSessionException e) {
        return new FutError(e.getMessage(), FutErrorCode.ExpiredSession, e.getMessage(), "", "");
    }

    @ExceptionHandler(value = FutErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public FutError handleFutErrorException(FutErrorException e) {
        return e.getFutError();
    }

    @ExceptionHandler(value = FutException.class)
    public ResponseEntity<FutError> handelFutException(FutException ex) {
        FutErrorException futEx = (FutErrorException) ex.getCause();
        return ResponseEntity.status(futEx.getFutError().getCode().getFutErrorCode()).body(futEx.getFutError());
    }

    @ExceptionHandler(value = InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public FutError handleInternalServerException(InternalServerException e) {
        return new FutError(e.getMessage(), FutErrorCode.InternalServerError, e.getMessage(), "", "");
    }

    @ExceptionHandler(value = InvalidDeckException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public FutError handleInvalidDeckException(InvalidDeckException e) {
        return new FutError(e.getMessage(), FutErrorCode.InvalidDeck, e.getMessage(), "", "");
    }

    @ExceptionHandler(value = NoSuchTradeExistsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public FutError handleNoSuchTradeExistsException(NoSuchTradeExistsException e) {
        return new FutError(e.getMessage(), FutErrorCode.NoSuchTradeExists, e.getMessage(), "", "");
    }

    @ExceptionHandler(value = NotEnoughCreditException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public FutError handleNotEnoughCreditException(NotEnoughCreditException e) {
        return new FutError(e.getMessage(), FutErrorCode.NotEnoughCredit, e.getMessage(), "", "");
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public FutError handleNotFoundException(NotFoundException e) {
        return new FutError(e.getMessage(), FutErrorCode.NotFound, e.getMessage(), "", "");
    }

    @ExceptionHandler(value = PermissionDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public FutError handlePermissionDeniedException(PermissionDeniedException e) {
        return new FutError(e.getMessage(), FutErrorCode.PermissionDenied, e.getMessage(), "", "");
    }

    @ExceptionHandler(value = PurchasedItemsFullException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public FutError handlePurchasedItemsFullException(PurchasedItemsFullException e) {
        return new FutError(e.getMessage(), FutErrorCode.PurchasedItemsFull, e.getMessage(), "", "");
    }

    @ExceptionHandler(value = ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public FutError handleServiceUnavailableException(ServiceUnavailableException e) {
        return new FutError(e.getMessage(), FutErrorCode.ServiceUnavailable, e.getMessage(), "", "");
    }

//    These below exceptions are custom ones there were not part of the fut api from c# ***************

    @ExceptionHandler(value = FileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public FutError handleFileException(FileException e) {
        return new FutError(e.getMessage(), FutErrorCode.InternalServerError, e.getMessage(), "", "");
    }


    @ExceptionHandler(value = AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public FutError handleAuthException(AuthException e) {
        return new FutError(e.getMessage(), FutErrorCode.PermissionDenied, e.getMessage(), "", "");
    }
}
