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
import com.velocitypowered.proxy.protocol.packet.JoinGamePacket;
import com.velocitypowered.proxy.scoreboard.DataHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.jetbrains.annotations.NotNull;

public class ChannelInjection extends ChannelDuplexHandler {

    @NotNull
    private final Player player;

    public ChannelInjection(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
        if (packet instanceof JoinGamePacket) {
            DataHolder.getScoreboardManager(player).getDownstreamScoreboard().clear();
        }
        super.write(context, packet, channelPromise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}
