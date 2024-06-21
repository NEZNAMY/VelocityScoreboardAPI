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
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for formatting scoreboard scores.
 */
public interface NumberFormat {

    @NotNull
    static NumberFormat blank() {
        return BlankFormat.INSTANCE;
    }

    @NotNull
    static NumberFormat styled(@NotNull Style style) {
        return new StyledFormat(style);
    }

    @NotNull
    static NumberFormat fixed(@NotNull Component text) {
        return new FixedFormat(text);
    }

    /**
     * Format that hides numbers completely.
     */
    class BlankFormat implements NumberFormat {

        /** Singleton instance of this class */
        public static final BlankFormat INSTANCE = new BlankFormat();

        private BlankFormat() {}
    }

    /**
     * Formatter that applies style to all scores.
     *
     * @param   style
     *          Style to apply to all scores
     */
    record StyledFormat(@NotNull Style style) implements NumberFormat {
    }

    class FixedFormat implements NumberFormat {

        private final Component component;


        public FixedFormat(Component component) {
            this.component = component;
        }

        public Component component() {
            return component;
        }
    }
}