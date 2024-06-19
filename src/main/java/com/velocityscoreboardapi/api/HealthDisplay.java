package com.velocityscoreboardapi.api;

import java.util.Locale;

public enum HealthDisplay {

    INTEGER, HEARTS;

    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.ROOT);
    }

    public static HealthDisplay fromString(String s) {
        return valueOf(s.toUpperCase(Locale.ROOT));
    }
}