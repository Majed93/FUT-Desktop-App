package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class Pid {
    private String externalRefType;

    private String externalRefValue;

    private String pidId;

    private String email;

    private String emailStatus;

    private String strength;

    private String dob;

    private String country;

    private String language;

    private String locale;

    private String status;

    private String reasonCode;

    private String tosVersion;

    private String parentalEmail;

    private String thirdPartyOptin;

    private String globalOptin;

    private String dateCreated;

    private String dateModified;

    private String lastAuthDate;

    private String registrationSource;

    private String authenticationSource;

    private String showEmail;

    private String discoverableEmail;

    private String anonymousPid;

    private String underagePid;

    private String defaultBillingAddressUri;

    private String defaultShippingAddressUri;

    private String passwordSignature;

}
