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

import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.scoreboard.CollisionRule;
import com.velocitypowered.api.scoreboard.NameVisibility;
import com.velocitypowered.api.scoreboard.TeamColor;
import com.velocitypowered.proxy.data.TextHolderImpl;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * Class storing properties of a scoreboard team.
 */
@AllArgsConstructor
@Getter
@ToString
public class TeamProperties {

    /** Cached array to prevent new array instantiation on each .values() call */
    private static final TeamColor[] COLORS = TeamColor.values();

    /** Display name of the team (used somewhere in spectator gamemode?) */
    @NotNull
    private TextHolder displayName;

    /** Team prefix */
    @NotNull
    private TextHolder prefix;

    /** Team suffix */
    @NotNull
    private TextHolder suffix;

    /** Nametag visibility for 1.8+ */
    @NotNull
    private NameVisibility nameVisibility = NameVisibility.ALWAYS;

    /** Collision rule for 1.9+ */
    @NotNull
    private CollisionRule collisionRule = CollisionRule.ALWAYS;

    /** Team color for 1.13+ */
    @NotNull
    private TeamColor color = TeamColor.RESET;

    /** Friendly fire between players in the same team */
    private boolean allowFriendlyFire;

    /** Seeing player in the same team as transparent when invisible */
    private boolean canSeeFriendlyInvisibles;

