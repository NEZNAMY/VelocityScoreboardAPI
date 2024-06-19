package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Display objective packet for assigning slot to objectives.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class DisplayObjectivePacket implements MinecraftPacket {

    /** Packet priority (higher value = higher priority) */
    private final int packetPriority;

    /** Display slot (0 = playerlist, 1 = sidebar, 2 = belowname) */
    private int position;

    /** Name of this objective (up to 16 characters) */
    private String objectiveName;

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_2)) {
            position = ProtocolUtils.readVarInt(buf);
        } else {
            position = buf.readByte();
        }
        objectiveName = ProtocolUtils.readString(buf);
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_2)) {
            ProtocolUtils.writeVarInt(buf, position);
        } else {
            buf.writeByte(position);
        }
        ProtocolUtils.writeString(buf, objectiveName);
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return false;
    }
}