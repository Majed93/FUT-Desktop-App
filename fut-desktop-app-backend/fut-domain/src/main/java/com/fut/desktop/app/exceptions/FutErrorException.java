package com.fut.desktop.app.exceptions;

import com.fut.desktop.app.domain.FutError;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FutErrorException extends FutException {

    private static final long serialVersionUID = -586262615541913652L;
    private FutError futError;

    public FutErrorException(FutError futError) {
        super(ExtractMessage(futError));
        this.futError = futError;
    }

    public FutErrorException(FutError futError, Exception e) {
        super(ExtractMessage(futError), e);
        this.futError = futError;
    }

    private static String ExtractMessage(FutError futError) {
        if (futError == null) {
            throw new IllegalArgumentException("futError is null");
        }

        String result = "Code: " + futError.getCode();
        if (!futError.getReason().isEmpty()) {
            result += ", Reason: " + futError.getReason();
        }
        if (!futError.getMessage().isEmpty()) {
            result += ", Message: " + futError.getMessage();
        }
        if (!futError.getDebug().isEmpty()) {
            result += ", Debug: " + futError.getDebug();
        }
        if (!futError.getString().isEmpty()) {
            result += ", String: " + futError.getString();
        }

        return result;
    }
}
