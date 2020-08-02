package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.constants.FutErrorCode;
import lombok.*;

@Getter
@Setter
@JsonFormat
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FutError {
    private String reason;
    private FutErrorCode code;
    private String message;
    private String debug;
    private String String;
}
