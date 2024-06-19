package com.velocityscoreboardapi.impl;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocityscoreboardapi.api.NumberFormat;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public class StyledFormat implements NumberFormat {

    @NotNull
    private final Style style;

    public StyledFormat(@NotNull Style style) {
        this.style = style;
    }

    @Override
    public void write(@NotNull ByteBuf buf, @NotNull ProtocolVersion protocolVersion) {
        ProtocolUtils.writeVarInt(buf, 0); // write BLANK before this gets implemented

        //ProtocolUtils.writeVarInt(buf, 1);
        //writeComponentStyle((ComponentStyle) format.getValue(), buf, protocolVersion); //TODO
    }
}
