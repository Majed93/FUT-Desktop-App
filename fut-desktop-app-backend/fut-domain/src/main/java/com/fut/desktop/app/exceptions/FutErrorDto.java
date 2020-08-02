package com.fut.desktop.app.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Getter
@Setter
@ToString
@JsonFormat
@NoArgsConstructor
@AllArgsConstructor
public class FutErrorDto {

    private Long timestamp;
    private Integer status;
    private String error;
    private String exception;
    private String message;
    private String path;
}
