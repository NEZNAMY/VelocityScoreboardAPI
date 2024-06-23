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

import java.util.function.Consumer;

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
     * @see     #setDisplaySlot(DisplaySlot)
     */
    @Nullable
    DisplaySlot getDisplaySlot();

    /**
     * Sets display slot of this objective.
     *
     * @param   displaySlot
     *          New display slot
     * @see     #getDisplaySlot()
     */
    void setDisplaySlot(@NotNull DisplaySlot displaySlot);

    /**
     * Gets title of this objective.
     * 
     * @return  title of this objective
     * @see     #setTitle(TextHolder) 
     */
    @NotNull
    TextHolder getTitle();

    /**
     * Sets title of this objective.
     * 
     * @param   title
     *          New title to use
     * @see     #getTitle() 
     */
    void setTitle(@NotNull TextHolder title);

    /**
     * Gets health display of this objective.
     * 
     * @return  health display of this objective
     * @see     #setHealthDisplay(HealthDisplay) 
     */
    @NotNull
    HealthDisplay getHealthDisplay();

    /**
     * Sets health display type.
     * 
     * @param   healthDisplay
     *          New health display
     * @see     #getHealthDisplay() 
     */
    void setHealthDisplay(@NotNull HealthDisplay healthDisplay);

    /**
     * Returns default number format for all scores in this objective.
     * 
     * @return  default number format for all scores in this objective
     * @see     #setNumberFormat(NumberFormat) 
     */
    @Nullable
    NumberFormat getNumberFormat();

    /**
     * Sets default number format for all scores in this objective.
     * 
     * @param   numberFormat
     *          Default number format for all scores
     */
    void setNumberFormat(@Nullable NumberFormat numberFormat);

    /**
     * Creates a new score builder.
     * 
     * @param   holder
     *          Score holder name
     * @return  Score builder
     */
    @NotNull
    Score.Builder scoreBuilder(@NotNull String holder);

    /**
     * Registers a score with the given Score.Builder object.
     *
     * @param builder the Score.Builder object representing the score to be registered
     * @return the registered Score object
     * @see Score.Builder
     */
    @NotNull
    Score registerScore(@NotNull Score.Builder builder);

    /**
     * Creates a score with the given holder and customization options.
     *
     * @param holder    The name of the score holder
     * @param consumer  The consumer function that defines the customization options for the score builder
     * @return The registered Score object
     */
    @NotNull
    default Score createScore(@NotNull String holder, @NotNull Consumer<Score.Builder> consumer) {
        Score.Builder builder = scoreBuilder(holder);
        consumer.accept(builder);
        return registerScore(builder);
    }

    /**
     * Gets score of given holder.
     * 
     * @param   holder
     *          Score holder name
     * @return  Score of given holder
     * @see     #removeScore(String) 
     */
    @Nullable
    Score getScore(@NotNull String holder);

    /**
     * Removes score of given holder.
     * 
     * @param   holder
     *          Score holder
     * @see     #getScore(String)
     */
    void removeScore(@NotNull String holder);

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
        Builder title(@NotNull TextHolder title);

        /**
         * Sets health display.
         *
         * @param   healthDisplay
         *          Health display type
         * @return  this, for chaining
         */
        @NotNull
        Builder healthDisplay(@NotNull HealthDisplay healthDisplay);

        /**
         * Sets display slot.
         *
         * @param   displaySlot
         *          Display slot
         * @return  this, for chaining
         */
        @NotNull
        Builder displaySlot(@NotNull DisplaySlot displaySlot);

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
