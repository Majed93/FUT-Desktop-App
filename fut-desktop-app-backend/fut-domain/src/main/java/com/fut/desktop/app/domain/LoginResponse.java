package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.extensions.ReferenceTypeExtensions;
import com.fut.desktop.app.extensions.StringExtensions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonFormat
@NoArgsConstructor
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = -8515557447434195087L;

    private String nucleusId;

    private Shards shards;

    private UserAccounts userAccounts;

    private String sessionId;

    private String mobileSessionId;

    private String phishingToken;

    private String personaId;

    private String dob;

    private String authBearerToken;

    private String mobileAuthBearerToken;

    private int pageSize;

    private Long lastLogin;

    private String refreshToken; // used my the mobile app.

    public LoginResponse(String nucleusId, Shards shards, UserAccounts userAccounts, String sessionId,
                         String phishingToken, String personaId, String dob, String authBearerToken, int pageSize,
                         Long lastLogin, String mobileAuthBearerToken, String mobileSessionId, String refreshToken) {
        StringExtensions.ThrowIfInvalidArgument(nucleusId);
        ReferenceTypeExtensions.ThrowIfInvalidArgument(shards);
        ReferenceTypeExtensions.ThrowIfInvalidArgument(userAccounts);
        StringExtensions.ThrowIfInvalidArgument(phishingToken);
        StringExtensions.ThrowIfInvalidArgument(personaId);
        StringExtensions.ThrowIfInvalidArgument(dob);
        this.nucleusId = nucleusId;
        this.shards = shards;
        this.userAccounts = userAccounts;
        this.sessionId = sessionId;
        this.phishingToken = phishingToken;
        this.personaId = personaId;
        this.dob = dob;
        this.authBearerToken = authBearerToken;
        this.pageSize = pageSize;
        this.lastLogin = lastLogin;
        this.mobileAuthBearerToken = mobileAuthBearerToken;
        this.mobileSessionId = mobileSessionId;
        this.refreshToken = refreshToken;
    }
}
