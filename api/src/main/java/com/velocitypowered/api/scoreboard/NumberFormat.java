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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for formatting scoreboard scores.
 */
public interface NumberFormat {

    /**
     * Returns blank format where scores are completely hidden.
     *
     * @return  Blank number format
     */
    @NotNull
    static NumberFormat blank() {
        return BlankFormat.INSTANCE;
    }

    /**
     * Returns styled format where scores use given style.
     *
     * @param   style
     *          Style to use for scores
     * @return  Styled format with given style
     */
    @NotNull
    static NumberFormat styled(@NonNull Style style) {
        return new StyledFormat(style);
    }

    /**
     * Returns fixed format where scores are replaced with given text.
     *
     * @param   text
     *          Text to replace scores with
     * @return  Fixed format with given text
     */
    @NotNull
    static NumberFormat fixed(@NonNull Component text) {
        return new FixedFormat(text);
    }

    /**
     * Format that hides numbers completely.
     */
    record BlankFormat() implements NumberFormat {

        /** Singleton instance of this class */
        public static final BlankFormat INSTANCE = new BlankFormat();
    }

    /**
     * Formatter that applies style to all scores.
     *
     * @param   style
     *          Style to apply to all scores
     */
    record StyledFormat(@NonNull Style style) implements NumberFormat {
    }

    /**
     * Formatter that replaces score with specified text.
     *
     * @param   component
     *          Component to display instead of score
     */
    record FixedFormat(@NonNull Component component) implements NumberFormat {
    }
}