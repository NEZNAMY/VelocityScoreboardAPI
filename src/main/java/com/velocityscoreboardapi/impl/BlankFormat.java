package com.velocityscoreboardapi.impl;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocityscoreboardapi.api.NumberFormat;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public class BlankFormat implements NumberFormat {

    public static final BlankFormat INSTANCE = new BlankFormat();

    private BlankFormat() {}

    public void write(@NotNull ByteBuf buf, @NotNull ProtocolVersion protocolVersion) {
        ProtocolUtils.writeVarInt(buf, 0);
    }
}
