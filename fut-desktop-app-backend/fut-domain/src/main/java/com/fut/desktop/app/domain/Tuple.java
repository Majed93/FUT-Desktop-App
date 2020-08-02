package com.fut.desktop.app.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Tuple {
    public final Object object;
    public final FutError error;
}