    /**
     * Constructs new instance using given ByteBuf to read data from.
     *
     * @param   buf
     *          Buffer to read data from
     * @param   protocolVersion
     *          Protocol version used to decode the data
     */
    public TeamProperties(@NonNull ByteBuf buf, @NonNull ProtocolVersion protocolVersion) {
        if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_13)) {
            displayName = TextHolder.of(ProtocolUtils.readString(buf));
            prefix = TextHolder.of((ProtocolUtils.readString(buf)));
            suffix = TextHolder.of((ProtocolUtils.readString(buf)));
        } else {
            displayName = new TextHolderImpl(ComponentHolder.read(buf, protocolVersion));
        }
        byte flags = buf.readByte();
        allowFriendlyFire = (flags & 0x01) > 0;
        canSeeFriendlyInvisibles = (flags & 0x02) > 0;
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_21_5)) {
            nameVisibility = NameVisibility.values()[ProtocolUtils.readVarInt(buf)];
            collisionRule = CollisionRule.values()[ProtocolUtils.readVarInt(buf)];
        } else {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                nameVisibility = NameVisibility.getByName(ProtocolUtils.readString(buf));
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_9)) {
                collisionRule = CollisionRule.getByName(ProtocolUtils.readString(buf));
            }
        }
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
            color = COLORS[ProtocolUtils.readVarInt(buf)];
            prefix = new TextHolderImpl(ComponentHolder.read(buf, protocolVersion));
            suffix = new TextHolderImpl(ComponentHolder.read(buf, protocolVersion));
        } else if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
            int value = buf.readByte();
            color = value == -1 ? TeamColor.RESET : COLORS[value];
        }
    }

    /**
     * Encodes these properties into byte buffer.
     *
     * @param   buf
     *          Buffer to write to
     * @param   protocolVersion
     *          Protocol version used to encode data
     */
    public void encode(@NonNull ByteBuf buf, @NonNull ProtocolVersion protocolVersion) {
        if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_13)) {
            ProtocolUtils.writeString(buf, displayName.getLegacyText(16));
            ProtocolUtils.writeString(buf, prefix.getLegacyText(16));
            ProtocolUtils.writeString(buf, suffix.getLegacyText(16));
        } else {
            ((TextHolderImpl)displayName).getHolder(protocolVersion).write(buf);
        }
        byte flags = 0;
        if (allowFriendlyFire) flags += 0x01;
        if (canSeeFriendlyInvisibles) flags += 0x02;
        buf.writeByte(flags);
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_21_5)) {
            ProtocolUtils.writeVarInt(buf, nameVisibility.ordinal());
            ProtocolUtils.writeVarInt(buf, collisionRule.ordinal());
        } else {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                ProtocolUtils.writeString(buf, nameVisibility.toString());
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_9)) {
                ProtocolUtils.writeString(buf, collisionRule.toString());
            }
        }
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
            ProtocolUtils.writeVarInt(buf, color.ordinal());
            ((TextHolderImpl)prefix).getHolder(protocolVersion).write(buf);
            ((TextHolderImpl)suffix).getHolder(protocolVersion).write(buf);
        } else if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
            buf.writeByte(0); // 1.8 - 1.12 does not actually use this field, non-zero values crash the client
            // buf.writeByte(color.ordinal());
        }
    }

    /**
     * Sets display name and returns {@code true} if value changed, {@code false} if not.
     *
     * @param   displayName
     *          New display name to use
     * @return  {@code true} if value changed, {@code false} if not
     */
    public boolean setDisplayName(@NonNull TextHolder displayName) {
        if (this.displayName == displayName) return false;
        this.displayName = displayName;
        return true;
    }

    /**
     * Sets prefix and returns {@code true} if value changed, {@code false} if not.
     *
     * @param   prefix
     *          New prefix to use
     * @return  {@code true} if value changed, {@code false} if not
     */
    public boolean setPrefix(@NonNull TextHolder prefix) {
        if (this.prefix == prefix) return false;
        this.prefix = prefix;
        return true;
    }

    /**
     * Sets suffix and returns {@code true} if value changed, {@code false} if not.
     *
     * @param   suffix
     *          New suffix to use
     * @return  {@code true} if value changed, {@code false} if not
     */
    public boolean setSuffix(@NonNull TextHolder suffix) {
        if (this.suffix == suffix) return false;
        this.suffix = suffix;
        return true;
    }

    /**
     * Sets nametag visibility and returns {@code true} if value changed, {@code false} if not.
     *
     * @param   nameVisibility
     *          New visibility to use
     * @return  {@code true} if value changed, {@code false} if not
     */
    public boolean setNameVisibility(@NonNull NameVisibility nameVisibility) {
        if (this.nameVisibility == nameVisibility) return false;
        this.nameVisibility = nameVisibility;
        return true;
    }

    /**
     * Sets collision rule and returns {@code true} if value changed, {@code false} if not.
     *
     * @param   collisionRule
     *          New collision rule to use
     * @return  {@code true} if value changed, {@code false} if not
     */
    public boolean setCollisionRule(@NonNull CollisionRule collisionRule) {
        if (this.collisionRule == collisionRule) return false;
        this.collisionRule = collisionRule;
        return true;
    }

    /**
     * Sets team color and returns {@code true} if value changed, {@code false} if not.
     *
     * @param   color
     *          New team color to use
     * @return  {@code true} if value changed, {@code false} if not
     */
    public boolean setColor(@NonNull TeamColor color) {
        if (this.color == color) return false;
        this.color = color;
        return true;
    }

    /**
     * Sets friendly fire flag and returns {@code true} if value changed, {@code false} if not.
     *
     * @param   allowFriendlyFire
     *          New flag value
     * @return  {@code true} if value changed, {@code false} if not
     */
    public boolean setAllowFriendlyFire(boolean allowFriendlyFire) {
        if (this.allowFriendlyFire == allowFriendlyFire) return false;
        this.allowFriendlyFire = allowFriendlyFire;
        return true;
    }

    /**
     * Sets can see friendly invisibles flag and returns {@code true} if value changed, {@code false} if not.
     *
     * @param   canSeeFriendlyInvisibles
     *          New flag value
     * @return  {@code true} if value changed, {@code false} if not
     */
    public boolean setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        if (this.canSeeFriendlyInvisibles == canSeeFriendlyInvisibles) return false;
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        return true;
    }
}
