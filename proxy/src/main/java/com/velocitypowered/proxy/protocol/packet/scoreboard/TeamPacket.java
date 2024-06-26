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

package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.data.PacketHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.scoreboard.TeamProperties;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Packet for setting scoreboard teams.
 */
public class TeamPacket implements MinecraftPacket {

    /** Packet action */
    private TeamAction action;

    /** Team name, limited to 16 characters on <1.18 */
    private String name;

    /** Team properties */
    private TeamProperties properties;

    /** Players in this team */
    private Collection<String> entries;

    /**
     * Constructs new instance for packet decoding.
     */
    public TeamPacket() {
    }

    /**
     * Constructs new instance with given parameters.
     *
     * @param   action
     *          Team action
     * @param   name
     *          Team name
     * @param   properties
     *          Team properties
     * @param   entries
     *          Entries in the team
     */
    public TeamPacket(@NotNull TeamAction action, @NotNull String name,
                      @Nullable TeamProperties properties, @Nullable Collection<String> entries) {
        this.action = action;
        this.name = name;
        this.properties = properties;
        this.entries = entries;
    }

    /**
     * Creates a packet for unregistering team.
     *
     * @param   name
     *          Team name
     * @return  Unregister team packet
     */
    public static TeamPacket unregister(@NotNull String name) {
        TeamPacket packet = new TeamPacket();
        packet.name = name;
        packet.action = TeamAction.UNREGISTER;
        return packet;
    }

    /**
     * Creates a packet for adding or removing entry.
     *
     * @param   name
     *          Team name
     * @param   entry
     *          Entry to add or remove
     * @param   add
     *          {@code true} for adding, {@code false} for removing
     * @return  Packet with given parameters
     */
    public static TeamPacket addOrRemovePlayer(@NotNull String name, @NotNull String entry, boolean add) {
        TeamPacket packet = new TeamPacket();
        packet.name = name;
        packet.action = (add ? TeamAction.ADD_PLAYER : TeamAction.REMOVE_PLAYER);
        packet.entries = Collections.singletonList(entry);
        return packet;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        name = ProtocolUtils.readString(buf);
        action = TeamAction.byId(buf.readByte());
        if (action == TeamAction.REGISTER || action == TeamAction.UPDATE) {
            properties = new TeamProperties(buf, protocolVersion);
        }
        if (action == TeamAction.REGISTER || action == TeamAction.ADD_PLAYER || action == TeamAction.REMOVE_PLAYER) {
            int len = protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8) ? ProtocolUtils.readVarInt(buf) : buf.readShort();
            entries = new HashSet<>();
            for (int i = 0; i < len; i++) {
                entries.add(ProtocolUtils.readString(buf));
            }
        }
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, name);
        buf.writeByte(action.ordinal());
        if (action == TeamAction.REGISTER || action == TeamAction.UPDATE) {
            properties.encode(buf, protocolVersion);
        }
        if (action == TeamAction.REGISTER || action == TeamAction.ADD_PLAYER || action == TeamAction.REMOVE_PLAYER) {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                ProtocolUtils.writeVarInt(buf, entries.size());
            } else {
                buf.writeShort(entries.size());
            }
            for (String player : entries) {
                ProtocolUtils.writeString(buf, player);
            }
        }
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return PacketHandler.handle(minecraftSessionHandler, this);
    }

    /**
     * Returns team action.
     *
     * @return  team action
     */
    @NotNull
    public TeamAction getAction() {
        return action;
    }

    /**
     * Returns team name.
     *
     * @return  team name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Returns team properties (only for register and update actions).
     *
     * @return  team properties
     */
    @Nullable
    public TeamProperties getProperties() {
        return properties;
    }

    /**
     * Returns entries in the team (only for register / add player / remove player).
     *
     * @return  entries in the team
     */
    @Nullable
    public Collection<String> getEntries() {
        return entries;
    }

    /**
     * Sets entries in this packet.
     *
     * @param   entries
     *          New entry list
     */
    public void setEntries(@NotNull Collection<String> entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        return "TeamPacket{action=" + action + ", name=" + name + ", properties=" + properties + ", entries=" + entries + "}";
    }

    /**
     * Enum for objective packet action.
     */
    public enum TeamAction {

        /** Registers the objective */
        REGISTER,

        /** Unregisters the objective */
        UNREGISTER,

        /** Updates objective properties */
        UPDATE,

        /** Adds players */
        ADD_PLAYER,

        /** Removes players */
        REMOVE_PLAYER;

        private static final TeamAction[] values = values();

        /**
         * Returns action by ID
         *
         * @param   id
         *          Action ID
         * @return  Action by ID
         */
        @NotNull
        public static TeamAction byId(int id) {
            return values[id];
        }
    }
}