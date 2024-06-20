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
import com.velocitypowered.api.scoreboard.DisplaySlot;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.PacketHandler;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Display objective packet for assigning slot to objectives.
 */
public class DisplayObjectivePacket implements MinecraftPacket {

    /** Packet priority (higher value = higher priority) */
    private final int packetPriority;

    /** Display slot */
    private DisplaySlot position;

    /** Name of this objective (up to 16 characters) */
    private String objectiveName;

    /**
     * Constructs new instance for packet decoding.
     */
    public DisplayObjectivePacket() {
        this.packetPriority = 0;
    }

    /**
     * Constructs new instance for packet sending.
     *
     * @param   packetPriority
     *          Priority of this packet
     * @param   position
     *          Display slot
     * @param   objectiveName
     *          Objective name
     */
    public DisplayObjectivePacket(int packetPriority, @NotNull DisplaySlot position, @NotNull String objectiveName) {
        this.packetPriority = packetPriority;
        this.position = position;
        this.objectiveName = objectiveName;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_2)) {
            position = DisplaySlot.values()[ProtocolUtils.readVarInt(buf)]; //TODO something to prevent new array creation each time?
        } else {
            position = DisplaySlot.values()[buf.readByte()]; //TODO something to prevent new array creation each time?
        }
        objectiveName = ProtocolUtils.readString(buf);
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_2)) {
            ProtocolUtils.writeVarInt(buf, position.ordinal());
        } else {
            buf.writeByte(position.ordinal());
        }
        ProtocolUtils.writeString(buf, objectiveName);
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return PacketHandler.handle(minecraftSessionHandler, this);
    }

    public int getPacketPriority() {
        return packetPriority;
    }

    @NotNull
    public DisplaySlot getPosition() {
        return position;
    }

    @NotNull
    public String getObjectiveName() {
        return objectiveName;
    }
}