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

import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.scoreboard.HealthDisplay;
import com.velocitypowered.api.scoreboard.NumberFormat;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.data.NumberFormatEncoder;
import com.velocitypowered.proxy.data.PacketHandler;
import com.velocitypowered.proxy.data.TextHolderImpl;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Scoreboard objective packet.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ObjectivePacket implements MinecraftPacket {

    /** Cached array to prevent new array instantiation on each .values() call */
    private static final HealthDisplay[] DISPLAYS = HealthDisplay.values();

    /** Packet action */
    private ObjectiveAction action;

    /**Name of this objective (up to 16 characters) */
    private String objectiveName;

    /** Objective title */
    private TextHolder title;

    /** Health display for 1.8+ */
    private HealthDisplay healthDisplay;

    /** Default number format for all scores in this objective (1.20.3+) */
    private NumberFormat numberFormat;

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        objectiveName = ProtocolUtils.readString(buf);
        if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
            title = TextHolder.of(ProtocolUtils.readString(buf));
            action = ObjectiveAction.byId(buf.readByte());
            healthDisplay = HealthDisplay.INTEGER; // To avoid NPE in processing
            return;
        }
        action = ObjectiveAction.byId(buf.readByte());
        if (action == ObjectiveAction.REGISTER || action == ObjectiveAction.UPDATE) {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                title = new TextHolderImpl(ComponentHolder.read(buf, protocolVersion));
                healthDisplay = DISPLAYS[ProtocolUtils.readVarInt(buf)];
            } else {
                title = TextHolder.of(ProtocolUtils.readString(buf));
                try {
                    healthDisplay = HealthDisplay.valueOf(ProtocolUtils.readString(buf).toUpperCase(Locale.US));
                } catch (IllegalArgumentException e) {
                    // Bad plugin using ProtocolLib to incorrectly write enum string
                    healthDisplay = HealthDisplay.INTEGER;
                }
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
                if (buf.readBoolean()) {
                    numberFormat = NumberFormatEncoder.read(buf, protocolVersion);
                }
            }
        }
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, objectiveName);
        if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
            ProtocolUtils.writeString(buf, title.getLegacyText(32));
            buf.writeByte(action.ordinal());
            return;
        }
        buf.writeByte(action.ordinal());
        if (action == ObjectiveAction.REGISTER || action == ObjectiveAction.UPDATE) {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                ((TextHolderImpl)title).getHolder(protocolVersion).write(buf);
                ProtocolUtils.writeVarInt(buf, healthDisplay.ordinal());
            } else {
                ProtocolUtils.writeString(buf, title.getLegacyText(32));
                ProtocolUtils.writeString(buf, healthDisplay.toString());
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
                buf.writeBoolean(numberFormat != null);
                if (numberFormat != null) {
                    NumberFormatEncoder.write(buf, protocolVersion, numberFormat);
                }
            }
        }
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return PacketHandler.handle(minecraftSessionHandler, this);
    }

    /**
     * Enum for objective packet action.
     */
    public enum ObjectiveAction {

        /**
         * Registers the objective
         */
        REGISTER,

        /**
         * Unregisters the objective
         */
        UNREGISTER,

        /**
         * Updates objective properties
         */
        UPDATE;

        private static final ObjectiveAction[] values = values();

        /**
         * Returns action by ID
         *
         * @param id Action ID
         * @return Action by ID
         */
        @NotNull
        public static ObjectiveAction byId(int id) {
            return values[id];
        }
    }
}
