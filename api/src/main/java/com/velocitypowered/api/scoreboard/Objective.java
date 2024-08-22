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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A scoreboard objective with scores.
 */
public interface Objective {

    /**
     * Returns name of this objective.
     *
     * @return  name of this objective
     */
    @NotNull
    String getName();

    /**
     * Returns display slot where this objective is displayed.
     *
     * @return  display slot where this objective is displayed
     */
    @Nullable
    DisplaySlot getDisplaySlot();

    /**
     * Gets title of this objective.
     * 
     * @return  title of this objective
     */
    @NotNull
    TextHolder getTitle();

    /**
     * Gets health display of this objective.
     * 
     * @return  health display of this objective
     */
    @NotNull
    HealthDisplay getHealthDisplay();

    /**
     * Returns default number format for all scores in this objective.
     * 
     * @return  default number format for all scores in this objective
     */
    @Nullable
    NumberFormat getNumberFormat();

    /**
     * Gets score of given holder.
     * 
     * @param   holder
     *          Score holder name
     * @return  Score of given holder
     */
    @Nullable
    Score getScore(@NotNull String holder);

    /**
     * Retrieves all scores associated with this objective.
     *
     * @return A collection of Score objects representing all scores in this objective
     */
    @NotNull
    Collection<? extends Score> getAllScores();
}
