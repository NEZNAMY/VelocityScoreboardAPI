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

import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.DisplaySlot;
import com.velocitypowered.api.scoreboard.HealthDisplay;
import com.velocitypowered.api.scoreboard.NumberFormat;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class for Objective events.
 */
@Getter
public abstract class ObjectiveEvent extends ScoreboardEvent {

    /** Name of the affected objective */
    @NonNull
    private final String objectiveName;

    /**
     * Constructs new instance with given parameters.
     *
     * @param   player
     *          Player who received the scoreboard change
     * @param   mutable
     *          Whether this affects proxy scoreboard or not
     * @param   objectiveName
     *          Name of affected objective
     */
    public ObjectiveEvent(@NonNull Player player, boolean mutable, @NonNull String objectiveName) {
        super(player, mutable);
        this.objectiveName = objectiveName;
    }

    /**
     * This event is called when an objective is assigned a display slot.
     */
    @Getter
    public static class Display extends ObjectiveEvent {

        /** New display slot */
        @NonNull
        private DisplaySlot newSlot;

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   mutable
         *          Whether this objective is mutable (proxy) or not (backend)
         * @param   objectiveName
         *          Name of affected objective
         * @param   newSlot
         *          New display slot
         */
        public Display(@NonNull Player player, boolean mutable, @NonNull String objectiveName, @NonNull DisplaySlot newSlot) {
            super(player, mutable, objectiveName);
            this.newSlot = newSlot;
        }

        /**
         * Sets new slot to display this objective in
         *
         * @param   newSlot
         *          New display slot
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setNewSlot(@NonNull DisplaySlot newSlot) {
            ensureMutable();
            this.newSlot = newSlot;
        }
    }

    /**
     * This event is called when an objective is registered.
     */
    @Getter
    public static class Register extends ObjectiveEvent {

        /** Title of the objective */
        @NonNull
        private TextHolder title;

        /** Health display of the objective */
        @NonNull
        private HealthDisplay healthDisplay;

        /** Default number format for all scores */
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
         * @param   title
         *          Title of the objective
         * @param   healthDisplay
         *          Health display type of the objective
         * @param   numberFormat
         *          Number format of the objective
         */
        public Register(@NonNull Player player, boolean mutable, @NonNull String objectiveName,
                        @NonNull TextHolder title, @NonNull HealthDisplay healthDisplay,
                        @Nullable NumberFormat numberFormat) {
            super(player, mutable, objectiveName);
            this.title = title;
            this.healthDisplay = healthDisplay;
            this.numberFormat = numberFormat;
        }

        /**
         * Sets the title to new value.
         *
         * @param   title
         *          New title
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setTitle(@NonNull TextHolder title) {
            ensureMutable();
            this.title = title;
        }

        /**
         * Sets the health display to new value.
         *
         * @param   healthDisplay
         *          New health display
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setHealthDisplay(@NonNull HealthDisplay healthDisplay) {
            ensureMutable();
            this.healthDisplay = healthDisplay;
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
     * This event is called when an objective is unregistered.
     */
    public static class Unregister extends ObjectiveEvent {

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   mutable
         *          Whether this objective is mutable (proxy) or not (backend)
         * @param   objectiveName
         *          Name of affected objective
         */
        public Unregister(@NonNull Player player, boolean mutable, @NonNull String objectiveName) {
            super(player, mutable, objectiveName);
        }
    }

    /**
     * This event is called when an objective is updated.
     */
    @Getter
    public static class Update extends ObjectiveEvent {

        /** Title of the objective */
        @NonNull
        private TextHolder title;

        /** Health display of the objective */
        @NonNull
        private HealthDisplay healthDisplay;

        /** Default number format for all scores */
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
         * @param   title
         *          Title of the objective
         * @param   healthDisplay
         *          Health display type of the objective
         * @param   numberFormat
         *          Number format of the objective
         */
        public Update(@NonNull Player player, boolean mutable, @NonNull String objectiveName,
                      @NonNull TextHolder title, @NonNull HealthDisplay healthDisplay,
                      @Nullable NumberFormat numberFormat) {
            super(player, mutable, objectiveName);
            this.title = title;
            this.healthDisplay = healthDisplay;
            this.numberFormat = numberFormat;
        }

        /**
         * Sets the title to new value.
         *
         * @param   title
         *          New title
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setTitle(@NonNull TextHolder title) {
            ensureMutable();
            this.title = title;
        }

        /**
         * Sets the health display to new value.
         *
         * @param   healthDisplay
         *          New health display
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setHealthDisplay(@NonNull HealthDisplay healthDisplay) {
            ensureMutable();
            this.healthDisplay = healthDisplay;
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
}
