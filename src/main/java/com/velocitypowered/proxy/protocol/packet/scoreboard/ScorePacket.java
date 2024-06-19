package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.NumberFormat;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ScorePacket implements MinecraftPacket {

    private boolean decodedFromDownstream;
    private String scoreHolder;
    private byte action;
    private String objectiveName;
    private int value;
    private ComponentHolder displayName;
    private NumberFormat numberFormat;

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        decodedFromDownstream = true;
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
        return false;
    }
}
