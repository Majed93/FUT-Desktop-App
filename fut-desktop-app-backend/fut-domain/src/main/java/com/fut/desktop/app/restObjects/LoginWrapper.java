package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.domain.LoginDetails;
import com.fut.desktop.app.domain.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonFormat
@Getter
@Setter
@AllArgsConstructor
public class LoginWrapper {
    private Integer id;

    private LoginDetails loginDetails;

    private LoginResponse loginResponse;

    private Account account;

}
