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
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.data.PacketHandler;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Score reset packet for 1.20.3+ players.
 */
public class ScoreResetPacket implements MinecraftPacket {

    /** Score holder who the score belongs to */
    private String scoreHolder;

    /** Objective from which the holder should be removed (null for all objectives ?) */
    private String objectiveName;

    /**
     * Constructs new instance for packet decoding.
     */
    public ScoreResetPacket() {
    }

    /**
     * Constructs new instance for packet sending.
     *
     * @param   scoreHolder
     *          Score holder
     * @param   objectiveName
     *          Objective name
     */
    public ScoreResetPacket(@NotNull String scoreHolder, @Nullable String objectiveName) {
        this.scoreHolder = scoreHolder;
        this.objectiveName = objectiveName;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        scoreHolder = ProtocolUtils.readString(buf);
        if (buf.readBoolean()) objectiveName = ProtocolUtils.readString(buf);
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, scoreHolder);
        buf.writeBoolean(objectiveName != null);
        if (objectiveName != null) ProtocolUtils.writeString(buf, objectiveName);
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return PacketHandler.handle(minecraftSessionHandler, this);
    }

    /**
     * Returns score holder who should be removed from objective.
     *
     * @return  score holder who should be removed from objective
     */
    @NotNull
    public String getScoreHolder() {
        return scoreHolder;
    }

    /**
     * Returns objective from which the holder should be removed, null for all objectives.
     *
     * @return  objective from which the holder should be removed
     */
    @Nullable
    public String getObjectiveName() {
        return objectiveName;
    }

    @Override
    public String toString() {
        return "ScoreResetPacket{holder=" + scoreHolder + ", objective=" + objectiveName + "}";
    }
}