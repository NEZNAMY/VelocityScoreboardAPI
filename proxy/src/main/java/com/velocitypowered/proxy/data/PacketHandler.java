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
import com.velocitypowered.api.scoreboard.ProxyObjective;
import com.velocitypowered.api.scoreboard.ProxyTeam;
import com.velocitypowered.api.scoreboard.ScoreboardManager;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.connection.backend.BackendPlaySessionHandler;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.protocol.packet.scoreboard.*;
import com.velocitypowered.proxy.scoreboard.*;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScoreboard;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;

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
    
    private static DownstreamScoreboard getDownstream(@NotNull MinecraftSessionHandler handler) {
        return ((VelocityScoreboardManager)ScoreboardManager.getInstance()).getBackendScoreboard(getPlayer(handler));
    }

    private static VelocityScoreboard getProxy(@NotNull MinecraftSessionHandler handler) {
        return ((VelocityScoreboardManager)ScoreboardManager.getInstance()).getProxyScoreboard(getPlayer(handler));
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
        // Filter out invalid packets
        if (getDownstream(handler).handle(packet)) return true;

        if (getProxy(handler).getObjective(packet.getPosition()) != null) {
            // This slot is occupied by proxy scoreboard, cancel packet
            return true;
        }

        return false;
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
        // Filter out invalid packets
        if (getDownstream(handler).handle(packet)) return true;

        VelocityObjective objective = getProxy(handler).getObjective(packet.getObjectiveName());
        if (objective != null) {
            // Proxy already contains objective with this name, cancel everything
            return true;
        }

        return false;
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
        // Filter out invalid packets
        if (getDownstream(handler).handle(packet)) return true;

        if (packet.getObjectiveName() == null) {
            // Null objective removes from all objectives, add back what was set by proxy
            for (ProxyObjective objective : getProxy(handler).getObjectives()) {
                VelocityScore score = (VelocityScore) objective.getScore(packet.getScoreHolder());
                if (score != null) score.sendUpdate();
            }
            return false;
        } else {
            VelocityObjective objective = getProxy(handler).getObjective(packet.getObjectiveName());
            if (objective != null) {
                // Proxy is occupying this objective, cancel packet
                return true;
            }
        }

        return false;
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
        // Filter out invalid packets
        if (getDownstream(handler).handle(packet)) return true;

        if (packet.getObjectiveName() == null) {
            // Null objective removes from all objectives, add back what was set by proxy
            for (ProxyObjective objective : getProxy(handler).getObjectives()) {
                VelocityScore score = (VelocityScore) objective.getScore(packet.getScoreHolder());
                if (score != null) score.sendUpdate();
            }
            return false;
        } else {
            VelocityObjective objective = getProxy(handler).getObjective(packet.getObjectiveName());
            if (objective != null) {
                // Proxy is occupying this objective, cancel packet
                return true;
            }
        }

        return false;
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
        // Filter out invalid packets
        if (getDownstream(handler).handle(packet)) return true;

        VelocityObjective objective = getProxy(handler).getObjective(packet.getObjectiveName());
        if (objective != null) {
            // Proxy is occupying this objective, cancel packet
            return true;
        }

        return false;
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
        // Filter out invalid packets
        if (getDownstream(handler).handle(packet)) return true;

        VelocityTeam team = getProxy(handler).getTeam(packet.getName());
        if (team != null) {
            // Proxy is occupying this team, cancel packet
            return true;
        } else {
            if (packet.getAction() == TeamPacket.TeamAction.ADD_PLAYER || packet.getAction() == TeamPacket.TeamAction.REMOVE_PLAYER) {
                Collection<String> modifiedEntries = new HashSet<>(packet.getEntries());
                for (ProxyTeam proxyTeam : getProxy(handler).getTeams()) {
                    for (String addedEntry : packet.getEntries()) {
                        if (proxyTeam.getEntries().contains(addedEntry)) {
                            // Proxy team has this player assigned, cancel action
                            modifiedEntries.remove(addedEntry);
                        }
                    }
                }
                packet.setEntries(modifiedEntries);
            }
        }

        return false;
    }
}
