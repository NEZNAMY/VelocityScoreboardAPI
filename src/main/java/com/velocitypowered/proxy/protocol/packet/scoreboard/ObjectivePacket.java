package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.Either;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.NumberFormat;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Locale;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ObjectivePacket implements MinecraftPacket {

    private boolean decodedFromDownstream;
    private String name;
    private Either<String, ComponentHolder> value;
    private HealthDisplay type;
    private byte action;
    private NumberFormat numberFormat;

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        decodedFromDownstream = true;
        name = ProtocolUtils.readString(buf);
        if (protocolVersion.noGreaterThan(ProtocolVersion.MINECRAFT_1_7_6)) {
            value = Either.left(ProtocolUtils.readString(buf));
            action = buf.readByte();
            return;
        }
        action = buf.readByte();
        if (action == 0 || action == 2) {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                value = Either.right(ComponentHolder.read(buf, protocolVersion));
                type = HealthDisplay.values()[ProtocolUtils.readVarInt(buf)];
            } else {
                value = Either.left(ProtocolUtils.readString(buf));
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
            ProtocolUtils.writeString(buf, value.getLeft());
            buf.writeByte(action);
            return;
        }
        ProtocolUtils.writeString(buf, name);
        buf.writeByte(action);
        if (action == 0 || action == 2) {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                value.getRight().write(buf);
                ProtocolUtils.writeVarInt(buf, type.ordinal());
            } else {
                ProtocolUtils.writeString(buf, value.getLeft());
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

    public enum HealthDisplay {

        INTEGER, HEARTS;

        @Override
        public String toString() {
            return super.toString().toLowerCase(Locale.ROOT);
        }

        public static HealthDisplay fromString(String s) {
            return valueOf(s.toUpperCase(Locale.ROOT));
        }
    }
}
