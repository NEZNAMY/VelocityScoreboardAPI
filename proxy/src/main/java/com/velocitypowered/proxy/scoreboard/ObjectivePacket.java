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

package com.velocitypowered.proxy.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.scoreboard.HealthDisplay;
import com.velocitypowered.api.scoreboard.NumberFormat;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.NumberFormatProvider;
import com.velocitypowered.proxy.protocol.packet.PacketHandler;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Scoreboard objective packet.
 */
public class ObjectivePacket implements MinecraftPacket {

    /**
     * Packet priority (higher value = higher priority)
     */
    private final int packetPriority;

    /**
     * Packet action
     */
    private ObjectiveAction action;

    /**
     * Name of this objective (up to 16 characters)
     */
    private String objectiveName;

    /**
     * Up to 32 character long title for <1.13 players
     */
    private String titleLegacy;

    /**
     * Title for 1.13+ players
     */
    private ComponentHolder titleModern;

    /**
     * Health display for 1.8+
     */
    private HealthDisplay healthDisplay;

    /**
     * Default number format for all scores in this objective (1.20.3+)
     */
    private NumberFormat numberFormat;

    /**
     * Constructs new instance for packet decoding.
     */
    public ObjectivePacket() {
        this.packetPriority = 0;
    }

    /**
     * Constructs new instance for packet sending.
     *
     * @param packetPriority Packet priority
     * @param action         Packet action
     * @param objectiveName  Objective name
     * @param titleLegacy    Objective title for 1.13 and under players
     * @param titleModern    Objective title for 1.13+ players
     * @param healthDisplay  Health display for 1.8+ players
     * @param numberFormat   Default number format for all scores in this objective (1.20.3+)
     */
    public ObjectivePacket(int packetPriority, @NotNull ObjectiveAction action, @NotNull String objectiveName, @Nullable String titleLegacy,
                           @Nullable ComponentHolder titleModern, @NotNull HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat) {
        this.packetPriority = packetPriority;
        this.action = action;
        this.objectiveName = objectiveName;
        this.titleLegacy = titleLegacy;
        this.titleModern = titleModern;
        this.healthDisplay = healthDisplay;
        this.numberFormat = numberFormat;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        objectiveName = ProtocolUtils.readString(buf);
        if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
            titleLegacy = ProtocolUtils.readString(buf);
        }
        action = ObjectiveAction.byId(buf.readByte());
        if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) return;
        if (action == ObjectiveAction.REGISTER || action == ObjectiveAction.UPDATE) {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                titleModern = ComponentHolder.read(buf, protocolVersion);
                healthDisplay = HealthDisplay.values()[ProtocolUtils.readVarInt(buf)];
            } else {
                titleLegacy = ProtocolUtils.readString(buf);
                healthDisplay = HealthDisplay.fromString(ProtocolUtils.readString(buf));
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
                if (buf.readBoolean()) {
                    numberFormat = NumberFormatProvider.read(buf, protocolVersion);
                }
            }
        }
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, objectiveName);
        if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
            ProtocolUtils.writeString(buf, titleLegacy);
            buf.writeByte(action.ordinal());
            return;
        }
        buf.writeByte(action.ordinal());
        if (action == ObjectiveAction.REGISTER || action == ObjectiveAction.UPDATE) {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                titleModern.write(buf);
                ProtocolUtils.writeVarInt(buf, healthDisplay.ordinal());
            } else {
                ProtocolUtils.writeString(buf, titleLegacy);
                ProtocolUtils.writeString(buf, healthDisplay.toString());
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
                buf.writeBoolean(numberFormat != null);
                if (numberFormat != null) {
                    numberFormat.write(buf, protocolVersion);
                }
            }
        }
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return PacketHandler.handle(minecraftSessionHandler, this);
    }

    public int getPacketPriority() {
        return packetPriority;
    }

    @NotNull
    public ObjectiveAction getAction() {
        return action;
    }

    @NotNull
    public String getObjectiveName() {
        return objectiveName;
    }

    @Nullable
    public String getTitleLegacy() {
        return titleLegacy;
    }

    @Nullable
    public ComponentHolder getTitleModern() {
        return titleModern;
    }

    @Nullable
    public HealthDisplay getHealthDisplay() {
        return healthDisplay;
    }

    @Nullable
    public NumberFormat readNumberFormat() {
        return numberFormat;
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
