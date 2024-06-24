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
 * Packet for setting scores in objectives for players 1.20.2 and below.
 */
public class ScorePacket implements MinecraftPacket {

    /** Packet action */
    private ScoreAction action;

    /** Score holder who the score belongs to */
    private String scoreHolder;

    /** Objective where the score should be modified */
    private String objectiveName;

    /** Score value */
    private int value;

    /**
     * Constructs new instance for packet decoding.
     */
    public ScorePacket() {
    }

    /**
     * Constructs new instance for packet sending.
     *
     * @param   action
     *          Packet action
     * @param   scoreHolder
     *          Score holder
     * @param   objectiveName
     *          Objective name
     * @param   value
     *          Score value
     */
    public ScorePacket(@NotNull ScoreAction action, @NotNull String scoreHolder,
                       @Nullable String objectiveName, int value) {
        this.action = action;
        this.scoreHolder = scoreHolder;
        this.objectiveName = objectiveName;
        this.value = value;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        scoreHolder = ProtocolUtils.readString(buf);
        action = ScoreAction.byId(buf.readByte());
        if (protocolVersion.greaterThan(ProtocolVersion.MINECRAFT_1_7_6) || action == ScoreAction.SET) {
            objectiveName = ProtocolUtils.readString(buf);
        }
        if (action == ScoreAction.SET) {
            value = protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6) ? buf.readInt() : ProtocolUtils.readVarInt(buf);
        }
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, scoreHolder);
        buf.writeByte(action.ordinal());
        if (protocolVersion.greaterThan(ProtocolVersion.MINECRAFT_1_7_6) || action == ScoreAction.SET) {
            ProtocolUtils.writeString(buf, objectiveName);
        }
        if (action == ScoreAction.SET) {
            if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
                buf.writeInt(value);
            } else {
                ProtocolUtils.writeVarInt(buf, value);
            }
        }
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return PacketHandler.handle(minecraftSessionHandler, this);
    }

    /**
     * Returns packet action.
     *
     * @return  packet action
     */
    @NotNull
    public ScoreAction getAction() {
        return action;
    }

    /**
     * Returns score holder.
     *
     * @return  score holder
     */
    @NotNull
    public String getScoreHolder() {
        return scoreHolder;
    }

    /**
     * Returns objective where this score should be modified.
     *
     * @return  objective where this score should be modified
     */
    @Nullable
    public String getObjectiveName() {
        return objectiveName;
    }

    /**
     * Returns score value.
     *
     * @return  score value
     */
    public int getValue() {
        return value;
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
