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

package com.velocitypowered.scoreboardapi;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.configuration.PlayerEnterConfigurationEvent;
import com.velocitypowered.api.event.player.configuration.PlayerFinishConfigurationEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboard;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboardManager;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScoreboard;
import org.jetbrains.annotations.NotNull;

/**
 * Class for listening to events to properly resend scoreboard on server switch.
 */
public class EventListener {

    /** Plugin instance */
    private final VelocityScoreboardAPI plugin;

    /**
     * Constructs new instance.
     *
     * @param   plugin
     *          Plugin instance
     */
    public EventListener(@NotNull VelocityScoreboardAPI plugin) {
        this.plugin = plugin;
    }

    /**
     * Injects custom channel duplex handler to listen to JoinGame packet for players below 1.20.5.
     *
     * @param e Login event
     */
    @Subscribe
    public void onJoin(PostLoginEvent e) {
        if (e.getPlayer().getProtocolVersion().lessThan(ProtocolVersion.MINECRAFT_1_20_5)) {
            ((ConnectedPlayer) e.getPlayer()).getConnection().getChannel().pipeline().addBefore(
                    "handler", "VelocityScoreboardAPI", new ChannelInjection(e.getPlayer(), plugin)
            );
        }
    }

    /**
     * Listens to configuration event start for 1.20.2+ to freeze scoreboard as the client
     * is about to reset it.
     *
     * @param   e
     *          Configuration start event
     */
    @Subscribe
    public void onConfigStart(@NotNull PlayerEnterConfigurationEvent e) {
        ((DownstreamScoreboard) VelocityScoreboardManager.getInstance().getBackendScoreboard(e.player())).clear();
        ((VelocityScoreboard) VelocityScoreboardManager.getInstance().getProxyScoreboard(e.player())).freeze();
    }

    /**
     * Listens to configuration event finish for 1.20.5+ to unfreeze scoreboard and resend it
     * because the client has just reset it.
     *
     * @param   e
     *          Configuration finish event
     */
    @Subscribe
    public void onConfigFinish(@NotNull PlayerFinishConfigurationEvent e) {
        if (e.player().getProtocolVersion().lessThan(ProtocolVersion.MINECRAFT_1_20_5)) return;
        ((VelocityScoreboard) VelocityScoreboardManager.getInstance().getProxyScoreboard(e.player())).resend();
    }
}
