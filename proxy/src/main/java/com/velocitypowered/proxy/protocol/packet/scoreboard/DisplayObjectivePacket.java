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
import com.velocitypowered.proxy.data.PacketHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Display objective packet for assigning slot to objectives.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DisplayObjectivePacket implements MinecraftPacket {

    /** Display slot */
    private DisplaySlot position;

    /** Name of this objective (up to 16 characters) */
    private String objectiveName;

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_2)) {
            position = DisplaySlot.values()[ProtocolUtils.readVarInt(buf)];
        } else {
            position = DisplaySlot.values()[buf.readByte()];
        }
        objectiveName = ProtocolUtils.readString(buf);
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_2)) {
            ProtocolUtils.writeVarInt(buf, position.ordinal());
        } else {
            int ordinal = position.ordinal();
            if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_8) && ordinal > 2) {
                // Sidebars for team colors were added in 1.8, display as normal sidebar
                ordinal = 1;
            }
            buf.writeByte(ordinal);
        }
        ProtocolUtils.writeString(buf, objectiveName);
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return PacketHandler.handle(minecraftSessionHandler, this);
    }
}