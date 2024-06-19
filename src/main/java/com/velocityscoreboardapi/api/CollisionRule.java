package com.velocityscoreboardapi.api;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Team collision rule enum.
 */
@AllArgsConstructor
public enum CollisionRule {

    /** Always pushes all players */
    ALWAYS("always"),

    /** Never pushes anyone */
    NEVER("never"),

    /** Only pushes players from other teams */
    PUSH_OTHER_TEAMS("pushOtherTeams"),

    /** Only pushes players from own team */
    PUSH_OWN_TEAM("pushOwnTeam");

    /** Map of code name to enum constant */
    private static final Map<String, CollisionRule> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(collisionRule -> collisionRule.string, collisionRule -> collisionRule));

    /** Code name of this constant */
    @NotNull
    private final String string;

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
    public static CollisionRule getByName(@NonNull String name) {
        return BY_NAME.getOrDefault(name, ALWAYS);
    }
}