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
import com.velocitypowered.api.scoreboard.NumberFormat;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.data.NumberFormatEncoder;
import com.velocitypowered.proxy.data.PacketHandler;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Score packet set for 1.20.3+ players.
 */
public class ScoreSetPacket implements MinecraftPacket {

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
    public ScoreSetPacket() {
    }

    /**
     * Constructs new instance for packet sending.
     *
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
    public ScoreSetPacket(@NotNull String scoreHolder, @NotNull String objectiveName,
                          int value, @Nullable ComponentHolder displayName, @Nullable NumberFormat numberFormat) {
        this.scoreHolder = scoreHolder;
        this.objectiveName = objectiveName;
        this.value = value;
        this.displayName = displayName;
        this.numberFormat = numberFormat;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        scoreHolder = ProtocolUtils.readString(buf);
        objectiveName = ProtocolUtils.readString(buf);
        value = ProtocolUtils.readVarInt(buf);
        if (buf.readBoolean()) displayName = ComponentHolder.read(buf, protocolVersion);
        if (buf.readBoolean()) numberFormat = NumberFormatEncoder.read(buf, protocolVersion);
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, scoreHolder);
        ProtocolUtils.writeString(buf, objectiveName);
        ProtocolUtils.writeVarInt(buf, value);
        buf.writeBoolean(displayName != null);
        if (displayName != null) displayName.write(buf);
        buf.writeBoolean(numberFormat != null);
        if (numberFormat != null) NumberFormatEncoder.write(buf, protocolVersion, numberFormat);
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return PacketHandler.handle(minecraftSessionHandler, this);
    }

    /**
     * Return score holder who should be set in the objective.
     *
     * @return  score holder who should be set in the objective
     */
    @NotNull
    public String getScoreHolder() {
        return scoreHolder;
    }

    /**
     * Returns name of objective where holder should be set.
     *
     * @return  name of objective where holder should be set
     */
    @NotNull
    public String getObjectiveName() {
        return objectiveName;
    }

    /**
     * Returns value assigned to score holder.
     *
     * @return  value assigned to score holder
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns custom name for the score holder.
     *
     * @return  custom name for the score holder
     */
    @Nullable
    public ComponentHolder getDisplayName() {
        return displayName;
    }

    /**
     * Returns number format for the score.
     *
     * @return  number format for the score
     */
    @Nullable
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    @Override
    public String toString() {
        return "ScoreSetPacket{holder=" + scoreHolder + ", objective=" + objectiveName + ", value=" + value +
                ", displayName=" + displayName + ", numberFormat=" + numberFormat + "}";
    }
}
