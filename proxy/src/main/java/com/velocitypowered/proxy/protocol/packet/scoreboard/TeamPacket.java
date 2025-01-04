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
import com.velocitypowered.proxy.data.StringCollection;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.scoreboard.TeamProperties;
import io.netty.buffer.ByteBuf;
import lombok.*;
import org.jetbrains.annotations.NotNull;

/**
 * Packet for setting scoreboard teams.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TeamPacket implements MinecraftPacket {

    /** Packet action */
    private TeamAction action;

    /** Team name, limited to 16 characters on <1.18 */
    private String name;

    /** Team properties */
    private TeamProperties properties;

    /** Players in this team */
    private StringCollection entries;

    /**
     * Creates a packet for unregistering team.
     *
     * @param   name
     *          Team name
     * @return  Unregister team packet
     */
    public static TeamPacket unregister(@NonNull String name) {
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
    public static TeamPacket addOrRemovePlayer(@NonNull String name, @NonNull String entry, boolean add) {
        TeamPacket packet = new TeamPacket();
        packet.name = name;
        packet.action = (add ? TeamAction.ADD_PLAYER : TeamAction.REMOVE_PLAYER);
        packet.entries = new StringCollection(entry);
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
            entries = new StringCollection(buf, protocolVersion);
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
            entries.write(buf, protocolVersion);
        }
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return PacketHandler.handle(minecraftSessionHandler, this);
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