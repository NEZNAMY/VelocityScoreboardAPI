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

package com.velocitypowered.api.event.scoreboard;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.NumberFormat;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Class for objective score events.
 */
@Getter
public abstract class ScoreEvent extends ScoreboardEvent {

    /** Name of the affected objective */
    @NonNull
    private final String objectiveName;

    /** Affected score holder */
    @NonNull
    private final String scoreHolder;

    /**
     * Constructs new instance with given parameters.
     *
     * @param   player
     *          Player who received the scoreboard change
     * @param   mutable
     *          Whether this affects proxy scoreboard or not
     * @param   objectiveName
     *          Name of affected objective
     * @param   scoreHolder
     *          Affected score holder
     */
    public ScoreEvent(@NonNull Player player, boolean mutable, @NonNull String objectiveName, @NonNull String scoreHolder) {
        super(player, mutable);
        this.objectiveName = objectiveName;
        this.scoreHolder = scoreHolder;
    }

    /**
     * This event is called when a score is set.
     */
    @Getter
    public static class Set extends ScoreEvent {

        /** Numeric score (for 1.20.2-) */
        private int score;

        /** Display name for this score (1.20.3+) */
        @Nullable
        private Component displayName;

        /** Number format for the score (1.20.3+) */
        @Nullable
        private NumberFormat numberFormat;

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   mutable
         *          Whether this objective is mutable (proxy) or not (backend)
         * @param   objectiveName
         *          Name of affected objective
         * @param   scoreHolder
         *          Affected score holder
         * @param   score
         *          Numeric score
         * @param   displayName
         *          Display name to replace the score holder name
         * @param   numberFormat
         *          Number format of the objective
         */
        public Set(@NonNull Player player, boolean mutable, @NonNull String objectiveName,
                        @NonNull String scoreHolder, int score, @Nullable Component displayName,
                        @Nullable NumberFormat numberFormat) {
            super(player, mutable, objectiveName, scoreHolder);
            this.score = score;
            this.displayName = displayName;
            this.numberFormat = numberFormat;
        }

        /**
         * Sets the score to new value.
         *
         * @param   score
         *          New score
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setScore(int score) {
            ensureMutable();
            this.score = score;
        }

        /**
         * Sets the display name to new value.
         *
         * @param   displayName
         *          New display name
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setDisplayName(@NonNull Component displayName) {
            ensureMutable();
            this.displayName = displayName;
        }

        /**
         * Sets the number format to new value.
         *
         * @param   numberFormat
         *          New number format
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setNumberFormat(@NonNull NumberFormat numberFormat) {
            ensureMutable();
            this.numberFormat = numberFormat;
        }
    }

    /**
     * This event is called when a score is reset.
     */
    public static class Reset extends ScoreEvent {

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   mutable
         *          Whether this objective is mutable (proxy) or not (backend)
         * @param   objectiveName
         *          Name of affected objective
         * @param   scoreHolder
         *          Affected score holder
         */
        public Reset(@NonNull Player player, boolean mutable, @NonNull String objectiveName, @NonNull String scoreHolder) {
            super(player, mutable, objectiveName, scoreHolder);
        }
    }
}
