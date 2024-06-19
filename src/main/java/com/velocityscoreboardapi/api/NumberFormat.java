package com.velocityscoreboardapi.api;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class NumberFormat {
    private final Type type;
    private final Object value;

    public void write(ByteBuf buf, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeVarInt(buf, type.ordinal());
        switch (type) {
            case BLANK:
                break;
            case STYLED:
                //writeComponentStyle((ComponentStyle) format.getValue(), buf, protocolVersion); //TODO
                break;
            case FIXED:
                ((ComponentHolder)value).write(buf);
                break;
        }
    }

    public static NumberFormat read(ByteBuf buf, ProtocolVersion protocolVersion) {
        int format = ProtocolUtils.readVarInt(buf);
        switch ( format ) {
            case 0:
                return new NumberFormat(Type.BLANK, null);
            case 1:
                //return new NumberFormat(Type.STYLED, readComponentStyle(buf, protocolVersion)); //TODO
            case 2:
                return new NumberFormat(Type.FIXED, ComponentHolder.read(buf, protocolVersion));
            default:
                throw new IllegalArgumentException("Unknown number format " + format);
        }
    }

    public enum Type {
        BLANK,
        STYLED,
        FIXED;
    }
}