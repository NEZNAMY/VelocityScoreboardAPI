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

import com.velocitypowered.api.TextHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * An interface representing a scoreboard team.
 */
public interface Team {

    /**
     * Returns name of this team.
     *
     * @return  name of this team
     */
    @NotNull
    String getName();

    /**
     * Returns display name of this team
     *
     * @return  Display name of this team
     */
    @NotNull
    TextHolder getDisplayName();

    /**
     * Returns team prefix.
     *
     * @return  Team prefix
     */
    @NotNull
    TextHolder getPrefix();

    /**
     * Returns team suffix.
     *
     * @return  Team suffix
     */
    @NotNull
    TextHolder getSuffix();

    /**
     * Returns nametag visibility rule (1.8+).
     *
     * @return  Nametag visibility rule
     */
    @NotNull
    NameVisibility getNameVisibility();

    /**
     * Returns collision rule (1.9+).
     *
     * @return  Collision rule
     */
    @NotNull
    CollisionRule getCollisionRule();

    /**
     * Returns team color (1.13+).
     *
     * @return  Team color
     */
    @NotNull
    TeamColor getColor();

    /**
     * Returns {@code true} if team allows friendly fire between players in
     * the same team, {@code false} if not.
     *
     * @return  Friendly fire flag value
     */
    boolean isAllowFriendlyFire();

    /**
     * Returns {@code true} if team allows seeing invisible players in the
     * same team as transparent, {@code false} if not.
     *
     * @return  Can see friendly invisibles flag value
     */
    boolean isCanSeeFriendlyInvisibles();

    /**
     * Returns entries currently present in this team. The returned collection is immutable.
     *
     * @return  Entries in this team
     */
    @NotNull
    Collection<String> getEntries();
}
