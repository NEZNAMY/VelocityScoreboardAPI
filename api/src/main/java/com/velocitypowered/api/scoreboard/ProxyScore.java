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
 * An interface representing proxy score entry of a player in a proxy objective.
 */
public interface ProxyScore extends Score {

    /**
     * Sets score to new value.
     *
     * @param   score
     *          New score value
     * @see     #getScore()
     */
    void setScore(int score);

    /**
     * Sets custom display name of the holder to new value (1.20.3+). Only visible in sidebar.
     *
     * @param   displayName
     *          New display name to use
     * @see     #getDisplayName()
     */
    void setDisplayName(@Nullable Component displayName);

    /**
     * Sets number formatting to apply to score (1.20.3+).
     *
     * @param   numberFormat
     *          New number format to use
     * @see     #getNumberFormat()
     */
    void setNumberFormat(@Nullable NumberFormat numberFormat);

    /**
     * Interface for building a new score entry.
     */
    interface Builder {

        /**
         * Sets score to specified value.
         *
         * @param   score
         *          Score value to display
         * @return  this, for chaining
         */
        @NotNull
        Builder score(int score);

        /**
         * Sets holder's display name to specified value.
         *
         * @param   displayName
         *          Text to display instead of holder's name
         * @return  this, for chaining
         */
        @NotNull
        Builder displayName(@Nullable Component displayName);

        /**
         * Sets number format to specified value.
         *
         * @param   format
         *          Number formatting to apply to score
         * @return  this, for chaining
         */
        @NotNull
        Builder numberFormat(@Nullable NumberFormat format);
    }
}
