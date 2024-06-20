package com.velocityscoreboardapi.api;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocityscoreboardapi.impl.BlankFormat;
import com.velocityscoreboardapi.impl.FixedFormat;
import com.velocityscoreboardapi.impl.StyledFormat;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public interface NumberFormat {

    @NotNull
    static NumberFormat blank() {
        return BlankFormat.INSTANCE;
    }

    @NotNull
    static NumberFormat style(@NotNull Style style) {
        return new StyledFormat(style);
    }

    @NotNull
    static NumberFormat fixed(@NotNull Component component) {
        return new FixedFormat(component);
    }

    void write(@NotNull ByteBuf buf, @NotNull ProtocolVersion protocolVersion);

    @NotNull
    static NumberFormat read(@NotNull ByteBuf buf, @NotNull ProtocolVersion protocolVersion) {
        int format = ProtocolUtils.readVarInt(buf);
        switch (format) {
            case 0:
                return BlankFormat.INSTANCE;
            case 1:
                return BlankFormat.INSTANCE;
                //return new NumberFormat(Type.STYLED, readComponentStyle(buf, protocolVersion)); //TODO
            case 2:
                return new FixedFormat(ComponentHolder.read(buf, protocolVersion));
            default:
                throw new IllegalArgumentException("Unknown number format " + format);
        }
    }
}