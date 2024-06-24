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

package com.velocitypowered.proxy.data;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.ScoreboardManager;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.connection.backend.BackendPlaySessionHandler;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.protocol.packet.scoreboard.*;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * This class handles outgoing scoreboard packets, allowing to cancel them.
 * If Scoreboard API gets merged into Velocity, content of this class will be moved to
 * {@link MinecraftSessionHandler} class.
 */
public class PacketHandler {

    /** Server connection field, because it is private */
    private static final Field serverConn;

    static {
        try {
            serverConn = BackendPlaySessionHandler.class.getDeclaredField("serverConn");
            serverConn.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets player from minecraft session handler.
     *
     * @param   handler
     *          Handler to get player from
     * @return  Player this handler belongs to
     */
    @NotNull
    private static Player getPlayer(@NotNull MinecraftSessionHandler handler) {
        try {
            VelocityServerConnection connection = (VelocityServerConnection) serverConn.get(handler);
            return connection.getPlayer();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles outgoing scoreboard packet.
     *
     * @param   handler
     *          Handler that received the packet
     * @param   packet
     *          Received packet
     * @return  {@code true} if packet should be cancelled, {@code false} if not
     */
    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull DisplayObjectivePacket packet) {
        return ((VelocityScoreboardManager)ScoreboardManager.getInstance()).getDownstreamScoreboard(getPlayer(handler)).handle(packet);
    }

    /**
     * Handles outgoing scoreboard packet.
     *
     * @param   handler
     *          Handler that received the packet
     * @param   packet
     *          Received packet
     * @return  {@code true} if packet should be cancelled, {@code false} if not
     */
    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull ObjectivePacket packet) {
        return ((VelocityScoreboardManager)ScoreboardManager.getInstance()).getDownstreamScoreboard(getPlayer(handler)).handle(packet);
    }

    /**
     * Handles outgoing scoreboard packet.
     *
     * @param   handler
     *          Handler that received the packet
     * @param   packet
     *          Received packet
     * @return  {@code true} if packet should be cancelled, {@code false} if not
     */
    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull ScorePacket packet) {
        return ((VelocityScoreboardManager)ScoreboardManager.getInstance()).getDownstreamScoreboard(getPlayer(handler)).handle(packet);
    }

    /**
     * Handles outgoing scoreboard packet.
     *
     * @param   handler
     *          Handler that received the packet
     * @param   packet
     *          Received packet
     * @return  {@code true} if packet should be cancelled, {@code false} if not
     */
    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull ScoreResetPacket packet) {
        return ((VelocityScoreboardManager)ScoreboardManager.getInstance()).getDownstreamScoreboard(getPlayer(handler)).handle(packet);
    }
    /**
     *
     * Handles outgoing scoreboard packet.
     *
     * @param   handler
     *          Handler that received the packet
     * @param   packet
     *          Received packet
     * @return  {@code true} if packet should be cancelled, {@code false} if not
     */
    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull ScoreSetPacket packet) {
        return ((VelocityScoreboardManager)ScoreboardManager.getInstance()).getDownstreamScoreboard(getPlayer(handler)).handle(packet);
    }

    /**
     * Handles outgoing scoreboard packet.
     *
     * @param   handler
     *          Handler that received the packet
     * @param   packet
     *          Received packet
     * @return  {@code true} if packet should be cancelled, {@code false} if not
     */
    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull TeamPacket packet) {
        return ((VelocityScoreboardManager)ScoreboardManager.getInstance()).getDownstreamScoreboard(getPlayer(handler)).handle(packet);
    }
}
