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

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScoreboard;
import org.jetbrains.annotations.NotNull;

public class ScoreboardManager {

    @NotNull private final Player player;
    @NotNull private final DownstreamScoreboard downstreamScoreboard;
    @NotNull private final Multimap<Object, VelocityScoreboard> pluginScoreboards = Multimaps.newSetMultimap(Maps.newConcurrentMap(), Sets::newConcurrentHashSet);

    public ScoreboardManager(@NotNull Player player) {
        this.player = player;
        downstreamScoreboard = new DownstreamScoreboard(player);
    }

    @NotNull
    public DownstreamScoreboard getDownstreamScoreboard() {
        return downstreamScoreboard;
    }

    public void registerScoreboard(@NotNull VelocityScoreboard scoreboard) {
        if (!pluginScoreboards.put(scoreboard.holder(), scoreboard)) throw new IllegalStateException("The player is already in this scoreboard");
    }

    public void unregisterScoreboard(@NotNull VelocityScoreboard scoreboard) {
        if (!pluginScoreboards.remove(scoreboard.holder(), scoreboard)) throw new IllegalStateException("The player is not in this scoreboard");
    }

    public void handleDisconnect() {
        for (VelocityScoreboard scoreboard : pluginScoreboards.values()) {
            scoreboard.getPlayers().remove((ConnectedPlayer) player);
        }
        pluginScoreboards.clear();
    }
}
