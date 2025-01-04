/*
 * This file is part of VelocityScoreboardAPI, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) NEZNAMY <n.e.z.n.a.m.y@azet.sk>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.velocitypowered.api.scoreboard;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Team collision rule enum.
 */
@RequiredArgsConstructor
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