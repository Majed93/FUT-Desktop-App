package com.fut.desktop.app.extensions;

public final class StringExtensions {

    public static void ThrowIfInvalidArgument(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }


}
