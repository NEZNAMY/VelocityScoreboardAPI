package com.velocityscoreboardapi.api;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Nametag visibility enum.
 */
public enum NameVisibility {

    /** Name can be seen by everyone */
    ALWAYS("always"),

    /** Name cannot be seen by anyone */
    NEVER("never"),

    /** Name is hidden from other teams */
    HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),

    /** Name is hidden from own team */
    HIDE_FOR_OWN_TEAM("hideForOwnTeam");

    /** Map of code name to enum constant */
    private static final Map<String, NameVisibility> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(visibility -> visibility.string, visibility -> visibility));

    /** Code name of this constant */
    @NotNull
    private final String string;

    /**
     * Constructs new instance with given code name.
     *
     * @param   string
     *          Code name used in protocol
     */
    NameVisibility(@NotNull String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    /**
     * Returns enum constant from code name. If invalid, {@link #ALWAYS}
     * is returned.
     *
     * @param   name
     *          Code name of the collision rule
     * @return  Enum constant from given code name
     */
    @NotNull
    public static NameVisibility getByName(@NotNull String name) {
        return BY_NAME.getOrDefault(name, ALWAYS);
    }
}