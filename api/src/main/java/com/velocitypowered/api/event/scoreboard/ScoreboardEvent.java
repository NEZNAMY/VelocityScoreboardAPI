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
import com.velocitypowered.api.scoreboard.ProxyScoreboard;
import com.velocitypowered.api.scoreboard.Scoreboard;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * An abstract event class for all scoreboard-related events.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ScoreboardEvent {

    /** Player who received the scoreboard change */
    @NotNull
    private final Player player;

    /**
     * Scoreboard source
     * If proxy is true, the scoreboard is a {@link ProxyScoreboard}, otherwise it is a backend {@link Scoreboard}
     */
    private final Scoreboard scoreboard;

    /**
     * Returns if the scoreboard is a {@link ProxyScoreboard} or a backend {@link Scoreboard}.
     *
     * @return  Scoreboard source
     */
    public boolean isProxy() {
        return scoreboard instanceof ProxyScoreboard;
    }
}
