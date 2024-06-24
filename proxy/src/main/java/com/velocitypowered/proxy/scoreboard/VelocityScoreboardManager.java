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

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.ScoreboardManager;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of ScoreboardManager, an entry point for Scoreboard API.
 */
public class VelocityScoreboardManager extends ScoreboardManager {

    private final Map<ConnectedPlayer, DownstreamScoreboard> downstreamScoreboards = new ConcurrentHashMap<>();
    private final Map<ConnectedPlayer, VelocityScoreboard> proxyScoreboards = new ConcurrentHashMap<>();

    @Override
    @NotNull
    public VelocityScoreboard getScoreboard(@NotNull Player player) {
        return proxyScoreboards.computeIfAbsent((ConnectedPlayer) player, p -> new VelocityScoreboard(p, getDownstreamScoreboard(p)));
    }

    @NotNull
    public DownstreamScoreboard getDownstreamScoreboard(@NotNull Player player) {
        return downstreamScoreboards.computeIfAbsent((ConnectedPlayer) player, DownstreamScoreboard::new);
    }
}
