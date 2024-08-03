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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A scoreboard with objectives and teams.
 */
public interface Scoreboard {

    /**
     * Gets objective by name. If no such objective is registered, returns {@code null}.
     *
     * @param   name
     *          Objective name
     * @return  Objective by given name or {@code null} if not present
     */
    @Nullable
    Objective getObjective(@NotNull String name);

    /**
     * Retrieves the objective displayed in the specified display slot.
     *
     * @param displaySlot The display slot where the objective is shown
     * @return The objective displayed in the specified display slot, or {@code null} if no objective is present
     */
    @Nullable
    Objective getObjective(@NotNull DisplaySlot displaySlot);

    /**
     * Retrieves the set of objectives in the scoreboard.
     *
     * @return a set of objectives in the scoreboard
     */
    @NotNull
    Collection<? extends Objective> getObjectives();

    /**
     * Returns team with give name. If no such team is present, returns {@code null}.
     *
     * @param   teamName
     *          Team name
     * @return  Team with given name, {@code null} if no such team exists
     */
    @Nullable
    Team getTeam(@NotNull String teamName);

    /**
     * Retrieves the set of teams in the scoreboard.
     *
     * @return a set of teams in the scoreboard
     */
    @NotNull
    Collection<? extends Team> getTeams();
}
