package com.velocityscoreboardapi.impl;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocityscoreboardapi.api.NumberFormat;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlankFormat implements NumberFormat {

    public static final BlankFormat INSTANCE = new BlankFormat();

    public void write(@NonNull ByteBuf buf, @NonNull ProtocolVersion protocolVersion) {
        ProtocolUtils.writeVarInt(buf, 0);
    }
}
