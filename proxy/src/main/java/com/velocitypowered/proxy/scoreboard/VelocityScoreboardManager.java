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

package com.velocitypowered.proxy.scoreboard;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scoreboard.Scoreboard;
import com.velocitypowered.api.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of ScoreboardManager, an entry point for Scoreboard API.
 */
public class VelocityScoreboardManager extends ScoreboardManager {

    /** Reference to server */
    private final ProxyServer server;

    /**
     * Constructs new instance with given server reference
     *
     * @param   server
     *          Velocity server reference
     */
    public VelocityScoreboardManager(@NotNull ProxyServer server) {
        this.server = server;
    }

    @Override
    @NotNull
    public Scoreboard getNewScoreboard(int priority, @NotNull Object plugin) {
        if (priority < 0) throw new IllegalArgumentException("Priority cannot be negative");
        if (priority == 0) throw new IllegalArgumentException("Priority 0 is reserved for downstream packets");
        return new VelocityScoreboard(priority, server, plugin);
    }
}
