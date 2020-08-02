package com.fut.desktop.app.models;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class LicenseResponse {
    private String email;
    private String key;
    private List<RoleLevel> roles;
    private List<PrivilegeLevel> privileges;
    private boolean authorized;
}
