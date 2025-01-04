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
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * A scoreboard proxy objective with proxy scores.
 */
public interface ProxyObjective extends Objective {

    /**
     * Sets display slot of this objective.
     *
     * @param   displaySlot
     *          New display slot
     * @see     #getDisplaySlot()
     */
    void setDisplaySlot(@NonNull DisplaySlot displaySlot);

    /**
     * Sets title of this objective.
     *
     * @param   title
     *          New title to use
     * @see     #getTitle()
     */
    void setTitle(@NonNull TextHolder title);

    /**
     * Sets health display type.
     * 
     * @param   healthDisplay
     *          New health display
     * @see     #getHealthDisplay() 
     */
    void setHealthDisplay(@NonNull HealthDisplay healthDisplay);

    /**
     * Sets default number format for all scores in this objective.
     * 
     * @param   numberFormat
     *          Default number format for all scores
     */
    void setNumberFormat(@Nullable NumberFormat numberFormat);

    /**
     * Gets score of given holder.
     *
     * @param   holder
     *          Score holder name
     * @return  Score of given holder
     * @see     #removeScore(String)
     * @see     #setScore(String, Consumer)
     */
    @Nullable
    ProxyScore getScore(@NonNull String holder);

    /**
     * Retrieves all scores associated with this objective.
     *
     * @return A collection of ProxyScore objects representing all scores in this objective
     */
    @NotNull
    Collection<ProxyScore> getAllScores();

    /**
     * Creates or updates a score with the given holder and customization options.
     *
     * @param holder    The name of the score holder
     * @param consumer  The consumer function that defines the customization options for the score builder
     * @return The registered or updated Score object
     * @see #getScore(String) 
     * @see #removeScore(String) 
     */
    @NotNull
    ProxyScore setScore(@NonNull String holder, @NonNull Consumer<ProxyScore.Builder> consumer);

    /**
     * Removes score of given holder.
     * 
     * @param   holder
     *          Score holder
     * @see     #getScore(String)
     * @see     #setScore(String, Consumer) 
     */
    void removeScore(@NonNull String holder);

    /**
     * Interface for building objectives.
     */
    interface Builder {

        /**
         * Sets objective title.
         *
         * @param   title
         *          Title to display
         * @return  this, for chaining
         */
        @NotNull
        Builder title(@NonNull TextHolder title);

        /**
         * Sets health display.
         *
         * @param   healthDisplay
         *          Health display type
         * @return  this, for chaining
         */
        @NotNull
        Builder healthDisplay(@NonNull HealthDisplay healthDisplay);

        /**
         * Sets display slot.
         *
         * @param   displaySlot
         *          Display slot
         * @return  this, for chaining
         */
        @NotNull
        Builder displaySlot(@NonNull DisplaySlot displaySlot);

        /**
         * Sets default number format for all scores.
         *
         * @param   numberFormat
         *          Default number format for all scores
         * @return  this, for chaining
         */
        @NotNull
        Builder numberFormat(@Nullable NumberFormat numberFormat);
    }
}
