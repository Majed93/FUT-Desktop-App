package com.fut.desktop.app.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * NOTE: Need to update models in the fut-simulator to
 */
@Getter
@AllArgsConstructor
public enum RoleLevel {
    ROLE_GOLD(1588),
    ROLE_SILVER(1648),
    ROLE_BRONZE(1754);

    private final int id;
}
