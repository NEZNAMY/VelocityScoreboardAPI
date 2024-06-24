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
import com.velocitypowered.api.scoreboard.DisplaySlot;
import com.velocitypowered.api.scoreboard.Objective;
import com.velocitypowered.api.scoreboard.ProxyObjective;
import com.velocitypowered.api.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

/**
 * Class for Objective events.
 */
public abstract class ObjectiveEvent extends ScoreboardEvent {

    /** Objective
     * If proxy is true, the objective is a {@link ProxyObjective}, otherwise it is a backend {@link Objective}
     * */
    @NotNull
    private final Objective objective;

    /**
     * Constructs new instance with given parameters.
     *
     * @param   player
     *          Player who received the scoreboard change
     * @param   scoreboard
     *          Scoreboard source
     * @param   objective
     *          Name of affected objective
     */
    public ObjectiveEvent(@NotNull Player player, @NotNull Scoreboard scoreboard, @NotNull Objective objective) {
        super(player, scoreboard);
        this.objective = objective;
    }

    /**
     * Returns effected objective.
     *
     * @return  Effected objective
     */
    @NotNull
    public Objective getObjective() {
        return objective;
    }

    /**
     * This event is called when an objective is assigned a display slot.
     */
    public static class Display extends ObjectiveEvent {

        /** New display slot */
        @NotNull
        private final DisplaySlot newSlot;

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   scoreboard
         *          Scoreboard source
         * @param   objective
         *          Name of affected objective
         * @param   newSlot
         *          New display slot
         */
        public Display(@NotNull Player player, @NotNull Scoreboard scoreboard, @NotNull Objective objective, @NotNull DisplaySlot newSlot) {
            super(player, scoreboard, objective);
            this.newSlot = newSlot;
        }

        /**
         * Returns new display slot for the objective.
         *
         * @return  New display slot for the objective
         */
        @NotNull
        public DisplaySlot getNewSlot() {
            return newSlot;
        }
    }

    /**
     * This event is called when an objective is registered.
     */
    public static class Register extends ObjectiveEvent {

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   scoreboard
         *          Scoreboard source
         * @param   objective
         *          Name of affected objective
         */
        public Register(@NotNull Player player, @NotNull Scoreboard scoreboard, @NotNull Objective objective) {
            super(player, scoreboard, objective);
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
         * @param   scoreboard
         *          Scoreboard source
         * @param   objective
         *          Name of affected objective
         */
        public Unregister(@NotNull Player player, @NotNull Scoreboard scoreboard, @NotNull Objective objective) {
            super(player, scoreboard, objective);
        }
    }

}
