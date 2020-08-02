package com.fut.desktop.app.models;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class LicenseRequest {
    private String email;
    private String key;
}
