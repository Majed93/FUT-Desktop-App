package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonFormat
@AllArgsConstructor//(suppressConstructorProperties = true)
@NoArgsConstructor
public class UserAccounts implements Serializable {

    private static final long serialVersionUID = 710171644610421005L;

    private UserAccountInfo userAccountInfo;
}
