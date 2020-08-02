package com.fut.desktop.app.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PrivilegeLevel {
    Normal(4984),
    Analytics(1213),
    AutoBid(7987);

    private final int id;
}
