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

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.ScoreboardManager;
import com.velocitypowered.proxy.protocol.packet.JoinGamePacket;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboard;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboardManager;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScoreboard;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.jetbrains.annotations.NotNull;

/**
 * Channel injection to listen to JoinGame packet. If Scoreboard API gets merged into Velocity,
 * this will be replaced with a line in JoinGame packet handler.
 */
public class ChannelInjection extends ChannelDuplexHandler {

    private final VelocityScoreboardAPI plugin;
    private final DownstreamScoreboard downstreamScoreboard;
    private final VelocityScoreboard proxyScoreboard;

    /**
     * Constructs new instance with given parameters.
     *
     * @param   player
     *          Player to inject
     * @param   plugin
     *          Plugin instance
     */
    public ChannelInjection(@NotNull Player player, @NotNull VelocityScoreboardAPI plugin) {
        this.plugin = plugin;
        downstreamScoreboard = ((VelocityScoreboardManager) ScoreboardManager.getInstance()).getBackendScoreboard(player);
        proxyScoreboard = ((VelocityScoreboardManager) ScoreboardManager.getInstance()).getProxyScoreboard(player);
    }

    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
        super.write(context, packet, channelPromise);
        if (packet instanceof JoinGamePacket) {
            downstreamScoreboard.clear();
            proxyScoreboard.freeze();
            plugin.getServer().getScheduler().buildTask(plugin, proxyScoreboard::resend).schedule();
        }
    }
}