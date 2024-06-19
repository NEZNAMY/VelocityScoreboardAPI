package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocityscoreboardapi.api.NumberFormat;
import com.velocityscoreboardapi.internal.PacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class ScorePacket implements MinecraftPacket {

    /** Packet priority (higher value = higher priority) */
    private final int packetPriority;

    /** Score holder who the score belongs to */
    private String scoreHolder;

    /** Packet action (0 = set, 1 = remove) */
    private byte action;

    /** Objective from which the holder should be removed (null for all objectives ?) */
    private String objectiveName;

    /** Score value */
    private int value;

    /** Display name to use for score holder instead of name */
    private ComponentHolder displayName;

    /** Number format of the score, null to use default number format from objective */
    private NumberFormat numberFormat;

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
            scoreHolder = ProtocolUtils.readString(buf);
            action = buf.readByte();
            if (action != 1) {
                objectiveName = ProtocolUtils.readString(buf);
                value = buf.readInt();
            }
            return;
        }
        scoreHolder = ProtocolUtils.readString(buf);
        if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
            action = buf.readByte();
        }
        objectiveName = ProtocolUtils.readString(buf);
        if (action != 1) {
            value = ProtocolUtils.readVarInt(buf);
        }
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
            if (buf.readBoolean()) displayName = ComponentHolder.read(buf, protocolVersion);
            if (buf.readBoolean()) numberFormat = NumberFormat.read(buf, protocolVersion);
        }
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, scoreHolder);
        if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
            buf.writeByte(action);
        }
        if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
            if (action != 1) {
                ProtocolUtils.writeString(buf, objectiveName);
                buf.writeInt(value);
            }
            return;
        }
        ProtocolUtils.writeString(buf, objectiveName);
        if (action != 1 || protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
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
}
