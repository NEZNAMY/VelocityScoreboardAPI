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
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.ScoreboardManager;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.JoinGamePacket;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboard;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboardManager;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScoreboard;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

/**
 * This is a class for managing server switching. The client usually resets the scoreboard
 * on server switch, so it needs to be reset. Sadly, it is not that simple.
 * Let's break it down per version - what events are called and what happens and when.
 * 1.20.1-:
 *   - JoinGamePacket is sent, which resets the scoreboard. It can be resent immediately
 *     (but not in Netty thread, so resend has to be scheduled and therefore scoreboard frozen for a moment).
 * 1.20.2 - 1.20.4:
 *   - (on initial connect, PlayerFinishConfigurationEvent is called and JoinGamePacket is sent)
 *   - PlayerEnterConfigurationEvent is called - world is set to null (scoreboard is reset and packets would cause NPE?)
 *   - PlayerFinishConfigurationEvent is called
 *   - JoinGamePacket is sent - new scoreboard is created
 * 1.20.5+:
 *   - (on initial connect, PlayerFinishConfigurationEvent is called and JoinGamePacket is sent)
 *   - PlayerEnterConfigurationEvent is called - world is set to null (scoreboard is reset and packets would cause NPE?)
 *   - PlayerFinishConfigurationEvent is called - new scoreboard is created
 *   - JoinGamePacket is sent - this is where broken clients actually create new scoreboard
 * Additionally, we have a new problem - VeloFlame avoids configuration phase, which causes the
 * scoreboard to not get reset (but only on 1.20.5+, because for 1.20.2 - 1.20.4 scoreboard is reset
 * during JoinGamePacket handling).
 * Let's put this all together to decide on the correct behavior.
 * First, we need to freeze the scoreboard when the client clears it:
 *   - 1.20.1- - Freeze it on JoinGamePacket.
 *   - 1.20.2+ - Freeze it on PlayerEnterConfigurationEvent (not called when configuration phase is disabled).
 * Then, we need to resend the scoreboard after the client creates a new instance:
 *   - 1.20.4- - Resend it on JoinGamePacket.
 *   - 1.20.5+ - Resend it on PlayerFinishConfigurationEvent (not called when configuration phase is disabled).
 */
@RequiredArgsConstructor
public class ServerSwitchManager {

    /** Including, but not limited to Labymod */
    private final boolean COMPENSATE_FOR_BRAIN_DAMAGED_CLIENTS = true; // Velocity-styled variable name :P

    /** Plugin instance */
    private final VelocityScoreboardAPI plugin;

    /**
     * Handles JoinGamePacket by freezing and resending the scoreboard if needed.
     *
     * @param   player
     *          Player who received the JoinGamePacket
     */
    public void onJoinGamePacket(@NotNull Player player) {
        DownstreamScoreboard downstreamScoreboard = ((VelocityScoreboardManager) ScoreboardManager.getInstance()).getBackendScoreboard(player);
        VelocityScoreboard proxyScoreboard = ((VelocityScoreboardManager) ScoreboardManager.getInstance()).getProxyScoreboard(player);
        if (player.getProtocolVersion().lessThan(ProtocolVersion.MINECRAFT_1_20_5)) {
            downstreamScoreboard.clear();
            proxyScoreboard.freeze();
        }
        plugin.getServer().getScheduler().buildTask(plugin, proxyScoreboard::resend).schedule();
    }

    /**
     * Injects custom channel duplex handler to listen to JoinGame packet for players below 1.20.5.
     *
     * @param e Login event
     */
    @Subscribe
    public void onJoin(PostLoginEvent e) {
        if (e.getPlayer().getProtocolVersion().lessThan(ProtocolVersion.MINECRAFT_1_20_5) || COMPENSATE_FOR_BRAIN_DAMAGED_CLIENTS) {
            try {
                ((ConnectedPlayer) e.getPlayer()).getConnection().getChannel().pipeline().addBefore(
                        "handler", "VelocityScoreboardAPI", new ChannelInjection(e.getPlayer())
                );
            } catch (NoSuchElementException ex) {
                // java.util.NoSuchElementException: handler
                // Looks like player left before this event was fired
            }
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
        if (e.player().getProtocolVersion().noLessThan(ProtocolVersion.MINECRAFT_1_20_5) && !COMPENSATE_FOR_BRAIN_DAMAGED_CLIENTS) {
            ((VelocityScoreboard) VelocityScoreboardManager.getInstance().getProxyScoreboard(e.player())).resend();
        }
    }

    /**
     * Channel injection to listen to JoinGame packet.
     */
    @RequiredArgsConstructor
    public class ChannelInjection extends ChannelDuplexHandler {

        private final Player player;

        @Override
        public void write(@NotNull ChannelHandlerContext context, @NotNull Object packet, @NotNull ChannelPromise channelPromise) throws Exception {
            super.write(context, packet, channelPromise);
            if (packet instanceof JoinGamePacket) {
                onJoinGamePacket(player);
            }
        }
    }
}
