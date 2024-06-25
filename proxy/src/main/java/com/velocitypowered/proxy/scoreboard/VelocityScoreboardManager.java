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

import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scoreboard.ScoreboardManager;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.data.TextHolderProviderImpl;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of ScoreboardManager, an entry point for Scoreboard API.
 */
public class VelocityScoreboardManager extends ScoreboardManager {

    private final ProxyServer server;
    private final Object plugin;
    private final Map<UUID, DownstreamScoreboard> downstreamScoreboards = new ConcurrentHashMap<>();
    private final Map<UUID, VelocityScoreboard> proxyScoreboards = new ConcurrentHashMap<>();
    private final TextHolderProviderImpl textHolderProvider = new TextHolderProviderImpl();

    /**
     * Constructs new instance with given parameters.
     *
     * @param server Server to call events to
     * @param plugin Scoreboard API plugin
     */
    public VelocityScoreboardManager(@NotNull ProxyServer server, @NotNull Object plugin) {
        this.server = server;
        this.plugin = plugin;
        this.registerEvents();
    }

    /**
     * Registers the event listeners for connecting and disconnecting players.
     */
    private void registerEvents() {
        server.getEventManager().register(plugin, DisconnectEvent.class, event -> {
            downstreamScoreboards.remove(event.getPlayer().getUniqueId());
            proxyScoreboards.remove(event.getPlayer().getUniqueId());
        });
        server.getScheduler().buildTask(plugin, textHolderProvider::clearCache)
                .repeat(30, TimeUnit.SECONDS)
                .schedule();
    }

    @Override
    @NotNull
    public VelocityScoreboard getProxyScoreboard(@NotNull Player player) {
        return proxyScoreboards.computeIfAbsent(player.getUniqueId(), p -> new VelocityScoreboard(server, (ConnectedPlayer) player, getBackendScoreboard(player)));
    }

    @NotNull
    public DownstreamScoreboard getBackendScoreboard(@NotNull Player player) {
        return downstreamScoreboards.computeIfAbsent(player.getUniqueId(), p -> new DownstreamScoreboard(server, player));
    }
}
