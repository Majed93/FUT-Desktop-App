package com.fut.desktop.app.domain.pinEvents;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class Custom {
    private String networkAccess;
    private String service_plat;
}
