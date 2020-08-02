package com.fut.desktop.app.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileException extends Exception {

    private String error;

    public FileException(String error, Exception ex) {
        super(error, ex);
        this.error = error;
    }
}
