package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocityscoreboardapi.api.HealthDisplay;
import com.velocityscoreboardapi.api.NumberFormat;
import com.velocityscoreboardapi.internal.PacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Scoreboard objective packet.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class ObjectivePacket implements MinecraftPacket {

    /** Packet priority (higher value = higher priority) */
    private final int packetPriority;

    /** Name of this objective (up to 16 characters) */
    private String objectiveName;

    /** Up to 32 character long title for <1.13 players */
    private String titleLegacy;

    /** Title for 1.13+ players */
    private ComponentHolder titleModern;

    /** Health display for 1.8+ */
    private HealthDisplay type;

    /** Packet action (0 = register, 1 = unregister, 2 = update) */
    private byte action;

    /** Default number format for all scores in this objective */
    private NumberFormat numberFormat;

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        objectiveName = ProtocolUtils.readString(buf);
        if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
            titleLegacy = ProtocolUtils.readString(buf);
            action = buf.readByte();
            return;
        }
        action = buf.readByte();
        if (action == 0 || action == 2) {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                titleModern = ComponentHolder.read(buf, protocolVersion);
                type = HealthDisplay.values()[ProtocolUtils.readVarInt(buf)];
            } else {
                titleLegacy = ProtocolUtils.readString(buf);
                type = HealthDisplay.fromString(ProtocolUtils.readString(buf));
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
                if (buf.readBoolean()) {
                    numberFormat = NumberFormat.read(buf, protocolVersion);
                }
            }
        }
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, objectiveName);
        if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
            ProtocolUtils.writeString(buf, titleLegacy);
            buf.writeByte(action);
            return;
        }
        buf.writeByte(action);
        if (action == 0 || action == 2) {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                titleModern.write(buf);
                ProtocolUtils.writeVarInt(buf, type.ordinal());
            } else {
                ProtocolUtils.writeString(buf, titleLegacy);
                ProtocolUtils.writeString(buf, type.toString());
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
}
