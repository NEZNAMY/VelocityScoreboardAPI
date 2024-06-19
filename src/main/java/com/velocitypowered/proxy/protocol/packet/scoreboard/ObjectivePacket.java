package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocityscoreboardapi.api.NumberFormat;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocityscoreboardapi.api.HealthDisplay;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ObjectivePacket implements MinecraftPacket {

    private boolean decodedFromDownstream;
    private String name;
    private String titleLegacy;
    private ComponentHolder titleModern;
    private HealthDisplay type;
    private byte action;
    private NumberFormat numberFormat;

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        decodedFromDownstream = true;
        name = ProtocolUtils.readString(buf);
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
        ProtocolUtils.writeString(buf, name);
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
        return false;
    }
}
