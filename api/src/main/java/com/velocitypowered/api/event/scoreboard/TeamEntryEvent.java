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
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class for team entry related events.
 */
public abstract class TeamEntryEvent extends TeamEvent {

    /** Affected entry */
    @NotNull
    private final String entry;

    /**
     * Constructs new instance with given parameters.
     *
     * @param   player
     *          Player who received the scoreboard change
     * @param   priority
     *          Scoreboard source priority
     * @param   team
     *          Team where entry was affected
     * @param   entry
     *          Affected entry
     */
    public TeamEntryEvent(@NotNull Player player, int priority, @NotNull String team, @NotNull String entry) {
        super(player, priority, team);
        this.entry = entry;
    }

    /**
     * Returns affected entry.
     *
     * @return  Affected entry
     */
    @NotNull
    public String getEntry() {
        return entry;
    }

    /**
     * This event is fired when an entry is added to a team.
     */
    public static class Add extends TeamEntryEvent {

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   priority
         *          Scoreboard source priority
         * @param   team
         *          Team where entry was added
         * @param   entry
         *          Added entry
         */
        public Add(@NotNull Player player, int priority, @NotNull String team, @NotNull String entry) {
            super(player, priority, team, entry);
        }
    }

    /**
     * This event is fired when an entry is removed from a team.
     */
    public static class Remove extends TeamEntryEvent {

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   priority
         *          Scoreboard source priority
         * @param   team
         *          Team where entry was removed
         * @param   entry
         *          Removed entry
         */
        public Remove(@NotNull Player player, int priority, @NotNull String team, @NotNull String entry) {
            super(player, priority, team, entry);
        }
    }
}
