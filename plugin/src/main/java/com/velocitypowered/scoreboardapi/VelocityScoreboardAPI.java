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
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scoreboard.*;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.data.DataHolder;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboard;
import org.jetbrains.annotations.NotNull;

/**
 * TODO:
 *  - Fix StyledFormat to actually read/write properly
 *  - Implement packet handler to properly distribute packets based on priorities
 *  - Easier setScore API function
 *  - Team#updateProperties to avoid sending up to 7 packets if only 1 can be sent
 *  - Fix ComponentHolder usage in API module?
 *  - Decide on priorities (int or jut boolean - proxy or not)
 *  - Finish Javadocs
 *  - Clone team entries on creation and in getters to prevent random modifications
 *  - Better logging than System.out.println and error throwing
 */
public class VelocityScoreboardAPI {

    /**
     * Constructs new instance with given server.
     *
     * @param   server
     *          Server instance
     * @throws  Exception
     *          If thrown during packer registration
     */
    @Inject
    public VelocityScoreboardAPI(@NotNull ProxyServer server) throws Exception {
        try {
            if (ProtocolVersion.MAXIMUM_VERSION != VelocityScoreboard.MAXIMUM_SUPPORTED_VERSION) {
                System.out.println("[VelocityScoreboardAPI] ---------------------------------------------------------------------------------------------------");
                System.out.println("[VelocityScoreboardAPI] Your Velocity build supports MC version " + ProtocolVersion.MAXIMUM_VERSION +
                        ", but this plugin only supports up to " + VelocityScoreboard.MAXIMUM_SUPPORTED_VERSION + ".");
                System.out.println("[VelocityScoreboardAPI] Plugin will be disabled for players with unsupported versions to avoid risk.");
                System.out.println("[VelocityScoreboardAPI] ---------------------------------------------------------------------------------------------------");
            }
        } catch (NoSuchFieldError e) {
            throw new IllegalStateException("The plugin requires a newer velocity build that supports MC 1.21.");
        }
        PacketRegistry.registerPackets(VelocityScoreboard.MAXIMUM_SUPPORTED_VERSION);
        ScoreboardManager.registerApi(server);
        System.out.println("[VelocityScoreboardAPI] Successfully injected Scoreboard API.");
    }

    /**
     * Injects custom channel duplex handler to listen to JoinGame packet.
     *
     * @param   e
     *          Login event
     */
    @Subscribe
    public void onJoin(PostLoginEvent e) {
        ((ConnectedPlayer)e.getPlayer()).getConnection().getChannel().pipeline().addBefore("handler", "VelocityPacketAPI", new ChannelInjection(e.getPlayer()));
    }

    /**
     * Removes player from scoreboards and map of scoreboards.
     *
     * @param   e
     *          Disconnect event
     */
    @Subscribe
    public void onQuit(DisconnectEvent e) {
        DataHolder.removeScoreboardManager(e.getPlayer());
    }
}
