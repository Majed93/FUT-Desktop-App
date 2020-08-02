package com.fut.desktop.app.extensions;

public final class ReferenceTypeExtensions {
    public static void ThrowIfInvalidArgument(Object input) {
        if (input == null) {
            throw new IllegalArgumentException();
        }
    }
}
