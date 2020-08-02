package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@JsonFormat
@AllArgsConstructor
@NoArgsConstructor
public class UserClub implements Serializable {

    private static final long serialVersionUID = -2950769456185450018L;

    private String established;

    private String year;

    private String lastAccessTime;

    private LocalDateTime lastAccessDateTime;

    private String Platform;

    private String ClubAbbr;

    private String ClubName;

    private long TeamId;

    private LocalDateTime establishedDateTime;

    private byte divisionOnline;

    private long badgeId;

    private Map<String, Long> skuAccessList;

    private long assetId;

    public void setLastAccessTime(String value) {
        lastAccessTime = value;
        lastAccessDateTime = DateTimeExtensions.FromUnixTime(value);
    }

    public void setEstablished(String value) {
        established = value;
        establishedDateTime = DateTimeExtensions.FromUnixTime(value);
    }

}
