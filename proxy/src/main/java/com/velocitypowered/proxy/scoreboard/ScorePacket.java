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
import com.velocitypowered.api.scoreboard.NumberFormat;
import com.velocitypowered.plugin.PacketHandler;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.FormatReader;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Packet for setting scores in objectives.
 */
public class ScorePacket implements MinecraftPacket {

    /** Packet priority (higher value = higher priority) */
    private final int packetPriority;

    /** Packet action (0 = set, 1 = remove) */
    private ScoreAction action;

    /** Score holder who the score belongs to */
    private String scoreHolder;

    /** Objective from which the holder should be removed (null for all objectives ?) */
    private String objectiveName;

    /** Score value */
    private int value;

    /** Display name to use for score holder instead of name (1.20.3+) */
    @Nullable
    private ComponentHolder displayName;

    /** Number format of the score, null to use default number format from objective (1.20.3+) */
    @Nullable
    private NumberFormat numberFormat;

    /**
     * Constructs new instance for packet decoding.
     */
    public ScorePacket() {
        this.packetPriority = 0;
    }

    /**
     * Constructs new instance for packet sending.
     *
     * @param   packetPriority
     *          Packet priority
     * @param   action
     *          Packet action
     * @param   scoreHolder
     *          Score holder
     * @param   objectiveName
     *          Objective name
     * @param   value
     *          Score value
     * @param   displayName
     *          Holder's display name (1.20.3+)
     * @param   numberFormat
     *          Number format of the score (1.20.3+)
     */
    public ScorePacket(int packetPriority, @NotNull ScoreAction action, @NotNull String scoreHolder, @Nullable String objectiveName,
                       int value, @Nullable ComponentHolder displayName, @Nullable NumberFormat numberFormat) {
        this.packetPriority = packetPriority;
        this.action = action;
        this.scoreHolder = scoreHolder;
        this.objectiveName = objectiveName;
        this.value = value;
        this.displayName = displayName;
        this.numberFormat = numberFormat;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
            scoreHolder = ProtocolUtils.readString(buf);
            action = ScoreAction.byId(buf.readByte());
            if (action != ScoreAction.RESET) {
                objectiveName = ProtocolUtils.readString(buf);
                value = buf.readInt();
            }
            return;
        }
        scoreHolder = ProtocolUtils.readString(buf);
        if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
            action = ScoreAction.byId(buf.readByte());
        }
        objectiveName = ProtocolUtils.readString(buf);
        if (action != ScoreAction.RESET) {
            value = ProtocolUtils.readVarInt(buf);
        }
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
            if (buf.readBoolean()) displayName = ComponentHolder.read(buf, protocolVersion);
            if (buf.readBoolean()) numberFormat = FormatReader.read(buf, protocolVersion);
        }
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, scoreHolder);
        if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
            buf.writeByte(action.ordinal());
        }
        if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
            if (action != ScoreAction.RESET) {
                ProtocolUtils.writeString(buf, objectiveName);
                buf.writeInt(value);
            }
            return;
        }
        ProtocolUtils.writeString(buf, objectiveName);
        if (action != ScoreAction.RESET || protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
            ProtocolUtils.writeVarInt(buf, value);
        }
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
            buf.writeBoolean(displayName != null);
            if (displayName != null) displayName.write(buf);
            buf.writeBoolean(numberFormat != null);
            if (numberFormat != null) numberFormat.write(buf, protocolVersion);
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
    public ScoreAction getAction() {
        return action;
    }

    @NotNull
    public String getScoreHolder() {
        return scoreHolder;
    }

    @Nullable
    public String getObjectiveName() {
        return objectiveName;
    }

    public int getValue() {
        return value;
    }

    @Nullable
    public ComponentHolder getDisplayName() {
        return displayName;
    }

    @Nullable
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    /**
     * Enum for score packet action.
     */
    public enum ScoreAction {

        /** Sets score (creates if it does not exist) */
        SET,

        /** Resets score */
        RESET;

        private static final ScoreAction[] values = values();

        /**
         * Returns action by ID
         *
         * @param   id
         *          Action ID
         * @return  Action by ID
         */
        @NotNull
        public static ScoreAction byId(int id) {
            return values[id];
        }
    }
}
