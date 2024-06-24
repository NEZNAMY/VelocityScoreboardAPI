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

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scoreboard.ScoreboardManager;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.data.LoggerManager;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboard;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.event.Level;

/**
 * TODO:
 *  - Easier setScore API function
 */
public class VelocityScoreboardAPI {

    /**
     * Constructs new instance with given parameter, injects packets and enables API.
     *
     * @param   server
     *          Proxy server
     */
    @Inject
    public VelocityScoreboardAPI(@NotNull ProxyServer server) {
        try {
            if (ProtocolVersion.MAXIMUM_VERSION != VelocityScoreboard.MAXIMUM_SUPPORTED_VERSION) {
                LoggerManager.log(Level.ERROR,"<red>" + "-".repeat(100));
                LoggerManager.log(Level.ERROR,"<red>Your Velocity build supports MC version " + ProtocolVersion.MAXIMUM_VERSION +
                        ", but this plugin only supports up to " + VelocityScoreboard.MAXIMUM_SUPPORTED_VERSION + ".");
                LoggerManager.log(Level.ERROR,"<red>Plugin will be disabled for players with unsupported versions to avoid risk.");
                LoggerManager.log(Level.ERROR,"<red>" + "-".repeat(100));
            }
        } catch (NoSuchFieldError e) {
            LoggerManager.log(Level.ERROR,"<red>" + "-".repeat(100));
            LoggerManager.log(Level.ERROR,"<red>The plugin requires a newer velocity build that supports MC 1.21.");
            LoggerManager.log(Level.ERROR,"<red>" + "-".repeat(100));
            return;
        }

        try {
            PacketRegistry.registerPackets(VelocityScoreboard.MAXIMUM_SUPPORTED_VERSION);
        } catch (Throwable e) {
            LoggerManager.log(Level.ERROR,"<red>" + "-".repeat(100));
            LoggerManager.log(Level.ERROR,"<red>An error occurred while registering packets.");
            LoggerManager.log(Level.ERROR,"<red>" + "-".repeat(100));
            return;
        }

        ScoreboardManager.setInstance(new VelocityScoreboardManager(server));
        LoggerManager.log(Level.INFO,"<green>Successfully injected Scoreboard API.");
    }

    /**
     * Injects custom channel duplex handler to listen to JoinGame packet.
     *
     * @param e Login event
     */
    @Subscribe
    public void onJoin(PostLoginEvent e) {
        ((ConnectedPlayer) e.getPlayer()).getConnection().getChannel().pipeline().addBefore("handler", "VelocityPacketAPI", new ChannelInjection(e.getPlayer()));
    }
}
