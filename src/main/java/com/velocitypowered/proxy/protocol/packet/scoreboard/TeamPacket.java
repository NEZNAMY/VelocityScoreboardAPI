package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Packet for setting scoreboard teams.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class TeamPacket implements MinecraftPacket {

    /** Packet priority (higher value = higher priority) */
    private final int packetPriority;

    /** Team name, limited to 16 characters on <1.18 */
    private String name;

    /** Packet action (0 = register, 1 = unregister, 2 = update properties, 3 = add player, 4 = remove player) */
    private byte mode;

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
    private String nameTagVisibility;

    /** Collision rule for 1.9+ */
    private String collisionRule;

    /** Team color enum */
    private int color;

    /**
     * Team options:
     *      0x01 - Allow friendly fire
     *      0x02 - Can see friendly invisibles
     */
    private byte flags;

    /** Players in this team */
    private String[] players;

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        name = ProtocolUtils.readString(buf);
        mode = buf.readByte();
        if (mode == 0 || mode == 2) {
            if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_13)) {
                displayNameLegacy = ProtocolUtils.readString(buf);
                prefixLegacy = ProtocolUtils.readString(buf);
                suffixLegacy = ProtocolUtils.readString(buf);
            } else {
                displayNameModern = ComponentHolder.read(buf, protocolVersion);
            }
            flags = buf.readByte();
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                nameTagVisibility = ProtocolUtils.readString(buf);
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_9)) {
                collisionRule = ProtocolUtils.readString(buf);
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                color = ProtocolUtils.readVarInt(buf);
                prefixModern = ComponentHolder.read(buf, protocolVersion);
                suffixModern = ComponentHolder.read(buf, protocolVersion);
            } else if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                color = buf.readByte();
            }
        }
        if (mode == 0 || mode == 3 || mode == 4) {
            int len = protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8) ? ProtocolUtils.readVarInt(buf) : buf.readShort();
            players = new String[len];
            for (int i = 0; i < len; i++) {
                players[i] = ProtocolUtils.readString(buf);
            }
        }
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, name);
        buf.writeByte(mode);
        if (mode == 0 || mode == 2) {
            if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_13)) {
                ProtocolUtils.writeString(buf, displayNameLegacy);
                ProtocolUtils.writeString(buf, prefixLegacy);
                ProtocolUtils.writeString(buf, suffixLegacy);
            } else {
                displayNameModern.write(buf);
            }
            buf.writeByte(flags);
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                ProtocolUtils.writeString(buf, nameTagVisibility);
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_9)) {
                ProtocolUtils.writeString(buf, collisionRule);
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                ProtocolUtils.writeVarInt(buf, color);
                prefixModern.write(buf);
                suffixModern.write(buf);
            } else if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                buf.writeByte(color);
            }
        }
        if (mode == 0 || mode == 3 || mode == 4) {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                ProtocolUtils.writeVarInt(buf, players.length);
            } else {
                buf.writeShort(players.length);
            }
            for (String player : players) {
                ProtocolUtils.writeString(buf, player);
            }
        }
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return false;
    }
}