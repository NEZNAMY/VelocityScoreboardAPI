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
 * An abstract event class for all scoreboard-related events.
 */
public abstract class ScoreboardEvent {

    /** Player who received the scoreboard change */
    @NotNull
    private final Player player;

    /** Scoreboard priority (0 = backend, higher = proxy plugins) */
    private final int priority;

    /**
     * Constructs new instance with given player.
     *
     * @param   player
     *          Player who received the change
     * @param   priority
     *          Scoreboard priority
     */
    protected ScoreboardEvent(@NotNull Player player, int priority) {
        this.player = player;
        this.priority = priority;
    }

    /**
     * Returns player who received the scoreboard change.
     *
     * @return  Player who received the scoreboard change
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns scoreboard priority. 0 is for backend scoreboard, higher values for proxy plugins.
     *
     * @return  Scoreboard priority
     */
    public int getPriority() {
        return priority;
    }
}
