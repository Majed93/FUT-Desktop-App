package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
@JsonFormat
@AllArgsConstructor//(suppressConstructorProperties = true)
@NoArgsConstructor
public class Persona implements Serializable {

    private static final long serialVersionUID = -9037610255076970188L;

    private Boolean onlineAccess;

    private long personaId;

    private String personaName;

    private Collection<UserClub> userClubList;

    private Boolean returningUser;

    private Boolean returningUserTier;

    private Boolean trial;

    private String userState;

    private String trialFree;
}
