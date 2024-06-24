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

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface representing score entry of a player in an objective.
 */
public interface Score {

    /**
     * Returns name of player holding this score.
     *
     * @return  name of player holding this score
     */
    @NotNull
    String getHolder();

    /**
     * Returns score value.
     *
     * @return  score value
     */
    int getScore();

    /**
     * Returns custom display name for holder (1.20.3+). Only visible in sidebar.
     *
     * @return  Custom display name for holder
     */
    @Nullable
    Component getDisplayName();

    /**
     * Returns number format applied to score (1.20.3+).
     *
     * @return  Number format applied to score
     */
    @Nullable
    NumberFormat getNumberFormat();
}
