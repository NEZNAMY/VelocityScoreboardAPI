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
import com.velocitypowered.api.scoreboard.ProxyTeam;
import com.velocitypowered.api.scoreboard.Scoreboard;
import com.velocitypowered.api.scoreboard.Team;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class for team-related events.
 */
@Getter
public abstract class TeamEvent extends ScoreboardEvent {

    /**
     * Affected team.
     * If proxy is true, the team is a {@link ProxyTeam}, otherwise it is a backend {@link Team}
     */
    @NotNull
    private final Team team;

    /**
     * Constructs new instance with given parameters.
     *
     * @param   player
     *          Player who received the scoreboard change
     * @param   scoreboard
     *          Scoreboard source
     * @param   team
     *          Name of affected team
     */
    public TeamEvent(@NonNull Player player, @NonNull Scoreboard scoreboard, @NonNull Team team) {
        super(player, scoreboard);
        this.team = team;
    }

    /**
     * This event is called when a team is registered.
     */
    public static class Register extends TeamEvent {

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   scoreboard
         *          Scoreboard source
         * @param   team
         *          Name of affected team
         */
        public Register(@NonNull Player player, @NonNull Scoreboard scoreboard, @NonNull Team team) {
            super(player, scoreboard, team);
        }
    }

    /**
     * This event is called when a team is unregistered.
     */
    public static class Unregister extends TeamEvent {

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   scoreboard
         *          Scoreboard source
         * @param   team
         *          Name of affected team
         */
        public Unregister(@NonNull Player player, @NonNull Scoreboard scoreboard, @NonNull Team team) {
            super(player, scoreboard, team);
        }
    }
}
