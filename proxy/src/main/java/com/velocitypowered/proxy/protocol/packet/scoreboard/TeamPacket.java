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
import com.velocitypowered.api.scoreboard.CollisionRule;
import com.velocitypowered.api.scoreboard.NameVisibility;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.scoreboard.PacketHandler;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Packet for setting scoreboard teams.
 */
public class TeamPacket implements MinecraftPacket {

    /** Packet priority (higher value = higher priority) */
    private final int packetPriority;

    /** Packet action */
    private TeamAction action;

    /** Team name, limited to 16 characters on <1.18 */
    private String name;

    /** Display name of the team (used somewhere in spectator gamemode?) for <1.13 */
    private String displayNameLegacy;

    /** Display name of the team (used somewhere in spectator gamemode?) for 1.13+ */
    private ComponentHolder displayNameModern;

    /** Team prefix for <1.13 (limited to 16 characters) */
    private String prefixLegacy;

    /** Team prefix for 1.13+ */
    private ComponentHolder prefixModern;

    /** Team suffix for <1.13 (limited to 16 characters) */
    private String suffixLegacy;

    /** Team suffix for 1.13+ */
    private ComponentHolder suffixModern;

    /** Nametag visibility for 1.8+ */
    private NameVisibility nameTagVisibility;

    /** Collision rule for 1.9+ */
    private CollisionRule collisionRule;

    /** Team color enum */
    private int color;

    /**
     * Team options:
     *      0x01 - Allow friendly fire
     *      0x02 - Can see friendly invisibles
     */
    private byte flags;

    /** Players in this team */
    private Collection<String> entries;

    /**
     * Constructs new instance for packet decoding.
     */
    public TeamPacket() {
        packetPriority = 0;
    }

    /**
     * Constructs new instance with given priority.
     *
     * @param   packetPriority
     *          Packet priority
     */
    public TeamPacket(int packetPriority) {
        this.packetPriority = packetPriority;
    }

    public TeamPacket(int packetPriority, @NotNull TeamAction action, @NotNull String name, @Nullable String displayNameLegacy,
                      @Nullable ComponentHolder displayNameModern, @Nullable String prefixLegacy, @Nullable ComponentHolder prefixModern,
                      @Nullable String suffixLegacy, @Nullable ComponentHolder suffixModern, @NotNull NameVisibility nameTagVisibility,
                      @NotNull CollisionRule collisionRule, int color, byte flags, @Nullable Collection<String> entries) {
        this.packetPriority = packetPriority;
        this.action = action;
        this.name = name;
        this.displayNameLegacy = displayNameLegacy;
        this.displayNameModern = displayNameModern;
        this.prefixLegacy = prefixLegacy;
        this.prefixModern = prefixModern;
        this.suffixLegacy = suffixLegacy;
        this.suffixModern = suffixModern;
        this.nameTagVisibility = nameTagVisibility;
        this.collisionRule = collisionRule;
        this.color = color;
        this.flags = flags;
        this.entries = entries;
    }

    /**
     * Creates a packet for unregistering team.
     *
     * @param   priority
     *          Packet priority
     * @param   name
     *          Team name
     * @return  Unregister team packet
     */
    public static TeamPacket unregister(int priority, @NotNull String name) {
        TeamPacket packet = new TeamPacket(priority);
        packet.name = name;
        packet.action = TeamAction.UNREGISTER;
        return packet;
    }

    /**
     * Creates a packet for adding or removing entry.
     *
     * @param   priority
     *          Packet priority
     * @param   name
     *          Team name
     * @param   entry
     *          Entry to add or remove
     * @param   add
     *          {@code true} for adding, {@code false} for removing
     * @return  Packet with given parameters
     */
    public static TeamPacket addOrRemovePlayer(int priority, @NotNull String name, @NotNull String entry, boolean add) {
        TeamPacket packet = new TeamPacket(priority);
        packet.name = name;
        packet.action = (add ? TeamAction.ADD_PLAYER : TeamAction.REMOVE_PLAYER);
        packet.entries = Collections.singletonList(entry);
        return packet;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        name = ProtocolUtils.readString(buf);
        action = TeamAction.byId(buf.readByte());
        if (action == TeamAction.REGISTER || action == TeamAction.UPDATE) {
            if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_13)) {
                displayNameLegacy = ProtocolUtils.readString(buf);
                prefixLegacy = ProtocolUtils.readString(buf);
                suffixLegacy = ProtocolUtils.readString(buf);
            } else {
                displayNameModern = ComponentHolder.read(buf, protocolVersion);
            }
            flags = buf.readByte();
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                nameTagVisibility = NameVisibility.getByName(ProtocolUtils.readString(buf));
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_9)) {
                collisionRule = CollisionRule.getByName(ProtocolUtils.readString(buf));
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                color = ProtocolUtils.readVarInt(buf);
                prefixModern = ComponentHolder.read(buf, protocolVersion);
                suffixModern = ComponentHolder.read(buf, protocolVersion);
            } else if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                color = buf.readByte();
            }
        }
        if (action == TeamAction.REGISTER || action == TeamAction.ADD_PLAYER || action == TeamAction.REMOVE_PLAYER) {
            int len = protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8) ? ProtocolUtils.readVarInt(buf) : buf.readShort();
            entries = new HashSet<>();
            for (int i = 0; i < len; i++) {
                entries.add(ProtocolUtils.readString(buf));
            }
        }
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, name);
        buf.writeByte(action.ordinal());
        if (action == TeamAction.REGISTER || action == TeamAction.UPDATE) {
            if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_13)) {
                ProtocolUtils.writeString(buf, displayNameLegacy);
                ProtocolUtils.writeString(buf, prefixLegacy);
                ProtocolUtils.writeString(buf, suffixLegacy);
            } else {
                displayNameModern.write(buf);
            }
            buf.writeByte(flags);
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                ProtocolUtils.writeString(buf, nameTagVisibility.toString());
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_9)) {
                ProtocolUtils.writeString(buf, collisionRule.toString());
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                ProtocolUtils.writeVarInt(buf, color);
                prefixModern.write(buf);
                suffixModern.write(buf);
            } else if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                buf.writeByte(0); // 1.8 - 1.12 does not actually use this field, non-zero values crash the client
                // buf.writeByte(color);
            }
        }
        if (action == TeamAction.REGISTER || action == TeamAction.ADD_PLAYER || action == TeamAction.REMOVE_PLAYER) {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                ProtocolUtils.writeVarInt(buf, entries.size());
            } else {
                buf.writeShort(entries.size());
            }
            for (String player : entries) {
                ProtocolUtils.writeString(buf, player);
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
    public TeamAction getAction() {
        return action;
    }

    @NotNull
    public String getName() {
        return name;
    }


    @Nullable
    public String getDisplayNameLegacy() {
        return displayNameLegacy;
    }

    @Nullable
    public ComponentHolder getDisplayNameModern() {
        return displayNameModern;
    }

    @Nullable
    public String getPrefixLegacy() {
        return prefixLegacy;
    }

    @Nullable
    public ComponentHolder getPrefixModern() {
        return prefixModern;
    }

    @Nullable
    public String getSuffixLegacy() {
        return suffixLegacy;
    }

    @Nullable
    public ComponentHolder getSuffixModern() {
        return suffixModern;
    }

    @NotNull
    public NameVisibility getNameTagVisibility() {
        return nameTagVisibility;
    }

    @NotNull
    public CollisionRule getCollisionRule() {
        return collisionRule;
    }

    public int getColor() {
        return color;
    }

    public byte getFlags() {
        return flags;
    }

    @Nullable
    public Collection<String> getEntries() {
        return entries;
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